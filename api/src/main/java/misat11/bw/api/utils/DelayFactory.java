package misat11.bw.api.utils;

import misat11.bw.api.special.SpecialItem;
import org.bukkit.entity.Player;

/**
 * @author Bedwars Team
 */
public interface DelayFactory {

    /**
     * @return
     */
    boolean getDelayActive();

    /**
     * @return
     */
    SpecialItem getSpecialItem();

    /**
     * @return
     */
    int getRemainDelay();

    /**
     * @return
     */
    Player getPlayer();
}
