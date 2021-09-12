package org.screamingsandals.bedwars.special.listener;

import org.bukkit.World;
import org.screamingsandals.bedwars.api.APIUtils;
import org.screamingsandals.bedwars.api.config.ConfigurationContainer;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.events.ApplyPropertyToBoughtItemEventImpl;
import org.screamingsandals.bedwars.game.GameManagerImpl;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.bedwars.player.PlayerManagerImpl;
import org.screamingsandals.bedwars.special.GolemImpl;
import org.screamingsandals.bedwars.utils.DelayFactory;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.screamingsandals.lib.entity.EntityHuman;
import org.screamingsandals.lib.entity.EntityProjectile;
import org.screamingsandals.lib.event.OnEvent;
import org.screamingsandals.lib.event.entity.SEntityDamageByEntityEvent;
import org.screamingsandals.lib.event.entity.SEntityDeathEvent;
import org.screamingsandals.lib.event.entity.SEntityTargetEvent;
import org.screamingsandals.lib.event.player.SPlayerDeathEvent;
import org.screamingsandals.lib.event.player.SPlayerInteractEvent;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.player.PlayerWrapper;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.world.LocationHolder;

@Service
public class GolemListener {
    private static final String GOLEM_PREFIX = "Module:Golem:";

    @OnEvent
    public void onGolemRegister(ApplyPropertyToBoughtItemEventImpl event) {
        if (event.getPropertyName().equalsIgnoreCase("golem")) {
            var stack = event.getStack().as(ItemStack.class); // TODO: get rid of this transformation
            APIUtils.hashIntoInvisibleString(stack, applyProperty(event));
            event.setStack(stack);
        }
    }

    @OnEvent
    public void onGolemUse(SPlayerInteractEvent event) {
        var player = event.getPlayer();
        if (!PlayerManagerImpl.getInstance().isPlayerInGame(player)) {
            return;
        }

        var gamePlayer = PlayerManagerImpl.getInstance().getPlayer(player).orElseThrow();
        var game = gamePlayer.getGame();

        if (event.getAction() == SPlayerInteractEvent.Action.RIGHT_CLICK_AIR || event.getAction() == SPlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            if (game.getStatus() == GameStatus.RUNNING && !gamePlayer.isSpectator && event.getItem() != null) {
                var stack = event.getItem();
                var unhidden = APIUtils.unhashFromInvisibleStringStartsWith(stack.as(ItemStack.class), GOLEM_PREFIX);

                if (unhidden != null) {
                    if (!game.isDelayActive(gamePlayer, GolemImpl.class)) {
                        event.setCancelled(true);

                        var speed = Double.parseDouble(unhidden.split(":")[2]);
                        var follow = Double.parseDouble(unhidden.split(":")[3]);
                        var health = Double.parseDouble(unhidden.split(":")[4]);
                        var showName = Boolean.parseBoolean(unhidden.split(":")[5]);
                        var delay = Integer.parseInt(unhidden.split(":")[6]);
                        //boolean collidable = Boolean.parseBoolean((unhidden.split(":")[7])); //keeping this to keep configs compatible
                        var name = unhidden.split(":")[8];

                        LocationHolder location;

                        if (event.getBlockClicked() == null) {
                            location = player.getLocation();
                        } else {
                            location = event.getBlockClicked().getLocation().add(event.getBlockFace().getDirection()).add(0.5, 0.5, 0.5);
                        }
                        var golem = new GolemImpl(game, gamePlayer, game.getPlayerTeam(gamePlayer),
                                stack, location, speed, follow, health, name, showName);

                        if (delay > 0) {
                            var delayFactory = new DelayFactory(delay, golem, gamePlayer, game);
                            game.registerDelay(delayFactory);
                        }

                        golem.spawn();
                    } else {
                        event.setCancelled(true);

                        var delay = game.getActiveDelay(gamePlayer, GolemImpl.class).getRemainDelay();
                        MiscUtils.sendActionBarMessage(player, Message.of(LangKeys.SPECIALS_ITEM_DELAY).placeholder("time", delay));
                    }
                }

            }
        }
    }

