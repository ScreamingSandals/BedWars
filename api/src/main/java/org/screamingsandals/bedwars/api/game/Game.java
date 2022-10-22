/*
 * Copyright (C) 2022 ScreamingSandals
 *
 * This file is part of Screaming BedWars.
 *
 * Screaming BedWars is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Screaming BedWars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Screaming BedWars. If not, see <https://www.gnu.org/licenses/>.
 */

package org.screamingsandals.bedwars.api.game;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus;
import org.screamingsandals.bedwars.api.Region;
import org.screamingsandals.bedwars.api.Team;
import org.screamingsandals.bedwars.api.boss.StatusBar;
import org.screamingsandals.bedwars.api.config.GameConfigurationContainer;
import org.screamingsandals.bedwars.api.player.BWPlayer;
import org.screamingsandals.bedwars.api.special.SpecialItem;
import org.screamingsandals.bedwars.api.utils.DelayFactory;
import org.screamingsandals.bedwars.api.variants.Variant;
import org.screamingsandals.lib.utils.Wrapper;

import java.io.File;
import java.util.List;
import java.util.UUID;


/**
 * @author ScreamingSandals
 */
@ApiStatus.NonExtendable
public interface Game {
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
	void joinToGame(BWPlayer player);

    /**
     * @param player
     */
	void leaveFromGame(BWPlayer player);

    /**
     * @param player
     * @param team
     */
	void selectPlayerTeam(BWPlayer player, Team team);

    /**
     * @param player
     */
	void selectPlayerRandomTeam(BWPlayer player);

    /**
     * @return defined world of the game
     */
	Wrapper getGameWorld();

    /**
     * @return
     */
	Wrapper getPos1();

    /**
     * @return
     */
	Wrapper getPos2();

    /**
     * @return
     */
	Wrapper getSpectatorSpawn();

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
	List<? extends BWPlayer> getConnectedPlayers();

    /**
     * @return list of game stores
     */
	List<? extends GameStore> getGameStores();

    /**
     * @return
     */
	int countGameStores();

    /**
     * @return Team instance from the name
     */
    Team getTeamFromName(String name);

    /**
     * @return
     */
	List<? extends Team> getAvailableTeams();

    /**
     * @return
     */
	int countAvailableTeams();

    /**
     * @return
     */
	List<? extends Team> getActiveTeams();

    /**
     * @return
     */
	int countActiveTeams();

    /**
     * @param player
     * @return
     */
	Team getTeamOfPlayer(BWPlayer player);

    /**
     * @param player
     * @return
     */
	boolean isPlayerInAnyTeam(BWPlayer player);

    boolean isTeamActive(Team team);

    /**
     * @param player
     * @param team
     * @return
     */
	boolean isPlayerInTeam(BWPlayer player, Team team);

    /**
     * @param location
     * @return
     */
	boolean isLocationInArena(Object location);

    /**
     * @param location
     * @return
     */
	boolean isBlockAddedDuringGame(Object location);

    /**
     * @return
     */
	List<? extends SpecialItem> getActiveSpecialItems();

    /**
     * @param type
     * @return
     */
	<I extends SpecialItem> List<I> getActiveSpecialItems(Class<I> type);

    /**
     * @param team
     * @return
     */
	List<SpecialItem> getActiveSpecialItemsOfTeam(Team team);

    /**
     * @param team
     * @param type
     * @return
     */
    <I extends SpecialItem> List<I> getActiveSpecialItemsOfTeam(Team team, Class<I> type);

    /**
     * @param team
     * @return
     */
	SpecialItem getFirstActiveSpecialItemOfTeam(Team team);

    /**
     * @param team
     * @param type
     * @return
     */
    <I extends SpecialItem> I getFirstActiveSpecialItemOfTeam(Team team, Class<I> type);

    /**
     * @param player
     * @return
     */
	List<SpecialItem> getActiveSpecialItemsOfPlayer(BWPlayer player);

    /**
     * @param player
     * @param type
     * @return
     */
    <I extends SpecialItem> List<I> getActiveSpecialItemsOfPlayer(BWPlayer player, Class<I> type);

    /**
     * @param player
     * @return
     */
	SpecialItem getFirstActiveSpecialItemOfPlayer(BWPlayer player);

    /**
     * @param player
     * @param type
     * @return
     */
    <I extends SpecialItem> I getFirstActiveSpecialItemOfPlayer(BWPlayer player, Class<I> type);

    /**
     * @return
     */
    List<DelayFactory> getActiveDelays();

    /**
     * @param player
     * @return
     */
    List<DelayFactory> getActiveDelaysOfPlayer(BWPlayer player);

    /**
     * @param player
     * @param specialItem
     * @return
     */
    DelayFactory getActiveDelay(BWPlayer player, Class<? extends SpecialItem> specialItem);

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
    boolean isDelayActive(BWPlayer player, Class<? extends SpecialItem> specialItem);

    /**
     * @param item
     */
	void registerSpecialItem(SpecialItem item);

    /**
     * @param item
     */
	void unregisterSpecialItem(SpecialItem item);

    /**
     * @param item
     * @return
     */
	boolean isRegisteredSpecialItem(SpecialItem item);

    /**
     * @return
     */
	List<? extends ItemSpawner> getItemSpawners();

    /**
     * @return
     */
	Region getRegion();

    /**
     * @return
     */
	StatusBar getStatusBar();

    // LOBBY

    /**
     * @return
     */
	Wrapper getLobbyWorld();

    /**
     * @return
     */
	Wrapper getLobbySpawn();

    /**
     * @return
     */
	int getLobbyCountdown();

    /**
     * @return
     */
	int countTeamChests();

    /**
     * @param location
     * @return
     */
	Team getTeamOfChest(Object location);

    /**
     * @param entity
     * @return
     */
	boolean isEntityShop(Object entity);

    /**
     * @return
     */
	boolean getBungeeEnabled();

    /**
     * @return
     */
    @Nullable
	Wrapper getArenaWeather();

    /**
     * @return
     */
    boolean isProtectionActive(BWPlayer player);

    int getPostGameWaiting();

    @Nullable
    Variant getGameVariant();

    /**
     * Returns configuration container for this game
     *
     * @return game's configuration container
     * @since 0.3.0
     */
    GameConfigurationContainer getConfigurationContainer();

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
    Wrapper getCustomPrefixComponent();

    Wrapper getDisplayNameComponent();

    /**
     * @since 0.3.0
     * @return
     */
    @Nullable Wrapper getLobbyPos1();

    /**
     * @since 0.3.0
     * @return
     */
    @Nullable Wrapper getLobbyPos2();

    /**
     * @since 0.3.0
     * @return
     */
    double getFee();

    /**
     * @since 0.3.0
     * @return
     */
    boolean invalidateTarget(Team team);
}
