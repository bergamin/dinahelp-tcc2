package dinahelp.util;

import java.io.File;
import javax.swing.JOptionPane;

/**
 * @author Guilherme Taffarel Bergamin
 * @author Akanbi Strossi de Jesus
 * @author Felipe Bochehin
 */
public class Validador {

	/**
	 * Verifica se o nome de arquivo ou pasta é válido.
	 * A mensagem é dada aqui ao invés de na tela para que se evite o
	 * esquecimento de algum caracter na mensagem.
	 * O "." Tecnicamente é um caracter válido, porém utilizamos ele para
	 * diferenciar uma pasta de um arquivo, então não podemos deixar arquivos e
	 * pastas serem criados com pontos.
	 */
	public static boolean nomeValido(String nomeArquivoPasta) {
		boolean retorno = !(nomeArquivoPasta.contains(".")
				|| nomeArquivoPasta.contains("\\")
				|| nomeArquivoPasta.contains("/")
				|| nomeArquivoPasta.contains(":")
				|| nomeArquivoPasta.contains("*")
				|| nomeArquivoPasta.contains("?")
				|| nomeArquivoPasta.contains("\"")
				|| nomeArquivoPasta.contains("<")
				|| nomeArquivoPasta.contains(">")
				|| nomeArquivoPasta.contains("|"));

		if (!retorno) {
			JOptionPane.showMessageDialog(null, "O nome não pode conter nenhum dos seguintes caracteres:\n"
					+ ". \\ / : * ? \" < > |");
		}
		return retorno;
	}

	/** Verifica se o caminho dado já existe */
	public static boolean caminhoExistente(String caminhoCompleto) {
		return new File(caminhoCompleto).exists();
	}
}
