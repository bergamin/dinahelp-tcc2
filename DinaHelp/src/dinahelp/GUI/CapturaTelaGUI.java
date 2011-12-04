package dinahelp.GUI;

import dinahelp.negocio.CapturaTelaNegocio;
import dinahelp.pojo.CapturaTela;
import com.sun.awt.AWTUtilities;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Date;

/**
 * @author Guilherme Taffarel Bergamin
 * @author Akanbi Strossi de Jesus
 * @author Felipe Bochehin
 */
public class CapturaTelaGUI extends javax.swing.JFrame implements MouseListener, MouseMotionListener {

	/** Guarda os dados de posição da captura */
	private CapturaTela ct = new CapturaTela();
	/** tipo de captura (vídeo ou captura estática) */
	private char tipo;

	@SuppressWarnings("LeakingThisInConstructor")
	public CapturaTelaGUI(char tipo) {
		this.tipo = tipo;
		initComponents();
		addMouseListener(this);
		addMouseMotionListener(this);
	}

	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setAlwaysOnTop(true);
        setUndecorated(true);

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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
	/** Eventos de mouse */
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

		if (ct.getLargura() < 0) {
			ct.setLargura(Math.abs(ct.getLargura()));
			ct.setXTela1(ct.getXTela1() - ct.getLargura());
			ct.setXTela2(ct.getXTela2() + ct.getLargura());
		}
		if (ct.getAltura() < 0) {
			ct.setAltura(Math.abs(ct.getAltura()));
			ct.setYTela1(ct.getYTela1() - ct.getAltura());
			ct.setYTela2(ct.getYTela2() + ct.getAltura());
		}
		if (tipo == 'I') {
			AWTUtilities.setWindowOpacity(this, 0.0f);
			Date d = new Date();
			long l = d.getTime();
			new CapturaTelaNegocio().captura(InicialGUI.aProjetos.getCaminho() + "\\" + l + ".png", ct.getXTela1(), ct.getYTela1(), ct.getLargura(), ct.getAltura());
			InicialGUI.aProjetos.addFilho(l + ".png");
		} else { // tipo == 'V';
			VideoGUI.x = ct.getXTela1();
			VideoGUI.y = ct.getYTela1();
			VideoGUI.largura = ct.getLargura();
			VideoGUI.altura = ct.getAltura();
		}
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

	/** desenha a reta */
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
