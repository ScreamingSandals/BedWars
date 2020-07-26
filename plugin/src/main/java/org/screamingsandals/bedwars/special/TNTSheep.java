package org.screamingsandals.bedwars.special;

import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.Team;
import org.screamingsandals.bedwars.game.TeamColor;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.lib.nms.entity.EntityUtils;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import static misat11.lib.lang.I18n.i18n;

public class TNTSheep extends SpecialItem implements org.screamingsandals.bedwars.api.special.TNTSheep {
    private LivingEntity entity;
    private TNTPrimed tnt;
    private Location loc;
    private ItemStack item;
    private double speed;
    private double followRange;
    private double maxTargetDistance;
    private int explosionTime;

    public TNTSheep(Game game, Player player, Team team, Location loc, ItemStack item,
                    double speed, double followRange, double maxTargetDistance, int explosionTime) {
        super(game, player, team);
        this.loc = loc;
        this.item = item;
        this.speed = speed;
        this.followRange = followRange;
        this.maxTargetDistance = maxTargetDistance;
        this.explosionTime = explosionTime * 20;
    }

    @Override
    public LivingEntity getEntity() {
        return entity;
    }

    @Override
    public Location getInitialLocation() {
        return loc;
    }

    @Override
    public TNTPrimed getTNT() {
        return tnt;
    }

    @Override
    public double getSpeed() {
        return speed;
    }

    @Override
    public double getFollowRange() {
        return followRange;
    }

    public void spawn() {
        Sheep sheep = (Sheep) loc.getWorld().spawnEntity(loc, EntityType.SHEEP);
        TeamColor color = TeamColor.fromApiColor(team.getColor());
        Player target = MiscUtils.findTarget(game, player, maxTargetDistance);

        sheep.setColor(DyeColor.getByWoolData((byte) color.woolData));

        if (target == null) {
            player.sendMessage(i18n("specials_tntsheep_no_target_found"));
            sheep.remove();
            return;
        }

        entity = sheep;
        EntityUtils.makeMobAttackTarget(sheep, speed, followRange, 0)
            .getTargetSelector().attackTarget(target);

        tnt = (TNTPrimed) loc.getWorld().spawnEntity(loc, EntityType.PRIMED_TNT);
        tnt.setFuseTicks(explosionTime);
        tnt.setIsIncendiary(false);
        sheep.addPassenger(tnt);

        game.registerSpecialItem(this);
        Main.registerGameEntity(sheep, (org.screamingsandals.bedwars.game.Game) game);
        Main.registerGameEntity(tnt, (org.screamingsandals.bedwars.game.Game) game);

        if (item.getAmount() > 1) {
            item.setAmount(item.getAmount() - 1);
        } else {
            try {
                if (player.getInventory().getItemInOffHand().equals(item)) {
                    player.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
                } else {
                    player.getInventory().remove(item);
                }
            } catch (Throwable e) {
                player.getInventory().remove(item);
            }
        }
        player.updateInventory();

        new BukkitRunnable() {

            @Override
            public void run() {
                tnt.remove();
                sheep.remove();
                game.unregisterSpecialItem(TNTSheep.this);
            }
        }.runTaskLater(Main.getInstance(), (explosionTime + 13));
    }
}
