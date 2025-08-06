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

package org.screamingsandals.bedwars.game;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.screamingsandals.bedwars.api.Team;
import org.screamingsandals.bedwars.api.config.GameConfigurationContainer;
import org.screamingsandals.bedwars.api.game.target.Target;
import org.screamingsandals.bedwars.api.player.BWPlayer;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.game.target.AExpirableTarget;
import org.screamingsandals.bedwars.game.target.TargetBlockImpl;
import org.screamingsandals.bedwars.game.upgrade.UpgradableImpl;
import org.screamingsandals.bedwars.game.upgrade.builtin.TrapUpgradeDefinition;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.lib.api.types.server.LocationHolder;
import org.screamingsandals.lib.container.Container;
import org.screamingsandals.lib.container.ContainerFactory;
import org.screamingsandals.lib.container.type.InventoryType;
import org.screamingsandals.lib.hologram.Hologram;
import org.screamingsandals.lib.hologram.HologramManager;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.spectator.sound.SoundSource;
import org.screamingsandals.lib.spectator.sound.SoundStart;
import org.screamingsandals.lib.tasker.DefaultThreads;
import org.screamingsandals.lib.tasker.Tasker;
import org.screamingsandals.lib.tasker.TaskerTime;
import org.screamingsandals.lib.utils.ResourceLocation;
import org.screamingsandals.lib.world.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Getter
@Setter
public class TeamImpl extends UpgradableImpl implements Team {
    private TeamColorImpl color;
    private String name;
    private Target target;
    private final List<Location> teamSpawns = new ArrayList<>();
    private int maxPlayers;
    private GameImpl game;

    private Container teamChestInventory;
    private final List<Location> chests = new ArrayList<>();
    private boolean started;
    private final List<BedWarsPlayer> players = new ArrayList<>();
    private final List<Member> teamMembers = new ArrayList<>();
    private Hologram hologram;
    private Hologram protectHologram;
    private final Random randomSpawn = new Random();
    private boolean forced = false;
    private final @NotNull List<@NotNull TrapUpgradeDefinition> traps = new ArrayList<>();

