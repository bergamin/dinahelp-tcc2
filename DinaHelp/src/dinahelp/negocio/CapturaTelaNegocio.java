package dinahelp.negocio;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

/**
 * @author Guilherme Taffarel Bergamin
 * @author Akanbi Strossi de Jesus
 * @author Felipe Bochehin
 */
public class CapturaTelaNegocio {

	/** Captura a tela com a área e o caminho passado por parâmetro */
	public void captura(String caminhoCompleto, int x, int y, int largura, int altura) {
		try {
			BufferedImage captura;
			captura = new Robot().createScreenCapture(new Rectangle(x, y, largura, altura));
			File arquivo = new File(caminhoCompleto);
			ImageIO.write(captura, "png", arquivo);
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(null, "Ocorreu um erro e a aplicação deve ser fechada\nIOException");
			System.exit(0);
		} catch (AWTException ex) {
			JOptionPane.showMessageDialog(null, "Ocorreu um erro e a aplicação deve ser fechada\nAWTException");
			System.exit(0);
		}
	}
}
