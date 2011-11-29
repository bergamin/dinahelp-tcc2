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

/**
 * @author Guilherme Taffarel Bergamin
 * @author Akanbi Strossi de Jesus
 * @author Felipe Bochehin
 */
public class AnimacaoNegocio {
	
	// Lista de imagens
	private ListaDeDados imagens;
	// Quantidade de imagens
	private int contImagens;
	// Tamanho de cada imagem
	private int[] tamanhos;
	// Arquivo temporário
	private File despejo;
	// Caminho para a gravação do arquivo temporário de vídeo.
	// No caso, a raiz de onde estiver instalado o programa.
	public String arquivoTemp = "temp.mov";
	// Área de captura da tela.
	public Rectangle retangulo; // NÃO VAI SER USADO! TIRAR DAQUI DEPOIS
	// Frames por segundo
	private int fps;
	// Previne a gravação de iniciar por engano enquanto o método init()
	// estiver executando.
	public boolean inicializando = false;
	// Isto é onde todos os frames encodados são gravados na memória
	private DataOutputStream bytesJPG;
	private ByteArrayOutputStream bytesJPG_2;
	private FileOutputStream bytesArquivo;
	private BufferedOutputStream bufferArquivo;
	// Encoder usado para encodar as imagens
	private JPEGImageEncoder encoder;
	// Usado para verificar a memória disponível
	private Runtime runtime;
	// Numeração para o nome dos arquivos temporários
	public int contMov = 0;
	// Número máximo de frames que podem ser gravados na memória
	public int NumMaxImg;
	// Usado para guardar cada imagem
	private BufferedImage imagem;
	// Usado para fazer a captura das imagens
	private Robot robot; // NÃO SERÁ USADO! REMOVER DEPOIS!
	// Parâmetros do encoder para encodar as imagens
	private JPEGEncodeParam param;
	// Qualidade do encode da animação (1.0f é o máximo)
	public float qualidadeEnc = 0.75f;
	// Guarda o primeiro e o último frame do vídeo (imagens vazias).
	// Assim temos certeza de que se o primeiro frame real for perdido,
	// não teremos que preencher o espaço dele com frames que nunca existiram
	private byte[] priUltFrame; // NÃO SERÁ USADO! REMOVER DEPOIS!
	// Guarda o nº de todos os frames perdidos
	private int[] framesPerdidos; // NÃO SERÁ USADO! REMOVER DEPOIS!
	// Captura o tamanho do ByteArrayOutputStream usado para guardar frames na
	// memória.
	private int tamanho = 0;
	
	public void run(){
		
		imagens = new ListaDeDados();
		imagens.totImg = contImagens;
		imagens.tamanhoImagens = tamanhos;
		imagens.setArquivo(despejo);
		
	}
	
	public void startDumper(ListaDeDados imagens) {
		// Create a new tempfile filename for each movie.
		try {
			File testFile = new File(arquivoTemp);
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
			e.printStackTrace();
		} catch (OutOfMemoryError o) {
			o.printStackTrace();
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
		/** Start encoding. */
		startDumper(imagens);
		/** Try to delete the file that we used as a
		 *  data source for the movie (the old dumpfile).
		 */
		try {
			imagens.fis.close();
			imagens.arquivo.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
		/** Free some memory from the arrays, and then init
		 *  again to be able to use it */
		imagens.tamanhoImagens = null;
		try {
			init();
		} catch (Exception e) {
			e.printStackTrace();
		}
		/** Wake up users waiting to use this VideoNegocio
		 *  again, and users waiting to merge the encoded
		 *  movie file.
		 */
		inicializando = false;
		wakeUp();
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
		imagem = robot.createScreenCapture(retangulo);
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
		imagem = robot.createScreenCapture(retangulo);
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
	public synchronized void wakeUp() {
		notifyAll();
	}
}
