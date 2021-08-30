package org.screamingsandals.bedwars.player;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.screamingsandals.bedwars.BedWarsPlugin;
import org.screamingsandals.bedwars.api.player.BWPlayer;
import org.screamingsandals.bedwars.commands.BedWarsPermission;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.lib.debug.Debug;
import org.screamingsandals.bedwars.utils.BungeeUtils;
import org.screamingsandals.bedwars.lib.nms.entity.PlayerUtils;
import org.screamingsandals.lib.material.Item;
import org.screamingsandals.lib.player.PlayerWrapper;
import org.screamingsandals.lib.player.gamemode.GameModeHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BedWarsPlayer extends PlayerWrapper implements BWPlayer {
    private GameImpl game = null;
    private String latestGame = null;
    private StoredInventory oldInventory = new StoredInventory();
    private List<Item> permaItemsPurchased = new ArrayList<>();
    private List<Player> hiddenPlayers = new ArrayList<>();
    @Getter
    @Setter
    private Item[] armorContents = null;

    public boolean isSpectator = false;
    public boolean isTeleportingFromGame_justForInventoryPlugins = false;
    public boolean mainLobbyUsed = false;

    public BedWarsPlayer(String name, UUID uuid) {
        super(name, uuid);
    }

    public void changeGame(GameImpl game) {
        if (this.game != null && game == null) {
            this.game.internalLeavePlayer(this);
            this.game = null;
            this.isSpectator = false;
            this.clean();
            if (GameImpl.isBungeeEnabled()) {
                BungeeUtils.movePlayerToBungeeServer(as(Player.class), BedWarsPlugin.isDisabling());
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

    public GameImpl getGame() {
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

    public List<Item> getPermaItemsPurchased() {
        return permaItemsPurchased;
    }

    private void resetPermaItems() {
        this.permaItemsPurchased.clear();
    }

    public void addPermaItem(Item stack) {
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
        isTeleportingFromGame_justForInventoryPlugins = true;
        if (!mainLobbyUsed) {
            teleport(oldInventory.leftLocation, this::restoreRest);
        }
        mainLobbyUsed = false;
        restoreRest();
    }

    private void restoreRest() {
        var player = as(Player.class);

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

    // TODO: SLib equivalent
    public void resetLife() {
        var player = as(Player.class);
        var entity = asEntity();

        player.setAllowFlight(false);
        player.setFlying(false);
        this.setExp(0.0F);
        this.setLevel(0);
        player.setSneaking(false);
        player.setSprinting(false);
        player.setFoodLevel(20);
        player.setSaturation(10);
        player.setExhaustion(0);
        player.setMaxHealth(20D);
        entity.setHealth(20D);
        entity.setFireTicks(0);
        entity.setFallDistance(0);
        this.setGameMode(GameModeHolder.of("survival"));

        if (player.isInsideVehicle()) {
            player.leaveVehicle();
        }

        for (PotionEffect e : player.getActivePotionEffects()) {
            player.removePotionEffect(e.getType());
        }
    }

    // TODO: SLib equivalent
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

    @Deprecated
    public boolean teleport(Location location) {
    	return PlayerUtils.teleportPlayer(as(Player.class), location);
    }

    @Deprecated
    public boolean teleport(Location location, Runnable runnable) {
        return PlayerUtils.teleportPlayer(as(Player.class), location, runnable);
    }

    // TODO: SLib equivalent
    public void hidePlayer(Player player) {
        var thisPlayer = as(Player.class);
        if (!hiddenPlayers.contains(player) && !player.equals(thisPlayer)) {
            hiddenPlayers.add(player);
            try {
                thisPlayer.hidePlayer(BedWarsPlugin.getInstance().getPluginDescription().as(JavaPlugin.class), player);
            } catch (Throwable t) {
                thisPlayer.hidePlayer(player);
            }
        }
    }

    // TODO: SLib equivalent
    public void showPlayer(Player player) {
        var thisPlayer = as(Player.class);
        if (hiddenPlayers.contains(player) && !player.equals(thisPlayer)) {
            hiddenPlayers.remove(player);
            try {
                thisPlayer.showPlayer(BedWarsPlugin.getInstance().getPluginDescription().as(JavaPlugin.class), player);
            } catch (Throwable t) {
                thisPlayer.showPlayer(player);
            }
        }

    }
}
