package org.screamingsandals.bedwars.api;

import org.jetbrains.annotations.ApiStatus;
import org.screamingsandals.bedwars.api.entities.EntitiesManager;
import org.screamingsandals.bedwars.api.game.GameManager;
import org.screamingsandals.bedwars.api.game.ItemSpawnerType;
import org.screamingsandals.bedwars.api.player.PlayerManager;
import org.screamingsandals.bedwars.api.statistics.PlayerStatisticsManager;
import org.screamingsandals.bedwars.api.utils.ColorChanger;
import org.screamingsandals.bedwars.api.utils.EventUtils;
import org.screamingsandals.lib.utils.Wrapper;

import java.util.List;

/**
 * @author Bedwars Team
 */
@ApiStatus.NonExtendable
public interface BedwarsAPI extends Wrapper {
    /**
     * @return Game manager of the bedwars plugin
     */
    GameManager<?> getGameManager();

    /**
     * @return Player manager of the bedwars plugin
     */
    PlayerManager<?, ?> getPlayerManager();

    /**
     * @return Entities manager of the bedwars plugin
     */
    EntitiesManager<?, ?> getEntitiesManager();

    /**
     * @return Event utils used for registering handlers for bedwars' events
     */
    EventUtils getEventUtils();

    /**
     * @return List of existing spawner types
     */
    List<ItemSpawnerType> getItemSpawnerTypes();

    /**
     * @param name Name of item spawner type
     * @return boolean Is spawner type registered
     */
    boolean isItemSpawnerTypeRegistered(String name);

    /**
     * @param name Name of item spawner type
     * @return ItemSpawnerType by name or null if type isn't exists
     */
    ItemSpawnerType getItemSpawnerTypeByName(String name);
    /**
     * @return String of Bedwars Version
     */
    String getPluginVersion();

    /**
     * @return Color changer for coloring ItemStacks
     */
    ColorChanger<?> getColorChanger();

    /**
     *
     * @return hub server name from config
     */
    String getHubServerName();

    /**
     *
     * @return PlayerStatisticsManager if statistics are enabled; otherwise null
     */
    PlayerStatisticsManager<?> getStatisticsManager();

    /**
     * @return Bedwars instance
     */
    static BedwarsAPI getInstance() {
        return Internal.bedwarsAPI;
    }

    @ApiStatus.Internal
    class Internal {
        protected static BedwarsAPI bedwarsAPI;

        public static void setBedWarsAPI(BedwarsAPI bedwarsAPI) {
            if (Internal.bedwarsAPI != null) {
                throw new UnsupportedOperationException("Already initialized!");
            }
            Internal.bedwarsAPI = bedwarsAPI;
        }
    }
}
