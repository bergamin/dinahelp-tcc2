package dinahelp.negocio;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Guilherme Taffarel Bergamin
 * @author Akanbi Strossi de Jesus
 * @author Felipe Bochehin
 */
public class AjudaTextualNegocio {
	
	/** Arquivo .doc da ajuda textual */
	private File arquivoTexto;
	
	/** Cria o arquivo .doc */
	public void geraArquivoAjudaTextual(String caminho, String nomeArquivo, String texto) {
		try {
			arquivoTexto = new File(caminho + "\\" + nomeArquivo);
			FileWriter fw = new FileWriter(arquivoTexto);
			PrintWriter pw = new PrintWriter(fw);
			pw.write(texto);
			fw.close();
		} catch (IOException e) {
		}
	}
	
	/** Retorna o texto do arquivo .doc passado por parâmetro */
	@SuppressWarnings("CallToThreadDumpStack")
	public String carregarArquivoAjudaTextual(String caminho) {
		try {
			arquivoTexto = new File(caminho);
			BufferedReader buffer = new BufferedReader(new FileReader(arquivoTexto));
			String str = "";
			
			/**
			 * Adiciona linha a linha o arquivo texto para retorno.
			 * \r é necessário pois o Windows precisa dele além do \n.
			 */
			while (buffer.ready()) {
				if (str.isEmpty())
					str = str + buffer.readLine();
				else
					str = str +"\r\n"+ buffer.readLine();
			}
			return str;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	/** Salva arquivo já criado que está sendo editado */
	@SuppressWarnings("CallToThreadDumpStack")
	public void editarArquivoAjudaTextual(String caminho, String texto) {
		try {
			FileWriter fw = new FileWriter(caminho);
			PrintWriter pw = new PrintWriter(fw);
			pw.write(texto);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
