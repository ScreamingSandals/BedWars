package org.screamingsandals.bedwars.listener;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
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

public class WorldListener implements Listener {
    @EventHandler
    public void onBurn(BlockBurnEvent event) {
        if (event.isCancelled()) {
            return;
        }

        for (String s : Main.getGameNames()) {
            Game game = Main.getGame(s);
            if (game.getStatus() == GameStatus.RUNNING || game.getStatus() == GameStatus.GAME_END_CELEBRATING) {
                if (GameCreator.isInArea(event.getBlock().getLocation(), game.getPos1(), game.getPos2())) {
                    if (!game.isBlockAddedDuringGame(event.getBlock().getLocation())) {
                        event.setCancelled(true);
                    }
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onFade(BlockFadeEvent event) {
        if (event.isCancelled()) {
            return;
        }

        for (String s : Main.getGameNames()) {
            Game game = Main.getGame(s);
            if (game.getStatus() == GameStatus.RUNNING || game.getStatus() == GameStatus.GAME_END_CELEBRATING) {
                if (GameCreator.isInArea(event.getBlock().getLocation(), game.getPos1(), game.getPos2())) {
                    if (!game.isBlockAddedDuringGame(event.getBlock().getLocation())) {
                        event.setCancelled(true);
                    }
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onForm(BlockFormEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (event.getNewState().getType() == Material.SNOW) {
            return;
        }

        for (String s : Main.getGameNames()) {
            Game game = Main.getGame(s);
            if (game.getStatus() == GameStatus.RUNNING || game.getStatus() == GameStatus.GAME_END_CELEBRATING) {
                if (GameCreator.isInArea(event.getBlock().getLocation(), game.getPos1(), game.getPos2())) {
                    if (!game.isBlockAddedDuringGame(event.getBlock().getLocation())) {
                        event.setCancelled(true);
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

        final String explosionExceptionTypeName = Main.getConfigurator().config.getString("destroy-placed-blocks-by-explosion-except", null);
        final boolean destroyPlacedBlocksByExplosion = Main.getConfigurator().config.getBoolean("destroy-placed-blocks-by-explosion", true);

        for (String gameName : Main.getGameNames()) {
            Game game = Main.getGame(gameName);
            if (game.getStatus() == GameStatus.RUNNING || game.getStatus() == GameStatus.GAME_END_CELEBRATING) {
                if (GameCreator.isInArea(event.getLocation(), game.getPos1(), game.getPos2())) {
                    event.blockList().removeIf(block -> {
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
                        return (explosionExceptionTypeName != null && !explosionExceptionTypeName.equals("") && block.getType().name().contains(explosionExceptionTypeName)) || !destroyPlacedBlocksByExplosion;
                    });
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
                    ((Cancellable) unload).setCancelled(false);
                    return;
                }
            }
        }
    }
}
