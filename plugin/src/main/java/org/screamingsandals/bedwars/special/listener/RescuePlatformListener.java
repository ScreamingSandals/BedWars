package org.screamingsandals.bedwars.special.listener;

import org.screamingsandals.bedwars.api.APIUtils;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.events.ApplyPropertyToBoughtItemEventImpl;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.player.PlayerManager;
import org.screamingsandals.bedwars.special.RescuePlatformImpl;
import org.screamingsandals.bedwars.utils.DelayFactory;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.screamingsandals.lib.entity.EntityHuman;
import org.screamingsandals.lib.event.OnEvent;
import org.screamingsandals.lib.event.entity.SEntityDamageEvent;
import org.screamingsandals.lib.event.player.SPlayerBlockBreakEvent;
import org.screamingsandals.lib.event.player.SPlayerInteractEvent;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.utils.BlockFace;
import org.screamingsandals.lib.utils.annotations.Service;

import java.util.ArrayList;

@Service
public class RescuePlatformListener {
    private static final String RESCUE_PLATFORM_PREFIX = "Module:RescuePlatform:";

    @OnEvent
    public void onRescuePlatformRegistered(ApplyPropertyToBoughtItemEventImpl event) {
        if (event.getPropertyName().equalsIgnoreCase("rescueplatform")) {
            var stack = event.getStack().as(ItemStack.class); // TODO: get rid of this transformation
            APIUtils.hashIntoInvisibleString(stack, applyProperty(event));
            event.setStack(stack);
        }
    }

    @OnEvent
    public void onPlayerUseItem(SPlayerInteractEvent event) {
        var player = event.getPlayer();
        if (!PlayerManager.getInstance().isPlayerInGame(player)) {
            return;
        }

        var gPlayer = PlayerManager.getInstance().getPlayer(player).orElseThrow();
        var game = gPlayer.getGame();

        if (event.getAction() == SPlayerInteractEvent.Action.RIGHT_CLICK_AIR || event.getAction() == SPlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            if (game.getStatus() == GameStatus.RUNNING && !gPlayer.isSpectator && event.getItem() != null) {
                var stack = event.getItem();
                var unhidden = APIUtils.unhashFromInvisibleStringStartsWith(stack.as(ItemStack.class), RESCUE_PLATFORM_PREFIX);

                if (unhidden != null) {
                    if (!game.isDelayActive(gPlayer, RescuePlatformImpl.class)) {
                        event.setCancelled(true);

                        var isBreakable = Boolean.parseBoolean(unhidden.split(":")[2]);
                        var delay = Integer.parseInt(unhidden.split(":")[3]);
                        var breakTime = Integer.parseInt(unhidden.split(":")[4]);
                        var distance = Integer.parseInt(unhidden.split(":")[5]);
                        var result = MiscUtils.getMaterialFromString(unhidden.split(":")[6], "GLASS");

                        var rescuePlatform = new RescuePlatformImpl(game, gPlayer, game.getPlayerTeam(gPlayer), stack);

                        if (player.getLocation().add(BlockFace.DOWN.getDirection()).getBlock().getType().isAir()) {
                            MiscUtils.sendActionBarMessage(player, Message.of(LangKeys.SPECIALS_RESCUE_PLATFORM_NOT_IN_AIR).placeholder("time", delay));
                            return;
                        }

                        if (delay > 0) {
                            var delayFactory = new DelayFactory(delay, rescuePlatform, gPlayer, game);
                            game.registerDelay(delayFactory);
                        }

                        rescuePlatform.createPlatform(isBreakable, breakTime, distance, result);
                    } else {
                        event.setCancelled(true);

                        var delay = game.getActiveDelay(gPlayer, RescuePlatformImpl.class).getRemainDelay();
                        MiscUtils.sendActionBarMessage(player, Message.of(LangKeys.SPECIALS_ITEM_DELAY).placeholder("time", delay));
                    }
                }
            }
        }
    }

    @OnEvent
    public void onFallDamage(SEntityDamageEvent event) {
        var entity = event.getEntity();
        if (event.isCancelled() || !(entity instanceof EntityHuman)) {
            return;
        }

        var player = ((EntityHuman) entity).asPlayer();
        if (!PlayerManager.getInstance().isPlayerInGame(player)) {
            return;
        }

        var gPlayer = PlayerManager.getInstance().getPlayer(player).orElseThrow();
        var game = gPlayer.getGame();
        if (gPlayer.isSpectator) {
            return;
        }

        var rescuePlatform = (RescuePlatformImpl) game.getFirstActivedSpecialItemOfPlayer(player.as(Player.class), RescuePlatformImpl.class);
        if (rescuePlatform != null && event.getDamageCause().is("FALL")) {
            var block = player.getLocation().add(BlockFace.DOWN.getDirection()).getBlock();
            if (block != null) {
                if (block.getType().is(rescuePlatform.getMaterial())) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @OnEvent
    public void onBlockBreak(SPlayerBlockBreakEvent event) {
        var player = event.getPlayer();
        if (!PlayerManager.getInstance().isPlayerInGame(player)) {
            return;
        }

        var gPlayer = PlayerManager.getInstance().getPlayer(player).orElseThrow();
        var game = gPlayer.getGame();
        if (gPlayer.isSpectator) {
            return;
        }

        var block = event.getBlock();
        for (var checkedPlatform : getCreatedPlatforms(game, player.as(Player.class))) {
            if (checkedPlatform != null) {
                for (var platformBlock : checkedPlatform.getPlatformBlocks()) {
                    if (platformBlock.equals(block) && !checkedPlatform.isBreakable()) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    private ArrayList<RescuePlatformImpl> getCreatedPlatforms(Game game, Player player) {
        var createdPlatforms = new ArrayList<RescuePlatformImpl>();
        for (var specialItem : game.getActivedSpecialItemsOfPlayer(player)) {
            if (specialItem instanceof RescuePlatformImpl) {
                RescuePlatformImpl platform = (RescuePlatformImpl) specialItem;
                createdPlatforms.add(platform);
            }
        }
        return createdPlatforms;
    }

    private String applyProperty(ApplyPropertyToBoughtItemEventImpl event) {
        return RESCUE_PLATFORM_PREFIX
                + MiscUtils.getBooleanFromProperty(
                "is-breakable", "specials.rescue-platform.is-breakable", event) + ":"
                + MiscUtils.getIntFromProperty(
                "delay", "specials.rescue-platform.delay", event) + ":"
                + MiscUtils.getIntFromProperty(
                "break-time", "specials.rescue-platform.break-time", event) + ":"
                + MiscUtils.getIntFromProperty(
                "distance", "specials.rescue-platform.distance", event) + ":"
                + MiscUtils.getMaterialFromProperty(
                "material", "specials.rescue-platform.material", event);
    }
}
