package org.screamingsandals.bedwars.special.listener;

import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.APIUtils;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.api.RunningTeam;
import org.screamingsandals.bedwars.api.events.BedwarsApplyPropertyToBoughtItem;
import org.screamingsandals.bedwars.api.events.BedwarsPlayerBreakBlock;
import org.screamingsandals.bedwars.api.events.BedwarsPlayerBuildBlock;
import org.screamingsandals.bedwars.api.special.SpecialItem;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.player.PlayerManager;
import org.screamingsandals.bedwars.special.Trap;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.player.PlayerMapper;

import java.util.List;
import java.util.Map;

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
                    PlayerMapper.wrapPlayer(event.getPlayer()).sendMessage(Message.of(LangKeys.SPECIALS_TRAP_BUILT).prefixOrDefault(((Game) event.getGame()).getCustomPrefixComponent()));
                    return;
                }
            }
        }

    }

    @EventHandler
    public void onTrapBreak(BedwarsPlayerBreakBlock event) {
        final Player player = event.getPlayer();
        if (!PlayerManager.getInstance().isPlayerInGame(player.getUniqueId())) {
            return;
        }

        for (SpecialItem special : event.getGame().getActivedSpecialItems(Trap.class)) {
            Trap trapBlock = (Trap) special;
            RunningTeam runningTeam = event.getTeam();

            if (trapBlock.isPlaced()
                    && event.getBlock().getLocation().equals(trapBlock.getLocation())) {
                event.setDrops(false);
                trapBlock.process(event.getPlayer(), runningTeam, true);
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (event.isCancelled() || !PlayerManager.getInstance().isPlayerInGame(player.getUniqueId())) {
            return;
        }

        double difX = Math.abs(event.getFrom().getX() - event.getTo().getX());
        double difZ = Math.abs(event.getFrom().getZ() - event.getTo().getZ());

        if (difX == 0.0 && difZ == 0.0) {
            return;
        }

        BedWarsPlayer gPlayer = PlayerManager.getInstance().getPlayer(player.getUniqueId()).orElseThrow();
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
