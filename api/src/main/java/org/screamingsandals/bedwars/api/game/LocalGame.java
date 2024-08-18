/*
 * Copyright (C) 2024 ScreamingSandals
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
import org.screamingsandals.lib.api.Wrapper;
import org.screamingsandals.lib.api.types.ComponentHolder;
import org.screamingsandals.lib.api.types.server.EntityHolder;
import org.screamingsandals.lib.api.types.server.LocationHolder;
import org.screamingsandals.lib.api.types.server.WorldHolder;

import java.io.File;
import java.util.List;


/**
 * @author ScreamingSandals
 * @since 0.3.0
 */
@ApiStatus.NonExtendable
public interface LocalGame extends Game {
    /**
     *
     * @return display name of the arena or null if there's no display name
     */
    @Nullable
    String getDisplayName();

    /**
     *
     */
	void start();

    /**
     *
     */
	void stop();

    // PLAYER MANAGEMENT

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
	WorldHolder getGameWorld();

    /**
     * @return
     */
	LocationHolder getPos1();

    /**
     * @return
     */
	LocationHolder getPos2();

    /**
     * @return
     */
	LocationHolder getSpectatorSpawn();

    /**
     * @return configured time of the game
     */
	int getGameTime();

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
	List<? extends Team> getActiveTeams();

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
	boolean isLocationInArena(LocationHolder location);

    /**
     * @param location
     * @return
     */
	boolean isBlockAddedDuringGame(LocationHolder location);

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
	WorldHolder getLobbyWorld();

    /**
     * @return
     */
	LocationHolder getLobbySpawn();

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
	Team getTeamOfChest(LocationHolder location);

    /**
     * @param entity
     * @return
     */
	boolean isEntityShop(EntityHolder entity);

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
    ComponentHolder getCustomPrefixComponent();

    /**
     * @since 0.3.0
     * @return
     */
    @Nullable LocationHolder getLobbyPos1();

    /**
     * @since 0.3.0
     * @return
     */
    @Nullable LocationHolder getLobbyPos2();

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
