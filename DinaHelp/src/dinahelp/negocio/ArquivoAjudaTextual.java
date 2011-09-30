package dinahelp.negocio;

import java.io.*;

/**
 * @author Guilherme Taffarel Bergamin
 * @author Akanbi Strossi de Jesus
 * @author Felipe Bochehin
 */
public class ArquivoAjudaTextual {

	private File arquivoTexto;

	public void geraArquivoAjudaTextual(String caminho, String nomeArquivo, String texto) {
		try {
			arquivoTexto = new File(caminho + "\\" + nomeArquivo);
			FileOutputStream fos = new FileOutputStream(arquivoTexto);
			PrintStream ps = new PrintStream(fos);
			ps.print(texto);
			fos.close();
		} catch (Exception e) {
		}
	}
}
