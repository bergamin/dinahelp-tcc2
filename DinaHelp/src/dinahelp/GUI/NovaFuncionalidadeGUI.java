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

	private static String COMANDO_ADD = "COMANDO_ADD";
	private static final long serialVersionUID = 1L;

	public NovaFuncionalidadeGUI() {
		initComponents();
	}

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jtfNovaFuncNome = new javax.swing.JTextField();
        jlNovaFuncNome = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtaNovaFuncDescricao = new javax.swing.JTextArea();
        jlNovaFuncDescricao = new javax.swing.JLabel();
        jbNovaFuncOK = new javax.swing.JButton();
        jbNovaFuncCancelar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Nova Funcionalidade");

        jlNovaFuncNome.setText("Nome:");

        jtaNovaFuncDescricao.setColumns(20);
        jtaNovaFuncDescricao.setRows(5);
        jScrollPane1.setViewportView(jtaNovaFuncDescricao);

        jlNovaFuncDescricao.setText("Descrição:");

        jbNovaFuncOK.setText("OK");
        jbNovaFuncOK.setActionCommand(COMANDO_ADD);
        jbNovaFuncOK.addActionListener(this);

        jbNovaFuncCancelar.setText("Cancelar");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jlNovaFuncNome)
                        .addGap(23, 23, 23)
                        .addComponent(jtfNovaFuncNome, javax.swing.GroupLayout.DEFAULT_SIZE, 322, Short.MAX_VALUE))
                    .addComponent(jlNovaFuncDescricao)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
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
                .addGap(18, 18, 18)
                .addComponent(jlNovaFuncDescricao)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 157, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbNovaFuncCancelar)
                    .addComponent(jbNovaFuncOK))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				new NovaFuncionalidadeGUI().setVisible(true);
			}
		});
	}
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton jbNovaFuncCancelar;
    private javax.swing.JButton jbNovaFuncOK;
    private javax.swing.JLabel jlNovaFuncDescricao;
    private javax.swing.JLabel jlNovaFuncNome;
    private javax.swing.JTextArea jtaNovaFuncDescricao;
    private javax.swing.JTextField jtfNovaFuncNome;
    // End of variables declaration//GEN-END:variables

	@Override
	public void actionPerformed(ActionEvent e) {
		String comando = e.getActionCommand();
		if (COMANDO_ADD.equals(comando)) {
			if (Validador.caminhoValido(jtfNovaFuncNome.getText())) {
				if (!Validador.caminhoExistente(InicialGUI.aProjetos.getCaminho() + "\\" + jtfNovaFuncNome.getText())) {
					InicialGUI.aProjetos.addFilho(jtfNovaFuncNome.getText());
					File dir = new File(InicialGUI.aProjetos.getCaminho() + "\\" + jtfNovaFuncNome.getText());
					dir.mkdirs();
					dispose();
				}
			}
		}
	}
}