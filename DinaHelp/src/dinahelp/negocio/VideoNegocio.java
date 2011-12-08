package dinahelp.negocio;

import dinahelp.util.ListaDeDados;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import com.sun.image.codec.jpeg.*;
import dinahelp.util.JpegParaMov;

/**
 * @author Guilherme Taffarel Bergamin
 * @author Akanbi Strossi de Jesus
 * @author Felipe Bochehin
 */
public class VideoNegocio extends Thread {

	// Área de captura da tela.
	public Rectangle retangulo;
	// Qualidade do encode de vídeo (1.0f é o máximo)
	public float qualidadeEnc = 0.75f;
	// Tempo em ms entre cada frame
	public double tempo;
	// Número máximo de frames que podem ser gravados na memória
	public int NumMaxImg;
	// Numeração para o nome dos arquivos temporários
	public int contMov = 0;
	// Setado para false para parar a gravação
	public boolean naoTerminado = false;
	// é colocado como false no momento em que pára a gravação.
	// serve para sincronizar com o áudio
	public boolean gravando = false;
	// é colocado como false quando o VideoNegocio estiver
	// pronto para uma nova gravação é setado para true no
	// método run() alterado novamente para false no método encode()
	public boolean executando = false;
	// Indica se o mouse deve ser capturado ou não
	public boolean mostraMouse = true;
	// Previne a gravação de iniciar por engano enquanto o método init()
	// estiver executando.
	public boolean inicializando = false;
	// Caminho para a gravação do arquivo temporário de vídeo.
	// No caso, a raiz de onde estiver instalado o programa.
	public String arquivoTemp = "temp.mov";
	// Usado para sincronização de áudio e vídeo
	// Alterado para true quando inicia a gravação do áudio
	// e para false quando termina a gravação.
	public boolean gravandoAudio = false;
	// Incrementado a cada frame capturado no método run()
	private int contImagens;
	// Incrementado a cada frame perdido
	private int contPerdidos;
	// Guarda o primeiro e o último frame do vídeo (imagens vazias).
	// Assim temos certeza de que se o primeiro frame real for perdido,
	// não teremos que preencher o espaço dele com frames que nunca existiram
	private byte[] priUltFrame;
	// Captura o tamanho do ByteArrayOutputStream usado para guardar frames na
	// memória.
	private int tamanho = 0;
	// Guarda o tamanho de todos os frames capturados
	private int[] tamanhos;
	// Guarda o nº de todos os frames perdidos
	private int[] framesPerdidos;
	// Isto é onde todos os frames encodados são gravados na memória
	private DataOutputStream bytesJPG;
	private ByteArrayOutputStream bytesJPG_2;
	private FileOutputStream bytesArquivo;
	private BufferedOutputStream bufferArquivo;
	// Usado diretamente como arquivo de saída. Arquivo usado para salvar os
	// frames depois que a gravação terminou no método run()
	private File despejo;
	// Usado no método run() para manter a captura em sincronia
	double tempoSinc;
	// Usado no método run() para manter a captura em sincronia
	private long tempoAtual;
	// Usado para verificar a memória disponível
	private Runtime runtime;
	// Usado para fazer a captura das imagens
	private Robot robot;
	// Usado para guardar a captura dos frames
	private BufferedImage imagem;
	private Graphics2D graphics2D;
	// Frames por segundo
	private int fps;
	// Encoder usado para encodar as imagens capturadas
	private JPEGImageEncoder encoder;
	// Parâmetros do encoder para encodar as imagens
	private JPEGEncodeParam param;
	// Utilizado para recarregar as imagens do arquivo onde as imagens foram
	// salvas para serem utilizadas pela classe JpegParaMov
	private ListaDeDados imagens;
	
	public VideoNegocio(Rectangle capSize, int fps) {
		retangulo = capSize;
		setFps(fps, fps);
		try {
			robot = new Robot();
		} catch (AWTException awte) {
			System.exit(1);
		}
	}

	public VideoNegocio() {
		this((new Rectangle(0, 0, 100, 100)), 15);
	}

