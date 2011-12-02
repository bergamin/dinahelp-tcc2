package dinahelp.negocio;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import dinahelp.util.JpegParaMov;
import dinahelp.util.ListaDeDados;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * @author Guilherme Taffarel Bergamin
 * @author Akanbi Strossi de Jesus
 * @author Felipe Bochehin
 */
public class AnimacaoNegocio extends Thread {

	/** Lista de imagens */
	private ListaDeDados imagens;
	/** Quantidade de imagens */
	private int contImagens;
	/** Tamanho de cada imagem */
	private int[] tamanhos;
	/** Arquivo temporário */
	private File despejo;
	/**
	 * Caminho para a gravação do arquivo temporário de vídeo.
	 * No caso, a raiz de onde estiver instalado o programa.
	 */
	public String arquivoTemp = "temp.mov";
	/**
	 * Área de captura da tela.
	 * NÃO VAI SER USADO! TIRAR DAQUI DEPOIS
	 */
	public Rectangle retangulo;
	/** Frames por segundo */
	private int fps = 1;
	/**
	 * Previne a gravação de iniciar por engano enquanto o método init()
	 * estiver executando.
	 */
	public boolean inicializando = false;
	/** Isto é onde todos os frames encodados são gravados na memória */
	private DataOutputStream bytesJPG;
	private ByteArrayOutputStream bytesJPG_2;
	private FileOutputStream bytesArquivo;
	private BufferedOutputStream bufferArquivo;
	/** Encoder usado para encodar as imagens */
	private JPEGImageEncoder encoder;
	/** Usado para verificar a memória disponível */
	private Runtime runtime;
	/** Numeração para o nome dos arquivos temporários */
	public int contMov = 0;
	/** Número máximo de frames que podem ser gravados na memória */
	public int NumMaxImg;
	/** Usado para guardar cada imagem */
	private BufferedImage imagem;
	/**
	 * Usado para fazer a captura das imagens
	 * NÃO SERÁ USADO! REMOVER DEPOIS!
	 */
	private Robot robot;
	/** Parâmetros do encoder para encodar as imagens */
	private JPEGEncodeParam param;
	/** Qualidade do encode da animação (1.0f é o máximo) */
	public float qualidadeEnc = 0.75f;
	/**
	 * Guarda o primeiro e o último frame do vídeo (imagens vazias).
	 * Assim temos certeza de que se o primeiro frame real for perdido,
	 * não teremos que preencher o espaço dele com frames que nunca existiram
	 * NÃO SERÁ USADO! REMOVER DEPOIS!
	 */
	private byte[] priUltFrame;
	/**
	 * Guarda o nº de todos os frames perdidos
	 * NÃO SERÁ USADO! REMOVER DEPOIS!
	 */
	private int[] framesPerdidos;
	/**
	 * Captura o tamanho do ByteArrayOutputStream usado para guardar frames na
	 * memória.
	 */
	private int tamanho = 0;
	/** Caminho das imagens que serão encodadas */
	private File[] listaImagens;
	
	// daqui em diante foi adicionado e não sei se será necessário!
	
	private double tempo;
	private boolean naoTerminado;
	private boolean gravando;
	private boolean executando;
	private long tempoSinc;

