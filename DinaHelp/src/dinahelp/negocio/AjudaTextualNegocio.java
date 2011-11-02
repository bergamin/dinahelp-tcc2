package dinahelp.negocio;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;


/**
 * @author Guilherme Taffarel Bergamin
 * @author Akanbi Strossi de Jesus
 * @author Felipe Bochehin
 */
public class AjudaTextualNegocio {

	private File arquivoTexto;

	public void geraArquivoAjudaTextual(String caminho, String nomeArquivo, String texto) {
		try {
			arquivoTexto = new File(caminho + "\\" + nomeArquivo);
			FileWriter fw = new FileWriter(arquivoTexto);
			PrintWriter pw = new PrintWriter(fw);
			pw.write(texto);
			fw.close();
		} catch (Exception e) {
		}
	}
}
