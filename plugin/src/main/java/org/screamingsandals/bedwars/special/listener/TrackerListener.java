package org.screamingsandals.bedwars.special.listener;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.utils.ItemUtils;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.events.ApplyPropertyToBoughtItemEventImpl;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.player.PlayerManagerImpl;
import org.bukkit.inventory.ItemStack;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.lib.event.OnEvent;
import org.screamingsandals.lib.event.player.SPlayerInteractEvent;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.tasker.Tasker;
import org.screamingsandals.lib.utils.annotations.Service;

@Service
public class TrackerListener {
    private static final String TRACKER_PREFIX = "Module:Tracker:";

    @OnEvent
    public void onTrackerRegistered(ApplyPropertyToBoughtItemEventImpl event) {
        if (event.getPropertyName().equalsIgnoreCase("tracker")) {
            var stack = event.getStack().as(ItemStack.class); // TODO: get rid of this transformation

            ItemUtils.hashIntoInvisibleString(stack, TRACKER_PREFIX);
            event.setStack(stack);
        }

    }

    @OnEvent
    public void onTrackerUse(SPlayerInteractEvent event) {
        var player = event.getPlayer();
        if (!PlayerManagerImpl.getInstance().isPlayerInGame(player)) {
            return;
        }

        var gamePlayer = PlayerManagerImpl.getInstance().getPlayer(player).orElseThrow();
        var game = gamePlayer.getGame();
        if (event.getAction() == SPlayerInteractEvent.Action.RIGHT_CLICK_AIR || event.getAction() == SPlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            if (game.getStatus() == GameStatus.RUNNING && !gamePlayer.isSpectator()) {
                if (event.getItem() != null) {
                    var stack = event.getItem();
                    var unhidden = ItemUtils.unhashFromInvisibleStringStartsWith(stack.as(ItemStack.class), TRACKER_PREFIX);
                    if (unhidden != null) {
                        event.setCancelled(true);

                        Tasker
                                .build(() -> {
                                    var target = MiscUtils.findTarget(game, player, Double.MAX_VALUE);
                                    if (target != null) {
                                        player.as(Player.class).setCompassTarget(target.getLocation().as(Location.class));

                                        int distance = (int) player.getLocation().as(Location.class).distance(target.getLocation().as(Location.class)); // TODO: remove this
                                        MiscUtils.sendActionBarMessage(player, Message.of(LangKeys.SPECIALS_TRACKER_TARGET_FOUND).placeholder("target", target.getDisplayName()).placeholder("distance", distance));
                                    } else {
                                        MiscUtils.sendActionBarMessage(player, Message.of(LangKeys.SPECIALS_TRACKER_NO_TARGET_FOUND));
                                        player.as(Player.class).setCompassTarget(game.getTeamOfPlayer(gamePlayer).getTeamSpawn().as(Location.class)); // TODO: remove this
                                    }
                                })
                                .afterOneTick()
                                .start();
                    }
                }
            }
        }
    }
}
