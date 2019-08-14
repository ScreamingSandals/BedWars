package misat11.bw.api.boss;

/**
 * @author Bedwars Team
 *
 */
public interface BossBar extends StatusBar {
	/**
	 * @return current message
	 */
	public String getMessage();
	
	/**
	 * @param message
	 */
	public void setMessage(String message);
	
	
}
