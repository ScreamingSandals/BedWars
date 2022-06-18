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

package org.screamingsandals.bedwars.game;

import lombok.Getter;
import lombok.Setter;
import org.screamingsandals.bedwars.api.Team;
import org.screamingsandals.bedwars.api.config.GameConfigurationContainer;
import org.screamingsandals.bedwars.api.player.BWPlayer;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.lib.SpecialSoundKey;
import org.screamingsandals.lib.block.BlockMapper;
import org.screamingsandals.lib.container.Container;
import org.screamingsandals.lib.container.ContainerFactory;
import org.screamingsandals.lib.container.type.InventoryTypeHolder;
import org.screamingsandals.lib.hologram.Hologram;
import org.screamingsandals.lib.hologram.HologramManager;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.spectator.sound.SoundSource;
import org.screamingsandals.lib.spectator.sound.SoundStart;
import org.screamingsandals.lib.tasker.Tasker;
import org.screamingsandals.lib.tasker.TaskerTime;
import org.screamingsandals.lib.world.LocationHolder;
import org.screamingsandals.lib.world.LocationMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Getter
@Setter
public class TeamImpl implements Team {
    private TeamColorImpl color;
    private String name;
    private LocationHolder targetBlock;
    private LocationHolder teamSpawn;
    private int maxPlayers;
    private GameImpl game;

    private Container teamChestInventory;
    private final List<LocationHolder> chests = new ArrayList<>();
    private boolean started;
    private boolean targetBlockIntact;
    private final List<BedWarsPlayer> players = new ArrayList<>();
    private Hologram hologram;
    private Hologram protectHologram;

    public void start() {
        if (started) {
            return;
        }

        // Check target blocks existence
        if (targetBlock.getBlock().getType().isAir()) {
            var placedBlock = targetBlock.getBlock();
            placedBlock.setType(color.getWoolBlockType());
        }

        // anchor wars
        var block = targetBlock.getBlock();
        if (block.getType().isSameType("respawn_anchor")) {
            Tasker.build(() -> {
                        var anchor = block.getType();
                        block.setType(anchor.with("charges", "0"));
                        if (game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.TARGET_BLOCK_RESPAWN_ANCHOR_FILL_ON_START, false)) {
                            var atomic = new AtomicInteger();
                            Tasker.build(taskBase -> () -> {
                                var charges = atomic.incrementAndGet();
                                if (charges > 4) {
                                    taskBase.cancel();
                                    return;
                                }
                                block.setType(anchor.with("charges", String.valueOf(charges)));
                                targetBlock.getWorld().playSound(SoundStart.sound(
                                        SpecialSoundKey.key(MainConfig.getInstance().node("target-block", "respawn-anchor", "sound", "charge").getString("block.respawn_anchor.charge")),
                                        SoundSource.BLOCK,
                                        1,
                                        1
                                ), targetBlock.getX(), targetBlock.getY(), targetBlock.getZ());
                            }).delay(50, TaskerTime.TICKS).repeat(10, TaskerTime.TICKS).start();
                        }
                    })
                    .afterOneTick()
                    .start();
        }


        if (game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.HOLOGRAMS_ABOVE_BEDS, false)) {
            var bed = targetBlock.getBlock();
            var loc = targetBlock.add(0.5, 1.5, 0.5);
            var isBlockTypeBed = game.getRegion().isBedBlock(bed.getBlockState().orElseThrow());
            var isAnchor = bed.getType().isSameType("respawn_anchor");
            var isCake = bed.getType().isSameType("cake");
            var enemies = game.getConnectedPlayers()
                    .stream()
                    .filter(player -> !players.contains(player))
                    .collect(Collectors.toList());
            var holo = HologramManager
                    .hologram(loc)
                    .firstLine(
                            Message
                                    .of(isBlockTypeBed ? LangKeys.IN_GAME_TARGET_BLOCK_HOLOGRAM_DESTROY_BED : (isAnchor ? LangKeys.IN_GAME_TARGET_BLOCK_HOLOGRAM_DESTROY_ANCHOR : (isCake ? LangKeys.IN_GAME_TARGET_BLOCK_HOLOGRAM_DESTROY_CAKE : LangKeys.IN_GAME_TARGET_BLOCK_HOLOGRAM_DESTROY_ANY)))
                                    .earlyPlaceholder("teamcolor", "<color:" + color.getTextColor().toString() + ">") // will be changed later
                    );
            enemies.forEach(holo::addViewer);
            holo.show();
            this.hologram = holo;
            var protectHolo = HologramManager
                    .hologram(loc)
                    .firstLine(
                            Message
                                    .of(isBlockTypeBed ? LangKeys.IN_GAME_TARGET_BLOCK_HOLOGRAM_PROTECT_BED : (isAnchor ? LangKeys.IN_GAME_TARGET_BLOCK_HOLOGRAM_PROTECT_ANCHOR : (isCake ? LangKeys.IN_GAME_TARGET_BLOCK_HOLOGRAM_PROTECT_CAKE : LangKeys.IN_GAME_TARGET_BLOCK_HOLOGRAM_PROTECT_ANY)))
                                    .earlyPlaceholder("teamcolor", "<color:" + color.getTextColor().toString() + ">") // will be changed later
                    );
            players.forEach(protectHolo::addViewer);
            protectHolo.show();
            this.protectHologram = protectHolo;
        }

        // team chest inventory
        final var message = Message.of(LangKeys.SPECIALS_TEAM_CHEST_NAME).prefixOrDefault(game.getCustomPrefixComponent()).asComponent();
        this.teamChestInventory = ContainerFactory.createContainer(InventoryTypeHolder.of("ender_chest"), message).orElseThrow();

        this.targetBlockIntact = true;
        this.started = true;
    }

    public void destroy() {
        if (!started) {
            return;
        }


        if (hologram != null) {
            hologram.destroy();
            hologram = null;
        }

        if (protectHologram != null) {
            protectHologram.destroy();
            protectHologram = null;
        }

        chests.clear();
        players.clear();
        started = false;
    }

    public void addTeamChest(LocationHolder location) {
        if (!chests.contains(location)) {
            chests.add(location);
        }
    }

    public void removeTeamChest(LocationHolder location) {
        chests.remove(location);
    }

    public boolean isTeamChestRegistered(LocationHolder location) {
        return chests.contains(location);
    }

    @Override
    public void addTeamChest(Object location) {
        try {
            addTeamChest(LocationMapper.wrapLocation(location));
        } catch (Exception ex) {
            // probably a block
            addTeamChest(BlockMapper.wrapBlock(location).getLocation());
        }
    }

    @Override
    public void removeTeamChest(Object location) {
        try {
            removeTeamChest(LocationMapper.wrapLocation(location));
        } catch (Exception ex) {
            // probably a block
            removeTeamChest(BlockMapper.wrapBlock(location).getLocation());
        }
    }

    @Override
    public boolean isTeamChestRegistered(Object location) {
        try {
            return isTeamChestRegistered(LocationMapper.wrapLocation(location));
        } catch (Exception ex) {
            // probably a block
            return isTeamChestRegistered(BlockMapper.wrapBlock(location).getLocation());
        }
    }

    @Override
    public int countTeamChests() {
        return chests.size();
    }

    @Override
    public int countConnectedPlayers() {
        return players.size();
    }

    @Override
    public boolean isPlayerInTeam(BWPlayer player) {
        if (!(player instanceof BedWarsPlayer)) {
            throw new IllegalArgumentException("Provided instance of player is not created by Bedwars plugin!");
        }
        return players.contains(player);
    }

    @Override
    public boolean isDead() {
        return players.isEmpty();
    }
}
