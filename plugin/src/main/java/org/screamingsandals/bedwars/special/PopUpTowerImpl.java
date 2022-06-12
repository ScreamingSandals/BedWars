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
import org.screamingsandals.bedwars.BedWarsPlugin;
import org.screamingsandals.bedwars.api.special.PopUpTower;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.game.TeamImpl;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.bedwars.utils.ArenaUtils;
import org.screamingsandals.lib.SpecialSoundKey;
import org.screamingsandals.lib.block.BlockTypeHolder;
import org.screamingsandals.lib.spectator.sound.SoundSource;
import org.screamingsandals.lib.spectator.sound.SoundStart;
import org.screamingsandals.lib.tasker.Tasker;
import org.screamingsandals.lib.tasker.TaskerTime;
import org.screamingsandals.lib.utils.BlockFace;
import org.screamingsandals.lib.world.LocationHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@EqualsAndHashCode(callSuper = true)
public class PopUpTowerImpl extends SpecialItemImpl implements PopUpTower {
    private final static List<BlockFace> pillarSides = List.of(BlockFace.NORTH, BlockFace.EAST, BlockFace.WEST, BlockFace.SOUTH);

    private final BlockTypeHolder material;
    private final LocationHolder centerPoint;
    private final BlockFace placementFace;
    private final List<LocationHolder> entranceLocation = new ArrayList<>();
    private List<LocationHolder> targetBlocks;

    public PopUpTowerImpl(GameImpl game, BedWarsPlayer player, TeamImpl team, BlockTypeHolder mat, LocationHolder centerPoint, BlockFace placementFace) {
        super(game, player, team);
        this.material = team != null ? mat.colorize(team.getColor().material1_13) : mat;
        this.centerPoint = centerPoint;
        this.placementFace = placementFace;
    }

    private void placeBlock(LocationHolder location, BlockTypeHolder type) {
        var block = location.getBlock();
        if (isLocationSafe(location) && ArenaUtils.isInArea(block.getLocation(), game.getPos1(), game.getPos2())) {
            block.setType(type);
            this.game.getRegion().addBuiltDuringGame(block.getLocation());
            try {
                this.player.playSound(
                        SoundStart.sound(SpecialSoundKey.key("minecraft:block.stone.place"), SoundSource.BLOCK, 1f, 1f),
                        block.getLocation().getX(),
                        block.getLocation().getY(),
                        block.getLocation().getZ()
                );
            } catch (Throwable ignored) {}
        }
    }

    @Override
    public void runTask() {
        targetBlocks = game.getActiveTeams()
                .stream()
                .map(TeamImpl::getTargetBlock)
                .collect(Collectors.toList());


        final var bottomRel = this.centerPoint.add(placementFace.getOppositeFace(), 2).add(BlockFace.UP);
        entranceLocation.add(bottomRel);
        entranceLocation.add(bottomRel.add(BlockFace.UP));

        placeAnimated(BlockFace.NORTH, BlockFace.WEST);
        placeAnimated(BlockFace.SOUTH, BlockFace.WEST);
        placeAnimated(BlockFace.WEST, BlockFace.SOUTH);
        placeAnimated(BlockFace.EAST, BlockFace.SOUTH);

        Tasker.build(() -> {
            // second platform
            final var secondPlatform = centerPoint.add(BlockFace.UP, 5);
            placeBlock(secondPlatform, material);
            pillarSides.forEach(blockFace -> placeBlock(secondPlatform.add(blockFace), material));

            placeBlock(secondPlatform.add(BlockFace.NORTH_WEST), material);
            placeBlock(secondPlatform.add(BlockFace.NORTH_EAST), material);
            placeBlock(secondPlatform.add(BlockFace.SOUTH_WEST), material);
            placeBlock(secondPlatform.add(BlockFace.SOUTH_EAST), material);

            final var northWestCornerBlock = secondPlatform.add(BlockFace.NORTH_WEST, 2).add(BlockFace.UP);
            placeBlock(northWestCornerBlock.add(BlockFace.DOWN), material);
            placeBlock(northWestCornerBlock.add(BlockFace.WEST), material);
            placeBlock(northWestCornerBlock.add(BlockFace.NORTH), material);
            placeBlock(northWestCornerBlock.add(BlockFace.NORTH).add(BlockFace.UP), material);
            placeBlock(northWestCornerBlock.add(BlockFace.WEST).add(BlockFace.UP), material);

            final var northEastCornerBlock = secondPlatform.add(BlockFace.NORTH_EAST, 2).add(BlockFace.UP);
            placeBlock(northEastCornerBlock.add(BlockFace.DOWN), material);
            placeBlock(northEastCornerBlock.add(BlockFace.EAST), material);
            placeBlock(northEastCornerBlock.add(BlockFace.NORTH), material);
            placeBlock(northEastCornerBlock.add(BlockFace.NORTH).add(BlockFace.UP), material);
            placeBlock(northEastCornerBlock.add(BlockFace.EAST).add(BlockFace.UP), material);

            final var southWestCornerBlock = secondPlatform.add(BlockFace.SOUTH_WEST, 2).add(BlockFace.UP);
            placeBlock(southWestCornerBlock.add(BlockFace.DOWN), material);
            placeBlock(southWestCornerBlock.add(BlockFace.WEST), material);
            placeBlock(southWestCornerBlock.add(BlockFace.SOUTH), material);
            placeBlock(southWestCornerBlock.add(BlockFace.SOUTH).add(BlockFace.UP), material);
            placeBlock(southWestCornerBlock.add(BlockFace.WEST).add(BlockFace.UP), material);

            final var southEastCornerBlock = secondPlatform.add(BlockFace.SOUTH_EAST, 2).add(BlockFace.UP);
            placeBlock(southEastCornerBlock.add(BlockFace.DOWN), material);
            placeBlock(southEastCornerBlock.add(BlockFace.EAST), material);
            placeBlock(southEastCornerBlock.add(BlockFace.SOUTH), material);
            placeBlock(southEastCornerBlock.add(BlockFace.SOUTH).add(BlockFace.UP), material);
            placeBlock(southEastCornerBlock.add(BlockFace.EAST).add(BlockFace.UP), material);

            // connection blocks
            placeRowAnimated(3, northWestCornerBlock.add(BlockFace.NORTH), BlockFace.EAST, 1);
            placeRowAnimated(3, southWestCornerBlock.add(BlockFace.SOUTH), BlockFace.EAST, 1);
            placeRowAnimated(4, southWestCornerBlock.add(BlockFace.SOUTH_WEST), BlockFace.NORTH, 1);
            placeRowAnimated(4, southEastCornerBlock.add(BlockFace.EAST), BlockFace.NORTH, 1);

            placeBlock(secondPlatform.add(placementFace, 3).add(BlockFace.UP, 2), material);
            placeBlock(secondPlatform.add(placementFace.getOppositeFace(), 3).add(BlockFace.UP, 2), material);

            final var firstLadderBlock = centerPoint.add(placementFace);
            placeLadderRow(5, firstLadderBlock, BlockFace.UP, placementFace.getOppositeFace());
        }).delay(40L, TaskerTime.TICKS).start();
    }

