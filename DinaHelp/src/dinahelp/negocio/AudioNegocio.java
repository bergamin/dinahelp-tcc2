package dinahelp.negocio;

import java.io.*;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.AudioFileFormat;

/**
 * @author Guilherme Taffarel Bergamin
 * @author Akanbi Strossi de Jesus
 * @author Felipe Bochehin
 */
public class AudioNegocio extends Thread {

	/** frequência de gravação */
	public float frequencia = 22050f;
	public int bitsAudio = 16;
	/** canais de gravação */
	public int canais = 1;
	/**	arquivo onde é gravado os dados do áudio */
	public File arquivoAudio = null;
	/**	utilizado para saber se a gravação ainda está sendo realizada */
	public boolean gravando = false;
	public boolean parado = true;
	/**	Deve-se sincronizar o áudio, ou ignorar os frames perdidos */
	public boolean sincAudio = true;
	/** quantas vezes a gravação deve esperar quando há lag na gravação */
	public int contVezesLag = 0;
	/** utilizado para manter a sincronia quando frames são perdidos */
	public int contTempoAdiante = 0;
	/** velocidade do áudio */
	public float velocidade = 1;
	/**
	 * Tempo em milisegungos que a gravação espera cada vez que o buffer
	 * é limpo
	 */
	public long tempoSleep = 900;
	/** lag máximo em partes de segundo. neste caso, 1/50 = 20ms */
	public int maxLag = 50;
	public int maxAdiante = 50;
	/**	tamanho do buffer de memória para a gravação do arquivo em bytes */
	private int tamanhoBufferMemoria = 1024 * 1024;
	/**	arquivo pronto .wav antes de ser copiado para o local selecionado*/
	private String wavTemp = "audioTemp.wav";
	/**
	 * arquivo temporário de buffer para os dados da gravação serem
	 * armazenados
	 */
	private String bufTemp = "bufferTemp.buf";
	/** OutputStream para onde os dados irão */
	private OutputStream outputStream;
	/**	DataLine para os dados de áudio */
	private TargetDataLine targetDataLine;
	/** formato do arquivo de áudio (será wav) */
	private AudioFileFormat.Type tipoArquivo;
	/**	formato de entrada para os dados de áudio */
	private AudioFormat formatoAudio;
	/** arquivo onde serão gravados os dados de áudio */
	private File arquivoSaida = null;
	/** variáveis usadas no método run() */
	private int bytesLidos, tamanhoEmBytes, lagAceito, bytesTotal, bytesAdicionados;
	private int bytesACortar, aceitoAdiante;
	private int bytesALer, framesPerdidos, bytesPerdidos, tamanhoBuffer;
	private long tempoAtual, tempoSinc = 0, frameAtual; //, totalFrames, tamanhoBufferEmFrames;
	/** usados para armazenar os dados de gravação. */
	private byte[] dados; //, bytesVazios;
	/**
	 * parâmetros utilizados para trazer a gravação de volta em sincronia
	 * quando há perda de frames
	 */
	private int framesLidos, repFrames; //, repBonus;
	private int posFrame, frameBonus;
	/**
	 * contador de quantas vezes o gravador deve esperar antes de corrigir
	 * uma gravação com delay
	 */
	private int countdown = 1, countup = 4; //, testeCont = 0;
	/** parâmetros utilizados pelo método preencher */
	private int posiFrame, contIntervalo, intervaloLoop, repCont;
	private int framesRest, adicionados, contBonus;

	/** Construtor */
	@SuppressWarnings("OverridableMethodCallInConstructor")
	public AudioNegocio() {
		if (!setArquivoAudio(wavTemp)) {
			System.out.println("ERRO! Falha ao abrir arquivo de saída " + wavTemp);
		}
	}

	/** inicialização */
	public void init() {

		if (targetDataLine != null) {
			parar();
		}
		tamanhoEmBytes = (int) bitsAudio * canais / 8;
		formatoAudio = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, frequencia, bitsAudio, canais, tamanhoEmBytes, frequencia, false);

		DataLine.Info info = new DataLine.Info(TargetDataLine.class, formatoAudio, (int) (frequencia * tamanhoEmBytes));
		TargetDataLine tDataLine = null;
		try {
			tDataLine = (TargetDataLine) AudioSystem.getLine(info);
			tDataLine.open(formatoAudio, info.getMaxBufferSize());
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
		AudioFileFormat.Type type = AudioFileFormat.Type.WAVE;

		targetDataLine = tDataLine;
		tipoArquivo = type;

		tamanhoBuffer = targetDataLine.getBufferSize();
//		tamanhoBufferEmFrames = tamanhoBuffer / tamanhoEmBytes;

		lagAceito = (int) frequencia / maxLag;
		aceitoAdiante = (int) frequencia / maxAdiante;
		dados = new byte[tamanhoBuffer];

		try {
			arquivoSaida = new File(bufTemp);
			FileOutputStream outFileStream = new FileOutputStream(arquivoSaida);
			outputStream = new BufferedOutputStream(outFileStream, tamanhoBufferMemoria);
		} catch (FileNotFoundException fe) {
			fe.printStackTrace();
		} catch (OutOfMemoryError oe) {
			oe.printStackTrace();
		}
	}

