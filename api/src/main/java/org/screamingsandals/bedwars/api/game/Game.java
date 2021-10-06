package org.screamingsandals.bedwars.api.game;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus;
import org.screamingsandals.bedwars.api.ArenaTime;
import org.screamingsandals.bedwars.api.Region;
import org.screamingsandals.bedwars.api.Team;
import org.screamingsandals.bedwars.api.boss.StatusBar;
import org.screamingsandals.bedwars.api.config.ConfigurationContainer;
import org.screamingsandals.bedwars.api.player.BWPlayer;
import org.screamingsandals.bedwars.api.special.SpecialItem;
import org.screamingsandals.bedwars.api.utils.DelayFactory;
import org.screamingsandals.lib.utils.Wrapper;

import java.io.File;
import java.util.List;
import java.util.UUID;


/**
 * @author ScreamingSandals
 * @param <P> BedWars Player
 * @param <T> BedWars Team
 * @param <B> Block
 * @param <W> World
 * @param <L> Location
 * @param <E> Entity
 * @param <C> Component Wrapper
 * @param <S> Game Store
 * @param <G> Item Spawner
 */
@ApiStatus.NonExtendable
public interface Game<P extends BWPlayer, T extends Team<?, ?, ?, ?, ?>, B extends Wrapper, W extends Wrapper, L extends Wrapper, E extends Wrapper, C extends Wrapper, S extends GameStore<?, ?, ?>, G extends ItemSpawner<?, ?, ?>> {
    /**
     *
     * @return arena's unique id
     */
    UUID getUuid();

    /**
     * @return Arena name
     */
	String getName();

    /**
     *
     * @return display name of the arena or null if there's no display name
     */
    @Nullable
    String getDisplayName();

    /**
     * @return GameStatus of the arena
     */
	GameStatus getStatus();

    /**
     *
     */
	void start();

    /**
     *
     */
	void stop();

    /**
     * @return true if GameStatus is different than DISABLED
     */
    default boolean isActivated() {
        return getStatus() != GameStatus.DISABLED;
    }

    // PLAYER MANAGEMENT

    /**
     * @param player
     */
	void joinToGame(P player);

    /**
     * @param player
     */
	void leaveFromGame(P player);

    /**
     * @param player
     * @param team
     */
	void selectPlayerTeam(P player, T team);

    /**
     * @param player
     */
	void selectPlayerRandomTeam(P player);

    /**
     * @return defined world of the game
     */
	W getGameWorld();

    /**
     * @return
     */
	L getPos1();

    /**
     * @return
     */
	L getPos2();

    /**
     * @return
     */
	L getSpectatorSpawn();

    /**
     * @return configured time of the game
     */
	int getGameTime();

    /**
     * @return configured minimal players to start the game
     */
	int getMinPlayers();

    /**
     * @return configured maximal players of the arena
     */
	int getMaxPlayers();

    /**
     * @return players in game
     */
	int countConnectedPlayers();

    /**
     * @return list of players in game
     */
	List<P> getConnectedPlayers();

    /**
     * @return list of game stores
     */
	List<S> getGameStores();

    /**
     * @return
     */
	int countGameStores();

    /**
     * @return Team instance from the name
     */
    T getTeamFromName(String name);

    /**
     * @return
     */
	List<T> getAvailableTeams();

    /**
     * @return
     */
	int countAvailableTeams();

    /**
     * @return
     */
	List<T> getActiveTeams();

    /**
     * @return
     */
	int countActiveTeams();

    /**
     * @param player
     * @return
     */
	T getTeamOfPlayer(P player);

    /**
     * @param player
     * @return
     */
	boolean isPlayerInAnyTeam(P player);

    boolean isTeamActive(T team);

    /**
     * @param player
     * @param team
     * @return
     */
	boolean isPlayerInTeam(P player, T team);

    /**
     * @param location
     * @return
     */
	boolean isLocationInArena(L location);

    /**
     * @param location
     * @return
     */
	boolean isBlockAddedDuringGame(Object location);

    /**
     * @return
     */
	List<SpecialItem<?,?,?>> getActiveSpecialItems();

    /**
     * @param type
     * @return
     */
	<I extends SpecialItem<?,?,?>> List<I> getActiveSpecialItems(Class<I> type);

    /**
     * @param team
     * @return
     */
	List<SpecialItem<?,?,?>> getActiveSpecialItemsOfTeam(T team);

