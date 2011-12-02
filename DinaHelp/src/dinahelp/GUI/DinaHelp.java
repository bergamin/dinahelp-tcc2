package dinahelp.GUI;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * @author Guilherme Taffarel Bergamin
 * @author Akanbi Strossi de Jesus
 * @author Felipe Bochehin
 */
public class DinaHelp {

	public static InicialGUI inicial;

	/**
	 * Inicia o programa e define o look and feel para o do sistema operacional
	 * em uso no momento
	 */
	public static void main(String[] args) {
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Windows".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (UnsupportedLookAndFeelException exc) {
			JOptionPane.showMessageDialog(null, "Ocorreu um erro e a aplicação será fechada\nUnsupportedLookAndFeelException");
			System.exit(0);
		} catch (ClassNotFoundException exc) {
			JOptionPane.showMessageDialog(null, "Ocorreu um erro e a aplicação será fechada\nClassNotFoundException");
			System.exit(0);
		} catch (InstantiationException exc) {
			JOptionPane.showMessageDialog(null, "Ocorreu um erro e a aplicação será fechada\nInstantiationException");
			System.exit(0);
		} catch (IllegalAccessException exc) {
			JOptionPane.showMessageDialog(null, "Ocorreu um erro e a aplicação será fechada\nIllegalAccessException");
			System.exit(0);
		}
		inicial = new InicialGUI();
		inicial.setVisible(true);
	}
}
