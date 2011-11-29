package dinahelp.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author Guilherme Taffarel Bergamin
 * @author Akanbi Strossi de Jesus
 * @author Felipe Bochehin
 */
public class ListaDeDados {
	
	// indica que todos os frames foram lidos
	public boolean acabou = false;
	// total de frames no array tamanhoImagens
	public int totImg = 0;
	// total de frames únicos enviados (frames duplicados são frames enviados
	// novamente para compensar perda)
	public int imagensUnicas = 0;
	// total incluindo duplicados
	public int imagensEnviadas = 0;
	// tamanho em bites de cada imagem a ser encodada
	public int[] tamanhoImagens;
	// nº dos frames perdidos
	public int[] framesPerdidos;
	// contador internos de quantos já foram perdidos até o momento
	private int contaPerdidos;
	// Array de bytes com os dados a serem lidos pelo método leDados
	private byte[] dados;
	public FileInputStream fis;
	public File arquivo;
	
	public ListaDeDados() {
		contaPerdidos = 0;
	}
	// Seta o arquivo com os frames

	public boolean setArquivo(File arquivo) {
		try {
			this.arquivo = arquivo;
			fis = new FileInputStream(this.arquivo);
			return true;
		} catch (FileNotFoundException e) {
			System.err.println("Arquivo não encontrado!");
			return false;
		}
	}

	// Lê o próximo frame e retorna um array com os dados
	public synchronized byte[] leDados() {
		try {
			if (imagensUnicas < totImg) {
				// Se o frame não for um frame perdido, cria um array de bytes do mesmo tamanho do próximo frame
				if (framesPerdidos[contaPerdidos] != imagensUnicas) {
					dados = new byte[tamanhoImagens[imagensUnicas]];
					fis.read(dados, 0, tamanhoImagens[imagensUnicas]);
					imagensUnicas++;
				} else {
					// Se for um frame perdido, mantêm-se os dados em memória para inserir novamente
					contaPerdidos++;
				}
				if ((imagensUnicas == totImg)) // || (contaPerdidos == framesPerdidos.length))
				{
					acabou = true; // Finaliza
				}
				imagensEnviadas++;
				return dados; // Retorna o frame
			} else {
//				System.err.println("Erro! Excesso de frames!");
				return null;
			}
		} catch (IOException e) {
//			System.err.println("Erro de arquivo: " + e);
			return null;
		}
	}
}
