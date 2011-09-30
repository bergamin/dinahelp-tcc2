package dinahelp.util;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import dinahelp.GUI.TelaInicial;

/**
 * @author Guilherme Taffarel Bergamin
 * @author Akanbi Strossi de Jesus
 * @author Felipe Bochehin
 */
public class Arvore extends JPanel {

	protected DefaultMutableTreeNode root;
	protected DefaultTreeModel modelo;
	protected JTree arvore;
	private Toolkit toolkit = Toolkit.getDefaultToolkit();

	public Arvore() {
		super(new GridLayout(1, 0));

		root = new DefaultMutableTreeNode("Projetos");
		modelo = new DefaultTreeModel(root);
		modelo.addTreeModelListener(new MyTreeModelListener());
		arvore = new JTree(modelo);
		arvore.setEditable(true);
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
				TreePath caminho = arvore.getSelectionPath();
				modelo.removeNodeFromParent(nodoAtual);
				return;
			}
		}
		toolkit.beep();
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

	public DefaultMutableTreeNode addFilho(DefaultMutableTreeNode pai, Object filho) {
		return addFilho(pai, filho, false);
	}

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

	public String getCaminho() {
		String retorno = arvore.getSelectionPath().toString();
		retorno = retorno.replace(',', '\\');
		retorno = retorno.substring(1, retorno.length() - 1);
		retorno = removeChar(retorno, '[');
		retorno = removeChar(retorno, ']');
		retorno = retorno.replace("\\ ", "\\");
		return TelaInicial.config.getWorkspace() + "\\" + retorno;
	}

	public static String removeChar(String s, char c) {

		String retorno = "";

		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) != c) {
				retorno += s.charAt(i);
			}
		}

		return retorno;

	}

	class MyTreeModelListener implements TreeModelListener {

		@Override
		public void treeNodesChanged(TreeModelEvent e) {
			DefaultMutableTreeNode nodo;
			nodo = (DefaultMutableTreeNode) (e.getTreePath().getLastPathComponent());

			/*
			 * If the event lists children, then the changed
			 * node is the child of the node we've already
			 * gotten.  Otherwise, the changed node and the
			 * specified node are the same.
			 */

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
