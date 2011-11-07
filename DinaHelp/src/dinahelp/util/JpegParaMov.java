package dinahelp.util;

import dinahelp.util.ListaDeDados;
import java.io.*;
import java.awt.Dimension;
import javax.media.*;
import javax.media.control.*;
import javax.media.protocol.*;
import javax.media.protocol.DataSource;
import javax.media.datasink.*;
import javax.media.format.VideoFormat;

public class JpegParaMov extends Thread implements ControllerListener, DataSinkListener {

	MediaLocator ml;
	int largura = -1, altura = -1, frameRate = 1;
	private ListaDeDados imagens = null;
	public int contaImagens = 0;
	private boolean acabou = false;
	
	@SuppressWarnings("static-access")
	public boolean executar(int largura, int altura, int frameRate, MediaLocator ml) {
		FonteDeDados ids = new FonteDeDados(largura, altura, frameRate);
		Processor p;

		try {
			p = Manager.createProcessor(ids);
		} catch (Exception e) {
			return false;
		}
		p.addControllerListener(this);
		p.configure();
		if (!aguardaEstado(p, p.Configured)) {
		//	System.err.println("ERRO! Não foi possível configurar o processador.");
			return false;
		}

		// Setando o descriptor para o formato do quicktime (mov)
//		p.setContentDescriptor(new ContentDescriptor(FileTypeDescriptor.MSVIDEO));
		p.setContentDescriptor(new ContentDescriptor(FileTypeDescriptor.QUICKTIME));
		
		TrackControl tc[] = p.getTrackControls();
		Format f[] = tc[0].getSupportedFormats();
		if (f == null || f.length <= 0) {
		//	System.err.println("Formato de entrada não suportado: " + tc[0].getFormat());
			return false;
		}

		tc[0].setFormat(f[0]);

//		System.err.println("setando o formato para: " + f[0]);

		p.realize();
		if (!aguardaEstado(p, p.Realized)) {
		//	System.err.println("ERRO! Não foi possível criar o processador.");
			return false;
		}
		
		DataSink ds;
		if ((ds = criaDataSink(p, ml)) == null) {
		//	System.err.println("ERRO! Não foi possível criar DataSink para o MediaLocator dado: " + ml);
			return false;
		}

		ds.addDataSinkListener(this);
		arquivoTerminado = false;

	//	System.err.println("Iniciando processamento...");

		// Agora o encode começa efetivamente
		try {
			p.start();
			ds.start();
		} catch (IOException e) {
		//	System.err.println("ERRO de I/O ao processar!");
			return false;
		}

		// Espera pelo evento de fim
		esperaArquivoTerminado();
		
		try {
			ds.close();
		} catch (Exception e) {
		}
		p.removeControllerListener(this);

//		System.err.println("...fim do processamento.");
		acorda();
		
		return true;
	}
	
	DataSink criaDataSink(Processor p, MediaLocator ml) {
		
		DataSource ds;
		
		if ((ds = p.getDataOutput()) == null) {
		//	System.err.println("ERRO! O processador não possui uma fonte de dados de saída");
			return null;
		}
		
		DataSink dsink;
		
		try {
			System.err.println("- create DataSink for: " + ml);
			dsink = Manager.createDataSink(ds, ml);
			dsink.open();
		} catch (Exception e) {
			System.err.println("Cannot create the DataSink: " + e);
			return null;
		}

		return dsink;
	}
	Object aguardaSinc = new Object();
	boolean estadoTransicaoOK = true;

	// Bloqueia até o processador ir para o estado indicado
	boolean aguardaEstado(Processor p, int estado) {
		synchronized (aguardaSinc) {
			try {
				while (p.getState() < estado && estadoTransicaoOK) {
					aguardaSinc.wait();
				}
			} catch (Exception e) {
			}
		}
		return estadoTransicaoOK;
	}

	/**
	 * Controller Listener.
	 */
	public void controllerUpdate(ControllerEvent evt) {

		if (evt instanceof ConfigureCompleteEvent
				|| evt instanceof RealizeCompleteEvent
				|| evt instanceof PrefetchCompleteEvent) {
			synchronized (aguardaSinc) {
				estadoTransicaoOK = true;
				aguardaSinc.notifyAll();
			}
		} else if (evt instanceof ResourceUnavailableEvent) {
			synchronized (aguardaSinc) {
				estadoTransicaoOK = false;
				aguardaSinc.notifyAll();
			}
		} else if (evt instanceof EndOfMediaEvent) {
			evt.getSourceController().stop();
			evt.getSourceController().close();
		}
	}
	Object waitFileSync = new Object();
	boolean arquivoTerminado = false;
	boolean fileSuccess = true;