	public void init() throws IOException {
		inicializando = true;
		bytesJPG = null;
		bufferArquivo = null;
		bytesArquivo = null;
		bytesJPG_2 = new ByteArrayOutputStream();
		encoder = null;
		tamanhos = null;
		framesPerdidos = null;
		System.gc();
		runtime = Runtime.getRuntime();
		Double convert = new Double(runtime.maxMemory()
				- runtime.totalMemory() + runtime.freeMemory() - 2097152 * 10);
		if (convert.intValue() < 0) {
			convert = new Double((runtime.maxMemory()
					- runtime.totalMemory() + runtime.freeMemory()) * 0.5d);
		}
		despejo = new File("dumpFile" + contMov);
		despejo.deleteOnExit();
		bytesArquivo = new FileOutputStream(despejo);
		bufferArquivo = new BufferedOutputStream(bytesArquivo,
				(convert.intValue() / 2));
		bytesJPG = new DataOutputStream(bufferArquivo);
		NumMaxImg = (int) (convert.intValue() / 24);
		imagem = robot.createScreenCapture(retangulo);
		encoder = JPEGCodec.createJPEGEncoder(bytesJPG_2);
		param = encoder.getDefaultJPEGEncodeParam(imagem);
		param.setQuality(qualidadeEnc, false);
		encoder.setJPEGEncodeParam(param);
		imagem = new BufferedImage(imagem.getWidth(),
				imagem.getHeight(),
				imagem.getType());
		encoder.encode(imagem);
		priUltFrame = bytesJPG_2.toByteArray();
		bytesJPG_2 = null;
		imagem = robot.createScreenCapture(retangulo);
		encoder = JPEGCodec.createJPEGEncoder(bytesJPG);
		param = encoder.getDefaultJPEGEncodeParam(imagem);
		param.setQuality(qualidadeEnc, false);
		encoder.setJPEGEncodeParam(param);
		bytesJPG.write(priUltFrame, 0, priUltFrame.length);
		boolean memError = true;

		while (memError) {
			memError = false;
			try {
				tamanhos = new int[NumMaxImg];
				framesPerdidos = new int[NumMaxImg];
			} catch (OutOfMemoryError oe) {
				memError = true;
				NumMaxImg /= 2;
			}
		}

		framesPerdidos[0] = NumMaxImg + 1;
		tamanhos[0] = bytesJPG.size();
		tamanho = tamanhos[0];
		contImagens = 1;
		contPerdidos = 0;
		inicializando = false;
		wakeUp();
	}

	public void setFps(int fps, int playbackFps) {
		this.fps = playbackFps;
		tempo = 1000d / fps;
	}

	public void setNaoTerminado(boolean naoTerminado) {
		this.naoTerminado = naoTerminado;
	}

	private Polygon createMouse(Point mousePos) {
		Polygon polly = new Polygon();
		polly.addPoint(mousePos.x - retangulo.x, mousePos.y - retangulo.y);
		polly.addPoint(mousePos.x - retangulo.x, mousePos.y - retangulo.y + 17);
		polly.addPoint(mousePos.x - retangulo.x + 5, mousePos.y - retangulo.y + 12);
		polly.addPoint(mousePos.x - retangulo.x + 12, mousePos.y - retangulo.y + 12);

		return polly;
	}

	public void setSyncTime(long syncTime) {
		this.tempoSinc = syncTime;
	}

	public double testCapTime() throws IOException {
		long syncTime;
		int iterations = 30;
		double avgTime = 0;
		try {
			for (int cnt = 0; cnt < iterations; cnt++) {
				syncTime = System.currentTimeMillis();
				imagem = robot.createScreenCapture(retangulo);
				avgTime = System.currentTimeMillis() - syncTime + avgTime;
			}
			avgTime /= iterations;
			return avgTime;
		} catch (OutOfMemoryError oe) {
			return Double.MAX_VALUE;
		}
	}

	public double[] testEnc() throws IOException {
		ByteArrayOutputStream jpgBytes = new ByteArrayOutputStream();
		int iterations = 20;
		double avgSize = 0, avgEncTime = 0;
		long syncTime;
		try {
			imagem = robot.createScreenCapture(retangulo);
			encoder = JPEGCodec.createJPEGEncoder(jpgBytes);
			param = encoder.getDefaultJPEGEncodeParam(imagem);
			param.setQuality(qualidadeEnc, false);
			encoder.setJPEGEncodeParam(param);
			encoder.encode(imagem);
			avgSize = jpgBytes.size();
			jpgBytes = new ByteArrayOutputStream((int) avgSize * iterations * 2);
			encoder = JPEGCodec.createJPEGEncoder(jpgBytes);
			param = encoder.getDefaultJPEGEncodeParam(imagem);
			param.setQuality(qualidadeEnc, false);
			encoder.setJPEGEncodeParam(param);
			for (int cnt = 0; cnt < iterations; cnt++) {
				syncTime = System.currentTimeMillis();
				encoder.encode(imagem);
				avgEncTime = System.currentTimeMillis() - syncTime + avgEncTime;
			}
			avgSize = jpgBytes.size() / iterations;
			avgEncTime /= iterations;
			double[] values = new double[2];
			values[0] = avgSize;
			values[1] = avgEncTime;
			return values;
		} catch (OutOfMemoryError oe) {
			double[] errors = {Double.MAX_VALUE, Double.MAX_VALUE};
			return errors;
		}
	}

