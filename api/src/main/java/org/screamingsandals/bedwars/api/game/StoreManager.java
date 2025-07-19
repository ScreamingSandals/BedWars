package org.screamingsandals.bedwars.api.game;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.api.events.OpenShopEvent;
import org.screamingsandals.bedwars.api.player.BWPlayer;

/**
 * @author ScreamingSandals
 * @since 0.3.0
 */
public interface StoreManager {
    /**
     * Opens the default store using the built-in shop system. It does not fire any event.
     * <p>
     * The player should be in a game, otherwise the behaviour of this method is undefined.
     * This method does not verify whether the player is playing BedWars.
     * <p>
     * Unless you have a specific reason, you should use {@link #tryOpenDefaultStore(BWPlayer)}.
     *
     * @param player the player
     * @since 0.3.0
     */
    default void openDefaultStore(@NotNull BWPlayer player) {
        openCustomStore(player, null);
    }

    /**
     * Opens a custom store using the built-in shop system. It does not fire any event.
     * <p>
     * The player should be in a game, otherwise the behaviour of this method is undefined.
     * This method does not verify whether the player is playing BedWars.
     * <p>
     * Unless you have a specific reason, you should use {@link #tryOpenCustomStore(BWPlayer, String)}.
     *
     * @param player the player
     * @param fileName the file name
     * @see #tryOpenCustomStore(BWPlayer, String)
     * @since 0.3.0
     */
    void openCustomStore(@NotNull BWPlayer player, @Nullable String fileName);

    /**
     * Tries opening a store using the provided GameStore instance.
     * <p>
     * It fires the {@link org.screamingsandals.bedwars.api.events.OpenShopEvent}, giving plugins the ability to
     * cancel the request or replace the shop system.
     * <p>
     * The player must be in a game, otherwise this method simply returns null.
     *
     * @param player the player
     * @param gameStore the game store
     * @return result of the fired event or null on failure
     * @since 0.3.0
     */
    OpenShopEvent.@Nullable Result tryOpenStore(@NotNull BWPlayer player, OpenShopEvent.@NotNull StoreLike gameStore);

    /**
     * Tries opening a default store.
     * <p>
     * It fires the {@link OpenShopEvent}, giving plugins the ability to
     * cancel the request or replace the shop system.
     * <p>
     * The player must be in a game, otherwise this method simply returns null.
     *
     * @param player the player
     * @return result of the fired event or null on failure
     * @since 0.3.0
     */
    default OpenShopEvent.@Nullable Result tryOpenDefaultStore(@NotNull BWPlayer player) {
        return tryOpenCustomStore(player, null);
    }

    /**
     * Tries opening a custom store.
     * <p>
     * It fires the {@link OpenShopEvent}, giving plugins the ability to
     * cancel the request or replace the shop system.
     * <p>
     * The player must be in a game, otherwise this method simply returns null.
     *
     * @param player the player
     * @param fileName the file name
     * @return result of the fired event or null on failure
     * @since 0.3.0
     */
    OpenShopEvent.@Nullable Result tryOpenCustomStore(@NotNull BWPlayer player, @Nullable String fileName);
}
