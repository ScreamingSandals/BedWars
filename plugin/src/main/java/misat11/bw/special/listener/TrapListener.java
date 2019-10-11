package misat11.bw.special.listener;

import misat11.bw.Main;
import misat11.bw.api.APIUtils;
import misat11.bw.api.GameStatus;
import misat11.bw.api.RunningTeam;
import misat11.bw.api.events.BedwarsApplyPropertyToBoughtItem;
import misat11.bw.api.events.BedwarsPlayerBreakBlock;
import misat11.bw.api.events.BedwarsPlayerBuildBlock;
import misat11.bw.api.special.SpecialItem;
import misat11.bw.game.Game;
import misat11.bw.game.GamePlayer;
import misat11.bw.special.Trap;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

import static misat11.lib.lang.I18n.i18n;

public class TrapListener implements Listener {
    private static final String TRAP_PREFIX = "Module:Trap:";

    @EventHandler
    public void onTrapRegistered(BedwarsApplyPropertyToBoughtItem event) {
        if (event.getPropertyName().equalsIgnoreCase("trap")) {
            ItemStack stack = event.getStack();
            Trap trap = new Trap(event.getGame(), event.getPlayer(),
                    event.getGame().getTeamOfPlayer(event.getPlayer()),
                    (List<Map<String, Object>>) event.getProperty("data"));

            int id = System.identityHashCode(trap);
            String trapString = TRAP_PREFIX + id;

            APIUtils.hashIntoInvisibleString(stack, trapString);
        }

    }

    @EventHandler
    public void onTrapBuild(BedwarsPlayerBuildBlock event) {
        if (event.isCancelled()) {
            return;
        }

        ItemStack trapItem = event.getItemInHand();
        String unhidden = APIUtils.unhashFromInvisibleStringStartsWith(trapItem, TRAP_PREFIX);
        if (unhidden != null) {
            int classID = Integer.parseInt(unhidden.split(":")[2]);

            for (SpecialItem special : event.getGame().getActivedSpecialItems(Trap.class)) {
                Trap trap = (Trap) special;
                if (System.identityHashCode(trap) == classID) {
                    trap.place(event.getBlock().getLocation());
                    event.getPlayer().sendMessage(i18n("trap_built"));
                    return;
                }
            }
        }

    }

    @EventHandler
    public void onTrapBreak(BedwarsPlayerBreakBlock event) {
        Player player = event.getPlayer();
        if (!Main.isPlayerInGame(player)) {
            return;
        }

        if (!Main.isPlayerInGame(event.getPlayer())) {
            return;
        }

        for (SpecialItem special : event.getGame().getActivedSpecialItems(Trap.class)) {
            Trap trapBlock = (Trap) special;
            RunningTeam runningTeam = event.getTeam();

            if (trapBlock.isPlaced()
                    && event.getBlock().getLocation().equals(trapBlock.getLocation())) {
                trapBlock.process(event.getPlayer(), runningTeam, true);
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (event.isCancelled() || !Main.isPlayerInGame(player)) {
            return;
        }

        double difX = Math.abs(event.getFrom().getX() - event.getTo().getX());
        double difZ = Math.abs(event.getFrom().getZ() - event.getTo().getZ());

        if (difX == 0.0 && difZ == 0.0) {
            return;
        }

        GamePlayer gPlayer = Main.getPlayerGameProfile(player);
        Game game = gPlayer.getGame();
        if (game.getStatus() == GameStatus.RUNNING && !gPlayer.isSpectator) {
            for (SpecialItem special : game.getActivedSpecialItems(Trap.class)) {
                Trap trapBlock = (Trap) special;

                if (trapBlock.isPlaced()) {
                    if (game.getTeamOfPlayer(player) != trapBlock.getTeam()) {
                        if (event.getTo().getBlock().getLocation().equals(trapBlock.getLocation())) {
                            trapBlock.process(player, game.getPlayerTeam(gPlayer), false);
                        }
                    }
                }
            }
        }
    }
}
