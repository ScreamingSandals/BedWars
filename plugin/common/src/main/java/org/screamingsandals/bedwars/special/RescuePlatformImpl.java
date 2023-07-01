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
import org.screamingsandals.bedwars.api.special.RescuePlatform;
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
import org.screamingsandals.lib.tasker.DefaultThreads;
import org.screamingsandals.lib.tasker.Tasker;
import org.screamingsandals.lib.tasker.TaskerTime;
import org.screamingsandals.lib.tasker.task.Task;
import org.screamingsandals.lib.utils.BlockFace;
import org.screamingsandals.lib.block.BlockPlacement;

import java.util.ArrayList;
import java.util.List;

@Getter
@EqualsAndHashCode(callSuper = true)
public class RescuePlatformImpl extends SpecialItemImpl implements RescuePlatform {
    private final ItemStack item;
    private List<BlockPlacement> platformBlocks;
    private Block material;
    private boolean breakable;
    private int breakingTime;
    private int livingTime;
    private Task task;

    public RescuePlatformImpl(GameImpl game, BedWarsPlayer player, TeamImpl team, ItemStack item) {
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
                    MiscUtils.sendActionBarMessage(player, Message.of(LangKeys.SPECIALS_RESCUE_PLATFORM_DESTROY).placeholder("time", time));
                }
            }

            if (livingTime == breakingTime) {
                for (var block : List.copyOf(platformBlocks)) {
                    block.location().getChunk().load(false);
                    block.block(Block.air());

                    removeBlockFromList(block);
                    game.getRegion().removeBlockBuiltDuringGame(block.location());

                }
                game.unregisterSpecialItem(this);
                this.task.cancel();
            }
        }, 20, TaskerTime.TICKS, 20, TaskerTime.TICKS);
    }

    private void addBlockToList(BlockPlacement block) {
        platformBlocks.add(block);
        game.getRegion().addBuiltDuringGame(block.location());
    }

    private void removeBlockFromList(BlockPlacement block) {
        platformBlocks.remove(block);
        game.getRegion().removeBlockBuiltDuringGame(block.location());
    }

    public void createPlatform(boolean bre, int time, int dist, Block bMat) {
        breakable = bre;
        breakingTime = time;
        material = bMat;
        platformBlocks = new ArrayList<>();

        var center = player.getLocation().clone();
        center = center.withY(center.getY() - dist);

        for (var blockFace : BlockFace.values()) {
            if (blockFace.equals(BlockFace.DOWN) || blockFace.equals(BlockFace.UP)) {
                continue;
            }

            var placedBlock = center.add(blockFace.getDirection()).getBlock();
            if (!placedBlock.block().isAir()) {
                continue;
            }

            var coloredMatrerial = material.colorize(team.getColor().material1_13);
            placedBlock.block(coloredMatrerial);
            addBlockToList(placedBlock);
        }

        if (breakingTime > 0) {
            game.registerSpecialItem(this);
            runTask();

            if (!MainConfig.getInstance().node("specials", "dont-show-success-messages").getBoolean()) {
                MiscUtils.sendActionBarMessage(player, Message.of(LangKeys.SPECIALS_RESCUE_PLATFORM_CREATED).placeholder("time", breakingTime));
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
                MiscUtils.sendActionBarMessage(player, Message.of(LangKeys.SPECIALS_RESCUE_PLATFORM_CREATED_UNBREAKABLE));
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
