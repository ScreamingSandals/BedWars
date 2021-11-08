package org.screamingsandals.bedwars.special.listener;

import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.events.ApplyPropertyToBoughtItemEventImpl;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.player.PlayerManagerImpl;
import org.screamingsandals.bedwars.special.RescuePlatformImpl;
import org.screamingsandals.bedwars.utils.DelayFactoryImpl;
import org.screamingsandals.bedwars.utils.ItemUtils;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.lib.event.OnEvent;
import org.screamingsandals.lib.event.entity.SEntityDamageEvent;
import org.screamingsandals.lib.event.player.SPlayerBlockBreakEvent;
import org.screamingsandals.lib.event.player.SPlayerInteractEvent;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.player.PlayerWrapper;
import org.screamingsandals.lib.utils.BlockFace;
import org.screamingsandals.lib.utils.annotations.Service;

@Service
public class RescuePlatformListener {
    private static final String RESCUE_PLATFORM_PREFIX = "Module:RescuePlatform:";

    @OnEvent
    public void onRescuePlatformRegistered(ApplyPropertyToBoughtItemEventImpl event) {
        if (event.getPropertyName().equalsIgnoreCase("rescueplatform")) {
            ItemUtils.saveData(event.getStack(), applyProperty(event));
        }
    }

    @OnEvent
    public void onPlayerUseItem(SPlayerInteractEvent event) {
        var player = event.getPlayer();
        if (!PlayerManagerImpl.getInstance().isPlayerInGame(player)) {
            return;
        }

        var gPlayer = PlayerManagerImpl.getInstance().getPlayer(player).orElseThrow();
        var game = gPlayer.getGame();

        if (event.getAction() == SPlayerInteractEvent.Action.RIGHT_CLICK_AIR || event.getAction() == SPlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            if (game != null && game.getStatus() == GameStatus.RUNNING && !gPlayer.isSpectator() && event.getItem() != null) {
                var stack = event.getItem();
                var unhidden = ItemUtils.getIfStartsWith(stack, RESCUE_PLATFORM_PREFIX);

                if (unhidden != null) {
                    if (!game.isDelayActive(gPlayer, RescuePlatformImpl.class)) {
                        event.setCancelled(true);

                        var propertiesSplit = unhidden.split(":");
                        var isBreakable = Boolean.parseBoolean(propertiesSplit[2]);
                        var delay = Integer.parseInt(propertiesSplit[3]);
                        var breakTime = Integer.parseInt(propertiesSplit[4]);
                        var distance = Integer.parseInt(propertiesSplit[5]);
                        var result = MiscUtils.getBlockTypeFromString(propertiesSplit[6], "GLASS");

                        var rescuePlatform = new RescuePlatformImpl(game, gPlayer, game.getPlayerTeam(gPlayer), stack);

                        if (!player.getLocation().add(BlockFace.DOWN).getBlock().getType().isAir()) {
                            MiscUtils.sendActionBarMessage(player, Message.of(LangKeys.SPECIALS_RESCUE_PLATFORM_NOT_IN_AIR).placeholder("time", delay));
                            return;
                        }

                        if (delay > 0) {
                            var delayFactory = new DelayFactoryImpl(delay, rescuePlatform, gPlayer, game);
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
        if (event.isCancelled() || !(entity instanceof PlayerWrapper)) {
            return;
        }

        var player = (PlayerWrapper) entity;
        if (!PlayerManagerImpl.getInstance().isPlayerInGame(player)) {
            return;
        }

        var gPlayer = PlayerManagerImpl.getInstance().getPlayer(player).orElseThrow();
        var game = gPlayer.getGame();
        if (gPlayer.isSpectator() || game == null) {
            return;
        }

        var rescuePlatform = game.getFirstActiveSpecialItemOfPlayer(gPlayer, RescuePlatformImpl.class);
        if (rescuePlatform != null && event.getDamageCause().is("FALL")) {
            var block = player.getLocation().add(BlockFace.DOWN).getBlock();
            if (block != null) {
                if (block.getType().isSameType(rescuePlatform.getMaterial())) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @OnEvent
    public void onBlockBreak(SPlayerBlockBreakEvent event) {
        var player = event.getPlayer();
        if (!PlayerManagerImpl.getInstance().isPlayerInGame(player)) {
            return;
        }

        var gPlayer = PlayerManagerImpl.getInstance().getPlayer(player).orElseThrow();
        var game = gPlayer.getGame();
        if (gPlayer.isSpectator() || game == null) {
            return;
        }

        var block = event.getBlock();
        for (var checkedPlatform : game.getActiveSpecialItemsOfPlayer(gPlayer, RescuePlatformImpl.class)) {
            if (checkedPlatform != null) {
                for (var platformBlock : checkedPlatform.getPlatformBlocks()) {
                    if (platformBlock.equals(block) && !checkedPlatform.isBreakable()) {
                        event.setCancelled(true);
                    }
                }
            }
        }
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
