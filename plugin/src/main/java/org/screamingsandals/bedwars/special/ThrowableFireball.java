package org.screamingsandals.bedwars.special;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.Team;
import org.screamingsandals.bedwars.api.game.Game;

public class ThrowableFireball extends SpecialItem implements org.screamingsandals.bedwars.api.special.ThrowableFireball {

    private float damage;
    private boolean incendiary;
    private boolean perfectVelocity;
    private boolean damagesThrower;

    public ThrowableFireball(Game game, Player player, Team team, float damage, boolean incendiary, boolean perfectVelocity, boolean damagesThrower) {
        super(game, player, team);
        this.damage = damage;
        this.incendiary = incendiary;
        this.perfectVelocity = perfectVelocity;
        this.damagesThrower = damagesThrower;
    }

    @Override
    public float getDamage() {
        return damage;
    }

    @Override
    public boolean isIncendiary() {
        return incendiary;
    }

    @Override
    public boolean hasPerfectVelocity() {
        return perfectVelocity;
    }

    @Override
    public boolean damagesThrower() {
        return damagesThrower;
    }

    @Override
    public void run() {
        Fireball fireball = player.launchProjectile(Fireball.class);
        Main.getInstance().registerEntityToGame(fireball, game);
        fireball.setShooter(null);
        fireball.setIsIncendiary(incendiary);
        fireball.setYield(damage);
        if (perfectVelocity) {
            fireball.setDirection(player.getEyeLocation().getDirection());
        }
        if (!damagesThrower) {
            fireball.setMetadata(player.getUniqueId().toString(), new FixedMetadataValue(Main.getInstance(), null));
        }
        fireball.setMetadata("fireball", new FixedMetadataValue(Main.getInstance(), null));
    }

}
