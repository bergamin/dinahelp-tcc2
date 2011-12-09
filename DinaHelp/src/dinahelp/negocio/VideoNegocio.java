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

	/** Área de captura da tela. */
	public Rectangle retangulo;
	/** Qualidade do encode de vídeo (1.0f é o máximo) */
	public float qualidadeEnc = 0.75f;
	/** Tempo em ms entre cada frame */
	public double tempo;
	/** Número máximo de frames que podem ser gravados na memória */
	public int NumMaxImg;
	/** Numeração para o nome dos arquivos temporários */
	public int contMov = 0;
	/** Setado para false para parar a gravação */
	public boolean naoTerminado = false;
	/**
	 * é colocado como false no momento em que pára a gravação.
	 * serve para sincronizar com o áudio
	 */
	public boolean gravando = false;
	/**
	 * é colocado como false quando o VideoNegocio estiver
	 * pronto para uma nova gravação é setado para true no
	 * método run() alterado novamente para false no método encode()
	 */
	public boolean executando = false;
	/** Indica se o mouse deve ser capturado ou não */
	public boolean mostraMouse = true;
	/**
	 * Previne a gravação de iniciar por engano enquanto o método init()
	 * estiver executando.
	 */
	public boolean inicializando = false;
	/**
	 * Caminho para a gravação do arquivo temporário de vídeo.
	 * No caso, a raiz de onde estiver instalado o programa.
	 */
	public String arquivoTemp = "temp.mov";
	/**
	 * Usado para sincronização de áudio e vídeo
	 * Alterado para true quando inicia a gravação do áudio
	 * e para false quando termina a gravação.
	 */
	public boolean gravandoAudio = false;
	/** Incrementado a cada frame capturado no método run() */
	private int contImagens;
	/** Incrementado a cada frame perdido */
	private int contPerdidos;
	/**
	 * Guarda o primeiro e o último frame do vídeo (imagens vazias).
	 * Assim temos certeza de que se o primeiro frame real for perdido,
	 * não teremos que preencher o espaço dele com frames que nunca existiram
	 */
	private byte[] priUltFrame;
	/**
	 * Captura o tamanho do ByteArrayOutputStream usado para guardar frames na
	 * memória.
	 */
	private int tamanho = 0;
	/** Guarda o tamanho de todos os frames capturados */
	private int[] tamanhos;
	/** Guarda o nº de todos os frames perdidos */
	private int[] framesPerdidos;
	/** Isto é onde todos os frames encodados são gravados na memória */
	private DataOutputStream bytesJPG;
	private ByteArrayOutputStream bytesJPG_2;
	private FileOutputStream bytesArquivo;
	private BufferedOutputStream bufferArquivo;
	/**
	 * Usado diretamente como arquivo de saída. Arquivo usado para salvar os
	 * frames depois que a gravação terminou no método run()
	 */
	private File despejo;
	/** Usado no método run() para manter a captura em sincronia */
	double tempoSinc;
	/** Usado no método run() para manter a captura em sincronia */
	private long tempoAtual;
	/** Usado para verificar a memória disponível */
	private Runtime runtime;
	/** Usado para fazer a captura das imagens */
	private Robot robot;
	/** Usado para guardar a captura dos frames */
	private BufferedImage imagem;
	private Graphics2D graphics2D;
	/** Frames por segundo */
	private int fps;
	/** Encoder usado para encodar as imagens capturadas */
	private JPEGImageEncoder encoder;
	/** Parâmetros do encoder para encodar as imagens */
	private JPEGEncodeParam param;
	/** Utilizado para recarregar as imagens do arquivo onde as imagens foram
	 * salvas para serem utilizadas pela classe JpegParaMov
	 */
	private ListaDeDados imagens;

	/** Construtor */
	@SuppressWarnings("OverridableMethodCallInConstructor")
	public VideoNegocio(Rectangle capSize, int fps) {
		retangulo = capSize;
		setFps(fps, fps);
		try {
			robot = new Robot();
		} catch (AWTException awte) {
			System.exit(1);
		}
	}

	/** Método de inicialização */
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
		Double conv = new Double(runtime.maxMemory() - runtime.totalMemory() + runtime.freeMemory() - 2097152 * 10);
		if (conv.intValue() < 0) {
			conv = new Double((runtime.maxMemory() - runtime.totalMemory() + runtime.freeMemory()) * 0.5d);
		}
		despejo = new File("dump" + contMov);
		despejo.deleteOnExit();
		bytesArquivo = new FileOutputStream(despejo);
		bufferArquivo = new BufferedOutputStream(bytesArquivo, (conv.intValue() / 2));
		bytesJPG = new DataOutputStream(bufferArquivo);
		NumMaxImg = (int) (conv.intValue() / 24);
		imagem = robot.createScreenCapture(retangulo);
		encoder = JPEGCodec.createJPEGEncoder(bytesJPG_2);
		param = encoder.getDefaultJPEGEncodeParam(imagem);
		param.setQuality(qualidadeEnc, false);
		encoder.setJPEGEncodeParam(param);
		imagem = new BufferedImage(imagem.getWidth(), imagem.getHeight(), imagem.getType());
		encoder.encode(imagem);
		priUltFrame = bytesJPG_2.toByteArray();
		bytesJPG_2 = null;
		imagem = robot.createScreenCapture(retangulo);
		encoder = JPEGCodec.createJPEGEncoder(bytesJPG);
		param = encoder.getDefaultJPEGEncodeParam(imagem);
		param.setQuality(qualidadeEnc, false);
		encoder.setJPEGEncodeParam(param);
		bytesJPG.write(priUltFrame, 0, priUltFrame.length);
		boolean erroMem = true;

		while (erroMem) {
			erroMem = false;
			try {
				tamanhos = new int[NumMaxImg];
				framesPerdidos = new int[NumMaxImg];
			} catch (OutOfMemoryError oe) {
				erroMem = true;
				NumMaxImg /= 2;
			}
		}

		framesPerdidos[0] = NumMaxImg + 1;
		tamanhos[0] = bytesJPG.size();
		tamanho = tamanhos[0];
		contImagens = 1;
		contPerdidos = 0;
		inicializando = false;
		acordar();
	}

	/** Seta o fps do vídeo */
	public void setFps(int fps, int playbackFps) {
		this.fps = playbackFps;
		tempo = 1000d / fps;
	}

	/** Seta se já terminou ou não a gravação */
	public void setNaoTerminado(boolean naoTerminado) {
		this.naoTerminado = naoTerminado;
	}

	/** Cria o desenho do cursor do mouse na captura */
	private Polygon criaMouse(Point posMouse) {
		Polygon cursor = new Polygon();
		cursor.addPoint(posMouse.x - retangulo.x, posMouse.y - retangulo.y);
		cursor.addPoint(posMouse.x - retangulo.x, posMouse.y - retangulo.y + 17);
		cursor.addPoint(posMouse.x - retangulo.x + 5, posMouse.y - retangulo.y + 12);
		cursor.addPoint(posMouse.x - retangulo.x + 12, posMouse.y - retangulo.y + 12);

		return cursor;
	}

	/** Seta tempo de sincronia */
	public void setTempoSinc(long tempoSinc) {
		this.tempoSinc = tempoSinc;
	}

	/** Inicia o tratamento das imagens jpeg para vídeo mov */
	public void iniciarDumper(ListaDeDados imagens) {
		try {
			File arqTemp = new File(arquivoTemp);

			String tempTotal = arqTemp.getPath();
			String argumentos[] = {"-w", Integer.toString(retangulo.width), "-h", Integer.toString(retangulo.height), "-f", Integer.toString(fps), "-o", tempTotal};

			JpegParaMov dumper = new JpegParaMov(argumentos);

			dumper.setListaDeDados(imagens);
			dumper.setPriority(Thread.NORM_PRIORITY);
			dumper.setAcabou(false);
			dumper.start();
			dumper.waitFor();
		} catch (Exception e) {
		} catch (OutOfMemoryError o) {
		}
	}

	/** Segura a thread para sincronização */
	public synchronized void aguardar() {
		try {
			wait(3000);
		} catch (InterruptedException ie) {
		}
	}

	/** "Acorda" a thread esperando para inicialização */
	public synchronized void acordar() {
		notifyAll();
	}

	/**
	 * Método principal
	 * Captura o frame, aguarda o tempo necessário para a próxima captura e caso
	 * demore mais que o fps permite, este frame é dado como perdido e é
	 * visto o nº de vezes necessários para que volte a sincronia
	 */
	@Override
	@SuppressWarnings("SleepWhileInLoop")
	public void run() {
		Polygon cursor;
		Point posMouse;

		while (true) {
			try {
				init();
				while (!naoTerminado) {
					aguardar();
				}
				while (inicializando) {
					aguardar();
				}
				gravando = true;
				executando = true;
				while (naoTerminado) {
					posMouse = MouseInfo.getPointerInfo().getLocation();
					imagem = robot.createScreenCapture(retangulo);
					if (mostraMouse) {
						graphics2D = imagem.createGraphics();
						cursor = criaMouse(posMouse);
						graphics2D.setColor(Color.WHITE);
						graphics2D.fill(cursor);
						graphics2D.setColor(Color.DARK_GRAY);
						graphics2D.draw(cursor);
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
				Runtime runt = Runtime.getRuntime();
				long mem = runt.maxMemory() - runt.totalMemory() + runt.freeMemory();
			} catch (Exception e) {
				Runtime runt = Runtime.getRuntime();
				long mem = runt.maxMemory() - runt.totalMemory() + runt.freeMemory();
			} finally {
				try {
					contMov++;
					if (((contImagens + contPerdidos) * tempo) < 2000) {
						int contDelay = 0;
						while (((contImagens + contPerdidos + contDelay++) * tempo) < 2000) {
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
					acordar();
					while (gravandoAudio) {
						aguardar();
					}
				} catch (Exception e) {
				}
			}
		}
	}

	/** Método que realiza o encode do vídeo */
	public void encode() {
		iniciarDumper(imagens);
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
		acordar();
	}
}
