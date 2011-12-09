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

	/** Localização de mídia */
	MediaLocator ml;
	/** Dimensões */
	int largura = -1, altura = -1, frameRate = 1;
	/** Imagens a serem tratadas */
	private ListaDeDados imagens = null;
	/** Contador de Imagens */
	public int contaImagens = 0;
	/** Flag de término do serviço */
	private boolean acabou = false;
	/** Utilizados no aguardaEstado() e controllerUpdate() */
	Object aguardaSinc = new Object();
	boolean estadoTransicaoOK = true;
	/** Utilizados no esperaArquivoTerminado() e dataSinkUpdate() */
	Object aguardaSincArquivo = new Object();
	boolean arquivoTerminado = false;
	boolean arquivoSucesso = true;

	/** Construtor */
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

		if ((ml = criarMediaLocator(outputURL)) == null) {
			System.exit(0);
		}
	}

	/**
	 * Metodo principal de execução
	 * Executa a transformação das imagens
	 */
	@SuppressWarnings("static-access")
	public boolean executar(int largura, int altura, int frameRate, MediaLocator ml) {
		FonteDeDados fd = new FonteDeDados(largura, altura, frameRate);
		Processor p;

		try {
			p = Manager.createProcessor(fd);
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

	/** Cria o DataSink */
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

	/**
	 * Bloqueia até que o Processor tenha executado até o estado informado por
	 * parâmetro. Retorna false em caso de falha
	 */
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

	/** monitora o Controller */
	@Override
	public void controllerUpdate(ControllerEvent e) {

		if (e instanceof ConfigureCompleteEvent
				|| e instanceof RealizeCompleteEvent
				|| e instanceof PrefetchCompleteEvent) {
			synchronized (aguardaSinc) {
				estadoTransicaoOK = true;
				aguardaSinc.notifyAll();
			}
		} else if (e instanceof ResourceUnavailableEvent) {
			synchronized (aguardaSinc) {
				estadoTransicaoOK = false;
				aguardaSinc.notifyAll();
			}
		} else if (e instanceof EndOfMediaEvent) {
			e.getSourceController().stop();
			e.getSourceController().close();
		}
	}

	/** Aguarda até escrita do arquivo estiver completa */
	boolean esperaArquivoTerminado() {
		synchronized (aguardaSincArquivo) {
			try {
				while (!arquivoTerminado) {
					aguardaSincArquivo.wait();
				}
			} catch (Exception e) {
			}
		}
		return arquivoSucesso;
	}

	/** Evento para a escrita do arquivo */
	@Override
	public void dataSinkUpdate(DataSinkEvent e) {

		if (e instanceof EndOfStreamEvent) {
			synchronized (aguardaSincArquivo) {
				arquivoTerminado = true;
				aguardaSincArquivo.notifyAll();
			}
		} else if (e instanceof DataSinkErrorEvent) {
			synchronized (aguardaSincArquivo) {
				arquivoTerminado = true;
				arquivoSucesso = false;
				aguardaSincArquivo.notifyAll();
			}
		}
	}

	/** Seta imagens */
	public void setImagens(ListaDeDados imagens) {
		this.imagens = imagens;
	}

	/** Seta acabou */
	public void setAcabou(boolean acabou) {
		this.acabou = acabou;
	}

	/**
	 * Chamado por outras classes para aguardar a finalização da escrita do
	 * arquivo mov
	 */
	public synchronized void aguardar() {
		try {
			while (!acabou) {
				wait(3000);
			}
		} catch (InterruptedException ie) {
		}
	}

	/** Acorda thread aguardando para execução */
	public synchronized void acorda() {
		acabou = true;
		notifyAll();
	}

	/** Apenas chama o método executar() */
	@Override
	public void run() {
		executar(largura, altura, frameRate, ml);
	}

	static void prUsage() {
		System.exit(-1);
	}

	/** Cria um MediaLocator a partir da url passada por parâmetro */
	static MediaLocator criarMediaLocator(String url) {

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

	/**
	 * fonte de dados para ler de uma lista de imagens jpeg e transformar em
	 * um stream de buffers do JMF
	 */
	class FonteDeDados extends PullBufferDataSource {

		/** Streams */
		PullBufferStream[] pbs;

		/** Construtor */
		FonteDeDados(int largura, int altura, int frameRate) {
			pbs = new PullBufferStream[1];
			pbs[0] = new StreamImagens(largura, altura, frameRate);
		}

		@Override
		public void setLocator(MediaLocator source) {
		}

		@Override
		public MediaLocator getLocator() {
			return null;
		}

		/**
		 * O tipo é RAW porque está sendo enviado buffers de vídeo sem
		 * formatação
		 */
		@Override
		public String getContentType() {
			return ContentDescriptor.RAW;
		}

		@Override
		public void connect() {
		}

		@Override
		public void disconnect() {
		}

		@Override
		public void start() {
		}

		@Override
		public void stop() {
		}

		/** Retorna os streams */
		@Override
		public PullBufferStream[] getStreams() {
			return pbs;
		}

		@Override
		public Time getDuration() {
			return DURATION_UNKNOWN;
		}

		@Override
		public Object[] getControls() {
			return new Object[0];
		}

		@Override
		public Object getControl(String type) {
			return null;
		}
	}

	/** Stream que irá junto com o FonteDeDados */
	class StreamImagens implements PullBufferStream {

		int largura, altura;
		VideoFormat formato;
		float frameRate;
		long sequencia = 0;
		boolean finalizado = false;

		/** Construtor */
		public StreamImagens(int largura, int altura, int frameRate) {
			this.largura = largura;
			this.altura = altura;
			this.frameRate = (float) frameRate;

			formato = new VideoFormat(VideoFormat.JPEG, new Dimension(largura, altura), Format.NOT_SPECIFIED, Format.byteArray, (float) frameRate);

		}

		@Override
		public boolean willReadBlock() {
			return false;
		}

		/** Chamado pelo Processor para ler um frame com dados de vídeo */
		@Override
		@SuppressWarnings("static-access")
		public void read(Buffer buf) throws IOException {

			if (imagens.acabou) {
				buf.setEOM(true);
				buf.setOffset(0);
				buf.setLength(0);
				finalizado = true;
				return;
			}

			float tempo1 = sequencia * (1000 / frameRate) * 1000000;
			long tempo = (long) tempo1;
			buf.setTimeStamp(tempo);

			buf.setSequenceNumber(sequencia++);

			byte[] bytesImg = imagens.leDados();
			byte dados[] = null;
			dados = bytesImg;
			buf.setData(dados);
			buf.setOffset(0);
			buf.setLength((int) bytesImg.length);
			buf.setFormat(formato);
			buf.setFlags(buf.getFlags() | buf.FLAG_KEY_FRAME);

		}

		/** Retorna o formato de cada frame de vídeo */
		@Override
		public Format getFormat() {
			return formato;
		}

		@Override
		public ContentDescriptor getContentDescriptor() {
			return new ContentDescriptor(ContentDescriptor.RAW);
		}

		@Override
		public long getContentLength() {
			return 0;
		}

		@Override
		public boolean endOfStream() {
			return finalizado;
		}

		@Override
		public Object[] getControls() {
			return new Object[0];
		}

		@Override
		public Object getControl(String type) {
			return null;
		}
	}
}
