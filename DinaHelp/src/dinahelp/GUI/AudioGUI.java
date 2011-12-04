package dinahelp.GUI;

import dinahelp.negocio.AudioNegocio;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Guilherme Taffarel Bergamin
 * @author Akanbi Strossi de Jesus
 * @author Felipe Bochehin
 */
public class AudioGUI extends javax.swing.JFrame implements ActionListener {

	/** Comandos dos botões */
	private static String COMANDO_GRAVA = "COMANDO_GRAVA";
	private static String COMANDO_PARA = "COMANDO_PARA";
	/** Thread de áudio */
	public static AudioNegocio audio;
	/** Frequencia */
	private float frequencia = 22050f;
	/** Parando a gravação */
	private boolean parando = false;

	public AudioGUI() {

		initComponents();
		criaAudio();

	}

	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        tfNomeAudio = new javax.swing.JTextField();
        bIniGrava = new javax.swing.JButton();
        bFimGrava = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Gravar Áudio");
        setResizable(false);

        jLabel1.setText("Nome:");

        bIniGrava.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dinahelp/util/imagens/video2.png"))); // NOI18N
        bIniGrava.setMnemonic('c');
        bIniGrava.setActionCommand(COMANDO_GRAVA);
        bIniGrava.addActionListener(this);

        bFimGrava.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dinahelp/util/imagens/parar.png"))); // NOI18N
        bFimGrava.setActionCommand(COMANDO_PARA);
        bFimGrava.addActionListener(this);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(bIniGrava, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bFimGrava, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(tfNomeAudio, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(tfNomeAudio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(bFimGrava, 0, 0, Short.MAX_VALUE)
                    .addComponent(bIniGrava, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bFimGrava;
    private javax.swing.JButton bIniGrava;
    private javax.swing.JLabel jLabel1;
    public static javax.swing.JTextField tfNomeAudio;
    // End of variables declaration//GEN-END:variables

	@Override
	public void actionPerformed(ActionEvent e) {
		String comando = e.getActionCommand();

		if (COMANDO_GRAVA.equals(comando)) {

			long tempoSinc = System.currentTimeMillis();
			audio.setSyncTime(tempoSinc);
			audio.stopped = false;
			audio.wakeUp();


		} else if (COMANDO_PARA.equals(comando)) {
			parando = true;
			PararThread pararThread = new PararThread();
			pararThread.setPriority(Thread.MIN_PRIORITY);
			pararThread.start();
			ConfirmaArquivoGUI c = new ConfirmaArquivoGUI("AUDIO");
			c.setVisible(true);
			dispose();
		}
	}

	private void criaAudio() {
		audio = new AudioNegocio();
		audio.channels = 1;
		audio.sampleSize = 16;
		audio.frequency = frequencia;
		audio.setPriority(Thread.MAX_PRIORITY);
		audio.start();
	}

	private void parar() throws InterruptedException {
		Thread.sleep(3000);
		audio.stopped = true;
		Thread.sleep(3000);
		audio.stopRecording();
		Thread.sleep(3000);
		while (audio.recording) {
			audio.hold();
		}
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
			}
		}
	}
}
