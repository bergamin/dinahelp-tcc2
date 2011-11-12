package dinahelp.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * @author Guilherme Taffarel Bergamin
 * @author Akanbi Strossi de Jesus
 * @author Felipe Bochehin
 */
public class CopiaArquivos {

	public CopiaArquivos(String origem, String destino) {
		//streams  
		FileInputStream orig;
		FileOutputStream dest;
		//canais  
		FileChannel fcOrigem;
		FileChannel fcDestino;

		try {
			orig = new FileInputStream(origem);
			dest = new FileOutputStream(destino);

			fcOrigem = orig.getChannel();
			fcDestino = dest.getChannel();
			//Faz a copia  
			fcOrigem.transferTo(0, fcOrigem.size(), fcDestino);

			orig.close();
			dest.close();

			// deleta o temporário
			new File(origem).delete();

		} catch (IOException e) {
//			e.printStackTrace();
		}
	}
}