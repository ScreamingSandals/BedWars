package misat11.bw.special;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.scheduler.BukkitRunnable;

import misat11.bw.Main;
import misat11.bw.api.Game;
import misat11.bw.api.Team;
import misat11.bw.game.TeamColor;
import static misat11.lib.lang.I18n.i18n;

public class TNTSheep extends SpecialItem implements misat11.bw.api.special.TNTSheep {

	private LivingEntity entity;
	private TNTPrimed tnt;
	private Location loc;
	private double speed;
	private double followRange;
	private boolean used = false;

	public TNTSheep(Game game, Player player, Team team, Location loc, double speed, double followRange) {
		super(game, player, team);
		this.loc = loc;
		this.speed = speed;
		this.followRange = followRange;
		game.registerSpecialItem(this);
	}

	public boolean use() {
		if (!used) {
			Sheep sheep = (Sheep) loc.getWorld().spawnEntity(loc, EntityType.SHEEP);
			TeamColor color = TeamColor.fromApiColor(team.getColor());
			sheep.setColor(DyeColor.getByWoolData((byte) color.woolData));
			Player target = Tracker.find(game, player);
			if (target == null) {
				player.sendMessage(i18n("specials_tntsheep_no_target_found"));
				sheep.remove();
				return false;
			}
			sheep.setTarget(target);
			entity = sheep;

			tnt = (TNTPrimed) loc.getWorld().spawnEntity(loc, EntityType.PRIMED_TNT);
			tnt.setFuseTicks(8 * 20); // TODO remove this hardcoded ****
			tnt.setIsIncendiary(false);
			sheep.addPassenger(tnt);

			Main.registerGameEntity(sheep, (misat11.bw.game.Game) game);
			Main.registerGameEntity(tnt, (misat11.bw.game.Game) game);

			new BukkitRunnable() {

				@Override
				public void run() {
					tnt.remove();
					sheep.remove();
					game.unregisterSpecialItem(TNTSheep.this);
				}
			}.runTaskLater(Main.getInstance(), (long) ((8 * 20) + 13));

			used = true;
		}
		return used;
	}

	@Override
	public LivingEntity getEntity() {
		return entity;
	}

	@Override
	public boolean isUsed() {
		return used;
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

}