	/**
	 * Block until file writing is done.
	 */
	boolean esperaArquivoTerminado() {
		synchronized (waitFileSync) {
			try {
				while (!arquivoTerminado) {
					waitFileSync.wait();
				}
			} catch (Exception e) {
			}
		}
		return fileSuccess;
	}

	/**
	 * Event handler for the file writer.
	 */
	public void dataSinkUpdate(DataSinkEvent evt) {

		if (evt instanceof EndOfStreamEvent) {
			synchronized (waitFileSync) {
				arquivoTerminado = true;
				waitFileSync.notifyAll();
			}
		} else if (evt instanceof DataSinkErrorEvent) {
			synchronized (waitFileSync) {
				arquivoTerminado = true;
				fileSuccess = false;
				waitFileSync.notifyAll();
			}
		}
	}

	public JpegParaMov(String args[]) {
		
		imagens = new ListaDeDados();
		
		if (args.length == 0) {
			prUsage();
		}

		// Parse the arguments.
		int i = 0;
		String outputURL = null;

		while (i < args.length) {

			if (args[i].equals("-w")) {
				i++;
				if (i >= args.length) {
					prUsage();
				}
				largura = new Integer(args[i]).intValue();
			} else if (args[i].equals("-h")) {
				i++;
				if (i >= args.length) {
					prUsage();
				}
				altura = new Integer(args[i]).intValue();
			} else if (args[i].equals("-f")) {
				i++;
				if (i >= args.length) {
					prUsage();
				}
				frameRate = new Integer(args[i]).intValue();
			} else if (args[i].equals("-o")) {
				i++;
				if (i >= args.length) {
					prUsage();
				}
				outputURL = args[i];
			}
			i++;
		}

		if (outputURL == null) {
			prUsage();
		}
		if (largura < 0 || altura < 0) {
			System.err.println("Please specify the correct image size.");
			prUsage();
		}

		// Check the frame rate.
		if (frameRate < 1) {
			frameRate = 1;
		}


		if ((ml = createMediaLocator(outputURL)) == null) {
			System.err.println("Cannot build media locator from: " + outputURL);
			System.exit(0);
		}
		
		
		
	}

	public void setListaDeDados(ListaDeDados JPGIm) { // mudar depois!!
		imagens = JPGIm;
	}

	public void setAcabou(boolean acabou) {
		this.acabou = acabou;
	}
	
	// called by other classes to wait for processor to finish writing mov file.
	public synchronized void waitFor() {
		try {
			while (!acabou) {
				wait(3000);
			}
		} catch (InterruptedException ie) {
			System.err.println("Exception while waiting for movieprocessor " + ie);
		}
	}

	// Acorda as threads que estão esperando
	public synchronized void acorda() {
		acabou = true;
		notifyAll();
	}

	public void run() {
		executar(largura, altura, frameRate, ml);
	}

	static void prUsage() {
		System.err.println("Usage: java JpegImagesToMovie -w <width> -h <height> -f <frame rate> -o <output URL> <input JPEG file 1> <input JPEG file 2> ...");
		System.exit(-1);
	}

	/**
	 * Create a media locator from the given string.
	 */
	static MediaLocator createMediaLocator(String url) {

		MediaLocator ml;

		if (url.indexOf(":") > 0 && (ml = new MediaLocator(url)) != null) {
			return ml;
		}

		if (url.startsWith(File.separator)) {
			if ((ml = new MediaLocator("file:" + url)) != null) {
				return ml;
			}
		} else {
			String file = "file:" + System.getProperty("user.dir") + File.separator + url;
			if ((ml = new MediaLocator(file)) != null) {
				return ml;
			}
		}

		return null;
	}

	///////////////////////////////////////////////
	//
	// Inner classes.
	///////////////////////////////////////////////
	/**
	 * A DataSource to read from a list of JPEG image files and
	 * turn that into a stream of JMF buffers.
	 * The DataSource is not seekable or positionable.
	 */
	class FonteDeDados extends PullBufferDataSource {

		PullBufferStream pbs[];

		FonteDeDados(int largura, int altura, int frameRate) {
			pbs = new PullBufferStream[1];
			pbs[0] = new StreamImagens(largura, altura, frameRate);
		}

		public void setLocator(MediaLocator source) {
		}

		public MediaLocator getLocator() {
			return null;
		}

