package dinahelp.GUI;

import com.sun.awt.AWTUtilities;
import dinahelp.negocio.VideoNegocio;
import dinahelp.util.Validador;
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
	private static String COMANDO_CANCELAR = "COMANDO_CANCELAR";
	/** Área de captura */
	public static int x;
	public static int y;
	public static int largura;
	public static int altura;
	/**
	 * Retângulo com a área de captura.
	 * É alterado conforme os parâmetros acima
	 */
	private Rectangle retangulo = new Rectangle();
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
        bCancelar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Ajuda em vídeo");
        setResizable(false);

        bIniGrava.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dinahelp/util/imagens/video2.png"))); // NOI18N
        bIniGrava.setMnemonic('c');
        bIniGrava.setActionCommand(COMANDO_GRAVA);
        bIniGrava.addActionListener(this);

        bFimGrava.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dinahelp/util/imagens/parar.png"))); // NOI18N
        bFimGrava.setActionCommand(COMANDO_PARA);
        bFimGrava.setEnabled(false);
        bFimGrava.addActionListener(this);

        bArea.setText("Sel. Área");
        bArea.setActionCommand(COMANDO_AREA);
        bArea.addActionListener(this);

        cbTelaInteira.setText("Tela Inteira");
        cbTelaInteira.setActionCommand(COMANDO_TELAINTEIRA);
        cbTelaInteira.addActionListener(this);

        jLabel1.setText("Nome:");

        bCancelar.setText("Cancelar");
        bCancelar.setActionCommand(COMANDO_CANCELAR);
        bCancelar.addActionListener(this);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
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
                                .addComponent(cbTelaInteira))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(42, 42, 42)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(bCancelar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(bIniGrava, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(bFimGrava, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(tfNomeVideo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bArea)
                    .addComponent(cbTelaInteira))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(bFimGrava, 0, 0, Short.MAX_VALUE)
                    .addComponent(bIniGrava, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bCancelar)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bArea;
    private javax.swing.JButton bCancelar;
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
			} else if (Validador.caminhoExistente(InicialGUI.aProjetos.getCaminho() + "\\" + tfNomeVideo.getText() + ".mov")) {
				JOptionPane.showMessageDialog(null, "Arquivo já existente");
			} else if (Validador.nomeValido(tfNomeVideo.getText())) {

				bIniGrava.setEnabled(false);
				bCancelar.setEnabled(false);
				setExtendedState(JFrame.ICONIFIED);
				DinaHelp.inicial.setExtendedState(JFrame.ICONIFIED);

				if (cbTelaInteira.isSelected()) {
					x = y = 0;
					largura = Toolkit.getDefaultToolkit().getScreenSize().width;
					altura = Toolkit.getDefaultToolkit().getScreenSize().height;
				}
				retangulo.setBounds(x, y, largura, altura);
				long tempoSinc = System.currentTimeMillis();
				video.setSyncTime(tempoSinc);
				video.setNaoTerminado(true);
				video.wakeUp();
				bFimGrava.setEnabled(true);
			}

		} else if (COMANDO_PARA.equals(comando)) {

			parando = true;
			PararThread pararThread = new PararThread();
			pararThread.setPriority(Thread.MIN_PRIORITY);
			pararThread.start();
			ConfirmaArquivoGUI c = new ConfirmaArquivoGUI("VIDEO");
			c.setVisible(true);
			DinaHelp.inicial.setEnabled(true);
			dispose();

		} else if (COMANDO_CANCELAR.equals(comando)) {

			DinaHelp.inicial.setEnabled(true);
			dispose();

		}
	}

	/** Parar gravação */
	private void parar() {

		video.naoTerminado = false;

		while (video.gravando) {
			video.hold();
		}

		video.encode();
	}

	/** Thread que pára a thread que está rodando no momento */
	private class PararThread extends Thread {

		@Override
		@SuppressWarnings("CallToThreadDumpStack")
		public void run() {
			try {
				parar();
			} catch (Exception e) {
				System.out.println("Thread de Parada Cancelada");
				e.printStackTrace();
			}
		}
	}

	/** Cria um VideoNegocio e inicia sua Thread */
	private void criaVideo() {
		video = new VideoNegocio(retangulo, fps);
		video.setPriority(Thread.MAX_PRIORITY);
		video.start();
	}
}
