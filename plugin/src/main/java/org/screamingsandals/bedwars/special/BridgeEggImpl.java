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

package org.screamingsandals.bedwars.special;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.kyori.adventure.sound.Sound;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.api.special.BridgeEgg;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.game.TeamImpl;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.bedwars.utils.ArenaUtils;
import org.screamingsandals.lib.SpecialSoundKey;
import org.screamingsandals.lib.block.BlockHolder;
import org.screamingsandals.lib.block.BlockTypeHolder;
import org.screamingsandals.lib.entity.EntityProjectile;
import org.screamingsandals.lib.tasker.Tasker;
import org.screamingsandals.lib.tasker.TaskerTime;
import org.screamingsandals.lib.tasker.task.TaskerTask;
import org.screamingsandals.lib.utils.MathUtils;
import org.screamingsandals.lib.world.LocationHolder;

@Getter
@EqualsAndHashCode(callSuper = true)
public class BridgeEggImpl extends SpecialItem implements BridgeEgg<GameImpl, BedWarsPlayer, TeamImpl, EntityProjectile, BlockTypeHolder> {
    private final double distance;
    private final double distanceSquared;
    private final EntityProjectile projectile;
    private final BlockTypeHolder material;
    private TaskerTask task;

    public BridgeEggImpl(GameImpl game, BedWarsPlayer player, TeamImpl team, EntityProjectile projectile, BlockTypeHolder mat, double distance) {
        super(game, player, team);
        this.projectile = projectile;
        material = team != null ? mat.colorize(team.getColor().material1_13) : mat;
        this.distance = distance;
        distanceSquared = MathUtils.square(distance);
    }

    private void setBlock(BlockHolder block) {
        if (block.getType().isAir() && ArenaUtils.isInArea(block.getLocation(), game.getPos1(), game.getPos2())) {
            block.setType(material);
            game.getRegion().addBuiltDuringGame(block.getLocation());
            player.playSound(
                    Sound.sound(SpecialSoundKey.key("minecraft:entity.chicken.egg"), Sound.Source.AMBIENT, 1f, 1f),
                    block.getLocation().getX(),
                    block.getLocation().getY(),
                    block.getLocation().getZ()
            );
        }
    }

    @Override
    public void runTask() {
        task = Tasker.build(taskBase -> () -> {
            final LocationHolder projectileLocation = projectile.getLocation();

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
        }).repeat(1, TaskerTime.TICKS).start();
    }
}
