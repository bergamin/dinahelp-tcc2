package dinahelp.util;

import java.io.File;
import javax.swing.JOptionPane;

public class Validador {

	public static boolean caminhoValido(String nomeArquivoPasta) {
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
	public static boolean caminhoExistente(String caminhoCompleto){
		File arquivo = new File(caminhoCompleto);
		boolean retorno = arquivo.exists();
		
		if(retorno){
			JOptionPane.showMessageDialog(null, "Arquivo já existente!");
		}
		
		return retorno;
	}
}
