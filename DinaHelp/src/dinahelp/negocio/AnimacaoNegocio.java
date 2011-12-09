package dinahelp.negocio;

import com.gif4j.GifEncoder;
import com.gif4j.GifFrame;
import com.gif4j.GifImage;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author Guilherme Taffarel Bergamin
 * @author Akanbi Strossi de Jesus
 * @author Felipe Bochehin
 */
public class AnimacaoNegocio extends Thread {

	/** Encoda as imagens passadas por parâmetro em um arquivo gif */
	public void imagensParaGIF(BufferedImage[] images, File fileToSave) throws IOException {
		GifImage gifImage = new GifImage();
		gifImage.setDefaultDelay(200);
		for (int i = 0; i < images.length; i++) {
			gifImage.addGifFrame(new GifFrame(images[i]));
		}
		//	gifImage.setLoopNumber(Integer.MAX_VALUE); // Repetições virtualmente indeterminadas
		gifImage.setLoopNumber(3);
		GifEncoder.encode(gifImage, fileToSave);
	}
}
