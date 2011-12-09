package dinahelp.util;

/**
 * @author Guilherme Taffarel Bergamin
 * @author Akanbi Strossi de Jesus
 * @author Felipe Bochehin
 */
public class Config {

	/**
	 * local onde serão gravados os projetos.
	 * 
	 * A idéia desta classe era termos uma tela de configuração para realizar
	 * a alteração de vários parâmetros e gravar isto em um arquivo para depois
	 * ser lido na chamada do programa. Ficará para implemenações futuras.
	 */
	private String workspace;

	/** Construtor */
	public Config(String workspace) {
		this.workspace = workspace;
	}

	/** seta o local de gravação padrão dos projetos */
	public void setWorkspace(String workspace) {
		this.workspace = workspace;
	}

	/** Retorna o local de gravação padrão dos projetos */
	public String getWorkspace() {
		return workspace;
	}
}
