/*
 * Copyright (C) 2025 ScreamingSandals
 *
 * This file is part of Screaming BedWars.
 *
 * Screaming BedWars is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Screaming BedWars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Screaming BedWars. If not, see <https://www.gnu.org/licenses/>.
 */

package org.screamingsandals.bedwars.special.listener;

import org.screamingsandals.bedwars.utils.ItemUtils;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.events.ApplyPropertyToBoughtItemEventImpl;
import org.screamingsandals.bedwars.events.PlayerBuildBlockEventImpl;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.lib.event.OnEvent;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.player.Players;
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

        if (!block.block().isSameType("ender_chest")) {
            return;
        }

        var unhidden = ItemUtils.getIfStartsWith(event.getItemInHand(), TEAM_CHEST_PREFIX);

        if (unhidden != null || MainConfig.getInstance().node("specials", "teamchest", "turn-all-enderchests-to-teamchests").getBoolean(true)) {
            team.addTeamChest(block.location());
            Message.of(LangKeys.SPECIALS_TEAM_CHEST_PLACED)
                    .prefixOrDefault(event.getGame().getCustomPrefixComponent())
                    .send(team.getPlayers().stream().map(Players::wrapPlayer).collect(Collectors.toList()));
        }
    }
}