	/** Construtor recebe o caminho das imagens a serem gravadas na animação */
	public AnimacaoNegocio(File[] listaImagens) {
		this.listaImagens = listaImagens;
	}
	public void init() throws IOException {
		inicializando = true;
		bytesJPG = null;
		bufferArquivo = null;
		bytesArquivo = null;
		/** This is an array for storing image data
		 *  directly into memory. It is only used in
		 *  The initialization process.
		 */
		bytesJPG_2 = new ByteArrayOutputStream();
		/** Clear memory. */
		encoder = null;
		tamanhos = null;
		framesPerdidos = null;
		System.gc();
		/** trying to allocate all available memory except 20MB
		 *  that are saved for performance purposes. If this fails,
		 *  allocate half of the available memory below.
		 *
		 *  maxMemory = the maximum memory that we can use.
		 *  totalMemory = the memory that we have reserved so far.
		 *  freeMemory = the part of totalMemory that is not used.
		 */
		runtime = Runtime.getRuntime();
		Double convert = new Double(runtime.maxMemory()
				- runtime.totalMemory() + runtime.freeMemory() - 2097152 * 10);
//		System.out.println("Memory attempt 1: " + convert);
		if (convert.intValue() < 0) {
			convert = new Double((runtime.maxMemory()
					- runtime.totalMemory() + runtime.freeMemory()) * 0.5d);
		}
//		System.out.println("Memory attempt 2: " + convert);
		/** Set up a save file for encoded jpg imagens.
		 *  This file is then used directly as output.
		 */
		despejo = new File("dumpFile" + contMov);
		/** This is just a safety check to see that the
		 *  file can really be written too. */
//        while (dumpFile.exists() && !dumpFile.delete()) {
//            dumpFile = mySaveQuery.getNextFile(dumpFile);
		//       }
		despejo.deleteOnExit();
		/** Allocate memory for frame storage in a file
		 *  buffer for the output file.
		 */
		bytesArquivo = new FileOutputStream(despejo);
//		System.out.println("filebuffer should use: "
//				+ (convert.intValue() / 2));
		bufferArquivo = new BufferedOutputStream(bytesArquivo,
				(convert.intValue() / 2));
		/** jpgBytes will be the output stream that we write
		 *  to later on in the program.
		 */
		bytesJPG = new DataOutputStream(bufferArquivo);
		/** Figure out how many integers we can hold in the
		 *  remaining memory. An integer takes 4B, and we will
		 *  have 2 arrays of integers. We don't want to fill the
		 *  entire memory, and we have used a third, or possibly
		 *  even half, already. So we will allocate 2 arrays of
		 *  integers, each using a sixth of the available memory.
		 *  That means we can at the most have
		 *  convert.intValue() / 24
		 *  integers in each array. That also means we can at
		 *  the most have that amount of frames in a film.
		 *
		 *  As a comparison, an integer array of 1 MB can hold
		 *  262144 integers, which would be enough for
		 *  2h25m38s of film at 30 FPS.
		 */
		NumMaxImg = (int) (convert.intValue() / 24);
//		System.out.println("Memory after filebuffer: "
//				+ (myRuntime.maxMemory() - myRuntime.totalMemory()
//				+ myRuntime.freeMemory()));
		/** Init the encoder to store encoded imagens directly to memory.
		 *  This will be changed later, but is an easy way of getting a
		 *  first frame for the film, since that frame is to be kept in
		 *  memory anyway  (see below).
		 *
		 *  First we take an "average (=random)" image for the method
		 *  encoder.getDefaultJPEGEncodeParam(image) below.
		 */
		//imagem = robot.createScreenCapture(retangulo);
		imagem = ImageIO.read(listaImagens[0]);
		/** Set the encoder to the OutputStream that stores in memory. */
		encoder = JPEGCodec.createJPEGEncoder(bytesJPG_2);
		/** Get an "average" JPEGEncodeParam */
		param = encoder.getDefaultJPEGEncodeParam(imagem);
		/** Set encoding quality */
		param.setQuality(qualidadeEnc, false);
		encoder.setJPEGEncodeParam(param);
		/** Store an empty image in the first frame of the film.
		 *  Then keep that jpg image in memory to also be used as the
		 *  last frame in the film as well
		 */
		imagem = new BufferedImage(imagem.getWidth(),
				imagem.getHeight(),
				imagem.getType());
		encoder.encode(imagem);
		priUltFrame = bytesJPG_2.toByteArray();
		/** Clear this output stream, which we will not use for 
		 *  anything more than this initialization procedure, 
		 *  to save some memory */
		bytesJPG_2 = null;
		/** Change the encoder to store encoded image in the
		 *  buffered FileOutputStream.
		 *
		 *  First we take another "average" screenshot.
		 */
		//imagem = robot.createScreenCapture(retangulo);
		imagem = ImageIO.read(listaImagens[0]);
		/** Set the encoder to the FileOutputStream */
		encoder = JPEGCodec.createJPEGEncoder(bytesJPG);
		/** Get an "average" JPEGEncodeParam */
		param = encoder.getDefaultJPEGEncodeParam(imagem);
		/** Set encoding quality */
		param.setQuality(qualidadeEnc, false);
		encoder.setJPEGEncodeParam(param);
		/** We write the black frame into the outputstream,
		 *  since we need at least one frame to prevent the
		 *  situation that we would miss capturing the first
		 *  frame, and then try to fill it by repeating a
		 *  frame that never existed.
		 */
		bytesJPG.write(priUltFrame, 0, priUltFrame.length);
		/** Allocate int Arrays for storing image sizes,
		 *  and missed imagens. Setup remaining variables.
		 */
//		System.out.println("arrays should use (in total): "
//				+ (NumMaxImg * 8));
		boolean memError = true;
		/** Allocate an integer array for storing the sizes of
		 *  each recorded image. Allocate an additional array
		 *  to store every frame that was missed. The number of
		 *  integers in these arrays represents the maximum number
		 *  of frames that can be recorded in a single film
		 *  using this design of the program. This is not pretty.
		 *
		 *  A very good alternative would be to store an integer
		 *  or possibly even something bigger, before each frame
		 *  in OutputStream, to indicate the size of each frame.
		 *  0 or -1 could be used for missing frames.
		 *
		 *  The reason for doing this with arrays with prereserved
		 *  memory in the first place was speed. It was unnecessary,
		 *  beacuse it does not give much speed, but now it will
		 *  not be changed now until the procedure described above
		 *  is implemented.
		 */
		while (memError) {
			memError = false;
			try {
				tamanhos = new int[NumMaxImg];
				//	Allow as many missed frames.
				framesPerdidos = new int[NumMaxImg];
			} catch (OutOfMemoryError oe) {
//				System.err.println("Unexpected memory error in ScreenGrabber.init()");
//				System.err.println("Trying to allocate smaller arrays.");
				memError = true;
				NumMaxImg /= 2;
			}
		}
//		System.out.println("Memory after arrays: "
//				+ (myRuntime.maxMemory() - myRuntime.totalMemory()
//				+ myRuntime.freeMemory()));
		/** This is needed in case no frames are missed, since
		 *  missedFrames must contain a value. If a frame is missed,
		 *  this value will be overwritten.
		 */
		framesPerdidos[0] = NumMaxImg + 1;
		/** One frame is already caught, setup size for that frame */
		tamanhos[0] = bytesJPG.size();
		/** Setup size counter. */
		tamanho = tamanhos[0];
		/** Setup frame counter */
		contImagens = 1;
		/** These last two lines are for waking up the
		 *  run method, in the unlikely event that it
		 *  is waiting on init to finish.
		 */
		inicializando = false;
		wakeUp();
	}

