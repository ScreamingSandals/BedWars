package misat11.bw.api.boss;

/**
 * @author Bedwars Team
 *
 */
public interface XPBar extends StatusBar {
	
	/**
	 * @param seconds
	 */
	public void setSeconds(int seconds);
	
	/**
	 * @return seconds
	 */
	public int getSeconds();

}