	public void startDumper(ListaDeDados imagens) {
		try {
			File testFile = new File(arquivoTemp);

			String tempTotal = testFile.getPath();
			String arguments[] = {"-w", Integer.toString(retangulo.width), "-h", Integer.toString(retangulo.height), "-f", Integer.toString(fps), "-o", tempTotal};

			JpegParaMov dumper = new JpegParaMov(arguments);

			dumper.setListaDeDados(imagens);
			dumper.setPriority(Thread.NORM_PRIORITY);
			dumper.setAcabou(false);
			dumper.start();
			dumper.waitFor();
		} catch (Exception e) {
		} catch (OutOfMemoryError o) {
		}
	}

	public synchronized void hold() {
		try {
			wait(3000);
		} catch (InterruptedException ie) {
		}
	}

	public synchronized void wakeUp() {
		notifyAll();
	}

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
			}
		}
		return temp.toByteArray();
	}

	@Override
	public void run() {
		Polygon mousePointer;
		Point mousePos;

		while (true) {
			try {
				init();
				while (!naoTerminado) {
					hold();
				}
				while (inicializando) {
					hold();
				}
				gravando = true;
				executando = true;
				while (naoTerminado) {
					mousePos = MouseInfo.getPointerInfo().getLocation();
					imagem = robot.createScreenCapture(retangulo);
					if (mostraMouse) {
						graphics2D = imagem.createGraphics();
						mousePointer = createMouse(mousePos);
						graphics2D.setColor(Color.WHITE);
						graphics2D.fill(mousePointer);
						graphics2D.setColor(Color.DARK_GRAY);
						graphics2D.draw(mousePointer);
					}
					encoder.encode(imagem);
					tamanhos[contImagens] = bytesJPG.size() - tamanho;
					tamanho += tamanhos[contImagens];
					tempoSinc += tempo;
					tempoAtual = System.currentTimeMillis();
					while (tempoSinc < tempoAtual) {
						framesPerdidos[contPerdidos++] = contImagens;
						tempoSinc += tempo;
					}
					contImagens++;
					Thread.sleep((long) tempoSinc - tempoAtual);
				}
			} catch (OutOfMemoryError o) {
				Runtime runtime = Runtime.getRuntime();
				long mem = runtime.maxMemory() - runtime.totalMemory()
						+ runtime.freeMemory();
			} catch (Exception e) {
				Runtime runtime = Runtime.getRuntime();
				long mem = runtime.maxMemory() - runtime.totalMemory()
						+ runtime.freeMemory();
			} finally {
				try {
					contMov++;
					if (((contImagens + contPerdidos) * tempo) < 2000) {
						int delayCounter = 0;
						while (((contImagens + contPerdidos + delayCounter++) * tempo) < 2000) {
							Thread.sleep((long) tempo);
						}
					}
					while (((contImagens + contPerdidos) * tempo) < 2000) {
						framesPerdidos[contPerdidos++] = contImagens;
					}
					try {
						bytesJPG.write(priUltFrame, 0, priUltFrame.length);
					} catch (IOException e) {
					}
					tamanhos[contImagens] = priUltFrame.length;
					imagens = new ListaDeDados();
					imagens.totImg = contImagens;
					imagens.tamanhoImagens = tamanhos;
					imagens.framesPerdidos = framesPerdidos;
					imagens.setArquivo(despejo);
					gravando = false;
					wakeUp();
					while (gravandoAudio) {
						hold();
					}
				} catch (Exception e) {
				}
			}
		}
	}

	public void encode() {
		startDumper(imagens);
		executando = false;
		try {
			imagens.fis.close();
			imagens.arquivo.delete();
		} catch (Exception e) {
		}
		imagens.tamanhoImagens = null;
		imagens.framesPerdidos = null;
		try {
			inicializando = true;
			if (!gravando) {
				init();
			}
		} catch (Exception e) {
		}
		inicializando = false;
		wakeUp();
	}
}
