package misat11.bw.api.special;

/**
 * @author Bedwars Team
 *
 */
public interface ArrowBlocker extends SpecialItem {
	/**
	 * @return
	 */
	public int getProtectionTime();

	/**
	 * @return
	 */
	public int getUsedTime();
	
	/**
	 * @return
	 */
	public boolean isActivated();
	
	/**
	 * 
	 */
	public void runTask();
}
