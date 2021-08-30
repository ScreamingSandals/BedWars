package org.screamingsandals.bedwars.special;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bukkit.entity.*;
import org.screamingsandals.bedwars.api.special.Golem;
import org.screamingsandals.bedwars.entities.EntitiesManagerImpl;
import org.screamingsandals.bedwars.game.CurrentTeam;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.game.TeamColor;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.bedwars.lib.nms.entity.EntityUtils;
import org.screamingsandals.lib.entity.EntityLiving;
import org.screamingsandals.lib.entity.EntityMapper;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.material.Item;
import org.screamingsandals.lib.material.builder.ItemFactory;
import org.screamingsandals.lib.nms.accessors.ServerPlayerAccessor;
import org.screamingsandals.lib.world.LocationHolder;

@Getter
@EqualsAndHashCode(callSuper = true)
public class GolemImpl extends SpecialItem implements Golem<GameImpl, BedWarsPlayer, CurrentTeam, EntityLiving> {
    private EntityLiving entity;
    private final LocationHolder location;
    private final Item item;
    private final double speed;
    private final double followRange;
    private final double health;
    private final String name;
    private final boolean showName;

    public GolemImpl(GameImpl game, BedWarsPlayer player, CurrentTeam team,
                     Item item, LocationHolder location, double speed, double followRange, double health,
                     String name, boolean showName) {
        super(game, player, team);
        this.location = location;
        this.item = item;
        this.speed = speed;
        this.followRange = followRange;
        this.health = health;
        this.name = name;
        this.showName = showName;
    }

    public void spawn() {
        final var color = TeamColor.fromApiColor(team.getColor());
        final var golem = EntityMapper.<EntityLiving>spawn("iron_golem", location).orElseThrow();
        golem.setHealth(health);
        golem.setCustomName(name
                .replace("%teamcolor%", color.chatColor.toString())
                .replace("%team%", team.getName()));
        golem.setCustomNameVisible(showName);
        try {
            golem.setInvulnerable(false);
        } catch (Throwable ignored) {
            // Still can throw an exception on some old versions
        }
        
        entity = golem;

        EntityUtils.makeMobAttackTarget(golem, speed, followRange, -1)
                .getTargetSelector()
                .attackNearestTarget(0, ServerPlayerAccessor.getType());

        game.registerSpecialItem(this);
        EntitiesManagerImpl.getInstance().addEntityToGame(golem, game);
        MiscUtils.sendActionBarMessage(player, Message.of(LangKeys.SPECIALS_GOLEM_CREATED));

        //TODO - make this better by checking full inventory
        item.setAmount(1);
        try {
            if (player.getPlayerInventory().getItemInOffHand().equals(item)) {
                player.getPlayerInventory().setItemInOffHand(ItemFactory.getAir());
            } else {
                player.getPlayerInventory().removeItem(item);
            }
        } catch (Throwable e) {
            player.getPlayerInventory().removeItem(item);
        }

        player.as(Player.class).updateInventory();
    }
}
