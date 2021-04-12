package org.screamingsandals.bedwars.special.listener;

import org.screamingsandals.bedwars.api.APIUtils;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.events.ApplyPropertyToBoughtItemEventImpl;
import org.screamingsandals.bedwars.events.PlayerBuildBlockEventImpl;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.lib.event.OnEvent;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.player.PlayerMapper;
import org.screamingsandals.lib.utils.annotations.Service;

import java.util.stream.Collectors;

@Service
public class TeamChestListener {

    public static final String TEAM_CHEST_PREFIX = "Module:TeamChest:";

    @OnEvent
    public void onTeamChestRegistered(ApplyPropertyToBoughtItemEventImpl event) {
        if (event.getPropertyName().equalsIgnoreCase("teamchest")) {
            var stack = event.getStack().as(ItemStack.class); // TODO: get rid of this transformation

            APIUtils.hashIntoInvisibleString(stack, TEAM_CHEST_PREFIX);
            event.setStack(stack);
        }
    }

    @OnEvent
    public void onTeamChestBuilt(PlayerBuildBlockEventImpl event) {
        if (event.isCancelled()) {
            return;
        }

        var block = event.getBlock();
        var team = event.getTeam();

        if (!block.getType().is("ender_chest")) {
            return;
        }

        String unhidden = APIUtils.unhashFromInvisibleStringStartsWith(event.getItemInHand().as(ItemStack.class), TEAM_CHEST_PREFIX);

        if (unhidden != null || MainConfig.getInstance().node("specials", "teamchest", "turn-all-enderchests-to-teamchests").getBoolean()) {
            team.addTeamChest(block.as(Block.class));
            Message
                    .of(LangKeys.SPECIALS_TEAM_CHEST_PLACED)
                    .prefixOrDefault(((Game) event.getGame()).getCustomPrefixComponent())
                    .send(team.getConnectedPlayers().stream().map(PlayerMapper::wrapPlayer).collect(Collectors.toList()));
        }
    }

}
