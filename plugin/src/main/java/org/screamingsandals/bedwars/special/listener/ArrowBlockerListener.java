package org.screamingsandals.bedwars.special.listener;

import org.screamingsandals.bedwars.utils.ItemUtils;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.events.ApplyPropertyToBoughtItemEventImpl;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.player.PlayerManagerImpl;
import org.screamingsandals.bedwars.special.ArrowBlockerImpl;
import org.screamingsandals.bedwars.utils.DelayFactoryImpl;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.lib.entity.EntityHuman;
import org.screamingsandals.lib.event.OnEvent;
import org.screamingsandals.lib.event.entity.SEntityDamageEvent;
import org.screamingsandals.lib.event.player.SPlayerInteractEvent;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.utils.annotations.Service;

@Service
public class ArrowBlockerListener {
    private static final String ARROW_BLOCKER_PREFIX = "Module:ArrowBlocker:";

    @OnEvent
    public void onArrowBlockerRegistered(ApplyPropertyToBoughtItemEventImpl event) {
        if (event.getPropertyName().equalsIgnoreCase("arrowblocker")) {
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
            if (game.getStatus() == GameStatus.RUNNING && !gPlayer.isSpectator() && event.getItem() != null) {
                var stack = event.getItem();
                var unhidden = ItemUtils.getIfStartsWith(stack, ARROW_BLOCKER_PREFIX);

                if (unhidden != null) {
                    if (!game.isDelayActive(gPlayer, ArrowBlockerImpl.class)) {
                        event.setCancelled(true);

                        int protectionTime = Integer.parseInt(unhidden.split(":")[2]);
                        int delay = Integer.parseInt(unhidden.split(":")[3]);
                        var arrowBlocker = new ArrowBlockerImpl(game, gPlayer, game.getPlayerTeam(gPlayer), stack, protectionTime);

                        if (arrowBlocker.isActivated()) {
                            player.sendMessage(Message.of(LangKeys.SPECIALS_ARROW_BLOCKER_ALREADY_ACTIVATED).prefixOrDefault(game.getCustomPrefixComponent()));
                            return;
                        }

                        if (delay > 0) {
                            var delayFactory = new DelayFactoryImpl(delay, arrowBlocker, gPlayer, game);
                            game.registerDelay(delayFactory);
                        }

                        arrowBlocker.activate();
                    } else {
                        event.setCancelled(true);

                        int delay = game.getActiveDelay(gPlayer, ArrowBlockerImpl.class).getRemainDelay();
                        MiscUtils.sendActionBarMessage(player, Message.of(LangKeys.SPECIALS_ITEM_DELAY).placeholder("time", delay));
                    }
                }
            }
        }
    }

    @OnEvent(priority = org.screamingsandals.lib.event.EventPriority.HIGH)
    public void onDamage(SEntityDamageEvent event) {
        var entity = event.getEntity();
        if (!(entity instanceof EntityHuman)) {
            return;
        }

        var player = ((EntityHuman) entity).asPlayer();

        if (!PlayerManagerImpl.getInstance().isPlayerInGame(player)) {
            return;
        }

        var gPlayer = PlayerManagerImpl.getInstance().getPlayer(player).orElseThrow();
        var game = gPlayer.getGame();

        if (gPlayer.isSpectator()) {
            return;
        }

        var arrowBlocker = game.getFirstActiveSpecialItemOfPlayer(gPlayer, ArrowBlockerImpl.class);
        if (arrowBlocker != null && event.getDamageCause().is("PROJECTILE")) {
            event.setCancelled(true);
        }
    }

    private String applyProperty(ApplyPropertyToBoughtItemEventImpl event) {
        return ARROW_BLOCKER_PREFIX
                + MiscUtils.getIntFromProperty(
                "protection-time", "specials.arrow-blocker.protection-time", event) + ":"
                + MiscUtils.getIntFromProperty(
                "delay", "specials.arrow-blocker.delay", event);
    }
}
