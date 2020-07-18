package org.screamingsandals.bedwars.special.listener;

import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.APIUtils;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.api.events.BedwarsApplyPropertyToBoughtItem;
import org.screamingsandals.bedwars.api.special.SpecialItem;
import org.screamingsandals.bedwars.game.GamePlayer;
import org.screamingsandals.bedwars.special.TNTSheep;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class TNTSheepListener implements Listener {
    private static final String TNT_SHEEP_PREFIX = "Module:TNTSheep:";

    @EventHandler
    public void onTNTSheepRegistered(BedwarsApplyPropertyToBoughtItem event) {
        if (event.getPropertyName().equalsIgnoreCase("tntsheep")) {
            ItemStack stack = event.getStack();
            APIUtils.hashIntoInvisibleString(stack, applyProperty(event));
        }

    }

    @EventHandler
    public void onTNTSheepUsed(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!Main.isPlayerInGame(player)) {
            return;
        }

        GamePlayer gamePlayer = Main.getPlayerGameProfile(player);
        Game game = gamePlayer.getGame();

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (game.getStatus() == GameStatus.RUNNING && !gamePlayer.isSpectator && event.getItem() != null) {
                ItemStack stack = event.getItem();
                String unhidden = APIUtils.unhashFromInvisibleStringStartsWith(stack, TNT_SHEEP_PREFIX);

                if (unhidden != null) {
                    event.setCancelled(true);

                    double speed = Double.parseDouble(unhidden.split(":")[2]);
                    double follow = Double.parseDouble(unhidden.split(":")[3]);
                    double maxTargetDistance = Double.parseDouble(unhidden.split(":")[4]);
                    int explosionTime = Integer.parseInt(unhidden.split(":")[5]);
                    Location startLocation;

                    if (event.getClickedBlock() == null) {
                        startLocation = player.getLocation();
                    } else {
                        startLocation = event.getClickedBlock().getRelative(event.getBlockFace()).getLocation();
                    }
                    TNTSheep sheep = new TNTSheep(game, player, game.getTeamOfPlayer(player),
                            startLocation, stack, speed, follow, maxTargetDistance, explosionTime);

                    sheep.spawn();
                }

            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTNTSheepDamage(EntityDamageByEntityEvent event) {
        if (event.isCancelled() || event.getCause().equals(DamageCause.CUSTOM)
                || event.getCause().equals(DamageCause.VOID)
                || event.getCause().equals(DamageCause.FALL)) {
            return;
        }

        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (Main.isPlayerInGame(player)) {
                GamePlayer gamePlayer = Main.getPlayerGameProfile(player);
                Game game = gamePlayer.getGame();
                if (event.getDamager() instanceof TNTPrimed && !game.getOriginalOrInheritedFriendlyfire()) {
                    TNTPrimed tnt = (TNTPrimed) event.getDamager();
                    List<SpecialItem> sheeps = game.getActivedSpecialItems(TNTSheep.class);
                    for (SpecialItem item : sheeps) {
                        if (item instanceof TNTSheep) {
                            TNTSheep sheep = (TNTSheep) item;
                            if (tnt.equals(sheep.getTNT())) {
                                if (sheep.getTeam() == game.getTeamOfPlayer(player)) {
                                    event.setCancelled(true);
                                }
                                return;
                            }
                        }
                    }
                }
            }
        } else if (event.getEntity() instanceof Creature) {
            Creature mob = (Creature) event.getEntity();
            for (String name : Main.getGameNames()) {
                Game game = Main.getGame(name);
                if (game.getStatus() == GameStatus.RUNNING && mob.getWorld().equals(game.getGameWorld())) {
                    List<SpecialItem> sheeps = game.getActivedSpecialItems(TNTSheep.class);
                    for (SpecialItem item : sheeps) {
                        if (item instanceof TNTSheep) {
                            TNTSheep sheep = (TNTSheep) item;
                            if (mob.equals(sheep.getEntity())) {
                                event.setDamage(0.0);
                                return;
                            }
                        }
                    }
                }
            }
        }

    }

    @EventHandler
    public void onTNTSheepInteractOtherUser(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        if (Main.isPlayerInGame(player)) {
            GamePlayer gamePlayer = Main.getPlayerGameProfile(player);
            Game game = gamePlayer.getGame();

            Entity rightClicked = event.getRightClicked();
            Entity vehicle = rightClicked.getVehicle();
            List<SpecialItem> sheeps = game.getActivedSpecialItems(TNTSheep.class);
            for (SpecialItem item : sheeps) {
                if (item instanceof TNTSheep) {
                    TNTSheep sheep = (TNTSheep) item;
                    if (sheep.getEntity().equals(rightClicked) || sheep.getEntity().equals(vehicle)) {
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }
    }

    private String applyProperty(BedwarsApplyPropertyToBoughtItem event) {
        return TNT_SHEEP_PREFIX
                + MiscUtils.getDoubleFromProperty(
                "speed", "specials.tnt-sheep.speed", event) + ":"
                + MiscUtils.getDoubleFromProperty(
                "follow-range", "specials.tnt-sheep.follow-range", event) + ":"
                + MiscUtils.getDoubleFromProperty(
                "max-target-distance", "specials.tnt-sheep.max-target-distance", event) + ":"
                + MiscUtils.getIntFromProperty(
                "explosion-time", "specials.tnt-sheep.explosion-time", event);
    }
}
