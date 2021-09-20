package org.screamingsandals.bedwars.special;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.screamingsandals.bedwars.BedWarsPlugin;
import org.screamingsandals.bedwars.api.special.AutoIgniteableTNT;
import org.screamingsandals.bedwars.entities.EntitiesManagerImpl;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.game.TeamImpl;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.lib.entity.type.EntityTypeHolder;
import org.screamingsandals.lib.tasker.Tasker;
import org.screamingsandals.lib.tasker.TaskerTime;
import org.screamingsandals.lib.world.LocationHolder;
import org.screamingsandals.lib.world.LocationMapper;

@Getter
@EqualsAndHashCode(callSuper = true)
public class AutoIgniteableTNTImpl extends SpecialItem implements AutoIgniteableTNT<GameImpl, BedWarsPlayer, TeamImpl> {

    private final int explosionTime;
    private final boolean allowedDamagingPlacer;

    public AutoIgniteableTNTImpl(GameImpl game, BedWarsPlayer player, TeamImpl team, int explosionTime, boolean damagePlacer) {
        super(game, player, team);
        this.explosionTime = explosionTime;
        this.allowedDamagingPlacer = damagePlacer;
    }
    @Override
    public void spawn(Object location) {
        spawn(LocationMapper.wrapLocation(location));
    }

    public void spawn(LocationHolder location) {
        var tnt = EntityTypeHolder.of("tnt").spawn(location).orElseThrow();
        EntitiesManagerImpl.getInstance().addEntityToGame(tnt, game);
        tnt.as(TNTPrimed.class).setFuseTicks(explosionTime * 20);
        if (!allowedDamagingPlacer) {
            tnt.as(Entity.class).setMetadata(player.getUuid().toString(), new FixedMetadataValue(BedWarsPlugin.getInstance().as(JavaPlugin.class), null));
        }
        tnt.as(Entity.class).setMetadata("autoignited", new FixedMetadataValue(BedWarsPlugin.getInstance().as(JavaPlugin.class), null));
        Tasker.build(() -> EntitiesManagerImpl.getInstance().removeEntityFromGame(tnt))
                .delay(explosionTime + 10, TaskerTime.TICKS)
                .start();
    }

}
