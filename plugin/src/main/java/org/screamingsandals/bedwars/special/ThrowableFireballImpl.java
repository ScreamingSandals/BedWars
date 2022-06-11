package org.screamingsandals.bedwars.special;

import lombok.Getter;
import org.screamingsandals.bedwars.api.special.ThrowableFireball;
import org.screamingsandals.bedwars.entities.EntitiesManagerImpl;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.game.TeamImpl;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.bedwars.player.PlayerManagerImpl;

@Getter
public class ThrowableFireballImpl extends SpecialItem implements ThrowableFireball<GameImpl, BedWarsPlayer, TeamImpl> {
    private final float damage;
    private final boolean incendiary;
    private final boolean damagesThrower;

    public ThrowableFireballImpl(GameImpl game, BedWarsPlayer player, TeamImpl team, float damage, boolean incendiary, boolean damagesThrower) {
        super(game, player, team);
        this.damage = damage;
        this.incendiary = incendiary;
        this.damagesThrower = damagesThrower;
    }

    @Override
    public boolean damagesThrower() {
        return damagesThrower;
    }

    @Override
    public void run() {
        var fireball = player.launchProjectile("minecraft:fireball").orElseThrow();
        fireball.setMetadata("is_incendiary", false);
        fireball.setMetadata("yield", damage);
        fireball.setBounce(false);
        fireball.setShooter(damagesThrower ? null : player);
        EntitiesManagerImpl.getInstance().addEntityToGame(fireball, PlayerManagerImpl.getInstance().getGameOfPlayer(player).orElseThrow());
    }
}
