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

    public static InicialGUI iniciaGUI;

    public static void main(String[] args) {
        try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (UnsupportedLookAndFeelException exc) {
            JOptionPane.showMessageDialog(null, "Ocorreu um erro e a aplicação deve ser fechada\nUnsupportedLookAndFeelException");
            System.exit(0);
        } catch (ClassNotFoundException exc) {
            JOptionPane.showMessageDialog(null, "Ocorreu um erro e a aplicação deve ser fechada\nClassNotFoundException");
            System.exit(0);
        } catch (InstantiationException exc) {
            JOptionPane.showMessageDialog(null, "Ocorreu um erro e a aplicação deve ser fechada\nInstantiationException");
            System.exit(0);
        } catch (IllegalAccessException exc) {
            JOptionPane.showMessageDialog(null, "Ocorreu um erro e a aplicação deve ser fechada\nIllegalAccessException");
            System.exit(0);
        }
        iniciaGUI = new InicialGUI();
        iniciaGUI.setVisible(true);
    }
}
