package dinahelp.GUI;

import com.sun.awt.AWTUtilities;
import dinahelp.negocio.VideoNegocio;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * @author Guilherme Taffarel Bergamin
 * @author Akanbi Strossi de Jesus
 * @author Felipe Bochehin
 */
public class VideoGUI extends javax.swing.JFrame implements ActionListener {

	/** Comandos dos botões */
	private static String COMANDO_AREA = "COMANDO_AREA";
	private static String COMANDO_TELAINTEIRA = "COMANDO_TELAINTEIRA";
	private static String COMANDO_GRAVA = "COMANDO_GRAVA";
	private static String COMANDO_PARA = "COMANDO_PARA";
	/** Área de captura */
	public static int x;
	public static int y;
	public static int largura;
	public static int altura;
	/**
	 * Retângulo com a área de captura.
	 * É alterado conforme os parâmetros acima
	 */
	private Rectangle retangulo = new Rectangle(0, 0, 360, 240);
	/** Thread de vídeo */
	public static VideoNegocio video;
	/** Parando a gravação */
	private boolean parando = false;
	/** Frames por segundo */
	private int fps = 30;
	/** Frequência */
	private float frequencia = 22050f;
	
	/** Construtor */
	public VideoGUI() {
		
		x = y = largura = altura = 0;
		
		criaVideo();
		initComponents();
	}

	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bIniGrava = new javax.swing.JButton();
        bFimGrava = new javax.swing.JButton();
        bArea = new javax.swing.JButton();
        cbTelaInteira = new javax.swing.JCheckBox();
        tfNomeVideo = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Ajuda em vídeo");
        setResizable(false);

        bIniGrava.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dinahelp/util/imagens/video2.png"))); // NOI18N
        bIniGrava.setMnemonic('c');
        bIniGrava.setActionCommand(COMANDO_GRAVA);
        bIniGrava.addActionListener(this);

        bFimGrava.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dinahelp/util/imagens/parar.png"))); // NOI18N
        bFimGrava.setActionCommand(COMANDO_PARA);
        bFimGrava.addActionListener(this);

        bArea.setText("Sel. Área");
        bArea.setActionCommand(COMANDO_AREA);
        bArea.addActionListener(this);

        cbTelaInteira.setText("Tela Inteira");
        cbTelaInteira.setActionCommand(COMANDO_TELAINTEIRA);
        cbTelaInteira.addActionListener(this);

        jLabel1.setText("Nome:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfNomeVideo, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(bArea)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbTelaInteira))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addComponent(bIniGrava, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bFimGrava, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(tfNomeVideo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bArea)
                    .addComponent(cbTelaInteira))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(bFimGrava, 0, 0, Short.MAX_VALUE)
                    .addComponent(bIniGrava, javax.swing.GroupLayout.PREFERRED_SIZE, 23, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bArea;
    private javax.swing.JButton bFimGrava;
    private javax.swing.JButton bIniGrava;
    private javax.swing.JCheckBox cbTelaInteira;
    private javax.swing.JLabel jLabel1;
    public static javax.swing.JTextField tfNomeVideo;
    // End of variables declaration//GEN-END:variables
	
	/** Execução dos comandos dos botões */
	@Override
	public void actionPerformed(ActionEvent e) {
		String comando = e.getActionCommand();

		if (COMANDO_AREA.equals(comando)) {
			x = y = largura = altura = 0;
			CapturaTelaGUI tsc = new CapturaTelaGUI('V');
			tsc.setExtendedState(MAXIMIZED_BOTH);
			AWTUtilities.setWindowOpacity(tsc, 0.5f);
			tsc.setVisible(true);
		} else if (COMANDO_TELAINTEIRA.equals(comando)) {
			if (cbTelaInteira.isSelected()) {
				bArea.setEnabled(false);
			} else {
				bArea.setEnabled(true);
			}
		} else if (COMANDO_GRAVA.equals(comando)) {
			if (!cbTelaInteira.isSelected() && x == 0 && y == 0 && largura == 0 && altura == 0) {
				JOptionPane.showMessageDialog(null, "Deve-se selecionar a area a ser gravada.");
			} else if (tfNomeVideo.getText().isEmpty()) {
				JOptionPane.showMessageDialog(null, "Deve-se escolher um nome para o arquivo de vídeo.");
			} else {

				setExtendedState(JFrame.ICONIFIED);
				DinaHelp.iniciaGUI.setExtendedState(JFrame.ICONIFIED);

				if (cbTelaInteira.isSelected()) {
					x = y = 0;
					largura = Toolkit.getDefaultToolkit().getScreenSize().width;
					altura = Toolkit.getDefaultToolkit().getScreenSize().height;
				}
				retangulo.setBounds(x, y, largura, altura);
				// Iniciar gravação sincronizada
				long tempoSinc = System.currentTimeMillis();
				video.setSyncTime(tempoSinc);
				// Inicia gravação do vídeo
				video.setNaoTerminado(true);
				video.wakeUp();
			}
		} else if (COMANDO_PARA.equals(comando)) {
			parando = true;
			PararThread pararThread = new PararThread();
			pararThread.setPriority(Thread.MIN_PRIORITY);
			pararThread.start();
			ConfirmaArquivoGUI c = new ConfirmaArquivoGUI("VIDEO");
			c.setVisible(true);
			dispose();
		}
	}

	private void parar() {

		video.naoTerminado = false;

		while (video.gravando) {
			video.hold();
		}

		// Encode do vídeo
		video.encode();
	}

	private class PararThread extends Thread {

		@Override
		public void run() {
			try {
				parar();
			} catch (Exception e) {
				System.out.println("Thread de Parada Cancelada");
				System.out.println(e);
			} finally {
//                myProgressBar.dispose();
			}
		}
	}
	/** This method just creates the ScreenGrabber.
	 *  This method is called from the init() method,
	 *  in a separate thread, to try to get some flow in
	 *  the display of the program.
	 */
	private void criaVideo() {
		/** Make the ScreenGrabber. It will start as a seperate
		 *  thread, with highest priority. The constructor to
		 *  ScreenGrabber will just perform a speed test, and
		 *  then it's done. Both retangulo and startFps are 
		 *  global parameters that are already initiated.
		 */
		video = new VideoNegocio(retangulo, fps);
		video.setPriority(Thread.MAX_PRIORITY);
		/** Starts the ScreenGrabber thread. This thread will
		 *  basically go through the ScreenGrabber.init() method, 
		 *  and then wait, until gravando is started. See the
		 *  ScreenGrabber.run() method.
		 */
		video.start();
	}
}
