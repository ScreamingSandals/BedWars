package org.screamingsandals.bedwars.special.listener;

import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.APIUtils;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.api.events.BedwarsApplyPropertyToBoughtItem;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.bedwars.player.PlayerManager;
import org.screamingsandals.bedwars.special.Tracker;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class TrackerListener implements Listener {
    private static final String TRACKER_PREFIX = "Module:Tracker:";

    @EventHandler
    public void onTrackerRegistered(BedwarsApplyPropertyToBoughtItem event) {
        if (event.getPropertyName().equalsIgnoreCase("tracker")) {
            ItemStack stack = event.getStack();

            APIUtils.hashIntoInvisibleString(stack, TRACKER_PREFIX);
        }

    }

    @EventHandler
    public void onTrackerUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!PlayerManager.getInstance().isPlayerInGame(player.getUniqueId())) {
            return;
        }

        BedWarsPlayer gamePlayer = PlayerManager.getInstance().getPlayer(player.getUniqueId()).orElseThrow();
        Game game = gamePlayer.getGame();
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (game.getStatus() == GameStatus.RUNNING && !gamePlayer.isSpectator) {
                if (event.getItem() != null) {
                    ItemStack stack = event.getItem();
                    String unhidden = APIUtils.unhashFromInvisibleStringStartsWith(stack, TRACKER_PREFIX);
                    if (unhidden != null) {
                        event.setCancelled(true);

                        Tracker tracker = new Tracker(game, player, game.getTeamOfPlayer(player));
                        tracker.runTask();
                    }
                }
            }
        }
    }
}
