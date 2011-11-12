package dinahelp.util;

import javax.swing.JOptionPane;

public class Validador {
	public boolean arquivoValido(String nome){
		boolean retorno = !(nome.contains(".")
						 || nome.contains("\\")
						 || nome.contains("/")
						 || nome.contains(":")
						 || nome.contains("*")
						 || nome.contains("?")
						 || nome.contains("\"")
						 || nome.contains("<")
						 || nome.contains(">")
						 || nome.contains("|"));
		
		if(!retorno)
			JOptionPane.showMessageDialog(null, "Os nomes de arquivo não podem conter nenhum dos seguintes caracteres:\n"
											  + ". \\ / : * ? \" < > |");
		return retorno;
	}
}
