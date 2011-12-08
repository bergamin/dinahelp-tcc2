package dinahelp.GUI;

import dinahelp.negocio.AjudaTextualNegocio;
import dinahelp.pojo.AjudaTextual;
import dinahelp.util.Validador;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

/**
 * @author Guilherme Taffarel Bergamin
 * @author Akanbi Strossi de Jesus
 * @author Felipe Bochehin
 */
public class AjudaTextualGUI extends javax.swing.JFrame implements ActionListener {

	/** Comandos dos botões */
	private static String COMANDO_SALVAR = "COMANDO_SALVAR";
	private static String COMANDO_SALVAR_SAIR = "COMANDO_SALVAR_SAIR";
	private static String COMANDO_CANCELAR = "COMANDO_CANCELAR";
	/** Caminho do arquivo */
	private String caminho;
	/**
	 * Origem da chamada da tela:
	 * CRIACAO ou EDICAO
	 */
	private String origem;

	/** Construtor */
	public AjudaTextualGUI(String origem) {
		initComponents();
		caminho = InicialGUI.aProjetos.getCaminho();
		this.origem = origem;
		if (origem.equalsIgnoreCase("EDICAO")) {
			txfTitulo.setEnabled(false);
		}
	}

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        txaEditTexto = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        btnSalva = new javax.swing.JButton();
        txfTitulo = new javax.swing.JTextField();
        btnCancelar = new javax.swing.JButton();
        btnSalvaSair = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Ajuda Textual");

        txaEditTexto.setColumns(20);
        txaEditTexto.setRows(5);
        jScrollPane1.setViewportView(txaEditTexto);

        jLabel1.setText("Titulo: ");

        btnSalva.setText("Salvar");
        btnSalva.setActionCommand(COMANDO_SALVAR);
        btnSalva.addActionListener(this);

        btnCancelar.setText("Cancelar");
        btnCancelar.setActionCommand(COMANDO_CANCELAR);
        btnCancelar.addActionListener(this);

        btnSalvaSair.setText("Salvar e Sair");
        btnSalvaSair.setActionCommand(COMANDO_SALVAR_SAIR);
        btnSalvaSair.addActionListener(this);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 387, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnSalvaSair, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSalva, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel1)
                    .addComponent(txfTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txfTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 425, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancelar)
                    .addComponent(btnSalva)
                    .addComponent(btnSalvaSair))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnSalva;
    private javax.swing.JButton btnSalvaSair;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    public static javax.swing.JTextArea txaEditTexto;
    public static javax.swing.JTextField txfTitulo;
    // End of variables declaration//GEN-END:variables

	/** Execução dos Botões */
	@Override
	public void actionPerformed(ActionEvent e) {
		String comando = e.getActionCommand();

		if (COMANDO_CANCELAR.equals(comando)) {
			DinaHelp.inicial.setEnabled(true);
			dispose();
		} else { // SALVAR ou SALVAR_SAIR
			AjudaTextualNegocio ajudaNegocio = new AjudaTextualNegocio();
			if (origem.equalsIgnoreCase("EDICAO")) {
				ajudaNegocio.editarArquivoAjudaTextual(caminho, txaEditTexto.getText());
			} else { // origem = "CRIACAO"
				if (txfTitulo.getText().isEmpty()) {
					JOptionPane.showMessageDialog(null, "O nome do arquivo deve ser preenchido");
				} else {
					if (Validador.nomeValido(txfTitulo.getText())) {
						if (!Validador.caminhoExistente(InicialGUI.aProjetos.getCaminho() + "\\" + txfTitulo.getText() + ".doc")) {
							InicialGUI.aProjetos.addFilho(txfTitulo.getText() + ".doc");
							AjudaTextual ajudaTextual = new AjudaTextual();
							ajudaTextual.setNomeAjuda(txfTitulo.getText() + ".doc");
							ajudaTextual.setTexto(txaEditTexto.getText());
							ajudaNegocio.geraArquivoAjudaTextual(caminho, ajudaTextual.getNomeAjuda(), ajudaTextual.getTexto());
							origem = "EDICAO";
							caminho += "\\" + txfTitulo.getText() + ".doc";
						} else {
							JOptionPane.showMessageDialog(null, "Arquivo já existente!");
						}
					}
				}
			}
			if (COMANDO_SALVAR_SAIR.equals(comando)) {
				DinaHelp.inicial.setEnabled(true);
				dispose();
			}
		}
	}
}
