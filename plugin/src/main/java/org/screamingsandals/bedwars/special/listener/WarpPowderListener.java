package org.screamingsandals.bedwars.special.listener;

import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.APIUtils;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.events.ApplyPropertyToBoughtItemEventImpl;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.player.PlayerManager;
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
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.screamingsandals.lib.event.OnEvent;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.player.PlayerMapper;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.utils.annotations.methods.OnPostEnable;

@Service
public class WarpPowderListener implements Listener {
    private static final String WARP_POWDER_PREFIX = "Module:WarpPowder:";

    @OnPostEnable
    public void postEnable() {
        Main.getInstance().registerBedwarsListener(this); // TODO: get rid of platform events
    }

    @OnEvent
    public void onPowderItemRegister(ApplyPropertyToBoughtItemEventImpl event) {
        if (event.getPropertyName().equalsIgnoreCase("warppowder")) {
            var stack = event.getStack().as(ItemStack.class); // TODO: get rid of this transformation
            APIUtils.hashIntoInvisibleString(stack, applyProperty(event));
            event.setStack(stack);
        }
    }

    @EventHandler
    public void onPlayerUseItem(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!PlayerManager.getInstance().isPlayerInGame(player.getUniqueId())) {
            return;
        }

        BedWarsPlayer gPlayer = PlayerManager.getInstance().getPlayer(player.getUniqueId()).orElseThrow();
        Game game = gPlayer.getGame();
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (game.getStatus() == GameStatus.RUNNING && !gPlayer.isSpectator) {
                if (event.getItem() != null) {
                    ItemStack stack = event.getItem();
                    String unhidden = APIUtils.unhashFromInvisibleStringStartsWith(stack, WARP_POWDER_PREFIX);

                    if (unhidden != null) {
                        event.setCancelled(true);
                        if (!game.isDelayActive(player, WarpPowder.class)) {
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
                            int delay = game.getActiveDelay(player, WarpPowder.class).getRemainDelay();
                            MiscUtils.sendActionBarMessage(PlayerMapper.wrapPlayer(player), Message.of(LangKeys.SPECIALS_ITEM_DELAY).placeholder("time", delay));
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

        if (!PlayerManager.getInstance().isPlayerInGame(player.getUniqueId())) {
            return;
        }

        BedWarsPlayer gPlayer = PlayerManager.getInstance().getPlayer(player.getUniqueId()).orElseThrow();
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
        if (event.isCancelled() || !PlayerManager.getInstance().isPlayerInGame(player.getUniqueId())) {
            return;
        }

        if (event.getFrom().getBlock().equals(event.getTo().getBlock())) {
            return;
        }

        BedWarsPlayer gPlayer = PlayerManager.getInstance().getPlayer(player.getUniqueId()).orElseThrow();
        Game game = gPlayer.getGame();
        if (gPlayer.isSpectator) {
            return;
        }

        WarpPowder warpPowder = (WarpPowder) game.getFirstActivedSpecialItemOfPlayer(player, WarpPowder.class);
        if (warpPowder != null) {
            warpPowder.cancelTeleport(true, true);

            if (player.getInventory().firstEmpty() == -1 && !player.getInventory().contains(warpPowder.getStack())) {
                player.getWorld().dropItemNaturally(player.getLocation(), warpPowder.getStack());
            } else {
                player.getInventory().addItem(warpPowder.getStack());
            }
            player.updateInventory();
        }
    }

    private String applyProperty(ApplyPropertyToBoughtItemEventImpl event) {
        return WARP_POWDER_PREFIX
                + MiscUtils.getIntFromProperty(
                "teleport-time", "specials.warp-powder.teleport-time", event) + ":"
                + MiscUtils.getIntFromProperty(
                "delay", "specials.warp-powder.delay", event);
    }
}
