package org.screamingsandals.bedwars.special;

import lombok.Getter;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.api.special.BridgeEgg;
import org.screamingsandals.bedwars.game.CurrentTeam;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.game.TeamColor;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.bedwars.utils.ArenaUtils;
import org.screamingsandals.lib.entity.EntityProjectile;
import org.screamingsandals.lib.material.MaterialHolder;
import org.screamingsandals.lib.material.MaterialMapping;
import org.screamingsandals.lib.tasker.Tasker;
import org.screamingsandals.lib.tasker.TaskerTime;
import org.screamingsandals.lib.tasker.task.TaskerTask;
import org.screamingsandals.lib.utils.MathUtils;
import org.screamingsandals.lib.world.BlockHolder;

@Getter
public class BridgeEggImpl extends SpecialItem implements BridgeEgg<GameImpl, BedWarsPlayer, CurrentTeam, EntityProjectile, MaterialHolder> {
    private final double distance;
    private final double distanceSquared;
    private final EntityProjectile projectile;
    private final MaterialHolder material;
    private TaskerTask task;

    public BridgeEggImpl(GameImpl game, BedWarsPlayer player, CurrentTeam team, EntityProjectile projectile, MaterialHolder mat, Double distance) {
        super(game, player, team);
        this.projectile = projectile;
        this.material = MaterialMapping.colorize(mat, TeamColor.fromApiColor(team.getColor()).material1_13);
        this.distance = distance;
        this.distanceSquared = MathUtils.square(distance);
    }

    private void setBlock(BlockHolder block) {
        if (block.getType().isAir() && ArenaUtils.isInArea(block.getLocation(), game.getPos1(), game.getPos2())) {
            block.setType(this.material);
            this.game.getRegion().addBuiltDuringGame(block.getLocation());
            this.player.playSound(
                    Sound.sound(Key.key("entity_chicken_egg"), Sound.Source.AMBIENT, 1f, 1f),
                    block.getLocation().getX(),
                    block.getLocation().getY(),
                    block.getLocation().getZ()
            );
        }
    }

    @Override
    public void runTask() {
        this.task = Tasker.build(() -> {
            final var projectileLocation = projectile.getLocation();

            if (!this.player.isInGame() || this.projectile.isDead() || this.team.isDead() || this.game.getStatus() != GameStatus.RUNNING) {
                this.task.cancel();
                return;
            }

            if (projectileLocation.getDistanceSquared(this.player.getLocation()) > this.distanceSquared) {
                this.task.cancel();
                return;
            }

            this.setBlock(projectileLocation.remove(0.0D, 3.0D, 0.0D).getBlock());
            this.setBlock(projectileLocation.remove(1.0D, 3.0D, 0.0D).getBlock());
            this.setBlock(projectileLocation.remove(0.0D, 3.0D, 1.0D).getBlock());
        })
        .repeat(1, TaskerTime.TICKS)
        .start();
    }
}
