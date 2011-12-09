package dinahelp.util;

import dinahelp.GUI.InicialGUI;
import java.awt.GridLayout;
import java.io.File;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 * @author Guilherme Taffarel Bergamin
 * @author Akanbi Strossi de Jesus
 * @author Felipe Bochehin
 */
public class Arvore extends JPanel {

	protected DefaultMutableTreeNode root;
	protected DefaultTreeModel modelo;
	protected JTree arvore;

	/** Construtor */
	public Arvore() {
		super(new GridLayout(1, 0));

		root = new DefaultMutableTreeNode("Projetos");
		modelo = new DefaultTreeModel(root);
		modelo.addTreeModelListener(new MyTreeModelListener());
		arvore = new JTree(modelo);
		arvore.setEditable(false);
		arvore.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		arvore.setShowsRootHandles(true);

		JPanel p = new JPanel();
		p.add(arvore);
		add(p);
	}

	/** Remove o nodo selecionado. */
	public void removeNodo() {
		TreePath selecaoAtual = arvore.getSelectionPath();
		if (selecaoAtual != null) {
			DefaultMutableTreeNode nodoAtual = (DefaultMutableTreeNode) (selecaoAtual.getLastPathComponent());
			MutableTreeNode pai = (MutableTreeNode) (nodoAtual.getParent());
			if (pai != null) {
				new File(getCaminho()).delete(); // SÓ NÃO DELETA PASTA COM ARQUIVOS DENTRO
				modelo.removeNodeFromParent(nodoAtual);
				return;
			}
		}
	}

	/** Adiciona filho no nodo selecionado. */
	public DefaultMutableTreeNode addFilho(Object filho) {
		DefaultMutableTreeNode nodoPai = null;
		TreePath caminhoPai = arvore.getSelectionPath();

		if (caminhoPai == null) {
			nodoPai = root;
		} else {
			nodoPai = (DefaultMutableTreeNode) (caminhoPai.getLastPathComponent());
		}

		return addFilho(nodoPai, filho, true);
	}

	/** Adiciona um novo nodo filho abaixo do pai informado por parâmetro */
	public DefaultMutableTreeNode addFilho(DefaultMutableTreeNode pai, Object filho) {
		return addFilho(pai, filho, false);
	}

	/** Adiciona um novo nodo filho abaixo do pai informado por parâmetro */
	public DefaultMutableTreeNode addFilho(DefaultMutableTreeNode pai, Object filho, boolean isVisivel) {
		DefaultMutableTreeNode nodoFilho = new DefaultMutableTreeNode(filho);

		if (pai == null) {
			pai = root;
		}

		modelo.insertNodeInto(nodoFilho, pai, pai.getChildCount());

		if (isVisivel) {
			arvore.scrollPathToVisible(new TreePath(nodoFilho.getPath()));
		}
		return nodoFilho;
	}

	/** Retorna o caminho completo da seleção + o workspace definido inicialmente */
	public String getCaminho() {
		if (arvore.getSelectionPath() != null) {
			String retorno = arvore.getSelectionPath().toString();
			retorno = retorno.replace(',', '\\');
			retorno = retorno.substring(1, retorno.length() - 1);
			retorno = removeChar(retorno, '[');
			retorno = removeChar(retorno, ']');
			retorno = retorno.replace("\\ ", "\\");
			retorno = InicialGUI.config.getWorkspace() + "\\" + retorno;
			return retorno;
		} else {
			return null;
		}
	}

	/** Método auxiliar ao getCaminho() para remover caracteres de uma String */
	public static String removeChar(String s, char c) {

		String retorno = "";

		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) != c) {
				retorno += s.charAt(i);
			}
		}

		return retorno;

	}

	/** Inner Class necessária para implementar a árvore */
	class MyTreeModelListener implements TreeModelListener {

		@Override
		public void treeNodesChanged(TreeModelEvent e) {
			DefaultMutableTreeNode nodo;
			nodo = (DefaultMutableTreeNode) (e.getTreePath().getLastPathComponent());
			int i = e.getChildIndices()[0];
			nodo = (DefaultMutableTreeNode) (nodo.getChildAt(i));
		}

		@Override
		public void treeNodesInserted(TreeModelEvent e) {
		}

		@Override
		public void treeNodesRemoved(TreeModelEvent e) {
		}

		@Override
		public void treeStructureChanged(TreeModelEvent e) {
		}
	}
}
