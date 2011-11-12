package dinahelp.GUI;

import dinahelp.util.CopiaArquivos;

/**
 * @author Guilherme Taffarel Bergamin
 * @author Akanbi Strossi de Jesus
 * @author Felipe Bochehin
 */
public class ConfirmaVideoGUI extends javax.swing.JFrame {

	@SuppressWarnings("ResultOfObjectAllocationIgnored")
	public ConfirmaVideoGUI() {
		initComponents();
		while (VideoGUI.video.running) { /* não faz nada enquanto estiver rodando o encode */ }
		new CopiaArquivos(VideoGUI.video.arquivoTemp, InicialGUI.aProjetos.getCaminho() + "\\" + VideoGUI.tfNomeVideo.getText() + ".mov");
		InicialGUI.aProjetos.addFilho(VideoGUI.tfNomeVideo.getText()+".mov");
		tfCaminhoArquivo.setText(InicialGUI.aProjetos.getCaminho() + "\\" + VideoGUI.tfNomeVideo.getText() + ".mov");
		bOK.setEnabled(true);
	}

	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblCarregamento = new javax.swing.JLabel();
        bOK = new javax.swing.JButton();
        tfCaminhoArquivo = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Arquivo criado com sucesso");

        lblCarregamento.setText("Arquivo criado com sucesso:");

        bOK.setText("OK");
        bOK.setEnabled(false);
        bOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bOKActionPerformed(evt);
            }
        });

        tfCaminhoArquivo.setEditable(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblCarregamento)
                    .addComponent(tfCaminhoArquivo, javax.swing.GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE)
                    .addComponent(bOK, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblCarregamento)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tfCaminhoArquivo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bOK)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

	private void bOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bOKActionPerformed
		dispose();
	}//GEN-LAST:event_bOKActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bOK;
    private javax.swing.JLabel lblCarregamento;
    private javax.swing.JTextField tfCaminhoArquivo;
    // End of variables declaration//GEN-END:variables
}
