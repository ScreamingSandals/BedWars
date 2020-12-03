package org.screamingsandals.bedwars.special.listener;

import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.APIUtils;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.api.events.BedwarsApplyPropertyToBoughtItem;
import org.screamingsandals.bedwars.game.GamePlayer;
import org.screamingsandals.bedwars.special.RescuePlatform;
import org.screamingsandals.bedwars.special.WarpPowder;
import org.screamingsandals.bedwars.utils.DelayFactory;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import static org.screamingsandals.bedwars.lib.lang.I18n.i18nonly;

public class WarpPowderListener implements Listener {
    private static final String WARP_POWDER_PREFIX = "Module:WarpPowder:";

    @EventHandler
    public void onPowderItemRegister(BedwarsApplyPropertyToBoughtItem event) {
        if (event.getPropertyName().equalsIgnoreCase("warppowder")) {
            ItemStack stack = event.getStack();
            APIUtils.hashIntoInvisibleString(stack, applyProperty(event));
        }
    }

    @EventHandler
    public void onPlayerUseItem(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!Main.isPlayerInGame(player)) {
            return;
        }

        GamePlayer gPlayer = Main.getPlayerGameProfile(player);
        Game game = gPlayer.getGame();
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (game.getStatus() == GameStatus.RUNNING && !gPlayer.isSpectator) {
                if (event.getItem() != null) {
                    if (!game.isDelayActive(player, WarpPowder.class)) {
                        ItemStack stack = event.getItem();
                        String unhidden = APIUtils.unhashFromInvisibleStringStartsWith(stack, WARP_POWDER_PREFIX);

                        if (unhidden != null) {
                            event.setCancelled(true);

                            int teleportTime = Integer.parseInt(unhidden.split(":")[2]);
                            int delay = Integer.parseInt(unhidden.split(":")[3]);
                            WarpPowder warpPowder = new WarpPowder(game, event.getPlayer(),
                                    game.getTeamOfPlayer(event.getPlayer()), stack, teleportTime);

                            if (event.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN)
                                    .getType() == Material.AIR) {
                                return;
                            }

                            if (delay > 0) {
                                DelayFactory delayFactory = new DelayFactory(delay, warpPowder, player, game);
                                game.registerDelay(delayFactory);
                            }

                            warpPowder.runTask();
                        } else {
                            event.setCancelled(true);
                            int delay = game.getActiveDelay(player, RescuePlatform.class).getRemainDelay();
                            MiscUtils.sendActionBarMessage(player, i18nonly("special_item_delay").replace("%time%", String.valueOf(delay)));
                        }
                    }
                }
            }
        }
    }


    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.isCancelled() || !(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();

        if (!Main.isPlayerInGame(player)) {
            return;
        }

        GamePlayer gPlayer = Main.getPlayerGameProfile(player);
        Game game = gPlayer.getGame();

        if (gPlayer.isSpectator) {
            return;
        }

        WarpPowder warpPowder = (WarpPowder) game.getFirstActivedSpecialItemOfPlayer(player, WarpPowder.class);
        if (warpPowder != null) {
            warpPowder.cancelTeleport(false, true);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (event.isCancelled() || !Main.isPlayerInGame(player)) {
            return;
        }

        if (event.getFrom().getBlock().equals(event.getTo().getBlock())) {
            return;
        }

        GamePlayer gPlayer = Main.getPlayerGameProfile(player);
        Game game = gPlayer.getGame();
        if (gPlayer.isSpectator) {
            return;
        }

        WarpPowder warpPowder = (WarpPowder) game.getFirstActivedSpecialItemOfPlayer(player, WarpPowder.class);
        if (warpPowder != null) {
            warpPowder.cancelTeleport(false, true);
        }
    }

    private String applyProperty(BedwarsApplyPropertyToBoughtItem event) {
        return WARP_POWDER_PREFIX
                + MiscUtils.getIntFromProperty(
                "teleport-time", "specials.warp-powder.teleport-time", event) + ":"
                + MiscUtils.getIntFromProperty(
                "delay", "specials.warp-powder.delay", event);
    }
}
