package org.screamingsandals.bedwars.special;

import lombok.Getter;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.screamingsandals.bedwars.BedWarsPlugin;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.api.special.PopUpTower;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.game.TeamImpl;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.bedwars.utils.ArenaUtils;
import org.screamingsandals.lib.block.BlockTypeHolder;
import org.screamingsandals.lib.tasker.Tasker;
import org.screamingsandals.lib.tasker.TaskerTime;
import org.screamingsandals.lib.utils.BlockFace;
import org.screamingsandals.lib.world.LocationHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class PopUpTowerImpl extends SpecialItem implements PopUpTower<GameImpl, BedWarsPlayer, TeamImpl> {
    private final static List<BlockFace> pillarSides = List.of(BlockFace.NORTH, BlockFace.EAST, BlockFace.WEST, BlockFace.SOUTH);

    private final BlockTypeHolder material;
    private final LocationHolder centerPoint;
    private final BlockFace placementFace;
    private final List<LocationHolder> entranceLocation = new ArrayList<>();
    private List<LocationHolder> targetBlocks;

    public PopUpTowerImpl(GameImpl game, BedWarsPlayer player, TeamImpl team, BlockTypeHolder mat, LocationHolder centerPoint, BlockFace placementFace) {
        super(game, player, team);
        this.material = mat.colorize(team.getColor().material1_13);
        System.out.println(this.material);
        this.centerPoint = centerPoint;
        this.placementFace = placementFace;
    }

    private void placeBlock(LocationHolder location, BlockTypeHolder type) {
        var block = location.getBlock();
        if (isLocationSafe(location) && ArenaUtils.isInArea(block.getLocation(), game.getPos1(), game.getPos2())) {
            block.setType(type);
            this.game.getRegion().addBuiltDuringGame(block.getLocation());
            this.player.playSound(
                    Sound.sound(Key.key("minecraft:block.stone.place"), Sound.Source.AMBIENT, 1f, 1f),
                    block.getLocation().getX(),
                    block.getLocation().getY(),
                    block.getLocation().getZ()
            );
        }
    }

    @Override
    public void runTask() {
        targetBlocks = game.getActiveTeams()
                .stream()
                .map(TeamImpl::getTargetBlock)
                .collect(Collectors.toList());

        final var bottomRel = this.centerPoint.add(placementFace.getOppositeFace().getDirection().multiply(2)).add(BlockFace.UP.getDirection());
        entranceLocation.add(bottomRel);
        entranceLocation.add(bottomRel.add(BlockFace.UP.getDirection()));

        placeAnimated(BlockFace.NORTH, BlockFace.WEST);
        placeAnimated(BlockFace.SOUTH, BlockFace.WEST);
        placeAnimated(BlockFace.WEST, BlockFace.SOUTH);
        placeAnimated(BlockFace.EAST, BlockFace.SOUTH);

        Tasker.build(() -> {
            // second platform
            final var secondPlatform = centerPoint.add(BlockFace.UP.getDirection().multiply(5));
            placeBlock(secondPlatform, material);
            pillarSides.forEach(blockFace -> placeBlock(secondPlatform.add(blockFace.getDirection()), material));

            placeBlock(secondPlatform.add(BlockFace.NORTH_WEST.getDirection()), material);
            placeBlock(secondPlatform.add(BlockFace.NORTH_EAST.getDirection()), material);
            placeBlock(secondPlatform.add(BlockFace.SOUTH_WEST.getDirection()), material);
            placeBlock(secondPlatform.add(BlockFace.SOUTH_EAST.getDirection()), material);

            final var northWestCornerBlock = secondPlatform.add(BlockFace.NORTH_WEST.getDirection().multiply(2)).add(BlockFace.UP.getDirection());
            placeBlock(northWestCornerBlock.add(BlockFace.DOWN.getDirection()), material);
            placeBlock(northWestCornerBlock.add(BlockFace.WEST.getDirection()), material);
            placeBlock(northWestCornerBlock.add(BlockFace.NORTH.getDirection()), material);
            placeBlock(northWestCornerBlock.add(BlockFace.WEST.getDirection()).add(BlockFace.UP.getDirection()), material);
            placeBlock(northWestCornerBlock.add(BlockFace.NORTH.getDirection()).add(BlockFace.UP.getDirection()), material);

            final var northEastCornerBlock = secondPlatform.add(BlockFace.NORTH_EAST.getDirection().multiply(2)).add(BlockFace.UP.getDirection());
            placeBlock(northEastCornerBlock.add(BlockFace.DOWN.getDirection()), material);
            placeBlock(northEastCornerBlock.add(BlockFace.EAST.getDirection()), material);
            placeBlock(northEastCornerBlock.add(BlockFace.NORTH.getDirection()), material);
            placeBlock(northEastCornerBlock.add(BlockFace.NORTH.getDirection()).add(BlockFace.UP.getDirection()), material);
            placeBlock(northEastCornerBlock.add(BlockFace.EAST.getDirection()).add(BlockFace.UP.getDirection()), material);

            final var southWestCornerBlock = secondPlatform.add(BlockFace.SOUTH_WEST.getDirection().multiply(2)).add(BlockFace.UP.getDirection());
            placeBlock(southWestCornerBlock.add(BlockFace.DOWN.getDirection()), material);
            placeBlock(southWestCornerBlock.add(BlockFace.WEST.getDirection()), material);
            placeBlock(southWestCornerBlock.add(BlockFace.SOUTH.getDirection()), material);
            placeBlock(southWestCornerBlock.add(BlockFace.SOUTH.getDirection()).add(BlockFace.UP.getDirection()), material);
            placeBlock(southWestCornerBlock.add(BlockFace.WEST.getDirection()).add(BlockFace.UP.getDirection()), material);

            final var southEastCornerBlock = secondPlatform.add(BlockFace.SOUTH_EAST.getDirection().multiply(2)).add(BlockFace.UP.getDirection());
            placeBlock(southEastCornerBlock.add(BlockFace.DOWN.getDirection()), material);
            placeBlock(southEastCornerBlock.add(BlockFace.EAST.getDirection()), material);
            placeBlock(southEastCornerBlock.add(BlockFace.SOUTH.getDirection()), material);
            placeBlock(southEastCornerBlock.add(BlockFace.EAST.getDirection()).add(BlockFace.UP.getDirection()), material);
            placeBlock(southEastCornerBlock.add(BlockFace.SOUTH.getDirection()).add(BlockFace.UP.getDirection()), material);

            // connection blocks
            placeRowAnimated(3, northWestCornerBlock.add(BlockFace.NORTH.getDirection()), BlockFace.EAST, 1);
            placeRowAnimated(3, southWestCornerBlock.add(BlockFace.SOUTH.getDirection()), BlockFace.EAST, 1);
            placeRowAnimated(4, southWestCornerBlock.add(BlockFace.SOUTH_WEST.getDirection()), BlockFace.NORTH, 1);
            placeRowAnimated(4, southEastCornerBlock.add(BlockFace.EAST.getDirection()), BlockFace.NORTH, 1);

            placeBlock(secondPlatform.add(placementFace.getDirection().multiply(3)).add(BlockFace.UP.getDirection().multiply(2)), material);
            placeBlock(secondPlatform.add(placementFace.getOppositeFace().getDirection().multiply(3)).add(BlockFace.UP.getDirection().multiply(2)), material);

            final var firstLadderBlock = centerPoint.add(placementFace.getDirection());
            placeLadderRow(5, firstLadderBlock, BlockFace.UP, placementFace.getOppositeFace());
        }).delay(40L, TaskerTime.TICKS).start();
    }

    public void placeAnimated(BlockFace direction, BlockFace start) {
        final var p1 = centerPoint.add(direction.getDirection().multiply(2)).add(start.getDirection().multiply(2));
        placeRowAndColumn(3, 5, p1, start.getOppositeFace());
    }

    public void placeRowAnimated(int length, LocationHolder loc, BlockFace face, int delay) {
        var lastLoc = loc;
        for (int i = 0; i < length; i++) {
            lastLoc = lastLoc.add(face.getDirection());
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
                .anyMatch(blockFace -> targetBlocks.contains(loc.add(blockFace.getDirection())));
    }

    public void placeLadderRow(int length, LocationHolder loc, BlockFace face, BlockFace ladderFace) {
        if (game.getStatus() != GameStatus.RUNNING) {
            return;
        }

        var lastLoc = loc;
        for (int i = 0; i < length; i++) {
            lastLoc = lastLoc.add(face.getDirection());
            final var ladder = lastLoc.getBlock();
            if (!isLocationSafe(lastLoc)) {
                continue;
            }
            var ladderType = BlockTypeHolder.of("minecraft:ladder[facing=" + ladderFace.name().toLowerCase() + "]");
            ladder.setType(ladderType);
            game.getRegion().removeBlockBuiltDuringGame(lastLoc);
            game.getRegion().addBuiltDuringGame(lastLoc);
            this.player.playSound(
                    Sound.sound(Key.key("minecraft:block.ladder.place"), Sound.Source.AMBIENT, 1f, 1f),
                    loc.getX(),
                    loc.getY(),
                    loc.getZ()
            );
        }
    }

    public boolean isLocationSafe(LocationHolder location) {
        final var locBlock = location.getBlock();
        return (locBlock.getType().isAir() || BedWarsPlugin.isBreakableBlock(location.getBlock().getType()) || game.getRegion().isBlockAddedDuringGame(location)) && !isTargetBlockNear(targetBlocks, location) && !isEntranceLocation(location);
    }

    public boolean isEntranceLocation(LocationHolder toCheck) {
        return entranceLocation.contains(toCheck);
    }
}
