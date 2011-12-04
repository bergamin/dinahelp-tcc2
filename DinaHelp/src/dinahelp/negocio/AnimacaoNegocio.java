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

	public void saveImageArrayAsAnimatedGif(BufferedImage[] images, File fileToSave)
			throws IOException {
		// create new GifImage instance
		GifImage gifImage = new GifImage();
		// set default delay between gif frames
		gifImage.setDefaultDelay(200);
		// add comment to gif image
		gifImage.addComment("Animated GIF image example");
		// add images wrapped by GifFrame
		for (int i = 0; i < images.length; i++) {
			gifImage.addGifFrame(new GifFrame(images[i]));
		}
		gifImage.setLoopNumber(Integer.MAX_VALUE);
		// save animated gif image
		GifEncoder.encode(gifImage, fileToSave);
	}
}