	/**	Set the fps values.
	 *
	 *  @param  fps The gravando fps.
	 *  @param  playbackFps The playback fps.
	 */
	public void setFps(int fps, int playbackFps) {
		this.fps = playbackFps;
		tempo = 1000d / fps;
//        mySnapShot.setFps(fps); // VER ISSO AQUI!!!!!!!
	}

	public void setNaoTerminado(boolean naoTerminado) {
		this.naoTerminado = naoTerminado;
	}
	/**	Starts a new JpegImagesToMovie, and waits for it to finish
	 * making a mov file of the jpg imagens.
	 *
	 *  @param  imagens  A DataList object that is set-up to read imagens from
	 *                  the temp-file containing a recently recorded
	 *                  movie.
	 */
	public void startDumper(ListaDeDados imagens) {
		// Create a new tempfile filename for each movie.
		try {
			File testFile = new File(arquivoTemp);
//            while (testFile.exists() && !testFile.delete()) {
//                testFile = mySaveQuery.filterFile(mySaveQuery.getNextFile(testFile));
//            }

			String tempTotal = testFile.getPath();
			String arguments[] = {"-w", Integer.toString(retangulo.width), "-h", Integer.toString(retangulo.height), "-f", Integer.toString(fps), "-o", tempTotal};

			// Create a new dumper.
			JpegParaMov dumper = new JpegParaMov(arguments);

			// Point dumper to datasource.
			dumper.setListaDeDados(imagens);
			// Run dumper, and wait for it to finish with waitFor().
			dumper.setPriority(Thread.NORM_PRIORITY);
			dumper.setAcabou(false);
			dumper.start();
			dumper.waitFor();
		} catch (Exception e) {
//			System.err.println(e);
		} catch (OutOfMemoryError o) {
//			System.out.println(o);
		}
	}

