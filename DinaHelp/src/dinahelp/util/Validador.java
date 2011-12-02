package dinahelp.util;

import java.io.File;
import javax.swing.JOptionPane;

/**
 * @author Guilherme Taffarel Bergamin
 * @author Akanbi Strossi de Jesus
 * @author Felipe Bochehin
 */
public class Validador {

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

	public static boolean caminhoExistente(String caminhoCompleto) {
		return new File(caminhoCompleto).exists();
	}
}
