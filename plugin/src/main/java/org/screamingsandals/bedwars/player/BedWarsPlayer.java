package org.screamingsandals.bedwars.player;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.player.BWPlayer;
import org.screamingsandals.bedwars.commands.BedWarsPermission;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.bedwars.lib.debug.Debug;
import org.screamingsandals.bedwars.utils.BungeeUtils;
import org.screamingsandals.bedwars.lib.nms.entity.PlayerUtils;
import org.screamingsandals.lib.player.PlayerWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BedWarsPlayer extends PlayerWrapper implements BWPlayer {
    private Game game = null;
    private String latestGame = null;
    private StoredInventory oldInventory = new StoredInventory();
    private List<ItemStack> permaItemsPurchased = new ArrayList<>();
    private List<Player> hiddenPlayers = new ArrayList<>();
    private ItemStack[] armorContents = null;

    public boolean isSpectator = false;
    public boolean isTeleportingFromGame_justForInventoryPlugins = false;
    public boolean mainLobbyUsed = false;

    public BedWarsPlayer(String name, UUID uuid) {
        super(name, uuid);
    }

    public void changeGame(Game game) {
        if (this.game != null && game == null) {
            this.game.internalLeavePlayer(this);
            this.game = null;
            this.isSpectator = false;
            this.clean();
            if (Game.isBungeeEnabled()) {
                BungeeUtils.movePlayerToBungeeServer(as(Player.class), Main.isDisabling());
            } else {
                this.restoreInv();
            }
        } else if (this.game == null && game != null) {
            this.storeInv();
            this.clean();
            this.game = game;
            this.isSpectator = false;
            this.mainLobbyUsed = false;
            this.game.internalJoinPlayer(this);
            if (this.game != null) {
                this.latestGame = this.game.getName();
            }
        } else if (this.game != null) {
            this.game.internalLeavePlayer(this);
            this.game = game;
            this.isSpectator = false;
            this.clean();
            this.mainLobbyUsed = false;
            this.game.internalJoinPlayer(this);
            if (this.game != null) {
                this.latestGame = this.game.getName();
            }
        }
    }

    public Game getGame() {
        return game;
    }

    public String getLatestGameName() {
        return this.latestGame;
    }

    public boolean isInGame() {
        return game != null;
    }

    public boolean canJoinFullGame() {
        return hasPermission(BedWarsPermission.FORCE_JOIN_PERMISSION.asPermission());
    }

    public List<ItemStack> getPermaItemsPurchased() {
        return permaItemsPurchased;
    }

    private void resetPermaItems() {
        this.permaItemsPurchased.clear();
    }

    public void addPermaItem(ItemStack stack) {
        this.permaItemsPurchased.add(stack);
    }

    public void storeInv() {
        var player = as(Player.class);

        oldInventory.inventory = player.getInventory().getContents();
        oldInventory.armor = player.getInventory().getArmorContents();
        oldInventory.xp = player.getExp();
        oldInventory.effects = player.getActivePotionEffects();
        oldInventory.mode = player.getGameMode();
        oldInventory.leftLocation = player.getLocation();
        oldInventory.level = player.getLevel();
        oldInventory.listName = player.getPlayerListName();
        oldInventory.displayName = player.getDisplayName();
        oldInventory.foodLevel = player.getFoodLevel();
    }

    public void restoreInv() {
        var player = as(Player.class);

        isTeleportingFromGame_justForInventoryPlugins = true;
        if (!mainLobbyUsed) {
            teleport(oldInventory.leftLocation);
        }
        mainLobbyUsed = false;

        player.getInventory().setContents(oldInventory.inventory);
        player.getInventory().setArmorContents(oldInventory.armor);

        player.addPotionEffects(oldInventory.effects);
        player.setLevel(oldInventory.level);
        player.setExp(oldInventory.xp);
        player.setFoodLevel(oldInventory.foodLevel);

        for (PotionEffect e : player.getActivePotionEffects())
            player.removePotionEffect(e.getType());

        player.addPotionEffects(oldInventory.effects);

        player.setPlayerListName(oldInventory.listName);
        player.setDisplayName(oldInventory.displayName);

        player.setGameMode(oldInventory.mode);

        if (oldInventory.mode == GameMode.CREATIVE)
            player.setAllowFlight(true);
        else
            player.setAllowFlight(false);

        player.updateInventory();
        player.resetPlayerTime();
        player.resetPlayerWeather();
    }

    public void resetLife() {
        var player = as(Player.class);

        player.setAllowFlight(false);
        player.setFlying(false);
        player.setExp(0.0F);
        player.setLevel(0);
        player.setSneaking(false);
        player.setSprinting(false);
        player.setFoodLevel(20);
        player.setSaturation(10);
        player.setExhaustion(0);
        player.setMaxHealth(20D);
        player.setHealth(player.getMaxHealth());
        player.setFireTicks(0);
        player.setFallDistance(0);
        player.setGameMode(GameMode.SURVIVAL);

        if (player.isInsideVehicle()) {
            player.leaveVehicle();
        }

        for (PotionEffect e : player.getActivePotionEffects()) {
            player.removePotionEffect(e.getType());
        }
    }

    public void invClean() {
        var player = as(Player.class);
        Debug.info("Cleaning inventory of: " + player.getName());
        PlayerInventory inv = player.getInventory();
        inv.setArmorContents(new ItemStack[4]);
        inv.setContents(new ItemStack[]{});
        player.updateInventory();
    }

    public void clean() {
        invClean();
        resetLife();
        resetPermaItems();
        this.armorContents = null;
        new ArrayList<>(this.hiddenPlayers).forEach(this::showPlayer);
    }

    public boolean teleport(Location location) {
    	return PlayerUtils.teleportPlayer(as(Player.class), location);
    }

    public boolean teleport(Location location, Runnable runnable) {
        return PlayerUtils.teleportPlayer(as(Player.class), location, runnable);
    }

    public void hidePlayer(Player player) {
        var thisPlayer = as(Player.class);
        if (!hiddenPlayers.contains(player) && !player.equals(thisPlayer)) {
            hiddenPlayers.add(player);
            try {
                thisPlayer.hidePlayer(Main.getInstance().getPluginDescription().as(JavaPlugin.class), player);
            } catch (Throwable t) {
                thisPlayer.hidePlayer(player);
            }
        }
    }

    public void showPlayer(Player player) {
        var thisPlayer = as(Player.class);
        if (hiddenPlayers.contains(player) && !player.equals(thisPlayer)) {
            hiddenPlayers.remove(player);
            try {
                thisPlayer.showPlayer(Main.getInstance().getPluginDescription().as(JavaPlugin.class), player);
            } catch (Throwable t) {
                thisPlayer.showPlayer(player);
            }
        }

    }

    public void setGameArmorContents(ItemStack[] armorContents) {
        this.armorContents = armorContents;
    }

    public ItemStack[] getGameArmorContents() {
        return armorContents;
    }
}
