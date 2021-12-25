package org.screamingsandals.bedwars.special.listener;

import org.screamingsandals.bedwars.utils.ItemUtils;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.events.ApplyPropertyToBoughtItemEventImpl;
import org.screamingsandals.bedwars.events.PlayerBuildBlockEventImpl;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.lib.event.OnEvent;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.player.PlayerMapper;
import org.screamingsandals.lib.utils.annotations.Service;

import java.util.stream.Collectors;

@Service
public class TeamChestListener {
    private static final String TEAM_CHEST_PREFIX = "Module:TeamChest:";

    @OnEvent
    public void onTeamChestRegistered(ApplyPropertyToBoughtItemEventImpl event) {
        if (event.getPropertyName().equalsIgnoreCase("teamchest")) {
            event.setStack(ItemUtils.saveData(event.getStack(), TEAM_CHEST_PREFIX));
        }
    }

    @OnEvent
    public void onTeamChestBuilt(PlayerBuildBlockEventImpl event) {
        if (event.isCancelled()) {
            return;
        }

        var block = event.getBlock();
        var team = event.getTeam();

        if (!block.getType().isSameType("ender_chest")) {
            return;
        }

        var unhidden = ItemUtils.getIfStartsWith(event.getItemInHand(), TEAM_CHEST_PREFIX);

        if (unhidden != null || MainConfig.getInstance().node("specials", "teamchest", "turn-all-enderchests-to-teamchests").getBoolean(true)) {
            team.addTeamChest(block.getLocation());
            Message.of(LangKeys.SPECIALS_TEAM_CHEST_PLACED)
                    .prefixOrDefault(event.getGame().getCustomPrefixComponent())
                    .send(team.getPlayers().stream().map(PlayerMapper::wrapPlayer).collect(Collectors.toList()));
        }
    }
}
