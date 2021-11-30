package org.screamingsandals.bedwars.api;

import org.jetbrains.annotations.ApiStatus;
import org.screamingsandals.bedwars.api.entities.EntitiesManager;
import org.screamingsandals.bedwars.api.game.GameManager;
import org.screamingsandals.bedwars.api.game.ItemSpawnerType;
import org.screamingsandals.bedwars.api.player.PlayerManager;
import org.screamingsandals.bedwars.api.statistics.PlayerStatisticsManager;
import org.screamingsandals.bedwars.api.utils.ColorChanger;
import org.screamingsandals.bedwars.api.utils.EventUtils;
import org.screamingsandals.bedwars.api.variants.VariantManager;
import org.screamingsandals.lib.utils.Wrapper;

import java.util.List;

/**
 * <p>The BedWars API main entry point.</p>
 *
 * @author ScreamingSandals
 */
@ApiStatus.NonExtendable
public interface BedwarsAPI extends Wrapper {
    /**
     * <p>Retrieves the game manager instance.</p>
     *
     * @return the game manager instance
     */
    GameManager<?> getGameManager();

    /**
     * <p>Retrieves the variant manager instance.</p>
     *
     * @return the variant manager instance
     */
    VariantManager getVariantManager();

    /**
     * <p>Retrieves the player manager instance.</p>
     *
     * @return the player manager instance
     */
    PlayerManager<?, ?> getPlayerManager();

    /**
     * <p>Retrieves the entities manager instance.</p>
     *
     * @return the entities manager instance
     */
    EntitiesManager<?, ?> getEntitiesManager();

    /**
     * @return Event utils used for registering handlers for bedwars' events
     */
    EventUtils getEventUtils();

    /**
     * <p>Retrieves a {@link List} of available item spawner types.</p>
     *
     * @return a {@link List} of item spawner types
     */
    List<ItemSpawnerType<?, ?, ?>> getItemSpawnerTypes();

    /**
     * @param name Name of item spawner type
     * @return boolean Is spawner type registered
     */
    boolean isItemSpawnerTypeRegistered(String name);

    /**
     * @param name Name of item spawner type
     * @return ItemSpawnerType by name or null if type isn't exists
     */
    ItemSpawnerType<?, ?, ?> getItemSpawnerTypeByName(String name);

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
