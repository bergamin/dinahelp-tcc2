package dinahelp.GUI;

import dinahelp.util.Validador;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JOptionPane;

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

        tfNovaFuncNome = new javax.swing.JTextField();
        lNovaFuncNome = new javax.swing.JLabel();
        bNovaFuncOK = new javax.swing.JButton();
        bNovaFuncCancelar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Nova Funcionalidade");

        lNovaFuncNome.setText("Nome:");

        bNovaFuncOK.setText("OK");
        bNovaFuncOK.setActionCommand(COMANDO_ADD);
        bNovaFuncOK.addActionListener(this);

        bNovaFuncCancelar.setText("Cancelar");
        bNovaFuncCancelar.setActionCommand(COMANDO_CANCELAR);
        bNovaFuncCancelar.addActionListener(this);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lNovaFuncNome)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfNovaFuncNome, javax.swing.GroupLayout.DEFAULT_SIZE, 341, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(bNovaFuncOK, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bNovaFuncCancelar)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lNovaFuncNome)
                    .addComponent(tfNovaFuncNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bNovaFuncCancelar)
                    .addComponent(bNovaFuncOK))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bNovaFuncCancelar;
    private javax.swing.JButton bNovaFuncOK;
    private javax.swing.JLabel lNovaFuncNome;
    private javax.swing.JTextField tfNovaFuncNome;
    // End of variables declaration//GEN-END:variables

	/** Execução dos botões */
	@Override
	public void actionPerformed(ActionEvent e) {
		String comando = e.getActionCommand();
		if (COMANDO_ADD.equals(comando)) {
			if (tfNovaFuncNome.getText().isEmpty()) {
				JOptionPane.showMessageDialog(null, "Deve-se informar o nome");
			} else if (Validador.caminhoExistente(InicialGUI.aProjetos.getCaminho() + "\\" + tfNovaFuncNome.getText())) {
				JOptionPane.showMessageDialog(null, "Diretório já existente");
			} else if (Validador.nomeValido(tfNovaFuncNome.getText())) {
				InicialGUI.aProjetos.addFilho(tfNovaFuncNome.getText());
				File dir = new File(InicialGUI.aProjetos.getCaminho() + "\\" + tfNovaFuncNome.getText());
				dir.mkdirs();
				DinaHelp.inicial.setEnabled(true);
				dispose();
			}
		} else if (COMANDO_CANCELAR.equals(comando)) {
			DinaHelp.inicial.setEnabled(true);
			dispose();
		}
	}
}