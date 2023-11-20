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

package org.screamingsandals.bedwars.special;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.screamingsandals.bedwars.api.special.ProtectionWall;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.game.TeamImpl;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.lib.block.Block;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.item.ItemStack;
import org.screamingsandals.lib.item.builder.ItemStackFactory;
import org.screamingsandals.lib.spectator.Component;
import org.screamingsandals.lib.tasker.DefaultThreads;
import org.screamingsandals.lib.tasker.Tasker;
import org.screamingsandals.lib.tasker.TaskerTime;
import org.screamingsandals.lib.tasker.task.Task;
import org.screamingsandals.lib.block.BlockPlacement;

import java.util.ArrayList;
import java.util.List;

@Getter
@EqualsAndHashCode(callSuper = true)
public class ProtectionWallImpl extends SpecialItemImpl implements ProtectionWall {
    private int breakingTime;
    private int livingTime;
    private int width;
    private int height;
    private int distance;
    private boolean breakable;

    private final ItemStack item;
    private Block material;
    private List<BlockPlacement> wallBlocks;
    private Task task;

    public ProtectionWallImpl(GameImpl game, BedWarsPlayer player, TeamImpl team, ItemStack item) {
        super(game, player, team);
        this.item = item;
    }

    @Override
    public void runTask() {
        this.task = Tasker.runDelayedAndRepeatedly(DefaultThreads.GLOBAL_THREAD, () -> {
            livingTime++;
            int time = breakingTime - livingTime;

            if (!MainConfig.getInstance().node("specials", "dont-show-success-messages").getBoolean()) {
                if (time < 6 && time > 0) {
                    MiscUtils.sendActionBarMessage(player, Message.of(LangKeys.SPECIALS_PROTECTION_WALL_DESTROY).placeholder("time", time));
                }
            }

            if (livingTime == breakingTime) {
                for (var block : wallBlocks) {
                    block.location().getChunk().load(false);
                    block.block(Block.air());

                    game.getRegion().removeBlockBuiltDuringGame(block.location());
                }
                game.unregisterSpecialItem(this);
                this.task.cancel();
            }
        }, 20, TaskerTime.TICKS, 20, TaskerTime.TICKS);
    }

    private void addBlockToList(BlockPlacement block) {
        wallBlocks.add(block);
        game.getRegion().addBuiltDuringGame(block.location());
    }

    public void createWall(boolean bre, int time, int wid, int hei, int dis, Block mat) {
        breakable = bre;
        breakingTime = time;
        width = wid;
        height = hei;
        distance = dis;
        material = mat;
        wallBlocks = new ArrayList<>();

        if (width % 2 == 0) {
            player.sendMessage(Component.text("The width of a protection block has to be odd! " + width + " is not an odd number."));
            width = width + 1;
            if (width % 2 == 0) {
                return;
            }
        }

        var wallLocation = player.getLocation();
        wallLocation = wallLocation.add(wallLocation.getFacingDirection().setY(0).normalize().multiply(distance));

        var face = MiscUtils.getCardinalDirection(player.getLocation());
        var widthStart = (int) Math.floor(((double) width) / 2.0);

        for (int w = widthStart * (-1); w < width - widthStart; w++) {
            for (int h = 0; h < height; h++) {
                var wallBlock = wallLocation.clone();

                switch (face) {
                    case SOUTH:
                    case NORTH:
                    case SELF:
                        wallBlock = wallBlock.add(0, h, w);
                        break;
                    case WEST:
                    case EAST:
                        wallBlock = wallBlock.add(w, h, 0);
                        break;
                    case SOUTH_EAST:
                        wallBlock = wallBlock.add(w, h, w);
                        break;
                    case SOUTH_WEST:
                        wallBlock = wallBlock.add(w, h, w * (-1));
                        break;
                    case NORTH_EAST:
                        wallBlock = wallBlock.add(w * (-1), h, w);
                        break;
                    case NORTH_WEST:
                        wallBlock = wallBlock.add(w * (-1), h, w * (-1));
                        break;
                    default:
                        wallBlock = null;
                        break;
                }

                if (wallBlock == null) {
                    continue;
                }

                var placedBlock = wallBlock.getBlock();
                if (!placedBlock.block().isAir()) {
                    continue;
                }

                var coloredMaterial = material.colorize(team.getColor().material1_13);
                placedBlock.block(coloredMaterial);
                addBlockToList(placedBlock);
            }
        }

        if (breakingTime > 0) {
            game.registerSpecialItem(this);
            runTask();

            if (!MainConfig.getInstance().node("specials", "dont-show-success-messages").getBoolean()) {
                MiscUtils.sendActionBarMessage(player, Message.of(LangKeys.SPECIALS_PROTECTION_WALL_CREATED).placeholder("time", breakingTime));
            }

            var stack = item.withAmount(1);
            try {
                if (player.getPlayerInventory().getItemInOffHand().equals(stack)) {
                    player.getPlayerInventory().setItemInOffHand(ItemStackFactory.getAir());
                } else {
                    player.getPlayerInventory().removeItem(stack);
                }
            } catch (Throwable e) {
                player.getPlayerInventory().removeItem(stack);
            }
            player.forceUpdateInventory();
        } else {
            game.registerSpecialItem(this);

            if (!MainConfig.getInstance().node("specials", "dont-show-success-messages").getBoolean()) {
                MiscUtils.sendActionBarMessage(player, Message.of(LangKeys.SPECIALS_PROTECTION_WALL_CREATED_UNBREAKABLE));
            }

            var stack = item.withAmount(1);
            try {
                if (player.getPlayerInventory().getItemInOffHand().equals(stack)) {
                    player.getPlayerInventory().setItemInOffHand(ItemStackFactory.getAir());
                } else {
                    player.getPlayerInventory().removeItem(stack);
                }
            } catch (Throwable e) {
                player.getPlayerInventory().removeItem(stack);
            }
            player.forceUpdateInventory();
        }
    }
}
