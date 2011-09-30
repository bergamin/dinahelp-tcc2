package dinahelp.util;

/**
 * @author Guilherme Taffarel Bergamin
 * @author Akanbi Strossi de Jesus
 * @author Felipe Bochehin
 */
public class Config {

	private String workspace;

	public Config(String workspace) {
		this.workspace = workspace;
	}

	public void setWorkspace(String workspace) {
		this.workspace = workspace;
	}

	public String getWorkspace() {
		return workspace;
	}
}
