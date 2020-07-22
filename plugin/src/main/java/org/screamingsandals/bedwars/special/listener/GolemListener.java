package org.screamingsandals.bedwars.special.listener;

import org.bukkit.entity.Projectile;
import org.bukkit.projectiles.ProjectileSource;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.APIUtils;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.api.events.BedwarsApplyPropertyToBoughtItem;
import org.screamingsandals.bedwars.api.special.SpecialItem;
import org.screamingsandals.bedwars.game.GamePlayer;
import org.screamingsandals.bedwars.special.Golem;
import org.screamingsandals.bedwars.utils.DelayFactory;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static misat11.lib.lang.I18n.i18nonly;

public class GolemListener implements Listener {
    private static final String GOLEM_PREFIX = "Module:Golem:";

    @EventHandler
    public void onGolemRegister(BedwarsApplyPropertyToBoughtItem event) {
        if (event.getPropertyName().equalsIgnoreCase("golem")) {
            ItemStack stack = event.getStack();
            APIUtils.hashIntoInvisibleString(stack, applyProperty(event));
        }
    }

    @EventHandler
    public void onGolemUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!Main.isPlayerInGame(player)) {
            return;
        }

        GamePlayer gamePlayer = Main.getPlayerGameProfile(player);
        Game game = gamePlayer.getGame();

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (game.getStatus() == GameStatus.RUNNING && !gamePlayer.isSpectator && event.getItem() != null) {
                ItemStack stack = event.getItem();
                String unhidden = APIUtils.unhashFromInvisibleStringStartsWith(stack, GOLEM_PREFIX);

                if (unhidden != null) {
                    if (!game.isDelayActive(player, Golem.class)) {
                        event.setCancelled(true);

                        double speed = Double.parseDouble(unhidden.split(":")[2]);
                        double follow = Double.parseDouble(unhidden.split(":")[3]);
                        double health = Double.parseDouble(unhidden.split(":")[4]);
                        boolean showName = Boolean.parseBoolean(unhidden.split(":")[5]);
                        int delay = Integer.parseInt(unhidden.split(":")[6]);
                        //boolean collidable = Boolean.parseBoolean((unhidden.split(":")[7])); //keeping this to keep configs compatible
                        String name = unhidden.split(":")[8];

                        Location location;

                        if (event.getClickedBlock() == null) {
                            location = player.getLocation();
                        } else {
                            location = event.getClickedBlock().getRelative(event.getBlockFace())
                                    .getLocation().add(0.5, 0.5, 0.5);
                        }
                        Golem golem = new Golem(game, player, game.getTeamOfPlayer(player),
                                stack, location, speed, follow, health, name, showName);

                        if (delay > 0) {
                            DelayFactory delayFactory = new DelayFactory(delay, golem, player, game);
                            game.registerDelay(delayFactory);
                        }

                        golem.spawn();
                    } else {
                        event.setCancelled(true);

                        int delay = game.getActiveDelay(player, Golem.class).getRemainDelay();
                        MiscUtils.sendActionBarMessage(player, i18nonly("special_item_delay").replace("%time%", String.valueOf(delay)));
                    }
                }

            }
        }
    }

    @EventHandler
    public void onGolemDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof IronGolem)) {
            return;
        }

        IronGolem ironGolem = (IronGolem) event.getEntity();
        for (String name : Main.getGameNames()) {
            Game game = Main.getGame(name);
            if (game.getStatus() == GameStatus.RUNNING && ironGolem.getWorld().equals(game.getGameWorld())) {
                List<SpecialItem> golems = game.getActivedSpecialItems(Golem.class);
                for (SpecialItem item : golems) {
                    if (item instanceof Golem) {
                        Golem golem = (Golem) item;
                        if (golem.getEntity().equals(ironGolem)) {
                            if (event.getDamager() instanceof Player) {
                                Player player = (Player) event.getDamager();
                                if (Main.isPlayerInGame(player)) {
                                    if (golem.getTeam() != game.getTeamOfPlayer(player)) {
                                        return;
                                    }
                                }
                            } else if (event.getDamager() instanceof Projectile) {
                                ProjectileSource shooter = ((Projectile) event.getDamager()).getShooter();
                                if (shooter instanceof Player) {
                                    Player player = (Player) shooter;
                                    if (Main.isPlayerInGame(player)) {
                                        if (golem.getTeam() != game.getTeamOfPlayer(player)) {
                                            return;
                                        }
                                    }
                                }
                            }

                            event.setCancelled(game.getOriginalOrInheritedFriendlyfire());
                            return;
                        }
                        return;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onGolemTarget(EntityTargetEvent event) {
    	if (!(event.getEntity() instanceof IronGolem)) {
            return;
        }

        IronGolem ironGolem = (IronGolem) event.getEntity();
        for (String name : Main.getGameNames()) {
            Game game = Main.getGame(name);
            if ((game.getStatus() == GameStatus.RUNNING || game.getStatus() == GameStatus.GAME_END_CELEBRATING) && ironGolem.getWorld().equals(game.getGameWorld())) {
                List<SpecialItem> golems = game.getActivedSpecialItems(Golem.class);
                for (SpecialItem item : golems) {
                    if (item instanceof Golem) {
                        Golem golem = (Golem) item;
                        if (golem.getEntity().equals(ironGolem)) {
                            if (event.getTarget() instanceof Player) {
                                final Player player = (Player) event.getTarget();
                                if (game.isProtectionActive(player)) {
                                    event.setCancelled(true);
                                    return;
                                }

                                if (Main.isPlayerInGame(player)) {
                                    if (golem.getTeam() == game.getTeamOfPlayer(player)) {
                                    	event.setCancelled(true);
                                        // Try to find enemy
                                        Player playerTarget = MiscUtils.findTarget(game, player, golem.getFollowRange());
                                        if (playerTarget != null) {
                                        	// Oh. We found enemy!
                                            ironGolem.setTarget(playerTarget);
                                            return;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onGolemTargetDie(PlayerDeathEvent event) {
        if (Main.isPlayerInGame(event.getEntity())) {
            Game game = Main.getPlayerGameProfile(event.getEntity()).getGame();

            List<SpecialItem> golems = game.getActivedSpecialItems(Golem.class);
            for (SpecialItem item : golems) {
                Golem golem = (Golem) item;
                IronGolem iron = (IronGolem) golem.getEntity();
                if (iron.getTarget() != null && iron.getTarget().equals(event.getEntity())) {
                    iron.setTarget(null);
                }
            }
        }
    }

    @EventHandler
    public void onGolemDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof IronGolem)) {
            return;
        }

        IronGolem ironGolem = (IronGolem) event.getEntity();
        for (String name : Main.getGameNames()) {
            Game game = Main.getGame(name);
            if ((game.getStatus() == GameStatus.RUNNING || game.getStatus() == GameStatus.GAME_END_CELEBRATING) && ironGolem.getWorld().equals(game.getGameWorld())) {
                List<SpecialItem> golems = game.getActivedSpecialItems(Golem.class);
                for (SpecialItem item : golems) {
                    if (item instanceof Golem) {
                        Golem golem = (Golem) item;
                        if (golem.getEntity().equals(ironGolem)) {
                            event.getDrops().clear();
                        }
                    }
                }
            }
        }
    }

    private String applyProperty(BedwarsApplyPropertyToBoughtItem event) {
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