    /**
     * @param team
     * @param type
     * @return
     */
    <I extends SpecialItem<?,?,?>> List<I> getActiveSpecialItemsOfTeam(T team, Class<I> type);

    /**
     * @param team
     * @return
     */
	SpecialItem<?,?,?> getFirstActiveSpecialItemOfTeam(T team);

    /**
     * @param team
     * @param type
     * @return
     */
    <I extends SpecialItem<?,?,?>> I getFirstActiveSpecialItemOfTeam(T team, Class<I> type);

    /**
     * @param player
     * @return
     */
	List<SpecialItem<?,?,?>> getActiveSpecialItemsOfPlayer(P player);

    /**
     * @param player
     * @param type
     * @return
     */
    <I extends SpecialItem<?,?,?>> List<I> getActiveSpecialItemsOfPlayer(P player, Class<I> type);

    /**
     * @param player
     * @return
     */
	SpecialItem<?,?,?> getFirstActiveSpecialItemOfPlayer(P player);

    /**
     * @param player
     * @param type
     * @return
     */
    <I extends SpecialItem<?,?,?>> I getFirstActiveSpecialItemOfPlayer(P player, Class<I> type);

    /**
     * @return
     */
    List<DelayFactory> getActiveDelays();

    /**
     * @param player
     * @return
     */
    List<DelayFactory> getActiveDelaysOfPlayer(P player);

    /**
     * @param player
     * @param specialItem
     * @return
     */
    DelayFactory getActiveDelay(P player, Class<? extends SpecialItem<?,?,?>> specialItem);

    /**
     * @param delayFactory
     */
    void registerDelay(DelayFactory delayFactory);

    /**
     * @param delayFactory
     */
    void unregisterDelay(DelayFactory delayFactory);

    /**
     * @param player
     * @param specialItem
     * @return
     */
    boolean isDelayActive(P player, Class<? extends SpecialItem<?,?,?>> specialItem);

    /**
     * @param item
     */
	void registerSpecialItem(SpecialItem<?,?,?> item);

    /**
     * @param item
     */
	void unregisterSpecialItem(SpecialItem<?,?,?> item);

    /**
     * @param item
     * @return
     */
	boolean isRegisteredSpecialItem(SpecialItem<?,?,?> item);

    /**
     * @return
     */
	List<G> getItemSpawners();

    /**
     * @return
     */
	Region<B> getRegion();

    /**
     * @return
     */
	StatusBar<?> getStatusBar();

    // LOBBY

    /**
     * @return
     */
	W getLobbyWorld();

    /**
     * @return
     */
	L getLobbySpawn();

    /**
     * @return
     */
	int getLobbyCountdown();

    /**
     * @return
     */
	int countTeamChests();

    /**
     * @param team
     * @return
     */
	int countTeamChests(T team);

    /**
     * @param location
     * @return
     */
	T getTeamOfChest(L location);

    /**
     * @param entity
     * @return
     */
	boolean isEntityShop(E entity);

    /**
     * @return
     */
	boolean getBungeeEnabled();

    /**
     * @return
     */
	ArenaTime getArenaTime();

    /**
     * @return
     */
	Wrapper getArenaWeather();

    /**
     * @return
     */
	Wrapper getLobbyBossBarColor();

    /**
     * @return
     */
	Wrapper getGameBossBarColor();

    /**
     * @return
     */
    boolean isProtectionActive(P player);

    int getPostGameWaiting();

    default boolean hasCustomPrefix() {
        return getCustomPrefix() != null;
    }

    String getCustomPrefix();

    /**
     * Returns configuration container for this game
     *
     * @return game's configuration container
     * @since 0.3.0
     */
    ConfigurationContainer getConfigurationContainer();

    /**
     * Checks if game is in edit mode
     *
     * @return true if game is in edit mode
     * @since 0.3.0
     */
    boolean isInEditMode();

    /**
     * This methods allows you to save the arena to config (useful when using custom config options)
     *
     * @since 0.3.0
     */
    void saveToConfig();

    /**
     * Gets file with this game
     *
     * @since 0.3.0
     * @return file where game is saved
     */
    File getFile();

    /**
     * @since 0.3.0
     * @return
     */
    C getCustomPrefixComponent();

    C getDisplayNameComponent();

    /**
     * @since 0.3.0
     * @return
     */
    @Nullable L getLobbyPos1();

    /**
     * @since 0.3.0
     * @return
     */
    @Nullable L getLobbyPos2();
}