		/**
		 * Content type is of RAW since we are sending buffers of video
		 * frames without a container format.
		 */
		public String getContentType() {
			return ContentDescriptor.RAW;
		}

		public void connect() {
		}

		public void disconnect() {
		}

		public void start() {
		}

		public void stop() {
		}

		/**
		 * Return the ImageSourceStreams.
		 */
		public PullBufferStream[] getStreams() {
			return pbs;
		}

		/**
		 * We could have derived the duration from the number of
		 * frames and frame rate.  But for the purpose of this program,
		 * it's not necessary.
		 */
		public Time getDuration() {
			return DURATION_UNKNOWN;
		}

		public Object[] getControls() {
			return new Object[0];
		}

		public Object getControl(String type) {
			return null;
		}
	}

	/**
	 * The source stream to go along with FonteDeDados.
	 */
	class StreamImagens implements PullBufferStream {

		int width, height;
		VideoFormat format;
		float frameRate;
		long seqNo = 0;
		boolean ended = false;

		public StreamImagens(int width, int height, int frameRate) {
			this.width = width;
			this.height = height;
			this.frameRate = (float) frameRate;


			/* The commented out code below is remains from a
			 * failed attempt to include avi output. The code is
			 * left in the source like this as a reminder to the
			 * author
			 */
//            format = new VideoFormat(VideoFormat.JPEG,
//                    new Dimension(largura, altura),
//                    Format.NOT_SPECIFIED,
//                    Format.byteArray,
//                    (float)frameRate);

			format = new VideoFormat(VideoFormat.JPEG,
					new Dimension(width, height),
					Format.NOT_SPECIFIED,
					Format.byteArray,
					(float) frameRate);


			/* The commented out code below is remains from a
			 * failed attempt to include avi output. The code is
			 * left in the source like this as a reminder to the
			 * author
			 */
//            final int rMask = 0x00ff0000;
//            final int gMask = 0x0000FF00;
//            final int bMask = 0x000000ff;

//            format =
//                new javax.media.format.RGBFormat(
//                    new Dimension(largura, altura),
//                    Format.NOT_SPECIFIED,
//                    Format.intArray,
//                    frameRate,
//                    24,
//                    rMask,
//                    gMask,
//                    bMask);
		}

		/**
		 * We should never need to block assuming data are read from files.
		 */
		public boolean willReadBlock() {
			return false;
		}

		/**
		 * This is called from the Processor to read a frame worth
		 * of video data.
		 */
		public void read(Buffer buf) throws IOException {

			// Check if we've acabou all the frames.
			if (imagens.acabou) {
				// We are done.  Set EndOfMedia.
				System.err.println("Done reading all images.");
				System.err.println("Frames: " + imagens.totImg);
				System.err.println("Missed frames: "
						+ (imagens.imagensEnviadas - imagens.totImg));
				buf.setEOM(true);
				buf.setOffset(0);
				buf.setLength(0);
				ended = true;
				return;
			}

			float time1 = seqNo * (1000 / frameRate) * 1000000;
			long time = (long) time1;
			buf.setTimeStamp(time);

			buf.setSequenceNumber(seqNo++);

			byte[] picBytes = imagens.leDados();			// read the next image in line
			// in the DataList.
			byte data[] = null;

//            int data[] = new int[picBytes.length / 4];

			// Read the entire JPEG image from the file.
			data = picBytes;

			/* The commented out code below is remains from a
			 * failed attempt to include avi output. The code is
			 * left in the source like this as a reminder to the
			 * author
			 */
//            int dataCnt = 0;
//            int mult;
//            for (int cnt = 0; cnt < data.length; cnt ++) {
//                mult = 256*256*256;
//                for (int loopCnt = 0; loopCnt < 4; loopCnt++) {
//                    data[contaImagens] += picBytes[dataCnt++] * mult;
//                    mult /= 256;
//                }
//            }
			buf.setData(data);


			buf.setOffset(0);
			buf.setLength((int) picBytes.length);
			buf.setFormat(format);
			buf.setFlags(buf.getFlags() | buf.FLAG_KEY_FRAME);

		}

		/**
		 * Return the format of each video frame.  That will be JPEG.
		 */
		public Format getFormat() {
			return format;
		}

		public ContentDescriptor getContentDescriptor() {
			return new ContentDescriptor(ContentDescriptor.RAW);
		}

		public long getContentLength() {
			return 0;
		}

		public boolean endOfStream() {
			return ended;
		}

		public Object[] getControls() {
			return new Object[0];
		}

		public Object getControl(String type) {
			return null;
		}
	}
}
