package org.screamingsandals.bedwars.player;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.screamingsandals.bedwars.BedWarsPlugin;
import org.screamingsandals.bedwars.api.player.BWPlayer;
import org.screamingsandals.bedwars.commands.BedWarsPermission;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.lib.debug.Debug;
import org.screamingsandals.bedwars.utils.BungeeUtils;
import org.screamingsandals.bedwars.lib.nms.entity.PlayerUtils;
import org.screamingsandals.lib.attribute.AttributeTypeHolder;
import org.screamingsandals.lib.item.Item;
import org.screamingsandals.lib.player.PlayerWrapper;
import org.screamingsandals.lib.player.gamemode.GameModeHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BedWarsPlayer extends PlayerWrapper implements BWPlayer {
    @Getter
    private GameImpl game;
    @Getter
    private String latestGameName;
    private final StoredInventory oldInventory = new StoredInventory();
    @Getter
    private final List<Item> permanentItemsPurchased = new ArrayList<>();
    private final List<Player> hiddenPlayers = new ArrayList<>();
    @Getter
    @Setter
    private Item[] armorContents;

    @Getter
    @Setter
    private boolean spectator;
    public boolean isTeleportingFromGame_justForInventoryPlugins = false;
    public boolean mainLobbyUsed = false;

    public BedWarsPlayer(String name, UUID uuid) {
        super(name, uuid);
    }

    public void changeGame(GameImpl game) {
        if (this.game != null && game == null) {
            this.game.internalLeavePlayer(this);
            this.game = null;
            this.setSpectator(false);
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
        oldInventory.setEffects(asEntity().getActivePotionEffects());
        oldInventory.setMode(getGameMode());
        oldInventory.setLeftLocation(getLocation());
        oldInventory.setLevel(getLevel());
        oldInventory.setListName(getPlayerListName());
        oldInventory.setDisplayName(getDisplayName());
        oldInventory.setFoodLevel(asEntity().getFoodLevel());
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
        var entity = asEntity();

        getPlayerInventory().setContents(oldInventory.getInventory());
        getPlayerInventory().setArmorContents(oldInventory.getArmor());

        setLevel(oldInventory.getLevel());
        setExp(oldInventory.getXp());
        entity.setFoodLevel(oldInventory.getFoodLevel());

        for (var e : entity.getActivePotionEffects()) {
            entity.removePotionEffect(e);
        }

        entity.addPotionEffects(oldInventory.getEffects());

        setPlayerListName(oldInventory.getListName());
        setDisplayName(oldInventory.getDisplayName());

        setGameMode(oldInventory.getMode());

        setAllowFlight(oldInventory.getMode().is("creative", "spectator"));

        forceUpdateInventory();
        resetPlayerTime();
        setPlayerWeather(null);
    }

    public void resetLife() {
        var entity = asEntity();

        setAllowFlight(false);
        setFlying(false);
        setExp(0.0F);
        setLevel(0);
        setSneaking(false);
        setSprinting(false);
        entity.setFoodLevel(20);
        entity.setSaturation(10);
        entity.setExhaustion(0);
        entity.getAttribute(AttributeTypeHolder.of("minecraft:generic.max_health")).ifPresent(attributeHolder -> attributeHolder.setBaseValue(20));
        entity.setHealth(20D);
        entity.setFireTicks(0);
        entity.setFallDistance(0);
        setGameMode(GameModeHolder.of("survival"));

        if (entity.isInsideVehicle()) {
            entity.leaveVehicle();
        }

        for (var e : entity.getActivePotionEffects()) {
            entity.removePotionEffect(e);
        }
    }

    public void invClean() {
        Debug.info("Cleaning inventory of: " + getName());
        var inv = getPlayerInventory();
        inv.setArmorContents(new Item[4]);
        inv.setContents(new Item[]{});
        forceUpdateInventory();
    }

    public void clean() {
        invClean();
        resetLife();
        this.permanentItemsPurchased.clear();
        this.armorContents = null;
        new ArrayList<>(this.hiddenPlayers).forEach(this::showPlayer);
    }

    @Deprecated
    public boolean teleport(Location location) {
    	return PlayerUtils.teleportPlayer(as(Player.class), location);
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
