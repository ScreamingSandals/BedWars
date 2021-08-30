package org.screamingsandals.bedwars.game;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.screamingsandals.bedwars.api.RunningTeam;
import org.screamingsandals.bedwars.api.TeamColor;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.lib.hologram.Hologram;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.player.PlayerWrapper;
import org.screamingsandals.lib.utils.AdventureHelper;
import org.screamingsandals.lib.utils.reflect.Reflect;
import org.screamingsandals.lib.world.BlockHolder;
import org.screamingsandals.lib.world.LocationHolder;

import java.util.ArrayList;
import java.util.List;

public class CurrentTeam implements RunningTeam<PlayerWrapper, LocationHolder, BlockHolder> {
    public final Team teamInfo;
    public final List<BedWarsPlayer> players = new ArrayList<>();
    private org.bukkit.scoreboard.Team scoreboardTeam;
    private Inventory chestInventory;
    private List<BlockHolder> chests = new ArrayList<>();
    private Game game;
    private Hologram holo;
    private Hologram protectHolo;

    public boolean isBed = true;

    public CurrentTeam(Team team, Game game) {
        this.teamInfo = team;
        this.game = game;
        final var message = Message.of(LangKeys.SPECIALS_TEAM_CHEST_NAME).prefixOrDefault(game.getCustomPrefixComponent()).asComponent();
        if (Reflect.hasMethod(Bukkit.getServer(), "createInventory", InventoryHolder.class, InventoryType.class, Component.class)) {
            this.chestInventory = Bukkit.createInventory(null, InventoryType.ENDER_CHEST, message);
        }
        else {
            this.chestInventory = Bukkit.createInventory(null, InventoryType.ENDER_CHEST, AdventureHelper.toLegacyNullable(message));
        }
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
    public LocationHolder getTeamSpawn() {
        return teamInfo.spawn;
    }

    @Override
    public LocationHolder getTargetBlock() {
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
    public List<PlayerWrapper> getConnectedPlayers() {
        return new ArrayList<>(players);
    }

    @Override
    public boolean isPlayerInTeam(PlayerWrapper player) {
        for (BedWarsPlayer gamePlayer : players) {
            if (gamePlayer.getUuid().equals(player.getUuid())) {
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
    public void addTeamChest(LocationHolder location) {
        addTeamChestBlock(location.getBlock());
    }

    @Override
    public void addTeamChestBlock(BlockHolder block) {
        if (!chests.contains(block)) {
            chests.add(block);
        }
    }

    @Override
    public void removeTeamChest(LocationHolder location) {
        removeTeamChestBlock(location.getBlock());
    }

    @Override
    public void removeTeamChestBlock(BlockHolder block) {
        chests.remove(block);
    }

    @Override
    public boolean isTeamChestRegistered(LocationHolder location) {
        return isTeamChestBlockRegistered(location.getBlock());
    }

    @Override
    public boolean isTeamChestBlockRegistered(BlockHolder block) {
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
}
