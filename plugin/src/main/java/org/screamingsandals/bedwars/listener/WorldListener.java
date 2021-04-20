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
import org.screamingsandals.bedwars.api.config.ConfigurationContainer;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.game.GameManager;
import org.screamingsandals.bedwars.utils.ArenaUtils;

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
        for (var game : GameManager.getInstance().getGames()) {
            if (game.getStatus() == GameStatus.RUNNING || game.getStatus() == GameStatus.GAME_END_CELEBRATING) {
                if (ArenaUtils.isInArea(block.getLocation(), game.getPos1(), game.getPos2())) {
                    if (!Main.isFarmBlock(block.getType()) && !game.isBlockAddedDuringGame(block.getLocation())) {
                        cancellable.setCancelled(true);
                    }
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onFertilize(BlockFertilizeEvent event) {
        if (event.isCancelled()) {
            return;
        }

        event.getBlocks().removeIf(blockState -> {
            for (var game : GameManager.getInstance().getGames()) {
                if (game.getStatus() == GameStatus.RUNNING || game.getStatus() == GameStatus.GAME_END_CELEBRATING) {
                    if (ArenaUtils.isInArea(blockState.getLocation(), game.getPos1(), game.getPos2())) {
                        return !game.isBlockAddedDuringGame(blockState.getLocation());
                    }
                }
            }
            return false;
        });
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
        final var explosionExceptionTypeName = MainConfig.getInstance().node("destroy-placed-blocks-by-explosion-except").getString();
        final var destroyPlacedBlocksByExplosion = MainConfig.getInstance().node("destroy-placed-blocks-by-explosion").getBoolean(true);

        GameManager.getInstance().getGames().forEach(game -> {
            if (ArenaUtils.isInArea(location, game.getPos1(), game.getPos2())) {
                if (game.getStatus() == GameStatus.RUNNING || game.getStatus() == GameStatus.GAME_END_CELEBRATING) {
                    blockList.removeIf(block -> {
                        if (!game.isBlockAddedDuringGame(block.getLocation())) {
                            if (game.getConfigurationContainer().getOrDefault(ConfigurationContainer.TARGET_BLOCK_EXPLOSIONS, Boolean.class, false)) {
                                for (RunningTeam team : game.getRunningTeams()) {
                                    if (team.getTargetBlock().equals(block.getLocation())) {
                                        game.targetBlockExplode(team);
                                        break;
                                    }
                                }
                            }
                            return true;
                        }
                        return (explosionExceptionTypeName != null && !explosionExceptionTypeName.equals("") && explosionExceptionTypeName.contains(block.getType().name()) || !destroyPlacedBlocksByExplosion;
                    });
                } else {
                    cancellable.setCancelled(true);
                }
            }
        });
    }

    @EventHandler
    public void onStructureGrow(StructureGrowEvent event) {
        if (event.isCancelled()) {
            return;
        }

        for (var game : GameManager.getInstance().getGames()) {
            if (game.getStatus() == GameStatus.RUNNING || game.getStatus() == GameStatus.GAME_END_CELEBRATING) {
                if (ArenaUtils.isInArea(event.getLocation(), game.getPos1(), game.getPos2())) {
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

        for (var game : GameManager.getInstance().getGames()) {
            if (game.getStatus() != GameStatus.DISABLED)
                // prevent creature spawn everytime, not just in game
                if (/*(game.getStatus() == GameStatus.RUNNING || game.getStatus() == GameStatus.GAME_END_CELEBRATING) &&*/ game.getConfigurationContainer().getOrDefault(ConfigurationContainer.PREVENT_SPAWNING_MOBS, Boolean.class, false)) {
                    if (ArenaUtils.isInArea(event.getLocation(), game.getPos1(), game.getPos2())) {
                        event.setCancelled(true);
                        return;
                        //}
                    } else /*if (game.getStatus() == GameStatus.WAITING) {*/
                        if (game.getLobbyWorld() == event.getLocation().getWorld()) {
                            if (event.getLocation().distanceSquared(game.getLobbySpawn()) <= Math
                                    .pow(MainConfig.getInstance().node("prevent-lobby-spawn-mobs-in-radius").getInt(), 2)) {
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

        for (var game : GameManager.getInstance().getGames()) {
            if (ArenaUtils.isInArea(event.getBlock().getLocation(), game.getPos1(), game.getPos2())) {
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

        for (var game : GameManager.getInstance().getGames()) {
            if (ArenaUtils.isInArea(event.getBlock().getLocation(), game.getPos1(), game.getPos2())) {
                if (game.getStatus() == GameStatus.RUNNING || game.getStatus() == GameStatus.GAME_END_CELEBRATING) {
                    if (event.getEntityType() == EntityType.FALLING_BLOCK
                            && game.getConfigurationContainer().getOrDefault(ConfigurationContainer.BLOCK_FALLING, Boolean.class, false)) {
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

            for (var game : GameManager.getInstance().getGames()) {
                if (game.getStatus() != GameStatus.DISABLED && game.getStatus() != GameStatus.WAITING
                        && ArenaUtils.isChunkInArea(chunk, game.getPos1(), game.getPos2())) {
                    ((Cancellable) unload).setCancelled(true);
                    return;
                }
            }
        }
    }
}
