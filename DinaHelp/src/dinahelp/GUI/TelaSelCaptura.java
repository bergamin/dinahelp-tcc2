package dinahelp.GUI;

import dinahelp.negocio.CapturaTelaBusiness;
import dinahelp.pojo.CapturaTela;
import com.sun.awt.AWTUtilities;
import java.awt.*;
import java.awt.event.*;
import java.util.Date;

/**
 * @author Guilherme Taffarel Bergamin
 * @author Akanbi Strossi de Jesus
 * @author Felipe Bochehin
 */
public class TelaSelCaptura extends javax.swing.JFrame implements MouseListener, MouseMotionListener {

	private CapturaTela ct = new CapturaTela();

	/** Creates new form TelaSelCaptura */
	@SuppressWarnings("LeakingThisInConstructor")
	public TelaSelCaptura() {

		initComponents();
		addMouseListener(this);
		addMouseMotionListener(this);
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setAlwaysOnTop(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 373, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 328, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {

		java.awt.EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				new TelaSelCaptura().setVisible(true);
			}
		});
	}
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

	@Override
	public void mousePressed(MouseEvent e) {
		ct.setX1(e.getX());
		ct.setY1(e.getY());
		ct.setXTela1(e.getXOnScreen());
		ct.setYTela1(e.getYOnScreen());
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		ct.setX2(e.getX());
		ct.setY2(e.getY());
		ct.setXTela2(e.getXOnScreen());
		ct.setYTela2(e.getYOnScreen());
		AWTUtilities.setWindowOpacity(this, 0.0f);
		Date d = new Date();
		long l = d.getTime();
		new CapturaTelaBusiness().captura(TelaInicial.aProjetos.getCaminho() + "\\" + l + ".png", ct.getXTela1(), ct.getYTela1(), ct.getLargura(), ct.getAltura());
		dispose();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		ct.setX(e.getX());
		ct.setY(e.getY());
		ct.setXTela(e.getXOnScreen());
		ct.setYTela(e.getYOnScreen());
		repaint();
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g); // Limpa a tela

		ct.setLargura(ct.getX1() - ct.getX());
		ct.setAltura(ct.getY1() - ct.getY());
		ct.setLargura(ct.getLargura() * -1);
		ct.setAltura(ct.getAltura() * -1);

		g.drawRect(ct.getX1(), ct.getY1(), ct.getLargura(), ct.getAltura());
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		//	throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		//	throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void mouseExited(MouseEvent e) {
		//	throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		//	throw new UnsupportedOperationException("Not supported yet.");
	}
}
