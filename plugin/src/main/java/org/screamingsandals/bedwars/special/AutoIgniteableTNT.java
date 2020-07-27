package org.screamingsandals.bedwars.special;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.Team;
import org.screamingsandals.bedwars.api.game.Game;

public class AutoIgniteableTNT extends SpecialItem
        implements org.screamingsandals.bedwars.api.special.AutoIgniteableTNT {

    private int explosionTime;
    private boolean damagePlacer;

    public AutoIgniteableTNT(Game game, Player player, Team team, int explosionTime, boolean damagePlacer) {
        super(game, player, team);
        this.explosionTime = explosionTime;
        this.damagePlacer = damagePlacer;
    }

    @Override
    public int getExplosionTime() {
        return explosionTime;
    }

    @Override
    public boolean isAllowedDamagingPlacer() {
        return damagePlacer;
    }

    @Override
    public void spawn(Location location) {
        TNTPrimed tnt = (TNTPrimed) location.getWorld().spawnEntity(location, EntityType.PRIMED_TNT);
        Main.getInstance().registerEntityToGame(tnt, game);
        tnt.setFuseTicks(explosionTime * 20);
        if (!damagePlacer)
            tnt.setMetadata(player.getUniqueId().toString(), new FixedMetadataValue(Main.getInstance(), null));
        tnt.setMetadata("autoignited", new FixedMetadataValue(Main.getInstance(), null));
        new BukkitRunnable() {
            public void run() {
                Main.getInstance().unregisterEntityFromGame(tnt);
            }
        }.runTaskLater(Main.getInstance(), explosionTime + 10);
    }

}
