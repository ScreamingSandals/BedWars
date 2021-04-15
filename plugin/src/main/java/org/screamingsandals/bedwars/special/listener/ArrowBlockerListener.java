package org.screamingsandals.bedwars.special.listener;

import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.APIUtils;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.events.ApplyPropertyToBoughtItemEventImpl;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.player.PlayerManager;
import org.screamingsandals.bedwars.special.ArrowBlocker;
import org.screamingsandals.bedwars.utils.DelayFactory;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.screamingsandals.lib.event.OnEvent;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.player.PlayerMapper;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.utils.annotations.methods.OnPostEnable;

@Service
public class ArrowBlockerListener implements Listener {
    private static final String ARROW_BLOCKER_PREFIX = "Module:ArrowBlocker:";

    @OnPostEnable
    public void postEnable() {
        Main.getInstance().registerBedwarsListener(this); // TODO: get rid of platform events
    }

    @OnEvent
    public void onArrowBlockerRegistered(ApplyPropertyToBoughtItemEventImpl event) {
        if (event.getPropertyName().equalsIgnoreCase("arrowblocker")) {
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

        BedWarsPlayer gPlayer = PlayerManager.getInstance().getPlayer(player.getUniqueId()).get();
        Game game = gPlayer.getGame();

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (game.getStatus() == GameStatus.RUNNING && !gPlayer.isSpectator && event.getItem() != null) {
                ItemStack stack = event.getItem();
                String unhidden = APIUtils.unhashFromInvisibleStringStartsWith(stack, ARROW_BLOCKER_PREFIX);

                if (unhidden != null) {
                    if (!game.isDelayActive(player, ArrowBlocker.class)) {
                        event.setCancelled(true);

                        int protectionTime = Integer.parseInt(unhidden.split(":")[2]);
                        int delay = Integer.parseInt(unhidden.split(":")[3]);
                        ArrowBlocker arrowBlocker = new ArrowBlocker(game, event.getPlayer(),
                                game.getTeamOfPlayer(event.getPlayer()), stack, protectionTime);

                        if (arrowBlocker.isActivated()) {
                            PlayerMapper.wrapPlayer(player).sendMessage(Message.of(LangKeys.SPECIALS_ARROW_BLOCKER_ALREADY_ACTIVATED).prefixOrDefault(game.getCustomPrefixComponent()));
                            return;
                        }

                        if (delay > 0) {
                            DelayFactory delayFactory = new DelayFactory(delay, arrowBlocker, player, game);
                            game.registerDelay(delayFactory);
                        }

                        arrowBlocker.activate();
                    } else {
                        event.setCancelled(true);

                        int delay = game.getActiveDelay(player, ArrowBlocker.class).getRemainDelay();
                        MiscUtils.sendActionBarMessage(PlayerMapper.wrapPlayer(player), Message.of(LangKeys.SPECIALS_ITEM_DELAY).placeholder("time", delay));
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player)) {
            return;
        }

        Player player = ((Player) entity).getPlayer();

        if (!PlayerManager.getInstance().isPlayerInGame(player.getUniqueId())) {
            return;
        }

        BedWarsPlayer gPlayer = PlayerManager.getInstance().getPlayer(player.getUniqueId()).get();
        Game game = gPlayer.getGame();

        if (gPlayer.isSpectator) {
            return;
        }

        ArrowBlocker arrowBlocker = (ArrowBlocker) game.getFirstActivedSpecialItemOfPlayer(player, ArrowBlocker.class);
        if (arrowBlocker != null && event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
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
