package dinahelp.GUI;

import dinahelp.negocio.ArquivoAjudaTextual;
import dinahelp.pojo.AjudaTextual;

/**
 * @author Guilherme Taffarel Bergamin
 * @author Akanbi Strossi de Jesus
 * @author Felipe Bochehin
 */
public class TelaAjudaTextual extends javax.swing.JFrame {

	private String caminho;
	private static final long serialVersionUID = 1L;

	/** Creates new form TelaAjudaTextual */
	public TelaAjudaTextual() {
		initComponents();
		caminho = TelaInicial.aProjetos.getCaminho();
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        txaEditTexto = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        btnSalva = new javax.swing.JButton();
        txfTitulo = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        txaEditTexto.setColumns(20);
        txaEditTexto.setRows(5);
        jScrollPane1.setViewportView(txaEditTexto);

        jLabel1.setText("Titulo: ");

        btnSalva.setText("Salvar");
        btnSalva.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                    .addComponent(btnSalva, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(txfTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(txfTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 288, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnSalva)
                .addContainerGap(24, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

	private void btnSalvaActionPerformed(java.awt.event.ActionEvent evt) {
		ArquivoAjudaTextual gravaAjudaTextual = new ArquivoAjudaTextual();
		AjudaTextual ajudaTextual = new AjudaTextual();
		ajudaTextual.setNomeAjuda(txfTitulo.getText() + ".txt");
		ajudaTextual.setTexto(txaEditTexto.getText());
		//Pegar o getNome() da funcionalidade para passar o caminho
		gravaAjudaTextual.geraArquivoAjudaTextual(caminho, ajudaTextual.getNomeAjuda(), ajudaTextual.getTexto());
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				new TelaAjudaTextual().setVisible(true);
			}
		});
	}
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSalva;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea txaEditTexto;
    private javax.swing.JTextField txfTitulo;
    // End of variables declaration//GEN-END:variables
}