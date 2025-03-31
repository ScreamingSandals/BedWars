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

package org.screamingsandals.bedwars.special;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.api.special.BridgeEgg;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.game.TeamImpl;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.bedwars.utils.ArenaUtils;
import org.screamingsandals.lib.block.BlockPlacement;
import org.screamingsandals.lib.block.Block;
import org.screamingsandals.lib.entity.Entity;
import org.screamingsandals.lib.spectator.sound.SoundSource;
import org.screamingsandals.lib.spectator.sound.SoundStart;
import org.screamingsandals.lib.tasker.DefaultThreads;
import org.screamingsandals.lib.tasker.Tasker;
import org.screamingsandals.lib.tasker.TaskerTime;
import org.screamingsandals.lib.tasker.task.Task;
import org.screamingsandals.lib.utils.ResourceLocation;
import org.screamingsandals.lib.world.Location;

@Getter
@EqualsAndHashCode(callSuper = true)
public class BridgeEggImpl extends SpecialItemImpl implements BridgeEgg {
    private final double distance;
    private final double distanceSquared;
    private final Entity projectile;
    private final Block material;
    private Task task;

    public BridgeEggImpl(GameImpl game, BedWarsPlayer player, TeamImpl team, Entity projectile, Block mat, double distance) {
        super(game, player, team);
        this.projectile = projectile;
        material = team != null ? mat.colorize(team.getColor().material1_13) : mat;
        this.distance = distance;
        distanceSquared = distance * distance;
    }

    private void setBlock(BlockPlacement block) {
        if (block.block().isAir() && ArenaUtils.isInArea(block.location(), game.getPos1(), game.getPos2())) {
            block.block(material);
            game.getRegion().addBuiltDuringGame(block.location());
            player.playSound(
                    SoundStart.sound(ResourceLocation.of("minecraft:entity.chicken.egg"), SoundSource.AMBIENT, 1f, 1f),
                    block.location().getX(),
                    block.location().getY(),
                    block.location().getZ()
            );
        }
    }

    @Override
    public void runTask() {
        task = Tasker.runRepeatedly(DefaultThreads.GLOBAL_THREAD, taskBase -> {
            final Location projectileLocation = projectile.getLocation();

            if (!player.isInGame() || projectile.isDead() || team.isDead() || game.getStatus() != GameStatus.RUNNING) {
                taskBase.cancel();
                return;
            }

            if (projectileLocation.getDistanceSquared(player.getLocation()) > distanceSquared) {
                taskBase.cancel();
                return;
            }

            setBlock(projectileLocation.subtract(0.0D, 3.0D, 0.0D).getBlock());
            setBlock(projectileLocation.subtract(1.0D, 3.0D, 0.0D).getBlock());
            setBlock(projectileLocation.subtract(0.0D, 3.0D, 1.0D).getBlock());
        }, 1, TaskerTime.TICKS);
    }
}
