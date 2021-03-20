package org.screamingsandals.bedwars.special.listener;

import org.screamingsandals.bedwars.api.APIUtils;
import org.screamingsandals.bedwars.api.RunningTeam;
import org.screamingsandals.bedwars.api.events.BedwarsApplyPropertyToBoughtItem;
import org.screamingsandals.bedwars.api.events.BedwarsPlayerBuildBlock;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.player.PlayerMapper;

import java.util.stream.Collectors;

public class TeamChestListener implements Listener {

    public static final String TEAM_CHEST_PREFIX = "Module:TeamChest:";

    @EventHandler
    public void onMagnetShoesRegistered(BedwarsApplyPropertyToBoughtItem event) {
        if (event.getPropertyName().equalsIgnoreCase("teamchest")) {
            ItemStack stack = event.getStack();

            APIUtils.hashIntoInvisibleString(stack, TEAM_CHEST_PREFIX);
        }
    }

    @EventHandler
    public void onTeamChestBuilt(BedwarsPlayerBuildBlock event) {
        if (event.isCancelled()) {
            return;
        }

        Block block = event.getBlock();
        RunningTeam team = event.getTeam();

        if (block.getType() != Material.ENDER_CHEST) {
            return;
        }

        String unhidden = APIUtils.unhashFromInvisibleStringStartsWith(event.getItemInHand(), TEAM_CHEST_PREFIX);

        if (unhidden != null || MainConfig.getInstance().node("specials", "teamchest", "turn-all-enderchests-to-teamchests").getBoolean()) {
            team.addTeamChest(block);
            Message
                    .of(LangKeys.SPECIALS_TEAM_CHEST_PLACED)
                    .prefixOrDefault(((Game) event.getGame()).getCustomPrefixComponent())
                    .send(team.getConnectedPlayers().stream().map(PlayerMapper::wrapPlayer).collect(Collectors.toList()));
        }
    }

}
