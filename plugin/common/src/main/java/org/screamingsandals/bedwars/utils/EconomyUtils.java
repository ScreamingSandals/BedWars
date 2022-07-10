/*
 * Copyright (C) 2022 ScreamingSandals
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

package org.screamingsandals.bedwars.utils;

import lombok.experimental.UtilityClass;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.lib.economy.EconomyManager;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.player.PlayerWrapper;

@UtilityClass
public class EconomyUtils {
    public void deposit(PlayerWrapper player, double coins) {
        if (MainConfig.getInstance().node("economy", "enabled").getBoolean()) {
            if (EconomyManager.depositPlayer(player, coins).isSuccessful()) {
                Message.of(LangKeys.IN_GAME_ECONOMY_DEPOSITED)
                        .defaultPrefix()
                        .placeholder("coins", coins)
                        .placeholder("currency", EconomyManager.getCurrencyNameSingular())
                        .send(player);
            }
        }
    }
}
