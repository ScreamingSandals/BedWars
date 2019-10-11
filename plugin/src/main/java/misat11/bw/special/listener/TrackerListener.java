package misat11.bw.special.listener;

import misat11.bw.Main;
import misat11.bw.api.APIUtils;
import misat11.bw.api.game.Game;
import misat11.bw.api.game.GameStatus;
import misat11.bw.api.events.BedwarsApplyPropertyToBoughtItem;
import misat11.bw.game.GamePlayer;
import misat11.bw.special.Tracker;
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
        if (!Main.isPlayerInGame(player)) {
            return;
        }

        GamePlayer gamePlayer = Main.getPlayerGameProfile(player);
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
