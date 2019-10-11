package misat11.bw.api.boss;

/**
 * @author Bedwars Team
 */
public interface BossBar extends StatusBar {
    /**
     * @return current message
     */
	String getMessage();

    /**
     * @param message
     */
	void setMessage(String message);


}
