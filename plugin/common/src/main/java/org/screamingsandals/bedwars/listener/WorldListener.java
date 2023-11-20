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

package org.screamingsandals.bedwars.listener;

import org.screamingsandals.bedwars.BedWarsPlugin;
import org.screamingsandals.bedwars.api.config.GameConfigurationContainer;
import org.screamingsandals.bedwars.api.events.TargetInvalidationReason;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.entities.EntitiesManagerImpl;
import org.screamingsandals.bedwars.game.GameManagerImpl;
import org.screamingsandals.bedwars.game.target.TargetBlockImpl;
import org.screamingsandals.bedwars.player.PlayerManagerImpl;
import org.screamingsandals.bedwars.utils.ArenaUtils;
import org.screamingsandals.lib.entity.projectile.ProjectileEntity;
import org.screamingsandals.lib.event.Cancellable;
import org.screamingsandals.lib.event.EventExecutionOrder;
import org.screamingsandals.lib.event.OnEvent;
import org.screamingsandals.lib.event.block.*;
import org.screamingsandals.lib.event.chunk.ChunkUnloadEvent;
import org.screamingsandals.lib.event.entity.CreatureSpawnEvent;
import org.screamingsandals.lib.event.entity.EntityChangeBlockEvent;
import org.screamingsandals.lib.event.entity.EntityExplodeEvent;
import org.screamingsandals.lib.event.world.PlantGrowEvent;
import org.screamingsandals.lib.player.Player;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.block.BlockPlacement;
import org.screamingsandals.lib.world.Location;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.Collection;
import java.util.Objects;

@Service
public class WorldListener {

    @OnEvent
    public void onBurn(BlockBurnEvent event) {
        if (event.cancelled()) {
            return;
        }

        onBlockChange(event.block(), event);
    }

    @OnEvent
    public void onFade(BlockFadeEvent event) {
        if (event.cancelled()) {
            return;
        }

        onBlockChange(event.block(), event);
    }

    @OnEvent
    public void onLeavesDecay(LeavesDecayEvent event) {
        if (event.cancelled()) {
            return;
        }

        onBlockChange(event.block(), event);
    }

    @OnEvent
    public void onGrow(BlockGrowEvent event) {
        if (event.cancelled()) {
            return;
        }

        if (event.newBlockState().block().isSameType("SNOW")) {
            return;
        }

        onBlockChange(event.block(), event);
    }

    public void onBlockChange(BlockPlacement block, Cancellable cancellable) {
        for (var game : GameManagerImpl.getInstance().getGames()) {
            if (game.getStatus() == GameStatus.RUNNING || game.getStatus() == GameStatus.GAME_END_CELEBRATING) {
                if (ArenaUtils.isInArea(block.location(), game.getPos1(), game.getPos2())) {
                    if (!BedWarsPlugin.isFarmBlock(block.block()) && !game.isBlockAddedDuringGame(block.location())) {
                        cancellable.cancelled(true);
                    }
                    return;
                }
            }
        }
    }

    @OnEvent
    public void onFertilize(BlockFertilizeEvent event) {
        if (event.cancelled()) {
            return;
        }

        event.changedBlockStates().removeIf(blockState -> {
            for (var game : GameManagerImpl.getInstance().getGames()) {
                if (game.getStatus() == GameStatus.RUNNING || game.getStatus() == GameStatus.GAME_END_CELEBRATING) {
                    if (ArenaUtils.isInArea(blockState.location(), game.getPos1(), game.getPos2())) {
                        return !game.isBlockAddedDuringGame(blockState.location());
                    }
                }
            }
            return false;
        });
    }

    @OnEvent
    public void onExplode(EntityExplodeEvent event) {
        if (event.cancelled()) {
            return;
        }
        var entity = event.entity();
        boolean originatedInArena = EntitiesManagerImpl.getInstance().isEntityInGame(entity);
        if (!originatedInArena && entity instanceof ProjectileEntity) {
            var shooter = ((ProjectileEntity) entity).getShooter();
            if (shooter instanceof Player) {
                originatedInArena = PlayerManagerImpl.getInstance().isPlayerInGame((Player) shooter);
            }
        }
        onExplode(event.location(), event.blocks(), event, originatedInArena);
    }

    @OnEvent
    public void onExplode(BlockExplodeEvent event) {
        if (event.cancelled()) {
            return;
        }
        onExplode(event.block().location(), event.destroyedBlocks(), event, false);
    }