    public void placeAnimated(BlockFace direction, BlockFace start) {
        final var p1 = centerPoint.add(direction, 2).add(start, 2);
        placeRowAndColumn(3, 5, p1, start.getOppositeFace());
    }

    public void placeRowAnimated(int length, LocationHolder loc, BlockFace face, int delay) {
        var lastLoc = loc;
        for (int i = 0; i < length; i++) {
            lastLoc = lastLoc.add(face);
            var finalLastLoc = lastLoc;
            Tasker.build(() -> placeBlock(finalLastLoc, material)).delay((delay += 1), TaskerTime.TICKS).start();
        }
    }

    public void placeRowAndColumn(int length, int height, LocationHolder loc, BlockFace face) {
        int sepTickedPlacement = 1;
        for (int i = 0; i < height; i++) {
            loc = loc.clone().add(0, 1, 0);
            var finalLoc = loc;
            for (int j = 0; j < length; j++) {
                placeRowAnimated(length, finalLoc, face, sepTickedPlacement);
                sepTickedPlacement += 2;
            }
        }
    }

    private boolean isTargetBlockNear(List<LocationHolder> targetBlocks, LocationHolder loc) {
        return targetBlocks.contains(loc) || Arrays.stream(BlockFace.values())
                .anyMatch(blockFace -> targetBlocks.contains(loc.add(blockFace)));
    }

    public void placeLadderRow(int length, LocationHolder loc, BlockFace face, BlockFace ladderFace) {
        var lastLoc = loc;
        for (int i = 0; i < length; i++) {
            lastLoc = lastLoc.add(face);
            final var ladder = lastLoc.getBlock();
            if (!isLocationSafe(lastLoc)) {
                continue;
            }
            var ladderType = BlockTypeHolder.of("minecraft:ladder[facing=" + ladderFace.name().toLowerCase() + "]");
            ladder.setType(ladderType);
            game.getRegion().removeBlockBuiltDuringGame(lastLoc);
            game.getRegion().addBuiltDuringGame(lastLoc);
            try {
                this.player.playSound(
                        SoundStart.sound(SpecialSoundKey.key("minecraft:block.ladder.place"), SoundSource.BLOCK, 1f, 1f),
                        loc.getX(),
                        loc.getY(),
                        loc.getZ()
                );
            } catch (Throwable ignored) {}
        }
    }

    public boolean isLocationSafe(LocationHolder location) {
        final var locBlock = location.getBlock();
        return (locBlock.getType().isAir() || BedWarsPlugin.isBreakableBlock(location.getBlock().getType()) || game.getRegion().isLocationModifiedDuringGame(location)) && !isTargetBlockNear(targetBlocks, location) && !isEntranceLocation(location);
    }

    public boolean isEntranceLocation(LocationHolder toCheck) {
        return entranceLocation.contains(toCheck);
    }
}
