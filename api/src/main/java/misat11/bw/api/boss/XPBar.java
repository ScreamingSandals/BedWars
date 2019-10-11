package misat11.bw.api.boss;

/**
 * @author Bedwars Team
 */
public interface XPBar extends StatusBar {
    /**
     * @param seconds
     */
    void setSeconds(int seconds);

    /**
     * @return seconds
     */
    int getSeconds();

}