    @OnEvent
    public void onGolemDamage(SEntityDamageByEntityEvent event) {
        if (!event.getEntity().getEntityType().is("IRON_GOLEM")) {
            return;
        }

        var ironGolem = event.getEntity();
        for (var game : GameManagerImpl.getInstance().getGames()) {
            if (game.getStatus() == GameStatus.RUNNING && ironGolem.getLocation().getWorld().equals(game.getGameWorld())) {
                var golems = game.getActivedSpecialItems(GolemImpl.class);
                for (var item : golems) {
                    if (item instanceof GolemImpl) {
                        var golem = (GolemImpl) item;
                        if (golem.getEntity().equals(ironGolem)) {
                            if (event.getDamager() instanceof EntityHuman) {
                                var player = ((EntityHuman) event.getDamager()).asPlayer();
                                if (PlayerManagerImpl.getInstance().isPlayerInGame(player)) {
                                    if (golem.getTeam() != game.getPlayerTeam(player.as(BedWarsPlayer.class))) {
                                        return;
                                    }
                                }
                            } else if (event.getDamager() instanceof EntityProjectile) {
                                var shooter = event.getDamager().as(EntityProjectile.class).getShooter();
                                if (shooter instanceof EntityHuman) {
                                    var player = shooter.as(PlayerWrapper.class);
                                    if (PlayerManagerImpl.getInstance().isPlayerInGame(player)) {
                                        if (golem.getTeam() != game.getPlayerTeam(player.as(BedWarsPlayer.class))) {
                                            return;
                                        }
                                    }
                                }
                            }

                            event.setCancelled(game.getConfigurationContainer().getOrDefault(ConfigurationContainer.FRIENDLY_FIRE, Boolean.class, false));
                            return;
                        }
                        return;
                    }
                }
            }
        }
    }

    @OnEvent
    public void onGolemTarget(SEntityTargetEvent event) {
        if (!event.getEntity().getEntityType().is("IRON_GOLEM")) {
            return;
        }

        var ironGolem = event.getEntity();
        for (var game : GameManagerImpl.getInstance().getGames()) {
            if ((game.getStatus() == GameStatus.RUNNING || game.getStatus() == GameStatus.GAME_END_CELEBRATING) && ironGolem.getLocation().getWorld().equals(game.getGameWorld())) {
                var golems = game.getActivedSpecialItems(GolemImpl.class);
                for (var item : golems) {
                    if (item instanceof GolemImpl) {
                        GolemImpl golem = (GolemImpl) item;
                        if (golem.getEntity().equals(ironGolem)) {
                            if (event.getTarget() instanceof EntityHuman) {
                                final var player = ((EntityHuman) event.getTarget()).asPlayer();

                                if (PlayerManagerImpl.getInstance().isPlayerInGame(player)) {
                                    var gPlayer = player.as(BedWarsPlayer.class);
                                    if (game.isProtectionActive(gPlayer)) {
                                        event.setCancelled(true);
                                        return;
                                    }

                                    if (golem.getTeam() == game.getPlayerTeam(gPlayer)) {
                                    	event.setCancelled(true);
                                        // Try to find enemy
                                        var playerTarget = MiscUtils.findTarget(game, player, golem.getFollowRange());
                                        if (playerTarget != null) {
                                        	// Oh. We found enemy!
                                            ironGolem.as(IronGolem.class).setTarget(playerTarget.as(Player.class));
                                            return;
                                        }
                                    }
                                } else {
                                    event.setCancelled(true);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @OnEvent
    public void onGolemTargetDie(SPlayerDeathEvent event) {
        if (PlayerManagerImpl.getInstance().isPlayerInGame(event.getPlayer())) {
            var game = PlayerManagerImpl.getInstance().getGameOfPlayer(event.getPlayer()).orElseThrow();

            var golems = game.getActivedSpecialItems(GolemImpl.class);
            for (var item : golems) {
                var golem = (GolemImpl) item;
                var iron = golem.getEntity().as(IronGolem.class);
                if (iron.getTarget() != null && iron.getTarget().equals(event.getPlayer().as(Player.class))) {
                    iron.setTarget(null);
                }
            }
        }
    }

    @OnEvent
    public void onGolemDeath(SEntityDeathEvent event) {
        if (!event.getEntity().getEntityType().is("IRON_GOLEM")) {
            return;
        }

        var ironGolem = event.getEntity();
        for (var game : GameManagerImpl.getInstance().getGames()) {
            if ((game.getStatus() == GameStatus.RUNNING || game.getStatus() == GameStatus.GAME_END_CELEBRATING) && ironGolem.getLocation().getWorld().equals(game.getGameWorld())) {
                var golems = game.getActivedSpecialItems(GolemImpl.class);
                for (var item : golems) {
                    if (item instanceof GolemImpl) {
                        GolemImpl golem = (GolemImpl) item;
                        if (golem.getEntity().equals(ironGolem)) {
                            event.getDrops().clear();
                        }
                    }
                }
            }
        }
    }

    private String applyProperty(ApplyPropertyToBoughtItemEventImpl event) {
        return GOLEM_PREFIX
                + MiscUtils.getDoubleFromProperty(
                "speed", "specials.golem.speed", event) + ":"
                + MiscUtils.getDoubleFromProperty(
                "follow-range", "specials.golem.follow-range", event) + ":"
                + MiscUtils.getDoubleFromProperty(
                "health", "specials.golem.health", event) + ":"
                + MiscUtils.getBooleanFromProperty(
                "show-name", "specials.golem.show-name", event) + ":"
                + MiscUtils.getIntFromProperty(
                "delay", "specials.golem.delay", event) + ":"
                + MiscUtils.getBooleanFromProperty("collidable", "specials.golem.collidable", event) + ":"
                + MiscUtils.getStringFromProperty(
                "name-format", "specials.golem.name-format", event);
    }
}
