package org.screamingsandals.bedwars.special;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.screamingsandals.bedwars.api.special.TNTSheep;
import org.screamingsandals.bedwars.entities.EntitiesManagerImpl;
import org.screamingsandals.bedwars.game.CurrentTeam;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.game.TeamColor;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.bedwars.lib.nms.entity.EntityUtils;
import org.bukkit.DyeColor;
import org.bukkit.entity.*;
import org.screamingsandals.lib.entity.EntityBasic;
import org.screamingsandals.lib.entity.EntityLiving;
import org.screamingsandals.lib.entity.EntityMapper;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.material.Item;
import org.screamingsandals.lib.material.builder.ItemFactory;
import org.screamingsandals.lib.tasker.Tasker;
import org.screamingsandals.lib.tasker.TaskerTime;
import org.screamingsandals.lib.world.LocationHolder;

@Getter
@EqualsAndHashCode(callSuper = true)
public class TNTSheepImpl extends SpecialItem implements TNTSheep<GameImpl, BedWarsPlayer, CurrentTeam, EntityBasic, LocationHolder, EntityLiving> {
    private final LocationHolder initialLocation;
    private final Item item;
    private final double speed;
    private final double followRange;
    private final double maxTargetDistance;
    private final int explosionTime;
    private EntityLiving entity;
    private EntityBasic tnt;

    public TNTSheepImpl(GameImpl game, BedWarsPlayer player, CurrentTeam team, LocationHolder loc, Item item,
                        double speed, double followRange, double maxTargetDistance, int explosionTime) {
        super(game, player, team);
        this.initialLocation = loc;
        this.item = item;
        this.speed = speed;
        this.followRange = followRange;
        this.maxTargetDistance = maxTargetDistance;
        this.explosionTime = explosionTime * 20;
    }

    public void spawn() {
        var sheep = EntityMapper.<EntityLiving>spawn("sheep", initialLocation).orElseThrow();
        var color = TeamColor.fromApiColor(team.getColor());
        var target = MiscUtils.findTarget(game, player, maxTargetDistance);

        sheep.as(Sheep.class).setColor(DyeColor.getByWoolData((byte) color.woolData));

        if (target == null) {
            Message
                    .of(LangKeys.SPECIALS_TNTSHEEP_NO_TARGET_FOUND)
                    .prefixOrDefault(game.getCustomPrefixComponent())
                    .send(player);
            sheep.remove();
            return;
        }

        entity = sheep;
        EntityUtils.makeMobAttackTarget(sheep, speed, followRange, 0)
            .getTargetSelector().attackTarget(target.as(Player.class));

        tnt = EntityMapper.spawn("tnt", initialLocation).orElseThrow();
        tnt.as(TNTPrimed.class).setFuseTicks(explosionTime);
        tnt.as(TNTPrimed.class).setIsIncendiary(false);
        sheep.addPassenger(tnt);

        game.registerSpecialItem(this);
        var entitiesManager = EntitiesManagerImpl.getInstance();
        entitiesManager.addEntityToGame(sheep, game);
        entitiesManager.addEntityToGame(tnt, game);

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

        Tasker.build(() -> {
                tnt.remove();
                sheep.remove();
                game.unregisterSpecialItem(TNTSheepImpl.this);
            })
            .delay(explosionTime + 13, TaskerTime.TICKS)
            .start();
    }
}
