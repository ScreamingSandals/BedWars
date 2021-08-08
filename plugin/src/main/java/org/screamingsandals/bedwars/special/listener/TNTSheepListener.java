package org.screamingsandals.bedwars.special.listener;

import org.bukkit.World;
import org.screamingsandals.bedwars.api.APIUtils;
import org.screamingsandals.bedwars.api.config.ConfigurationContainer;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.events.ApplyPropertyToBoughtItemEventImpl;
import org.screamingsandals.bedwars.game.GameManagerImpl;
import org.screamingsandals.bedwars.player.PlayerManagerImpl;
import org.screamingsandals.bedwars.special.TNTSheepImpl;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.inventory.ItemStack;
import org.screamingsandals.lib.entity.EntityHuman;
import org.screamingsandals.lib.event.EventPriority;
import org.screamingsandals.lib.event.OnEvent;
import org.screamingsandals.lib.event.entity.SEntityDamageByEntityEvent;
import org.screamingsandals.lib.event.player.SPlayerInteractEntityEvent;
import org.screamingsandals.lib.event.player.SPlayerInteractEvent;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.world.LocationHolder;

@Service
public class TNTSheepListener {
    private static final String TNT_SHEEP_PREFIX = "Module:TNTSheep:";

    @OnEvent
    public void onTNTSheepRegistered(ApplyPropertyToBoughtItemEventImpl event) {
        if (event.getPropertyName().equalsIgnoreCase("tntsheep")) {
            var stack = event.getStack().as(ItemStack.class); // TODO: get rid of this transformation
            APIUtils.hashIntoInvisibleString(stack, applyProperty(event));
            event.setStack(stack);
        }

    }

    @OnEvent
    public void onTNTSheepUsed(SPlayerInteractEvent event) {
        var player = event.getPlayer();
        if (!PlayerManagerImpl.getInstance().isPlayerInGame(player)) {
            return;
        }

        var gamePlayer = PlayerManagerImpl.getInstance().getPlayer(player).orElseThrow();
        var game = gamePlayer.getGame();

        if (event.getAction() == SPlayerInteractEvent.Action.RIGHT_CLICK_AIR || event.getAction() == SPlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            if (game.getStatus() == GameStatus.RUNNING && !gamePlayer.isSpectator && event.getItem() != null) {
                var stack = event.getItem();
                String unhidden = APIUtils.unhashFromInvisibleStringStartsWith(stack.as(ItemStack.class), TNT_SHEEP_PREFIX);

                if (unhidden != null) {
                    event.setCancelled(true);

                    var speed = Double.parseDouble(unhidden.split(":")[2]);
                    var follow = Double.parseDouble(unhidden.split(":")[3]);
                    var maxTargetDistance = Double.parseDouble(unhidden.split(":")[4]);
                    var explosionTime = Integer.parseInt(unhidden.split(":")[5]);
                    LocationHolder startLocation;

                    if (event.getBlockClicked() == null) {
                        startLocation = player.getLocation();
                    } else {
                        startLocation = event.getBlockClicked().getLocation().add(event.getBlockFace().getDirection());
                    }
                    var sheep = new TNTSheepImpl(game, gamePlayer, game.getPlayerTeam(gamePlayer),
                            startLocation, stack, speed, follow, maxTargetDistance, explosionTime);

                    sheep.spawn();
                }

            }
        }
    }

    @OnEvent(priority = EventPriority.HIGHEST)
    public void onTNTSheepDamage(SEntityDamageByEntityEvent event) {
        if (event.isCancelled() || event.getDamageCause().is("CUSTOM", "VOID", "FALL")) {
            return;
        }

        if (event.getEntity() instanceof EntityHuman) {
            var player = ((EntityHuman) event.getEntity()).asPlayer();
            if (PlayerManagerImpl.getInstance().isPlayerInGame(player)) {
                var gamePlayer = PlayerManagerImpl.getInstance().getPlayer(player).orElseThrow();
                var game = gamePlayer.getGame();
                if (event.getDamager().as(Entity.class) instanceof TNTPrimed && !game.getConfigurationContainer().getOrDefault(ConfigurationContainer.FRIENDLY_FIRE, Boolean.class, false)) {
                    var tnt = event.getDamager();
                    var sheeps = game.getActivedSpecialItems(TNTSheepImpl.class);
                    for (var item : sheeps) {
                        if (item instanceof TNTSheepImpl) {
                            var sheep = (TNTSheepImpl) item;
                            if (tnt.equals(sheep.getTnt())) {
                                if (sheep.getTeam() == game.getPlayerTeam(gamePlayer)) {
                                    event.setCancelled(true);
                                }
                                return;
                            }
                        }
                    }
                }
            }
        } else if (event.getEntity().as(Entity.class) instanceof Creature) {
            var mob = event.getEntity();
            for (var game : GameManagerImpl.getInstance().getGames()) {
                if (game.getStatus() == GameStatus.RUNNING && mob.getLocation().getWorld().as(World.class).equals(game.getGameWorld())) {
                    var sheeps = game.getActivedSpecialItems(TNTSheepImpl.class);
                    for (var item : sheeps) {
                        if (item instanceof TNTSheepImpl) {
                            var sheep = (TNTSheepImpl) item;
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

    @OnEvent
    public void onTNTSheepInteractOtherUser(SPlayerInteractEntityEvent event) {
        var player = event.getPlayer();
        if (PlayerManagerImpl.getInstance().isPlayerInGame(player)) {
            var gamePlayer = PlayerManagerImpl.getInstance().getPlayer(player).orElseThrow();
            var game = gamePlayer.getGame();

            var rightClicked = event.getClickedEntity();
            var vehicle = rightClicked.getVehicle();
            var sheeps = game.getActivedSpecialItems(TNTSheepImpl.class);
            for (var item : sheeps) {
                if (item instanceof TNTSheepImpl) {
                    var sheep = (TNTSheepImpl) item;
                    if (sheep.getEntity().equals(rightClicked) || sheep.getEntity().equals(vehicle)) {
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }
    }

    private String applyProperty(ApplyPropertyToBoughtItemEventImpl event) {
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
