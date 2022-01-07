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

package org.screamingsandals.bedwars.listener;

import org.screamingsandals.bedwars.BedWarsPlugin;
import org.screamingsandals.bedwars.api.config.ConfigurationContainer;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.entities.EntitiesManagerImpl;
import org.screamingsandals.bedwars.game.GameManagerImpl;
import org.screamingsandals.bedwars.utils.ArenaUtils;
import org.screamingsandals.lib.event.Cancellable;
import org.screamingsandals.lib.event.OnEvent;
import org.screamingsandals.lib.event.block.*;
import org.screamingsandals.lib.event.chunk.SChunkUnloadEvent;
import org.screamingsandals.lib.event.entity.SCreatureSpawnEvent;
import org.screamingsandals.lib.event.entity.SEntityChangeBlockEvent;
import org.screamingsandals.lib.event.entity.SEntityExplodeEvent;
import org.screamingsandals.lib.event.world.SPlantGrowEvent;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.block.BlockHolder;
import org.screamingsandals.lib.world.LocationHolder;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class WorldListener {

    @OnEvent
    public void onBurn(SBlockBurnEvent event) {
        if (event.cancelled()) {
            return;
        }

        onBlockChange(event.block(), event);
    }

    @OnEvent
    public void onFade(SBlockFadeEvent event) {
        if (event.cancelled()) {
            return;
        }

        onBlockChange(event.block(), event);
    }

    @OnEvent
    public void onLeavesDecay(SLeavesDecayEvent event) {
        if (event.cancelled()) {
            return;
        }

        onBlockChange(event.block(), event);
    }

    @OnEvent
    public void onGrow(SBlockGrowEvent event) {
        if (event.cancelled()) {
            return;
        }

        if (event.newBlockState().getType().isSameType("SNOW")) {
            return;
        }

        onBlockChange(event.block(), event);
    }

    public void onBlockChange(BlockHolder block, Cancellable cancellable) {
        for (var game : GameManagerImpl.getInstance().getGames()) {
            if (game.getStatus() == GameStatus.RUNNING || game.getStatus() == GameStatus.GAME_END_CELEBRATING) {
                if (ArenaUtils.isInArea(block.getLocation(), game.getPos1(), game.getPos2())) {
                    if (!BedWarsPlugin.isFarmBlock(block.getType()) && !game.isBlockAddedDuringGame(block.getLocation())) {
                        cancellable.cancelled(true);
                    }
                    return;
                }
            }
        }
    }

    @OnEvent
    public void onFertilize(SBlockFertilizeEvent event) {
        if (event.cancelled()) {
            return;
        }

        event.changedBlockStates().removeIf(blockState -> {
            for (var game : GameManagerImpl.getInstance().getGames()) {
                if (game.getStatus() == GameStatus.RUNNING || game.getStatus() == GameStatus.GAME_END_CELEBRATING) {
                    if (ArenaUtils.isInArea(blockState.getLocation(), game.getPos1(), game.getPos2())) {
                        return !game.isBlockAddedDuringGame(blockState.getLocation());
                    }
                }
            }
            return false;
        });
    }

    @OnEvent
    public void onExplode(SEntityExplodeEvent event) {
        if (event.cancelled()) {
            return;
        }
        onExplode(event.location(), event.blocks(), event);
    }

    @OnEvent
    public void onExplode(SBlockExplodeEvent event) {
        if (event.cancelled()) {
            return;
        }
        onExplode(event.block().getLocation(), event.destroyedBlocks(), event);
    }

    public void onExplode(LocationHolder location, Collection<BlockHolder> blockList, org.screamingsandals.lib.event.Cancellable cancellable) {
        final var explosionExceptionTypeName = MainConfig.getInstance().node("destroy-placed-blocks-by-explosion-except").childrenList().stream().map(ConfigurationNode::getString).collect(Collectors.toList());
        final var destroyPlacedBlocksByExplosion = MainConfig.getInstance().node("destroy-placed-blocks-by-explosion").getBoolean(true);

        GameManagerImpl.getInstance().getGames().forEach(game -> {
            if (ArenaUtils.isInArea(location, game.getPos1(), game.getPos2())) {
                if (game.getStatus() == GameStatus.RUNNING || game.getStatus() == GameStatus.GAME_END_CELEBRATING) {
                    blockList.removeIf(block -> {
                        if (!game.isBlockAddedDuringGame(block.getLocation())) {
                            if (game.getConfigurationContainer().getOrDefault(ConfigurationContainer.TARGET_BLOCK_EXPLOSIONS, Boolean.class, false)) {
                                for (var team : game.getActiveTeams()) {
                                    if (team.getTargetBlock().equals(block.getLocation())) {
                                        game.targetBlockExplode(team);
                                        break;
                                    }
                                }
                            }
                            return true;
                        }
                        return explosionExceptionTypeName.contains(block.getType().platformName()) || !destroyPlacedBlocksByExplosion;
                    });
                } else {
                    cancellable.cancelled(true);
                }
            }
        });
    }

    @OnEvent
    public void onIgnite(SBlockIgniteEvent event) {
        if (event.cancelled()) {
            return;
        }

        for (var game : GameManagerImpl.getInstance().getGames()) {
            if (game.getStatus() == GameStatus.RUNNING || game.getStatus() == GameStatus.GAME_END_CELEBRATING) {
                if (ArenaUtils.isInArea(event.block().getLocation(), game.getPos1(), game.getPos2())) {
                    game.getRegion().addBuiltDuringGame(event.block().getLocation());
                    return;
                }
            }
        }
    }

    @OnEvent
    public void onStructureGrow(SPlantGrowEvent event) {
        if (event.cancelled()) {
            return;
        }

        for (var game : GameManagerImpl.getInstance().getGames()) {
            if (game.getStatus() == GameStatus.RUNNING || game.getStatus() == GameStatus.GAME_END_CELEBRATING) {
                if (ArenaUtils.isInArea(event.getLocation(), game.getPos1(), game.getPos2())) {
                    event.cancelled(true);
                    return;
                }
            }
        }
    }

    @OnEvent
    public void onCreatureSpawn(SCreatureSpawnEvent event) {
        if (event.cancelled() || event.spawnReason() == SCreatureSpawnEvent.SpawnReason.CUSTOM) {
            return;
        }

        for (var game : GameManagerImpl.getInstance().getGames()) {
            if (game.getStatus() != GameStatus.DISABLED)
                // prevent creature spawn everytime, not just in game
                if (/*(game.getStatus() == GameStatus.RUNNING || game.getStatus() == GameStatus.GAME_END_CELEBRATING) &&*/ game.getConfigurationContainer().getOrDefault(ConfigurationContainer.PREVENT_SPAWNING_MOBS, Boolean.class, false)) {
                    if (ArenaUtils.isInArea(event.entity().getLocation(), game.getPos1(), game.getPos2())) {
                        event.cancelled(true);
                        return;
                        //}
                    } else /*if (game.getStatus() == GameStatus.WAITING) {*/
                        if (game.getLobbyWorld().equals(event.entity().getLocation().getWorld())) {
                            if (event.entity().getLocation().getDistanceSquared(game.getLobbySpawn()) <= Math
                                    .pow(MainConfig.getInstance().node("prevent-lobby-spawn-mobs-in-radius").getInt(), 2)) {
                                event.cancelled(true);
                                return;
                            }
                        }
                }
        }
    }

    @OnEvent
    public void onLiquidFlow(SBlockFromToEvent event) {
        if (event.cancelled()) {
            return;
        }

        for (var game : GameManagerImpl.getInstance().getGames()) {
            if (ArenaUtils.isInArea(event.sourceBlock().getLocation(), game.getPos1(), game.getPos2())) {
                if (game.getStatus() == GameStatus.RUNNING || game.getStatus() == GameStatus.GAME_END_CELEBRATING) {
                    var block = event.facedBlock();
                    if (block.getType().isAir()
                            || game.isBlockAddedDuringGame(block.getLocation())) {
                        game.getRegion().addBuiltDuringGame(block.getLocation());
                    } else {
                        event.cancelled(true);
                    }
                } else if (game.getStatus() != GameStatus.DISABLED) {
                    event.cancelled(true);
                }
            }
        }

    }

    @OnEvent
    public void onEntityChangeBlock(SEntityChangeBlockEvent event) {
        if (event.cancelled()) {
            return;
        }

        for (var game : GameManagerImpl.getInstance().getGames()) {
            if (ArenaUtils.isInArea(event.block().getLocation(), game.getPos1(), game.getPos2())) {
                if (game.getStatus() == GameStatus.RUNNING || game.getStatus() == GameStatus.GAME_END_CELEBRATING) {
                    if (event.entity().getEntityType().is("FALLING_BLOCK")
                            && game.getConfigurationContainer().getOrDefault(ConfigurationContainer.BLOCK_FALLING, Boolean.class, false)) {
                        if (!event.block().getType().equals(event.to())) {
                            if (!game.isBlockAddedDuringGame(event.block().getLocation())) {
                                if (!event.block().getType().isAir()) {
                                    game.getRegion().putOriginalBlock(event.block().getLocation(),
                                            event.block().getBlockState().orElseThrow());
                                }
                                game.getRegion().addBuiltDuringGame(event.block().getLocation());
                            }
                        }
                        return; // allow block fall
                    }
                }

                if (game.getStatus() != GameStatus.DISABLED) {
                    event.cancelled(true);
                }
            }
        }
    }

    @OnEvent(priority = org.screamingsandals.lib.event.EventPriority.HIGHEST)
    public void onBedWarsSpawncancelled(SCreatureSpawnEvent event) {
        if (!event.cancelled()) {
            return;
        }
        // Fix for uSkyBlock plugin
        if (event.spawnReason() == SCreatureSpawnEvent.SpawnReason.CUSTOM && EntitiesManagerImpl.getInstance().isEntityInGame(event.entity())) {
            event.cancelled(false);
        }
    }

    @OnEvent
    public void onChunkUnload(SChunkUnloadEvent unload) {
        var chunk = unload.chunk();

        for (var game : GameManagerImpl.getInstance().getGames()) {
            if (game.getStatus() != GameStatus.DISABLED && game.getStatus() != GameStatus.WAITING
                    && ArenaUtils.isChunkInArea(chunk, game.getPos1(), game.getPos2())) {
                unload.cancelled(true);
                return;
            }
        }
    }
}