    public void onExplode(Location location, Collection<BlockPlacement> blockList, org.screamingsandals.lib.event.Cancellable cancellable, boolean originatedInArena) {
        final var explosionExceptionTypeName = MainConfig.getInstance().node("destroy-placed-blocks-by-explosion-except").childrenList().stream().map(ConfigurationNode::getString).toArray();
        final var destroyPlacedBlocksByExplosion = MainConfig.getInstance().node("destroy-placed-blocks-by-explosion").getBoolean(true);
        final var breakableExplosions = MainConfig.getInstance().node("breakable", "explosions").getBoolean(true);

        for (var game : GameManagerImpl.getInstance().getGames()) {
            if (ArenaUtils.isInArea(location, game.getPos1(), game.getPos2())) {
                if (game.getStatus() == GameStatus.RUNNING || game.getStatus() == GameStatus.GAME_END_CELEBRATING) {
                    blockList.removeIf(block -> {
                        if (!game.isBlockAddedDuringGame(block.location())) {
                            if (game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.TARGET_BLOCK_ALLOW_DESTROYING_WITH_EXPLOSIONS, false)) {
                                for (var team : game.getActiveTeams()) {
                                    if (team.getTarget() instanceof TargetBlockImpl && ((TargetBlockImpl) team.getTarget()).getTargetBlock().equals(block.location())) {
                                        game.internalProcessInvalidation(team, team.getTarget(), null, TargetInvalidationReason.TARGET_BLOCK_EXPLODED);
                                        return true;
                                    }
                                }
                            }
                            if (breakableExplosions && BedWarsPlugin.isBreakableBlock(block.block())) {
                                game.getRegion().putOriginalBlock(block.location(), block.blockSnapshot());
                                return false;
                            } else {
                                return true;
                            }
                        }
                        return block.block().is(explosionExceptionTypeName) || !destroyPlacedBlocksByExplosion;
                    });
                } else {
                    cancellable.cancelled(true);
                }
                return;
            }
        }

        if (originatedInArena) {
            cancellable.cancelled(true);
        }
    }

    @OnEvent
    public void onIgnite(BlockIgniteEvent event) {
        if (event.cancelled()) {
            return;
        }

        for (var game : GameManagerImpl.getInstance().getGames()) {
            if (game.getStatus() == GameStatus.RUNNING || game.getStatus() == GameStatus.GAME_END_CELEBRATING) {
                if (ArenaUtils.isInArea(event.block().location(), game.getPos1(), game.getPos2())) {
                    game.getRegion().addBuiltDuringGame(event.block().location());
                    return;
                }
            }
        }
    }

    @OnEvent
    public void onStructureGrow(PlantGrowEvent event) {
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
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.cancelled() || event.spawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM) {
            return;
        }

        for (var game : GameManagerImpl.getInstance().getGames()) {
            if (game.getStatus() != GameStatus.DISABLED)
                // prevent creature spawn everytime, not just in game
                if (/*(game.getStatus() == GameStatus.RUNNING || game.getStatus() == GameStatus.GAME_END_CELEBRATING) &&*/ game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.PREVENT_SPAWNING_MOBS, false)) {
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
    public void onLiquidFlow(BlockFromToEvent event) {
        if (event.cancelled()) {
            return;
        }

        for (var game : GameManagerImpl.getInstance().getGames()) {
            if (ArenaUtils.isInArea(event.sourceBlock().location(), game.getPos1(), game.getPos2())) {
                if (game.getStatus() == GameStatus.RUNNING || game.getStatus() == GameStatus.GAME_END_CELEBRATING) {
                    var block = event.facedBlock();
                    if (block.block().isAir() || game.isBlockAddedDuringGame(block.location())) {
                        game.getRegion().addBuiltDuringGame(block.location());
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
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (event.cancelled()) {
            return;
        }

        for (var game : GameManagerImpl.getInstance().getGames()) {
            if (ArenaUtils.isInArea(event.block().location(), game.getPos1(), game.getPos2())) {
                if (game.getStatus() == GameStatus.RUNNING || game.getStatus() == GameStatus.GAME_END_CELEBRATING) {
                    if (event.entity().getEntityType().is("FALLING_BLOCK")
                            && game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.ALLOW_BLOCK_FALLING, false)) {
                        if (!event.block().block().equals(event.to())) {
                            if (!game.isBlockAddedDuringGame(event.block().location())) {
                                if (!event.block().block().isAir()) {
                                    game.getRegion().putOriginalBlock(event.block().location(), Objects.requireNonNull(event.block().blockSnapshot()));
                                }
                                game.getRegion().addBuiltDuringGame(event.block().location());
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

    @OnEvent(order = EventExecutionOrder.LAST)
    public void onBedWarsSpawncancelled(CreatureSpawnEvent event) {
        if (!event.cancelled()) {
            return;
        }
        // Fix for uSkyBlock plugin
        if (event.spawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM && EntitiesManagerImpl.getInstance().isEntityInGame(event.entity())) {
            event.cancelled(false);
        }
    }

    @OnEvent
    public void onChunkUnload(ChunkUnloadEvent unload) {
        var chunk = unload.chunk();

        for (var game : GameManagerImpl.getInstance().getGames()) {
            if (game.getStatus() != GameStatus.DISABLED && game.getStatus() != GameStatus.WAITING && ArenaUtils.isChunkInArea(chunk, game.getPos1(), game.getPos2())) {
                unload.cancelled(true);
                return;
            }
        }
    }
}
