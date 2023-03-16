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

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.RunningTeam;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.bedwars.game.GameCreator;

import java.util.List;

public class WorldListener implements Listener {
    @EventHandler
    public void onBurn(BlockBurnEvent event) {
        if (event.isCancelled()) {
            return;
        }

        onBlockChange(event.getBlock(), event);
    }

    @EventHandler
    public void onFade(BlockFadeEvent event) {
        if (event.isCancelled()) {
            return;
        }

        onBlockChange(event.getBlock(), event);
    }

    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent event) {
        if (event.isCancelled()) {
            return;
        }

        onBlockChange(event.getBlock(), event);
    }

    @EventHandler
    public void onGrow(BlockGrowEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (event.getNewState().getType() == Material.SNOW) {
            return;
        }

        onBlockChange(event.getBlock(), event);
    }

    public void onBlockChange(Block block, Cancellable cancellable) {
        for (String s : Main.getGameNames()) {
            Game game = Main.getGame(s);
            if (game.getStatus() == GameStatus.RUNNING || game.getStatus() == GameStatus.GAME_END_CELEBRATING) {
                if (GameCreator.isInArea(block.getLocation(), game.getPos1(), game.getPos2())) {
                    if (!Main.isFarmBlock(block.getType()) && !game.isBlockAddedDuringGame(block.getLocation())) {
                        cancellable.setCancelled(true);
                    }
                    return;
                }
            }
        }
    }


    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        if (event.isCancelled()) {
            return;
        }
        onExplode(event.getLocation(), event.blockList(), event);
    }

    @EventHandler
    public void onExplode(BlockExplodeEvent event) {
        if (event.isCancelled()) {
            return;
        }
        onExplode(event.getBlock().getLocation(), event.blockList(), event);
    }

    public void onExplode(Location location, List<Block> blockList, Cancellable cancellable) {
        if (cancellable.isCancelled()) {
            return;
        }

        final List<String> explosionExceptionTypeName = Main.getConfigurator().config.getStringList("destroy-placed-blocks-by-explosion-except");
        final boolean destroyPlacedBlocksByExplosion = Main.getConfigurator().config.getBoolean("destroy-placed-blocks-by-explosion", true);

        for (String gameName : Main.getGameNames()) {
            Game game = Main.getGame(gameName);
            if (GameCreator.isInArea(location, game.getPos1(), game.getPos2())) {
                if (game.getStatus() == GameStatus.RUNNING || game.getStatus() == GameStatus.GAME_END_CELEBRATING) {
                    blockList.removeIf(block -> {
                        if (!game.isBlockAddedDuringGame(block.getLocation())) {
                            if (game.getOriginalOrInheritedTargetBlockExplosions()) {
                                for (RunningTeam team : game.getRunningTeams()) {
                                    if (team.getTargetBlock().equals(block.getLocation())) {
                                        game.targetBlockExplode(team);
                                        break;
                                    }
                                }
                            }
                            return true;
                        }
                        return (explosionExceptionTypeName.contains(block.getType().name())) || !destroyPlacedBlocksByExplosion;
                    });
                } else {
                    cancellable.setCancelled(true);
                }
            }
        }

    }

    @EventHandler
    public void onIgnite(BlockIgniteEvent event) {
        if (event.isCancelled()) {
            return;
        }

        for (String gameName : Main.getGameNames()) {
            Game game = Main.getGame(gameName);
            if (game.getStatus() == GameStatus.RUNNING || game.getStatus() == GameStatus.GAME_END_CELEBRATING) {
                if (GameCreator.isInArea(event.getBlock().getLocation(), game.getPos1(), game.getPos2())) {
                    game.getRegion().addBuiltDuringGame(event.getBlock().getLocation());
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onStructureGrow(StructureGrowEvent event) {
        if (event.isCancelled()) {
            return;
        }

        for (String gameName : Main.getGameNames()) {
            Game game = Main.getGame(gameName);
            if (game.getStatus() == GameStatus.RUNNING || game.getStatus() == GameStatus.GAME_END_CELEBRATING) {
                if (GameCreator.isInArea(event.getLocation(), game.getPos1(), game.getPos2())) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.isCancelled() || event.getSpawnReason() == SpawnReason.CUSTOM) {
            return;
        }

        for (String gameName : Main.getGameNames()) {
            Game game = Main.getGame(gameName);
            if (game.getStatus() != GameStatus.DISABLED)
                // prevent creature spawn everytime, not just in game
                if (/*(game.getStatus() == GameStatus.RUNNING || game.getStatus() == GameStatus.GAME_END_CELEBRATING) &&*/ game.getOriginalOrInheritedPreventSpawningMobs()) {
                    if (GameCreator.isInArea(event.getLocation(), game.getPos1(), game.getPos2())) {
                        event.setCancelled(true);
                        return;
                        //}
                    } else /*if (game.getStatus() == GameStatus.WAITING) {*/
                        if (game.getLobbyWorld() == event.getLocation().getWorld()) {
                            if (event.getLocation().distanceSquared(game.getLobbySpawn()) <= Math
                                    .pow(Main.getConfigurator().config.getInt("prevent-lobby-spawn-mobs-in-radius"), 2)) {
                                event.setCancelled(true);
                                return;
                            }
                        }
                }
        }
    }

    @EventHandler
    public void onLiquidFlow(BlockFromToEvent event) {
        if (event.isCancelled()) {
            return;
        }

        for (String gameName : Main.getGameNames()) {
            Game game = Main.getGame(gameName);
            if (GameCreator.isInArea(event.getBlock().getLocation(), game.getPos1(), game.getPos2())) {
                if (game.getStatus() == GameStatus.RUNNING || game.getStatus() == GameStatus.GAME_END_CELEBRATING) {
                    Block block = event.getToBlock();
                    if (block.getType() == Material.AIR
                            || game.getRegion().isBlockAddedDuringGame(block.getLocation())) {
                        game.getRegion().addBuiltDuringGame(block.getLocation());
                    } else {
                        event.setCancelled(true);
                    }
                } else if (game.getStatus() != GameStatus.DISABLED) {
                    event.setCancelled(true);
                }
            }
        }

    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (event.isCancelled()) {
            return;
        }

        for (String gameName : Main.getGameNames()) {
            Game game = Main.getGame(gameName);
            if (GameCreator.isInArea(event.getBlock().getLocation(), game.getPos1(), game.getPos2())) {
                if (game.getStatus() == GameStatus.RUNNING || game.getStatus() == GameStatus.GAME_END_CELEBRATING) {
                    if (event.getEntityType() == EntityType.FALLING_BLOCK
                            && game.getOriginalOrInheritedAllowBlockFalling()) {
                        if (event.getBlock().getType() != event.getTo()) {
                            if (!game.getRegion().isBlockAddedDuringGame(event.getBlock().getLocation())) {
                                if (event.getBlock().getType() != Material.AIR) {
                                    game.getRegion().putOriginalBlock(event.getBlock().getLocation(),
                                            event.getBlock().getState());
                                }
                                game.getRegion().addBuiltDuringGame(event.getBlock().getLocation());
                            }
                        }
                        return; // allow block fall
                    }
                }

                if (game.getStatus() != GameStatus.DISABLED) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBedWarsSpawnIsCancelled(CreatureSpawnEvent event) {
        if (!event.isCancelled()) {
            return;
        }
        // Fix for uSkyBlock plugin
        if (event.getSpawnReason() == SpawnReason.CUSTOM && Main.getInstance().isEntityInGame(event.getEntity())) {
            event.setCancelled(false);
        }
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent unload) {
        if (unload instanceof Cancellable) {
            Chunk chunk = unload.getChunk();

            for (String name : Main.getGameNames()) {
                Game game = Main.getGame(name);
                if (game.getStatus() != GameStatus.DISABLED && game.getStatus() != GameStatus.WAITING
                        && GameCreator.isChunkInArea(chunk, game.getPos1(), game.getPos2())) {
                    ((Cancellable) unload).setCancelled(true);
                    return;
                }
            }
        }
    }
}