    public void start() {
        if (started) {
            return;
        }

        if (target instanceof AExpirableTarget) {
            ((AExpirableTarget) target).setRemainingTime(((AExpirableTarget) target).getCountdown());
        }

        if (target instanceof TargetBlockImpl) {
            var targetBlock = ((TargetBlockImpl) target).getTargetBlock();
            // Check target blocks existence
            if (targetBlock.getBlock().block().isAir()) {
                var placedBlock = targetBlock.getBlock();
                placedBlock.block(color.getWoolBlockType());
            }

            ((TargetBlockImpl) this.target).setValid(true);

            // anchor wars
            var block = targetBlock.getBlock();
            if (block.block().isSameType("respawn_anchor")) {
                Tasker.run(DefaultThreads.GLOBAL_THREAD, () -> {
                            var anchor = block.block();
                            block.block(anchor.with("charges", "0"));
                            if (game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.TARGET_BLOCK_RESPAWN_ANCHOR_FILL_ON_START, false)) {
                                var atomic = new AtomicInteger();
                                Tasker.runDelayedAndRepeatedly(DefaultThreads.GLOBAL_THREAD, taskBase -> {
                                    var charges = atomic.incrementAndGet();
                                    if (charges > 4) {
                                        taskBase.cancel();
                                        return;
                                    }
                                    block.block(anchor.with("charges", String.valueOf(charges)));
                                    targetBlock.getWorld().playSound(SoundStart.sound(
                                            ResourceLocation.of(MainConfig.getInstance().node("target-block", "respawn-anchor", "sound", "charge").getString("block.respawn_anchor.charge")),
                                            SoundSource.BLOCK,
                                            1,
                                            1
                                    ), targetBlock.getX(), targetBlock.getY(), targetBlock.getZ());
                                }, 50, TaskerTime.TICKS, 10, TaskerTime.TICKS);
                            }
                        });
            }


            if (game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.HOLOGRAMS_ABOVE_BEDS, false)) {
                var bed = targetBlock.getBlock();
                var loc = targetBlock.add(0.5, 1.5, 0.5);
                var isBlockTypeBed = game.getRegion().isBedBlock(bed.blockSnapshot());
                var isAnchor = bed.block().isSameType("respawn_anchor");
                var isCake = bed.block().isSameType("cake");
                var isItDoor = bed.block().is("#doors");
                var enemies = game.getConnectedPlayers()
                        .stream()
                        .filter(player -> !players.contains(player))
                        .collect(Collectors.toList());
                var holo = HologramManager
                        .hologram(loc)
                        .firstLine(
                                Message
                                        .of(isItDoor ? LangKeys.IN_GAME_TARGET_BLOCK_HOLOGRAM_DESTROY_DOOR : (isBlockTypeBed ? LangKeys.IN_GAME_TARGET_BLOCK_HOLOGRAM_DESTROY_BED : (isAnchor ? LangKeys.IN_GAME_TARGET_BLOCK_HOLOGRAM_DESTROY_ANCHOR : (isCake ? LangKeys.IN_GAME_TARGET_BLOCK_HOLOGRAM_DESTROY_CAKE : LangKeys.IN_GAME_TARGET_BLOCK_HOLOGRAM_DESTROY_ANY))))
                                        .earlyPlaceholder("teamcolor", "<color:" + color.getTextColor().toString() + ">") // will be changed later
                        );
                enemies.forEach(holo::addViewer);
                holo.show();
                this.hologram = holo;
                var protectHolo = HologramManager
                        .hologram(loc)
                        .firstLine(
                                Message
                                        .of(isItDoor ? LangKeys.IN_GAME_TARGET_BLOCK_HOLOGRAM_PROTECT_DOOR : isBlockTypeBed ? LangKeys.IN_GAME_TARGET_BLOCK_HOLOGRAM_PROTECT_BED : (isAnchor ? LangKeys.IN_GAME_TARGET_BLOCK_HOLOGRAM_PROTECT_ANCHOR : (isCake ? LangKeys.IN_GAME_TARGET_BLOCK_HOLOGRAM_PROTECT_CAKE : LangKeys.IN_GAME_TARGET_BLOCK_HOLOGRAM_PROTECT_ANY)))
                                        .earlyPlaceholder("teamcolor", "<color:" + color.getTextColor().toString() + ">") // will be changed later
                        );
                players.forEach(protectHolo::addViewer);
                protectHolo.show();
                this.protectHologram = protectHolo;
            }
        }

        // team chest inventory
        final var message = Message.of(LangKeys.SPECIALS_TEAM_CHEST_NAME).prefixOrDefault(game.getCustomPrefixComponent()).asComponent();
        this.teamChestInventory = Objects.requireNonNull(ContainerFactory.createContainer(InventoryType.of("ender_chest"), message));
        syncBuiltInUpgrades(game.getGameVariant().getUpgrades());
        resetUpgrades();
        this.traps.clear();
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
        teamMembers.clear();
        this.traps.clear();
        started = false;
        forced = false;
    }

    public void addTeamChest(Location location) {
        if (!chests.contains(location)) {
            chests.add(location);
        }
    }

    public void removeTeamChest(Location location) {
        chests.remove(location);
    }

    public boolean isTeamChestRegistered(Location location) {
        return chests.contains(location);
    }

    @Override
    public void addTeamChest(LocationHolder location) {
        addTeamChest(location.as(Location.class));
    }

    @Override
    public void removeTeamChest(LocationHolder location) {
        removeTeamChest(location.as(Location.class));
    }

    @Override
    public boolean isTeamChestRegistered(LocationHolder location) {
        return isTeamChestRegistered(location.as(Location.class));
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
        return forced && target != null ? !target.isValid() : players.isEmpty(); // forced teams in test play without players work a little differently
    }

    @Override
    public Location getRandomSpawn() {
        return teamSpawns.get(randomSpawn.nextInt(teamSpawns.size()));
    }

    @Data
    public static class Member {
        private final @NotNull UUID uuid;
        private final @NotNull String name;
    }
}