	/** Used to sync video.
	 * Users may call this method, and are then woken
	 * once when the last frame is captured, and once more
	 * when the gravando is completely finished.
	 *
	 * The gravando and executando flags must be checked in order
	 * to safely determine if gravando is executando upon return
	 * from this method.
	 *
	 * The VideoNegocio itself calls this method from run(), and
	 * then waits here for capture to start.
	 */
	public synchronized void hold() {
		try {
			wait(3000);
		} catch (InterruptedException ie) {
//			System.err.println(ie);
		}
	}

	/** Called by the user to start capturing imagens.
	 *
	 * Also wakes up users waiting for the grabber to finish.
	 * This method is called once when the last frame is captured,
	 * and once more when all video data are written to the
	 * temp file.
	 */
	public synchronized void wakeUp() {
		notifyAll();
	}

	/** Currently unused method to convert a BufferedImage to
	 *  a byte array.
	 *
	 *  @param  inImage The BufferedImage.
	 *  @return A byte array containging inImage.
	 */
	private byte[] bufferedImageToByteArray(BufferedImage inImage) {
		int[] intArray = inImage.getRGB(0, 0,
				inImage.getWidth(),
				inImage.getHeight(),
				null,
				0,
				inImage.getWidth());
		ByteArrayOutputStream temp = new ByteArrayOutputStream();
		DataOutputStream data = new DataOutputStream(temp);
		for (int cnt = 0; cnt < intArray.length; cnt++) {
			try {
				data.writeInt(intArray[cnt]);
			} catch (Exception e) {
//				System.out.println("total error");
			}
		}
		return temp.toByteArray();
	}

