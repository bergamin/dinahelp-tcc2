package dinahelp.util;

import java.io.*;
import java.awt.Dimension;
import javax.media.*;
import javax.media.control.*;
import javax.media.protocol.*;
import javax.media.protocol.DataSource;
import javax.media.datasink.*;
import javax.media.format.VideoFormat;

/**
 * @author Guilherme Taffarel Bergamin
 * @author Akanbi Strossi de Jesus
 * @author Felipe Bochehin
 */
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
			return false;
		}

		p.setContentDescriptor(new ContentDescriptor(FileTypeDescriptor.QUICKTIME));

		TrackControl tc[] = p.getTrackControls();
		Format f[] = tc[0].getSupportedFormats();
		if (f == null || f.length <= 0) {
			return false;
		}

		tc[0].setFormat(f[0]);

		p.realize();
		if (!aguardaEstado(p, p.Realized)) {
			return false;
		}

		DataSink ds;
		if ((ds = criaDataSink(p, ml)) == null) {
			return false;
		}

		ds.addDataSinkListener(this);
		arquivoTerminado = false;

		try {
			p.start();
			ds.start();
		} catch (IOException e) {
			return false;
		}

		esperaArquivoTerminado();

		try {
			ds.close();
		} catch (Exception e) {
		}
		p.removeControllerListener(this);

		acorda();

		return true;
	}

	DataSink criaDataSink(Processor p, MediaLocator ml) {

		DataSource ds;

		if ((ds = p.getDataOutput()) == null) {
			return null;
		}

		DataSink dsink;

		try {
			dsink = Manager.createDataSink(ds, ml);
			dsink.open();
		} catch (Exception e) {
			return null;
		}

		return dsink;
	}
	Object aguardaSinc = new Object();
	boolean estadoTransicaoOK = true;

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
			prUsage();
		}

		if (frameRate < 1) {
			frameRate = 1;
		}

		if ((ml = createMediaLocator(outputURL)) == null) {
			System.exit(0);
		}



	}

	public void setListaDeDados(ListaDeDados JPGIm) {
		imagens = JPGIm;
	}

	public void setAcabou(boolean acabou) {
		this.acabou = acabou;
	}

	public synchronized void waitFor() {
		try {
			while (!acabou) {
				wait(3000);
			}
		} catch (InterruptedException ie) {
		}
	}

	public synchronized void acorda() {
		acabou = true;
		notifyAll();
	}

	public void run() {
		executar(largura, altura, frameRate, ml);
	}

	static void prUsage() {
		System.exit(-1);
	}

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

		public PullBufferStream[] getStreams() {
			return pbs;
		}

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

			format = new VideoFormat(VideoFormat.JPEG,
					new Dimension(width, height),
					Format.NOT_SPECIFIED,
					Format.byteArray,
					(float) frameRate);

		}

		public boolean willReadBlock() {
			return false;
		}

		public void read(Buffer buf) throws IOException {

			if (imagens.acabou) {
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

			byte[] picBytes = imagens.leDados();
			byte data[] = null;
			data = picBytes;
			buf.setData(data);
			buf.setOffset(0);
			buf.setLength((int) picBytes.length);
			buf.setFormat(format);
			buf.setFlags(buf.getFlags() | buf.FLAG_KEY_FRAME);

		}
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
