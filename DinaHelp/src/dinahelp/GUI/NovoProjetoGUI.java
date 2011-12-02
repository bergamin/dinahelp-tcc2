/**
 * IMPORTANTE!!!
 * ESTA CLASSE NÃO É MAIS USADA!!!
 * REMOVER ISSO E O MENU TAMBÉM!!!
 */

package dinahelp.GUI;

import javax.swing.JOptionPane;

/**
 * @author Guilherme Taffarel Bergamin
 * @author Akanbi Strossi de Jesus
 * @author Felipe Bochehin
 */
public class NovoProjetoGUI extends javax.swing.JFrame {

	public NovoProjetoGUI() {
		initComponents();
	}

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jlNovoProjNome = new javax.swing.JLabel();
        jtfNovoProjNome = new javax.swing.JTextField();
        jbNovoProjCancelar = new javax.swing.JButton();
        jbNovoProjOK = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Novo Projeto");
        setLocationByPlatform(true);
        setResizable(false);

        jlNovoProjNome.setText("Nome:");

        jbNovoProjCancelar.setText("Cancelar");
        jbNovoProjCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbNovoProjCancelarActionPerformed(evt);
            }
        });

        jbNovoProjOK.setText("OK");
        jbNovoProjOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbNovoProjOKActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jlNovoProjNome)
                        .addGap(18, 18, 18)
                        .addComponent(jtfNovoProjNome, javax.swing.GroupLayout.DEFAULT_SIZE, 319, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jbNovoProjOK, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbNovoProjCancelar)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlNovoProjNome)
                    .addComponent(jtfNovoProjNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jbNovoProjOK)
                    .addComponent(jbNovoProjCancelar))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

	private void jbNovoProjCancelarActionPerformed(
			java.awt.event.ActionEvent evt) {
		dispose();
	}

	private void jbNovoProjOKActionPerformed(java.awt.event.ActionEvent evt) {
		if (jtfNovoProjNome.getText().isEmpty()) {
			JOptionPane.showMessageDialog(null,
					"O nome do projeto deve ser preenchido");
		} else {
			//			TreePath teste = TelaInicial.jtProjetos.getSelectionPath().add(new DefaultMutableTreeNode(jtfNovoProjNome.getText()));
			//			TreePath teste = TelaInicial.jtProjetos.getSelectionPath().pathByAddingChild(new DefaultMutableTreeNode(jtfNovoProjNome.getText()));
			//			TelaInicial.jtProjetos.addSelectionPath(TelaInicial.jtProjetos.getSelectionPath().pathByAddingChild(new DefaultMutableTreeNode(NovoProjetoGUI.jtfNovoProjNome.getText())));

			dispose();
		}
	}
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jbNovoProjCancelar;
    private javax.swing.JButton jbNovoProjOK;
    private javax.swing.JLabel jlNovoProjNome;
    public static javax.swing.JTextField jtfNovoProjNome;
    // End of variables declaration//GEN-END:variables
}