	/** Main working method.
	 *  It captures a frame, and sleeps for the amount of tempo
	 *  left until the next frame should be captured.
	 *	If capturing the frame took longer than fps would allow,
	 *	the frame is marked as missed, and then copied the number
	 *  of times required to get back in sync.
	 *	The method ends with a call to startDumper(), where a temp
	 *	mov file is made, then this method finally changes the
	 *  executando parameter, and makes one last call to wakeUp().
	 *	The gravando is started when the user sets the naoTerminado
	 *	flag to true, and then calls VideoNegocio.wakeUp(). It is
	 *	stopped when the user sets the naoTerminado flag to false.
	 */
	@Override
	public void run() {
		/** This loop will run until the VM exits, from somewhere else. */
		while (true) {
			try {
				/** Make sure the grabber is inited. */
				init();
				/** Wait for gravando to start. */
				while (!naoTerminado) {
					hold();
				}
				/** Safety check */
				while (inicializando) {
					hold();
				}
				/** 2 Flags that are readable by the user. */
				gravando = true;
				executando = true;
				/** Main gravando loop. naoTerminado is set to false
				 *  by user to stop gravando. */
				for(int i = 0; i <= listaImagens.length; i++) {
					/** This is where we capture the image. */
					//imagem = robot.createScreenCapture(retangulo);
					imagem = ImageIO.read(listaImagens[i]);
					/** If the preview window is visible, we should
					 *  update the image showing there.
					 */
//                    if (preview) {
//                        mySnapShot.updatePreviewImage(image);
//                    }
					/** Encode a jpg directly to the OutputStream. */
					encoder.encode(imagem);
//                    jpgBytes.write(this.bufferedImageToByteArray(image));
					/** Save the size of the jpg in a separate array */
					tamanhos[contImagens] = bytesJPG.size() - tamanho;
					tamanho += tamanhos[contImagens];
//					/** The next part is used to stay in sync. */
//					tempoSinc += tempo;
//					tempoAtual = System.currentTimeMillis();
//					while (tempoSinc < tempoAtual) {
//						framesPerdidos[contPerdidos++] = contImagens;
//						tempoSinc += tempo;
//					}
					contImagens++;
					/** The loop is finished. */
//					Thread.sleep((long) tempoSinc - tempoAtual);
					Thread.sleep(1000 / fps);
				}
			} catch (OutOfMemoryError o) {
				Runtime runtime = Runtime.getRuntime();
				long mem = runtime.maxMemory() - runtime.totalMemory()
						+ runtime.freeMemory();
//				System.out.println("Interrompido por falta de memória");
//				System.out.println("Memória: " + mem);
//				System.out.println(o);
			} catch (Exception e) {
				Runtime runtime = Runtime.getRuntime();
				long mem = runtime.maxMemory() - runtime.totalMemory()
						+ runtime.freeMemory();
//				System.out.println("Interrompido por limite de frames atingido ou falta de memória");
//				System.out.println("Número máximo de imagens: " + NumMaxImg);
//				System.out.println("Memória: " + mem);
//				System.out.println(e);
			} finally {
				/** We're finished gravando video. */
				try {
					contMov++;
					/** Write a final frame, and close the file for temporary
					 *  image data. */
					try {
						bytesJPG.write(priUltFrame, 0, priUltFrame.length);
					} catch (IOException e) {
//						System.err.println(e);
					}
					tamanhos[contImagens] = priUltFrame.length;
					/** At this point we are done gravando.
					 *  We create a new DataList object for this movie.
					 *  The DataList object acts as an input source
					 *  for the JpegImagesToMovieMod class.
					 *  The DataList object, imagens, is global, so
					 *  that the encode() method has access to it as well.
					 */
					imagens = new ListaDeDados();
					imagens.totImg = contImagens;
					imagens.tamanhoImagens = tamanhos;
					imagens.framesPerdidos = framesPerdidos;
					imagens.setArquivo(despejo);
					gravando = false;
					/** wake up users waiting to sync audio. */
					wakeUp();
					/** Recording is now finished, encoding starts
					 *  when someone calls the encode() method.
					 *  In case there is a thread waiting to stop 
					 *  Audio gravando, this thread will now hold,
					 *  briefly before doing anything else. This is
					 *  done only for maximum sync between audio and
					 *  video. After audio gravando is stopped, this
					 *  thread will loop back to the top of this
					 *  method and call the init() method again.
					 */
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/** This is the method where the encoding of
	 *  the mov-file takes place. In essence, this
	 *  method picks up exactly where the run() method
	 *  left off. The reason for keeping this code
	 *  outside the run() method, is that the user may
	 *  want to record at one thread priority (typically
	 *  the highest), and encode at another.
	 *
	 *  Before calling the encode() method, the user should
	 *  assign an EncoderProgressBar to this VideoNegocio
	 *  object by setting the myProgressBar parameter.
	 */
	public void encode() {
		/** Make sure the progress bar has direct access
		 *  to the DataList, so it can stop encoding if 
		 *  the user requests it.
		 */
//        myProgressBar.myDataList = imagens;
		/** Start encoding. */
		startDumper(imagens);
		/** We return here when we are done encoding,
		 *  or when the encoding has been interrupted.
		 *  Now we can set the executando flag, to tell
		 *  the user waiting to merge audio and video
		 *  that we are ready.
		 */
		executando = false;
		/** Try to delete the file that we used as a
		 *  data source for the movie (the old dumpfile).
		 */
		try {
			imagens.fis.close();
			imagens.arquivo.delete();
		} catch (Exception e) {
//			System.out.println(e);
		}
		/** Free some memory from the arrays, and then init
		 *  again to be able to use it */
		imagens.tamanhoImagens = null;
		imagens.framesPerdidos = null;
		try {
			inicializando = true;
			if (!gravando) {
				init();
			}
		} catch (Exception e) {
//			System.out.println(e);
		}
		/** Wake up users waiting to use this VideoNegocio
		 *  again, and users waiting to merge the encoded
		 *  movie file.
		 */
		inicializando = false;
		wakeUp();
	}
	public void setSyncTime(long syncTime) {
		this.tempoSinc = syncTime;
	}
//	public void run() throws IOException, InterruptedException {
//
//		imagens = new ListaDeDados();
//		imagens.totImg = contImagens;
//		imagens.tamanhoImagens = tamanhos;
//		imagens.setArquivo(despejo);
//
//		for (int i = 0; i <= listaImagens.length; i++) {
//			File arquivo = new File("C:\\DinaHelp\\meme.jpeg");
//			imagem = ImageIO.read(arquivo);
//			// imagem = ImageIO.read(listaImagens[i]);
//			encoder.encode(imagem);
//			tamanhos[contImagens] = bytesJPG.size() - tamanho;
//			tamanho += tamanhos[contImagens];
//			contImagens++;
//			Thread.sleep(1000 / fps);
//		}
//
//	}
//
//	public void startDumper(ListaDeDados imagens) {
//		// Create a new tempfile filename for each movie.
//		try {
//			File testFile = new File(arquivoTemp);
//			String tempTotal = testFile.getPath();
//			String arguments[] = {"-w", Integer.toString(retangulo.width), "-h", Integer.toString(retangulo.height), "-f", Integer.toString(fps), "-o", tempTotal};
//
//			// Create a new dumper.
//			JpegParaMov dumper = new JpegParaMov(arguments);
//
//			// Point dumper to datasource.
//			dumper.setListaDeDados(imagens);
//			// Run dumper, and wait for it to finish with waitFor().
//			dumper.setPriority(Thread.NORM_PRIORITY);
//			dumper.setAcabou(false);
//			dumper.start();
//			dumper.waitFor();
//		} catch (Exception e) {
//			e.printStackTrace();
//		} catch (OutOfMemoryError o) {
//			o.printStackTrace();
//		}
//	}
//
//	/** This is the method where the encoding of
//	 *  the mov-file takes place. In essence, this
//	 *  method picks up exactly where the run() method
//	 *  left off. The reason for keeping this code
//	 *  outside the run() method, is that the user may
//	 *  want to record at one thread priority (typically
//	 *  the highest), and encode at another.
//	 *
//	 *  Before calling the encode() method, the user should
//	 *  assign an EncoderProgressBar to this VideoNegocio
//	 *  object by setting the myProgressBar parameter.
//	 */
//	public void encode() {
//		/** Start encoding. */
//		startDumper(imagens);
//		/** Try to delete the file that we used as a
//		 *  data source for the movie (the old dumpfile).
//		 */
//		try {
//			imagens.fis.close();
//			imagens.arquivo.delete();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		/** Free some memory from the arrays, and then init
//		 *  again to be able to use it */
//		imagens.tamanhoImagens = null;
//		try {
//			init();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		/** Wake up users waiting to use this VideoNegocio
//		 *  again, and users waiting to merge the encoded
//		 *  movie file.
//		 */
//		inicializando = false;
//		wakeUp();
//	}
//
//	public void init() throws IOException {
//		inicializando = true;
//		bytesJPG = null;
//		bufferArquivo = null;
//		bytesArquivo = null;
//		/** This is an array for storing image data
//		 *  directly into memory. It is only used in
//		 *  The initialization process.
//		 */
//		bytesJPG_2 = new ByteArrayOutputStream();
//		/** Clear memory. */
//		encoder = null;
//		tamanhos = null;
//		System.gc();
//		/** trying to allocate all available memory except 20MB
//		 *  that are saved for performance purposes. If this fails,
//		 *  allocate half of the available memory below.
//		 *
//		 *  maxMemory = the maximum memory that we can use.
//		 *  totalMemory = the memory that we have reserved so far.
//		 *  freeMemory = the part of totalMemory that is not used.
//		 */
//		runtime = Runtime.getRuntime();
//		Double convert = new Double(runtime.maxMemory()
//				- runtime.totalMemory() + runtime.freeMemory() - 2097152 * 10);
////		System.out.println("Memory attempt 1: " + convert);
//		if (convert.intValue() < 0) {
//			convert = new Double((runtime.maxMemory()
//					- runtime.totalMemory() + runtime.freeMemory()) * 0.5d);
//		}
////		System.out.println("Memory attempt 2: " + convert);
//		/** Set up a save file for encoded jpg imagens.
//		 *  This file is then used directly as output.
//		 */
//		despejo = new File("dumpFile" + contMov);
//		/** This is just a safety check to see that the
//		 *  file can really be written too. */
////        while (dumpFile.exists() && !dumpFile.delete()) {
////            dumpFile = mySaveQuery.getNextFile(dumpFile);
//		//       }
//		despejo.deleteOnExit();
//		/** Allocate memory for frame storage in a file
//		 *  buffer for the output file.
//		 */
//		bytesArquivo = new FileOutputStream(despejo);
////		System.out.println("filebuffer should use: "
////				+ (convert.intValue() / 2));
//		bufferArquivo = new BufferedOutputStream(bytesArquivo,
//				(convert.intValue() / 2));
//		/** jpgBytes will be the output stream that we write
//		 *  to later on in the program.
//		 */
//		bytesJPG = new DataOutputStream(bufferArquivo);
//		/** Figure out how many integers we can hold in the
//		 *  remaining memory. An integer takes 4B, and we will
//		 *  have 2 arrays of integers. We don't want to fill the
//		 *  entire memory, and we have used a third, or possibly
//		 *  even half, already. So we will allocate 2 arrays of
//		 *  integers, each using a sixth of the available memory.
//		 *  That means we can at the most have
//		 *  convert.intValue() / 24
//		 *  integers in each array. That also means we can at
//		 *  the most have that amount of frames in a film.
//		 *
//		 *  As a comparison, an integer array of 1 MB can hold
//		 *  262144 integers, which would be enough for
//		 *  2h25m38s of film at 30 FPS.
//		 */
//		NumMaxImg = (int) (convert.intValue() / 24);
////		System.out.println("Memory after filebuffer: "
////				+ (myRuntime.maxMemory() - myRuntime.totalMemory()
////				+ myRuntime.freeMemory()));
//		/** Init the encoder to store encoded imagens directly to memory.
//		 *  This will be changed later, but is an easy way of getting a
//		 *  first frame for the film, since that frame is to be kept in
//		 *  memory anyway  (see below).
//		 *
//		 *  First we take an "average (=random)" image for the method
//		 *  encoder.getDefaultJPEGEncodeParam(image) below.
//		 */
//		//imagem = robot.createScreenCapture(retangulo);
//		////////////////////////////////////////////////////////////////////////
//		File f = new File("C:\\DinaHelp\\meme.jpeg");
//		imagem = ImageIO.read(f);
//		////////////////////////////////////////////////////////////////////////
//		/** Set the encoder to the OutputStream that stores in memory. */
//		encoder = JPEGCodec.createJPEGEncoder(bytesJPG_2);
//		/** Get an "average" JPEGEncodeParam */
//		param = encoder.getDefaultJPEGEncodeParam(imagem);
//		/** Set encoding quality */
//		param.setQuality(qualidadeEnc, false);
//		encoder.setJPEGEncodeParam(param);
//		/** Store an empty image in the first frame of the film.
//		 *  Then keep that jpg image in memory to also be used as the
//		 *  last frame in the film as well
//		 */
//		imagem = new BufferedImage(imagem.getWidth(),
//				imagem.getHeight(),
//				imagem.getType());
//		encoder.encode(imagem);
//		priUltFrame = bytesJPG_2.toByteArray();
//		/** Clear this output stream, which we will not use for 
//		 *  anything more than this initialization procedure, 
//		 *  to save some memory */
//		bytesJPG_2 = null;
//		/** Change the encoder to store encoded image in the
//		 *  buffered FileOutputStream.
//		 *
//		 *  First we take another "average" screenshot.
//		 */
//		//	imagem = robot.createScreenCapture(retangulo);
//		File f2 = new File("C:\\DinaHelp\\meme.jpeg");
//		imagem = ImageIO.read(f);
//		/** Set the encoder to the FileOutputStream */
//		encoder = JPEGCodec.createJPEGEncoder(bytesJPG);
//		/** Get an "average" JPEGEncodeParam */
//		param = encoder.getDefaultJPEGEncodeParam(imagem);
//		/** Set encoding quality */
//		param.setQuality(qualidadeEnc, false);
//		encoder.setJPEGEncodeParam(param);
//		/** We write the black frame into the outputstream,
//		 *  since we need at least one frame to prevent the
//		 *  situation that we would miss capturing the first
//		 *  frame, and then try to fill it by repeating a
//		 *  frame that never existed.
//		 */
//		bytesJPG.write(priUltFrame, 0, priUltFrame.length);
//		/** Allocate int Arrays for storing image sizes,
//		 *  and missed imagens. Setup remaining variables.
//		 */
////		System.out.println("arrays should use (in total): "
////				+ (NumMaxImg * 8));
//		boolean memError = true;
//		/** Allocate an integer array for storing the sizes of
//		 *  each recorded image. Allocate an additional array
//		 *  to store every frame that was missed. The number of
//		 *  integers in these arrays represents the maximum number
//		 *  of frames that can be recorded in a single film
//		 *  using this design of the program. This is not pretty.
//		 *
//		 *  A very good alternative would be to store an integer
//		 *  or possibly even something bigger, before each frame
//		 *  in OutputStream, to indicate the size of each frame.
//		 *  0 or -1 could be used for missing frames.
//		 *
//		 *  The reason for doing this with arrays with prereserved
//		 *  memory in the first place was speed. It was unnecessary,
//		 *  beacuse it does not give much speed, but now it will
//		 *  not be changed now until the procedure described above
//		 *  is implemented.
//		 */
//		while (memError) {
//			memError = false;
//			try {
//				tamanhos = new int[NumMaxImg];
//				//	Allow as many missed frames.
//				framesPerdidos = new int[NumMaxImg];
//			} catch (OutOfMemoryError oe) {
////				System.err.println("Unexpected memory error in ScreenGrabber.init()");
////				System.err.println("Trying to allocate smaller arrays.");
//				memError = true;
//				NumMaxImg /= 2;
//			}
//		}
////		System.out.println("Memory after arrays: "
////				+ (myRuntime.maxMemory() - myRuntime.totalMemory()
////				+ myRuntime.freeMemory()));
//		/** This is needed in case no frames are missed, since
//		 *  missedFrames must contain a value. If a frame is missed,
//		 *  this value will be overwritten.
//		 */
//		framesPerdidos[0] = NumMaxImg + 1;
//		/** One frame is already caught, setup size for that frame */
//		tamanhos[0] = bytesJPG.size();
//		/** Setup size counter. */
//		tamanho = tamanhos[0];
//		/** Setup frame counter */
//		contImagens = 1;
//		/** These last two lines are for waking up the
//		 *  run method, in the unlikely event that it
//		 *  is waiting on init to finish.
//		 */
//		inicializando = false;
//		wakeUp();
//	}
//
//	public synchronized void wakeUp() {
//		notifyAll();
//	}
}
