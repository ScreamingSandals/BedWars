package org.screamingsandals.bedwars.special.listener;

import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.api.special.PopUpTower;
import org.screamingsandals.bedwars.events.ApplyPropertyToBoughtItemEventImpl;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.bedwars.player.PlayerManagerImpl;
import org.screamingsandals.bedwars.special.BridgeEggImpl;
import org.screamingsandals.bedwars.special.PopUpTowerImpl;
import org.screamingsandals.bedwars.utils.DelayFactoryImpl;
import org.screamingsandals.bedwars.utils.ItemUtils;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.lib.entity.EntityMapper;
import org.screamingsandals.lib.entity.EntityProjectile;
import org.screamingsandals.lib.event.OnEvent;
import org.screamingsandals.lib.event.player.SPlayerInteractEvent;
import org.screamingsandals.lib.item.builder.ItemFactory;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.utils.BlockFace;
import org.screamingsandals.lib.utils.annotations.Service;

@Service
public class PopUpTowerListener {
    private static final String POPUP_TOWER_PREFIX = "Module:PopupTower:";

    @OnEvent
    public void onPopUpTowerRegistration(ApplyPropertyToBoughtItemEventImpl event) {
        if (event.getPropertyName().equalsIgnoreCase("popuptower")) {
            ItemUtils.saveData(event.getStack(), this.applyProperty(event));
        }
    }

    @OnEvent
    public void onPopUpTowerUse(SPlayerInteractEvent event) {
        final var player = event.getPlayer();
        if (!PlayerManagerImpl.getInstance().isPlayerInGame(player)) {
            return;
        }

        var gamePlayer = player.as(BedWarsPlayer.class);
        final var game = gamePlayer.getGame();
        if (event.getAction() == SPlayerInteractEvent.Action.RIGHT_CLICK_AIR || event.getAction() == SPlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            if (game.getStatus() == GameStatus.RUNNING && !gamePlayer.isSpectator() && event.getItem() != null) {
                var stack = event.getItem();
                String unhidden = ItemUtils.getIfStartsWith(stack, POPUP_TOWER_PREFIX);
                if (unhidden != null) {
                    if (!game.isDelayActive(gamePlayer, PopUpTowerImpl.class)) {
                        event.setCancelled(true);

                        var material = MiscUtils.getBlockTypeFromString(unhidden.split(":")[2], "WOOL");
                        var delay = Integer.parseInt(unhidden.split(":")[3]);

                        var playerFace = MiscUtils.yawToFace(player.getLocation().getYaw(), false);

                        var popupTower = new PopUpTowerImpl(game, gamePlayer, game.getPlayerTeam(gamePlayer), material, player.getLocation().getBlock().getLocation().add(playerFace).add(BlockFace.DOWN), playerFace);

                        if (delay > 0) {
                            var delayFactory = new DelayFactoryImpl(delay, popupTower, gamePlayer, game);
                            game.registerDelay(delayFactory);
                        }

                        popupTower.runTask();

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

                        var delay = game.getActiveDelay(gamePlayer, PopUpTowerImpl.class).getRemainDelay();
                        MiscUtils.sendActionBarMessage(player, Message.of(LangKeys.SPECIALS_ITEM_DELAY).placeholder("time", delay));
                    }
                }
            }
        }
    }

    // TODO: make more things configurable
    private String applyProperty(ApplyPropertyToBoughtItemEventImpl event) {
        return POPUP_TOWER_PREFIX
                + MiscUtils.getMaterialFromProperty(
                "material", "specials.popup-tower.material", event) + ":"
                + MiscUtils.getIntFromProperty(
                "delay", "specials.popup-tower.delay", event);
    }
}
