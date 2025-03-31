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

package org.screamingsandals.bedwars.game.target;

import lombok.Data;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.game.TeamImpl;

// currently used only in one case
@Data
public class TargetBlockDestroyedInfo {
    private final TeamImpl team;
    private final boolean isItBedBlock;
    private final boolean isItAnchor;
    private final boolean isItCake;

    public TargetBlockDestroyedInfo(GameImpl game, TeamImpl team) {
        this.team = team;
        if (team.getTarget() instanceof TargetBlockImpl) {
            var block = ((TargetBlockImpl) team.getTarget()).getTargetBlock().getBlock();
            this.isItBedBlock = game.getRegion().isBedBlock(block.blockSnapshot());
            this.isItAnchor = block.block().isSameType("respawn_anchor");
            this.isItCake = block.block().isSameType("cake");
        } else {
            this.isItBedBlock = this.isItAnchor = this.isItCake = false;
        }
    }
}