	/** Pára a gravação */
	public void parar() {
		try {
			targetDataLine.stop();
			targetDataLine.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**	Seta o tempo de sincronia */
	public void setTempoSinc(long tempoSinc) {
		this.tempoSinc = tempoSinc;
	}

	/** Utilizado para sincronizar o áudio */
	public synchronized void aguardar() {
		try {
			wait(6000);
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		}
	}

	/** "Acorda" a thread que está aguardando para gravação */
	public synchronized void acordar() {
		notifyAll();
	}

	/**
	 * Método auxiliar do método run() para deixá-lo mais legível.
	 * Este método preenche espaços em branco utilizando o que estiver em
	 * memória no momento
	 */
	private void preencher() throws Exception {

		framesLidos = bytesLidos / tamanhoEmBytes;
		repFrames = framesPerdidos / framesLidos + 1;
		frameBonus = framesPerdidos % framesLidos;
		posFrame = 0;
		adicionados = 0;
		intervaloLoop = framesLidos / frameBonus;
		contBonus = frameBonus;

		while (0 < contBonus--) {
			outputStream.write(dados, posFrame, tamanhoEmBytes);
			adicionados++;
			contIntervalo = intervaloLoop;
			while (0 < contIntervalo--) {
				repCont = repFrames;
				while (0 < repCont--) {
					outputStream.write(dados, posFrame, tamanhoEmBytes);
					adicionados++;
				}
				posFrame += tamanhoEmBytes;
			}
		}

		posiFrame = frameBonus * intervaloLoop;
		framesRest = framesLidos - posiFrame;

		while (0 < framesRest--) {
			repCont = repFrames;
			while (0 < repCont--) {
				outputStream.write(dados, posFrame, tamanhoEmBytes);
				adicionados++;
			}
			posFrame += tamanhoEmBytes;
		}
	}

	/**
	 * Método principal
	 * verifica os dados disponíveis e os pega. Aguarda por
	 * tempoSleep milisegundos e repete até o data line ser fechado.
	 */
	@Override
	@SuppressWarnings("SleepWhileInLoop")
	public void run() {
		while (true) {
			init();
			bytesAdicionados = 0;
			bytesTotal = 0;
			while (parado) {
				aguardar();
			}
			try {
				targetDataLine.start();
				bytesLidos = targetDataLine.read(dados, 0, tamanhoEmBytes);
				gravando = true;
				outputStream.write(dados, 0, bytesLidos);
				bytesTotal += bytesLidos;
				while (targetDataLine.isRunning()) {
					tempoAtual = System.currentTimeMillis();
					bytesALer = targetDataLine.available() - 100 * tamanhoEmBytes;
					if (0 < bytesALer - tamanhoEmBytes) {
						bytesLidos = targetDataLine.read(dados, 0, bytesALer);
						tempoAtual = System.currentTimeMillis();
						bytesTotal += bytesLidos;
						frameAtual = (long) (frequencia / velocidade) * (tempoAtual - tempoSinc) / 1000L;
						framesPerdidos = (int) (frameAtual - targetDataLine.getLongFramePosition() - bytesAdicionados / tamanhoEmBytes);
						if (!parado && sincAudio && (0 < framesPerdidos - lagAceito)) {
							countup = contTempoAdiante;
							if (0 < 1 - countdown--) {
								bytesPerdidos = framesPerdidos * tamanhoEmBytes;
								preencher();
								bytesTotal += bytesPerdidos;
								bytesAdicionados += bytesPerdidos;
								countdown = contVezesLag;
							} else {
								outputStream.write(dados, 0, bytesLidos);
							}
						} else if (sincAudio
								&& (framesPerdidos + aceitoAdiante < 0)) {
							countdown = contVezesLag;
							if (0 < 1 - countup--) {
								framesLidos = bytesLidos / tamanhoEmBytes;
								bytesACortar = -framesPerdidos * tamanhoEmBytes;
								if (0 <= framesLidos + framesPerdidos) {
									outputStream.write(dados, bytesACortar, bytesLidos - bytesACortar);
									bytesTotal -= bytesACortar;
									bytesAdicionados -= bytesACortar;
									countdown = contVezesLag;
								} else {
									bytesTotal -= bytesLidos;
									bytesAdicionados -= bytesLidos;
								}
								countup = contTempoAdiante;
							} else {
								outputStream.write(dados, 0, bytesLidos);
							}
						} else {
							outputStream.write(dados, 0, bytesLidos);
							countdown = contVezesLag;
							countup = contTempoAdiante;
						}
					}
					Thread.sleep(tempoSleep);
				}
			} catch (OutOfMemoryError o) {
				System.out.println(o);
			} catch (ArrayIndexOutOfBoundsException oe) {
				System.out.println(oe);
			} catch (Exception e) {
				System.out.println(e);
			} finally {
				finalizar();
			}
		}
	}

	/** Seta o arquivo de áudio */
	public synchronized boolean setArquivoAudio(String nomeArquivo) {
		boolean set = false;
		try {
			arquivoAudio = new File(nomeArquivo);
			set = true;
		} catch (Exception e) {
			System.err.println(e);
		} finally {
			return set;
		}
	}

	/** Finaliza o processo */
	public synchronized void finalizar() {
		try {
			outputStream.close();
			FileInputStream fileInStream = new FileInputStream(arquivoSaida);
			long bytesAudio = arquivoSaida.length();
			long tamanhoDoFrame = (long) bitsAudio * canais / 8;
			BufferedInputStream audioBufInStream = new BufferedInputStream(fileInStream, tamanhoBufferMemoria);
			AudioInputStream audioInStream = new AudioInputStream(audioBufInStream, formatoAudio, bytesAudio / tamanhoDoFrame);
			AudioSystem.write(audioInStream, tipoArquivo, arquivoAudio);
			audioBufInStream.close();
			arquivoSaida.delete();
		} catch (Exception e) {
			System.err.println(e);
		} finally {
			gravando = false;
			acordar();
		}
	}
}
