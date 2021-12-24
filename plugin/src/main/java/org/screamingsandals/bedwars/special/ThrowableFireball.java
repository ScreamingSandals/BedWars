package org.screamingsandals.bedwars.special;

import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.Team;
import org.screamingsandals.bedwars.api.game.Game;

public class ThrowableFireball extends SpecialItem implements org.screamingsandals.bedwars.api.special.ThrowableFireball {

    private float damage;
    private boolean incendiary;
    private boolean damagesThrower;

    public ThrowableFireball(Game game, Player player, Team team, float damage, boolean incendiary, boolean damagesThrower) {
        super(game, player, team);
        this.damage = damage;
        this.incendiary = incendiary;
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
    public boolean damagesThrower() {
        return damagesThrower;
    }

    @Override
    public void run() {
        Fireball fireball = player.launchProjectile(Fireball.class);
        // TODO allow setting perfect velocity (to player's exact facing instead of random spread)
        Main.getInstance().registerEntityToGame(fireball, game);
        fireball.setIsIncendiary(incendiary);
        fireball.setYield(damage);
        fireball.setBounce(false);
        fireball.setShooter(damagesThrower ? null : player);
    }

}
