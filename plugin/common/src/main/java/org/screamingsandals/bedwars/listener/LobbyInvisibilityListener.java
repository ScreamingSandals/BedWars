/*
 * Copyright (C) 2023 ScreamingSandals
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

package org.screamingsandals.bedwars.listener;

import org.screamingsandals.bedwars.api.config.GameConfigurationContainer;
import org.screamingsandals.bedwars.events.GameStartedEventImpl;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.lib.block.Block;
import org.screamingsandals.lib.event.OnEvent;
import org.screamingsandals.lib.utils.annotations.Service;

import java.util.Objects;

@Service
public class LobbyInvisibilityListener {

    @OnEvent
    public void onGameStarted(GameStartedEventImpl event) {
        final var game = event.getGame();
        if (game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.INVISIBLE_LOBBY_ON_GAME_START, true)) {
            if (game.getLobbyPos1() != null && game.getLobbyPos2() != null) {
                MiscUtils.getLocationsBetween(game.getLobbyPos1(), game.getLobbyPos2()).forEach(loc -> {
                    if (!loc.getBlock().block().isAir()) {
                        final var block = loc.getBlock();
                        final var blockState = Objects.requireNonNull(block.blockSnapshot());
                        game.getRegion().putOriginalBlock(loc, blockState);
                        block.alterBlockWithoutPhysics(Block.air());
                    }
                });
            }
        }
    }


}
