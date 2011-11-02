/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * VideoGUI.java
 *
 * Created on 29/10/2011, 14:14:45
 */
package dinahelp.GUI;

import com.sun.awt.AWTUtilities;
import dinahelp.negocio.AudioNegocio;
import dinahelp.negocio.VideoNegocio;
import dinahelp.pojo.Merge;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

/**
 *
 * @author Guilherme
 */
public class VideoGUI extends javax.swing.JFrame implements ActionListener{
	
	private static String COMANDO_AREA = "COMANDO_AREA";
	private static String COMANDO_TELAINTEIRA = "COMANDO_TELAINTEIRA";
	private static String COMANDO_GRAVA = "COMANDO_GRAVA";
	private static String COMANDO_PARA = "COMANDO_PARA";
	public static int x;
	public static int y;
	public static int largura;
	public static int altura;
	public AudioNegocio audio;
	public VideoNegocio video;
	private boolean gravaAudio = false;
	private boolean parando = false;
	private Rectangle retangulo = new Rectangle(0, 0, 360, 240);
	private int fps = 15;
	private int frequencia = 22050;
	
	/** Creates new form VideoGUI */
	public VideoGUI() {
		x = y = largura = altura = 0;
		audio = new AudioNegocio();
		video = new VideoNegocio();
		initComponents();
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bIniGrava = new javax.swing.JButton();
        bFimGrava = new javax.swing.JButton();
        bArea = new javax.swing.JButton();
        cbTelaInteira = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        bIniGrava.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dinahelp/util/imagens/video2.png"))); // NOI18N
        bIniGrava.setActionCommand(COMANDO_GRAVA);
        bIniGrava.addActionListener(this);

        bFimGrava.setText("P");
        bFimGrava.setActionCommand(COMANDO_PARA);
        bFimGrava.addActionListener(this);

        bArea.setText("Sel. Área");
        bArea.setActionCommand(COMANDO_AREA);
        bArea.addActionListener(this);

        cbTelaInteira.setText("Tela Inteira");
        cbTelaInteira.setActionCommand(COMANDO_TELAINTEIRA);
        cbTelaInteira.addActionListener(this);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(bIniGrava, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bFimGrava))
                    .addComponent(cbTelaInteira)
                    .addComponent(bArea, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(bArea)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbTelaInteira)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(bIniGrava, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bFimGrava))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		/* Set the Nimbus look and feel */
		//<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
		 * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
		 */
		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException ex) {
			java.util.logging.Logger.getLogger(VideoGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger(VideoGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger(VideoGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(VideoGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}
		//</editor-fold>

		/* Create and display the form */
		java.awt.EventQueue.invokeLater(new Runnable() {

			public void run() {
				new VideoGUI().setVisible(true);
			}
		});
	}
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bArea;
    private javax.swing.JButton bFimGrava;
    private javax.swing.JButton bIniGrava;
    private javax.swing.JCheckBox cbTelaInteira;
    // End of variables declaration//GEN-END:variables

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
			if(cbTelaInteira.isSelected())
				bArea.setEnabled(false);
			else
				bArea.setEnabled(true);
		} else if (COMANDO_GRAVA.equals(comando)) {
			if(!cbTelaInteira.isSelected() && x == 0 && y == 0 && largura == 0 && altura == 0)
				JOptionPane.showMessageDialog(null, "Deve-se selecionar a area a ser gravada.");
			else{
				// Iniciar gravação sincronizada
				long tempoSinc = System.currentTimeMillis();
				audio.setSyncTime(tempoSinc);
				video.setSyncTime(tempoSinc);
				if (gravaAudio) {
					// É necessário avisar o VideoNegocio de que o áudio também será gravado.
					video.audioRecording = true;
					// Inicia gravação do áudio
					audio.stopped = false;
					audio.wakeUp();
				}
				// Inicia gravação do vídeo
				video.setNaoTerminado(true);
				video.wakeUp();
			}
		} else if (COMANDO_PARA.equals(comando)) {
			
			parando = true;
            PararThread pararThread = new PararThread();
            pararThread.setPriority(Thread.MIN_PRIORITY);
            pararThread.start();
		}
	}
	
	private void parar() {
        
        video.naoTerminado = false;
        audio.stopped = true;
        
		// Testar sem isso depois
		while (video.recording) {
			video.hold();
		}

        if (gravaAudio) {
            audio.stopRecording();
            video.audioRecording = false;
            video.wakeUp(); // VER ISSO
        }
        
		/** Encode video */
       video.encode();
        
        /** Merging audio and video */
        if (gravaAudio) {
            if (video.unRecoverableError) {
//                restoreGUI(); // TALVEZ USAR ISSO PRA ZERAR AS COISAS
                JOptionPane.showMessageDialog(null, "Erro ao criar arquivo mov. Abortando.");
            } else {
                if (video.error) {
                    JOptionPane.showMessageDialog(null, "Erro ao gravar o video.");
                }
                while (video.running) 
                    video.hold();
                
                while (audio.recording)
                    audio.hold();
                
                // Juntar áudio e vídeo
                try {
                    String arquivoAudio = "";
					String caminhoVideo = "c:\\teste\\projetos\\video.mov"; // Ver o caminho de gravação do áudio/vídeo
                    arquivoAudio = audio.audioFile.toURL().toString(); // Ver o caminho de gravação do áudio/vídeo
                    String argumentosMerge[] = {"-o", caminhoVideo, video.tempFile, arquivoAudio};
                    
                    // Restaura a GUI e deixa o merge executando em segundo plano
					// restoreGUI(); // Ver para talvez zerar as coisas de volta para o padrão
                    if (mergeAudioVideo(argumentosMerge))
                        JOptionPane.showMessageDialog(null, "Vídeo gravado com sucesso em "+argumentosMerge[1]);
                } catch (Exception e) {
//                    restoreGUI();
                    System.out.println(e);
                }
            }
        } else {
//            restoreGUI();
        }
    }
	
	private class PararThread extends Thread {
        
        public void run() {
            try {
                parar();
            } catch (Exception e) {
                System.out.println("Stop thread cancelled");
                System.out.println(e);
            } finally {
//                myProgressBar.dispose();
            }
        }
    }
	
	private boolean mergeAudioVideo(String[] mergeArguments) {
        try {                    
            new Merge(mergeArguments);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao mesclar arquivos");
        } catch (OutOfMemoryError o) {
            JOptionPane.showMessageDialog(null, "Erro ao mesclar arquivos");
        }
        return false;
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
         *  then it's done. Both capRect and startFps are 
         *  global parameters that are already initiated.
         */
		if(cbTelaInteira.isSelected()){
		//	retangulo.setBounds(x, y, WIDTH, WIDTH);
			x = y = 0;
			largura = Toolkit.getDefaultToolkit().getScreenSize().width;
			altura = Toolkit.getDefaultToolkit().getScreenSize().height;
		}
		retangulo.setBounds(x, y, largura, altura);
        video = new VideoNegocio(retangulo, fps);
        video.setPriority(Thread.MAX_PRIORITY);
        /** Starts the ScreenGrabber thread. This thread will
         *  basically go through the ScreenGrabber.init() method, 
         *  and then wait, until recording is started. See the
         *  ScreenGrabber.run() method.
         */
        video.start();        
    } 
    
    /** This method just creates the Sampler.
     *  This method is called from the init() method,
     *  in a separate thread, to get some flow in
     *  the display of the program.
     */
    private void criaAudio() {
        /** Make the Sampler, with the defaultFileName
         *  (a local parameter to the Sampler class) as the
         *  name of the file that will be created by the
         *  sampler. The Sampler is a separate thread, with
         *  highest priority. The constructor will just
         *  create the save file, and then it's done.
         */
        audio = new AudioNegocio();
                
        /** Pass on initial parameters to the sampler */
        audio.channels = 1;
        audio.sampleSize = 16;
        audio.frequency = frequencia;
        
        audio.setPriority(Thread.MAX_PRIORITY);
        /** Starts the Sampler thread. This thread will
         *  basically go through the Sampler.init() method, 
         *  and then wait, until recording is started. See the
         *  Sampler.run() method.
         */
        audio.start();
    }
}
