package dinahelp.GUI;

import dinahelp.util.Validador;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * @author Guilherme Taffarel Bergamin
 * @author Akanbi Strossi de Jesus
 * @author Felipe Bochehin
 */
public class NovaFuncionalidadeGUI extends javax.swing.JFrame implements ActionListener {

	/** Comando dos botões */
	private static String COMANDO_ADD = "COMANDO_ADD";
	private static String COMANDO_CANCELAR = "COMANDO_CANCELAR";
	
	public NovaFuncionalidadeGUI() {
		initComponents();
	}

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jtfNovaFuncNome = new javax.swing.JTextField();
        jlNovaFuncNome = new javax.swing.JLabel();
        jbNovaFuncOK = new javax.swing.JButton();
        jbNovaFuncCancelar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Nova Funcionalidade");

        jlNovaFuncNome.setText("Nome:");

        jbNovaFuncOK.setText("OK");
        jbNovaFuncOK.setActionCommand(COMANDO_ADD);
        jbNovaFuncOK.addActionListener(this);

        jbNovaFuncCancelar.setText("Cancelar");
        jbNovaFuncCancelar.setActionCommand(COMANDO_CANCELAR);
        jbNovaFuncCancelar.addActionListener(this);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jlNovaFuncNome)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtfNovaFuncNome, javax.swing.GroupLayout.DEFAULT_SIZE, 341, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jbNovaFuncOK, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbNovaFuncCancelar)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlNovaFuncNome)
                    .addComponent(jtfNovaFuncNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbNovaFuncCancelar)
                    .addComponent(jbNovaFuncOK))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jbNovaFuncCancelar;
    private javax.swing.JButton jbNovaFuncOK;
    private javax.swing.JLabel jlNovaFuncNome;
    private javax.swing.JTextField jtfNovaFuncNome;
    // End of variables declaration//GEN-END:variables

	/** Execução dos botões */
	@Override
	public void actionPerformed(ActionEvent e) {
		String comando = e.getActionCommand();
		if (COMANDO_ADD.equals(comando)) {
			if (Validador.nomeValido(jtfNovaFuncNome.getText())) {
				if (!Validador.caminhoExistente(InicialGUI.aProjetos.getCaminho() + "\\" + jtfNovaFuncNome.getText())) {
					InicialGUI.aProjetos.addFilho(jtfNovaFuncNome.getText());
					File dir = new File(InicialGUI.aProjetos.getCaminho() + "\\" + jtfNovaFuncNome.getText());
					dir.mkdirs();
					dispose();
				}
			}
		} else if (COMANDO_CANCELAR.equals(comando)) {
			dispose();
		}
	}
}