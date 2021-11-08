package org.screamingsandals.bedwars.special.listener;

import org.screamingsandals.bedwars.utils.ItemUtils;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.events.ApplyPropertyToBoughtItemEventImpl;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.bedwars.player.PlayerManagerImpl;
import org.screamingsandals.bedwars.special.BridgeEggImpl;
import org.screamingsandals.bedwars.utils.DelayFactoryImpl;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.lib.entity.EntityMapper;
import org.screamingsandals.lib.entity.EntityProjectile;
import org.screamingsandals.lib.event.OnEvent;
import org.screamingsandals.lib.event.entity.SProjectileHitEvent;
import org.screamingsandals.lib.event.player.SPlayerInteractEvent;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.item.builder.ItemFactory;
import org.screamingsandals.lib.utils.annotations.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class BridgeEggListener {
    private static final String BRIDGE_EGG_PREFIX = "Module:BridgeEgg:";
    private final Map<EntityProjectile, BridgeEggImpl> bridges = new HashMap<>();

    @OnEvent
    public void onEggRegistration(ApplyPropertyToBoughtItemEventImpl event) {
        if (event.getPropertyName().equalsIgnoreCase("bridgeegg")) {
            ItemUtils.saveData(event.getStack(), this.applyProperty(event));
        }
    }

    @OnEvent
    public void onEggUse(SPlayerInteractEvent event) {
        final var player = event.getPlayer();
        if (!PlayerManagerImpl.getInstance().isPlayerInGame(player)) {
            return;
        }

        BedWarsPlayer gamePlayer = PlayerManagerImpl.getInstance().getPlayer(player).orElseThrow();
        final var game = gamePlayer.getGame();
        if (event.getAction() == SPlayerInteractEvent.Action.RIGHT_CLICK_AIR || event.getAction() == SPlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            if (game != null && game.getStatus() == GameStatus.RUNNING && !gamePlayer.isSpectator() && event.getItem() != null) {
                var stack = event.getItem();
                String unhidden = ItemUtils.getIfStartsWith(stack, BRIDGE_EGG_PREFIX);
                if (unhidden != null) {
                    if (!game.isDelayActive(gamePlayer, BridgeEggImpl.class)) {
                        event.setCancelled(true);

                        final var propertiesSplit = unhidden.split(":");
                        var distance = Double.parseDouble(propertiesSplit[2]);
                        var material = MiscUtils.getBlockTypeFromString(propertiesSplit[3], "GLASS");
                        var delay = Integer.parseInt(propertiesSplit[4]);

                        var egg = EntityMapper.<EntityProjectile>spawn("egg", player.getLocation().add(0, 1, 0)).orElseThrow();
                        egg.setVelocity(player.getLocation().getFacingDirection().multiply(2));

                        var bridgeEgg = new BridgeEggImpl(game, gamePlayer, game.getPlayerTeam(gamePlayer), egg, material, distance);

                        if (delay > 0) {
                            var delayFactory = new DelayFactoryImpl(delay, bridgeEgg, gamePlayer, game);
                            game.registerDelay(delayFactory);
                        }

                        this.bridges.put(egg, bridgeEgg);
                        bridgeEgg.runTask();

                        stack.setAmount(1); // we are removing exactly one egg
                        try {
                            if (player.getPlayerInventory().getItemInOffHand().equals(stack)) {
                                player.getPlayerInventory().setItemInOffHand(ItemFactory.getAir());
                            } else {
                                player.getPlayerInventory().removeItem(stack);
                            }
                        } catch (Throwable e) {
                            player.getPlayerInventory().removeItem(stack);
                        }
                        player.forceUpdateInventory();
                    } else {
                        event.setCancelled(true);

                        var delay = game.getActiveDelay(gamePlayer, BridgeEggImpl.class).getRemainDelay();
                        MiscUtils.sendActionBarMessage(player, Message.of(LangKeys.SPECIALS_ITEM_DELAY).placeholder("time", delay));
                    }
                }
            }
        }
    }

    @OnEvent
    public void onProjectileHit(SProjectileHitEvent event) {
        final var egg = event.getEntity();
        if (!(egg instanceof EntityProjectile)) {
            return;
        }

        if (this.bridges.containsKey(egg)) {
            egg.remove();
            this.bridges.get(egg).getTask().cancel();
            this.bridges.remove(egg);
        }
    }

    private String applyProperty(ApplyPropertyToBoughtItemEventImpl event) {
        return BRIDGE_EGG_PREFIX
                + MiscUtils.getDoubleFromProperty(
                "distance", "specials.bridge-egg.distance", event) + ":"
                + MiscUtils.getMaterialFromProperty(
                "material", "specials.bridge-egg.material", event) + ":"
                + MiscUtils.getIntFromProperty(
                "delay", "specials.bridge-egg.delay", event);
    }
}
