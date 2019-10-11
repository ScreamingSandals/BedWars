package misat11.bw.api.special;

import org.bukkit.Location;

/**
 * @author Bedwars Team
 */
public interface LuckyBlock extends SpecialItem {
    /**
     * @return
     */
    boolean isPlaced();

    /**
     * @return
     */
    Location getBlockLocation();
}
