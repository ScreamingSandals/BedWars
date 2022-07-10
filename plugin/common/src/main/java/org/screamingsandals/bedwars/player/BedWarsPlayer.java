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

package org.screamingsandals.bedwars.player;

import lombok.Getter;
import lombok.Setter;
import org.screamingsandals.bedwars.BedWarsPlugin;
import org.screamingsandals.bedwars.api.player.BWPlayer;
import org.screamingsandals.bedwars.commands.BedWarsPermission;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.lib.debug.Debug;
import org.screamingsandals.bedwars.utils.BungeeUtils;
import org.screamingsandals.lib.attribute.AttributeTypeHolder;
import org.screamingsandals.lib.item.Item;
import org.screamingsandals.lib.player.ExtendablePlayerWrapper;
import org.screamingsandals.lib.player.PlayerWrapper;
import org.screamingsandals.lib.player.gamemode.GameModeHolder;

import java.util.ArrayList;
import java.util.List;

public class BedWarsPlayer extends ExtendablePlayerWrapper implements BWPlayer {
    @Getter
    private GameImpl game;
    @Getter
    private String latestGameName;
    private final StoredInventory oldInventory = new StoredInventory();
    @Getter
    private final List<Item> permanentItemsPurchased = new ArrayList<>();
    private final List<PlayerWrapper> hiddenPlayers = new ArrayList<>();
    @Getter
    @Setter
    private Item[] armorContents;

    @Getter
    @Setter
    private boolean spectator;
    public boolean isTeleportingFromGame_justForInventoryPlugins = false;
    public boolean mainLobbyUsed = false;

    public BedWarsPlayer(PlayerWrapper player) {
        super(player);
    }

    public void changeGame(GameImpl game) {
        if (this.game != null && game == null) {
            this.game.internalLeavePlayer(this);
            this.game = null;
            this.setSpectator(false);
            this.clean();
            if (GameImpl.isBungeeEnabled()) {
                BungeeUtils.movePlayerToBungeeServer(this, BedWarsPlugin.isDisabling());
            } else {
                this.restoreInv();
            }
        } else if (this.game == null && game != null) {
            this.storeInv();
            this.clean();
            this.game = game;
            this.setSpectator(false);
            this.mainLobbyUsed = false;
            this.game.internalJoinPlayer(this);
            if (this.game != null) {
                this.latestGameName = this.game.getName();
            }
        } else if (this.game != null) {
            this.game.internalLeavePlayer(this);
            this.game = game;
            this.setSpectator(false);
            this.clean();
            this.mainLobbyUsed = false;
            this.game.internalJoinPlayer(this);
            if (this.game != null) {
                this.latestGameName = this.game.getName();
            }
        }
    }

    public boolean isInGame() {
        return game != null;
    }

    public boolean canJoinFullGame() {
        return hasPermission(BedWarsPermission.FORCE_JOIN_PERMISSION.asPermission());
    }

    public void addPermanentItem(Item stack) {
        this.permanentItemsPurchased.add(stack);
    }

    public void storeInv() {
        oldInventory.setInventory(getPlayerInventory().getContents());
        oldInventory.setArmor(getPlayerInventory().getArmorContents());
        oldInventory.setXp(getExp());
        oldInventory.setEffects(getActivePotionEffects());
        oldInventory.setMode(getGameMode());
        oldInventory.setLeftLocation(getLocation());
        oldInventory.setLevel(getLevel());
        oldInventory.setListName(getPlayerListName());
        oldInventory.setDisplayName(getDisplayName());
        oldInventory.setFoodLevel(getFoodLevel());
    }

    public void restoreInv() {
        isTeleportingFromGame_justForInventoryPlugins = true;
        if (!mainLobbyUsed) {
            teleport(oldInventory.getLeftLocation(), this::restoreRest);
        } else {
            mainLobbyUsed = false;
            restoreRest();
        }
    }

    private void restoreRest() {
        getPlayerInventory().setContents(oldInventory.getInventory());
        getPlayerInventory().setArmorContents(oldInventory.getArmor());

        setLevel(oldInventory.getLevel());
        setExp(oldInventory.getXp());
        setFoodLevel(oldInventory.getFoodLevel());

        for (var e : getActivePotionEffects()) {
            removePotionEffect(e);
        }

        addPotionEffects(oldInventory.getEffects());

        setPlayerListName(oldInventory.getListName());
        setDisplayName(oldInventory.getDisplayName());

        setGameMode(oldInventory.getMode());

        setAllowFlight(oldInventory.getMode().is("creative", "spectator"));

        forceUpdateInventory();
        resetPlayerTime();
        setPlayerWeather(null);
    }

    public void resetLife() {
        setAllowFlight(false);
        setFlying(false);
        setExp(0.0F);
        setLevel(0);
        setSneaking(false);
        setSprinting(false);
        setFoodLevel(20);
        setSaturation(10);
        setExhaustion(0);
        getAttribute(AttributeTypeHolder.of("minecraft:generic.max_health")).ifPresent(attributeHolder -> attributeHolder.setBaseValue(20));
        setHealth(20D);
        setFireTicks(0);
        setFallDistance(0);
        setGameMode(GameModeHolder.of("survival"));

        if (isInsideVehicle()) {
            leaveVehicle();
        }

        for (var e : getActivePotionEffects()) {
            removePotionEffect(e);
        }
    }

    public void invClean() {
        Debug.info("Cleaning inventory of: " + getName());
        var inv = getPlayerInventory();
        inv.setArmorContents(new Item[4]);
        inv.setContents(new Item[inv.getSize()]);
        forceUpdateInventory();
    }

    public void clean() {
        invClean();
        resetLife();
        this.permanentItemsPurchased.clear();
        this.armorContents = null;
        new ArrayList<>(this.hiddenPlayers).forEach(this::showPlayer);
    }

    public void hidePlayer(PlayerWrapper playerWrapper) {
        if (!hiddenPlayers.contains(playerWrapper) && !equals(playerWrapper)) {
            hiddenPlayers.add(playerWrapper);
            super.hidePlayer(playerWrapper);
        }
    }

    public void showPlayer(PlayerWrapper playerWrapper) {
        if (hiddenPlayers.contains(playerWrapper) && !equals(playerWrapper)) {
            hiddenPlayers.remove(playerWrapper);
            super.showPlayer(playerWrapper);
        }
    }
}
