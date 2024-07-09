/*
 * Copyright (C) 2023 ScreamingSandals
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

package org.screamingsandals.bedwars.game;

import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.screamingsandals.bedwars.api.RunningTeam;
import org.screamingsandals.bedwars.api.TeamColor;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.lib.nms.holograms.Hologram;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.screamingsandals.bedwars.lib.lang.I.i18nc;

public class CurrentTeam implements RunningTeam {
    public final Team teamInfo;
    public final List<GamePlayer> players = new ArrayList<>();
    public final List<Member> teamMembers = new ArrayList<>();
    private org.bukkit.scoreboard.Team scoreboardTeam;
    private Inventory chestInventory;
    private List<Block> chests = new ArrayList<>();
    private Game game;
    private Hologram holo;
    private Hologram protectHolo;

    public boolean isBed = true;
    public boolean forced = false;

    public CurrentTeam(Team team, Game game) {
        this.teamInfo = team;
        this.game = game;
        this.chestInventory = Bukkit.createInventory(null, InventoryType.ENDER_CHEST, i18nc("team_chest", game.getCustomPrefix()));
    }

    public boolean isDead() {
        return players.isEmpty();
    }

    public boolean isAlive() {
        return !players.isEmpty();
    }

    public org.bukkit.scoreboard.Team getScoreboardTeam() {
        return scoreboardTeam;
    }

    public void setScoreboardTeam(org.bukkit.scoreboard.Team scoreboardTeam) {
        this.scoreboardTeam = scoreboardTeam;
    }

    public void setBedHolo(Hologram holo) {
        this.holo = holo;
    }

    public Hologram getBedHolo() {
        return this.holo;
    }

    public boolean hasBedHolo() {
        return this.holo != null;
    }

    public void setProtectHolo(Hologram protectHolo) {
        this.protectHolo = protectHolo;
    }

    public Hologram getProtectHolo() {
        return this.protectHolo;
    }

    public boolean hasProtectHolo() {
        return this.protectHolo != null;
    }

    @Override
    public String getName() {
        return teamInfo.name;
    }

    @Override
    public TeamColor getColor() {
        return teamInfo.color.toApiColor();
    }

    @Override
    public boolean isNewColor() {
        return teamInfo.newColor;
    }

    @Override
    public Location getTeamSpawn() {
        return teamInfo.spawn;
    }

    @Override
    public Location getTargetBlock() {
        return teamInfo.bed;
    }

    @Override
    public int getMaxPlayers() {
        return teamInfo.maxPlayers;
    }

    @Override
    public int countConnectedPlayers() {
        return players.size();
    }

    @Override
    public List<Player> getConnectedPlayers() {
        List<Player> playerList = new ArrayList<>();
        for (GamePlayer gamePlayer : players) {
            playerList.add(gamePlayer.player);
        }
        return playerList;
    }

    @Override
    public boolean isPlayerInTeam(Player player) {
        for (GamePlayer gamePlayer : players) {
            if (gamePlayer.player.equals(player)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isTargetBlockExists() {
        return isBed;
    }

    @Override
    public void addTeamChest(Location location) {
        addTeamChest(location.getBlock());
    }

    @Override
    public void addTeamChest(Block block) {
        if (!chests.contains(block)) {
            chests.add(block);
        }
    }

    @Override
    public void removeTeamChest(Location location) {
        removeTeamChest(location.getBlock());
    }

    @Override
    public void removeTeamChest(Block block) {
        if (chests.contains(block)) {
            chests.remove(block);
        }
    }

    @Override
    public boolean isTeamChestRegistered(Location location) {
        return isTeamChestRegistered(location.getBlock());
    }

    @Override
    public boolean isTeamChestRegistered(Block block) {
        return chests.contains(block);
    }

    @Override
    public Inventory getTeamChestInventory() {
        return chestInventory;
    }

    @Override
    public Game getGame() {
        return game;
    }

    @Override
    public int countTeamChests() {
        return chests.size();
    }

    @Data
    public static class Member {
        private final UUID uuid;
        private final String name;
    }
}
