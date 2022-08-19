package org.screamingsandals.bedwars.listener;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.bedwars.game.GameCreator;
import org.screamingsandals.bedwars.game.GamePlayer;

public class Player18Listener implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();
        if (Main.isPlayerInGame(player)) {
            GamePlayer gPlayer = Main.getPlayerGameProfile(player);
            Game game = gPlayer.getGame();
            if (game.getOriginalOrInheritedDamageWhenPlayerIsNotInArena() && game.getStatus() == GameStatus.RUNNING
                    && !gPlayer.isSpectator) {
                if (!GameCreator.isInArea(event.getTo(), game.getPos1(), game.getPos2())) {
                    double reduction = 0;
                    ItemStack helmet = player.getInventory().getHelmet();
                    ItemStack chest = player.getInventory().getChestplate();
                    ItemStack boots = player.getInventory().getBoots();
                    ItemStack pants = player.getInventory().getLeggings();
                    if (helmet != null) {
                        if (helmet.getType() == Material.LEATHER_HELMET) {
                            reduction += 0.04;
                        } else if (helmet.getType() == Material.valueOf("GOLD_HELMET")) {
                            reduction += 0.08;
                        } else if (helmet.getType() == Material.CHAINMAIL_HELMET) {
                            reduction += 0.08;
                        } else if (helmet.getType() == Material.IRON_HELMET) {
                            reduction += 0.08;
                        } else if (helmet.getType() == Material.DIAMOND_HELMET) {
                            reduction += 0.12;
                        }
                    }
                    if (chest != null) {
                        if (chest.getType() == Material.LEATHER_CHESTPLATE) {
                            reduction += 0.12;
                        } else if (chest.getType() == Material.valueOf("GOLD_CHESTPLATE")) {
                            reduction += 0.20;
                        } else if (chest.getType() == Material.CHAINMAIL_CHESTPLATE) {
                            reduction += 0.20;
                        } else if (chest.getType() == Material.IRON_CHESTPLATE) {
                            reduction += 0.24;
                        } else if (chest.getType() == Material.DIAMOND_CHESTPLATE) {
                            reduction += 0.32;
                        }
                    }
                    if (pants != null) {
                        if (pants.getType() == Material.LEATHER_LEGGINGS) {
                            reduction += 0.08;
                        } else if (pants.getType() == Material.valueOf("GOLD_LEGGINGS")) {
                            reduction += 0.12;
                        } else if (pants.getType() == Material.CHAINMAIL_LEGGINGS) {
                            reduction += 0.16;
                        } else if (pants.getType() == Material.IRON_LEGGINGS) {
                            reduction += 0.20;
                        } else if (pants.getType() == Material.DIAMOND_LEGGINGS) {
                            reduction += 0.24;
                        }
                    }
                    if (boots != null) {
                        if (boots.getType() == Material.LEATHER_BOOTS) {
                            reduction += 0.04;
                        } else if (boots.getType() == Material.valueOf("GOLD_BOOTS")) {
                            reduction += 0.04;
                        } else if (boots.getType() == Material.CHAINMAIL_BOOTS) {
                            reduction += 0.04;
                        } else if (boots.getType() == Material.IRON_BOOTS) {
                            reduction += 0.08;
                        } else if (boots.getType() == Material.DIAMOND_BOOTS) {
                            reduction += 0.12;
                        }
                    }
                    final double damageAmount = 5 / (1 - reduction) + Math.random(); // + some random damage because this calculation does not care about enchantments
                    player.damage(damageAmount);
                }
            } else if (Main.getConfigurator().config.getBoolean("preventSpectatorFlyingAway", false) && gPlayer.isSpectator && (game.getStatus() == GameStatus.RUNNING || game.getStatus() == GameStatus.GAME_END_CELEBRATING)) {
                if (!GameCreator.isInArea(event.getTo(), game.getPos1(), game.getPos2())) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
