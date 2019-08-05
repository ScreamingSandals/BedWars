package misat11.bw.special;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import misat11.bw.Main;
import misat11.bw.api.Game;
import misat11.bw.api.Team;
import misat11.bw.game.TeamColor;
import misat11.lib.nms.NMSUtils;

public class Golem extends SpecialItem implements misat11.bw.api.special.Golem {

	private LivingEntity entity;
	private Location loc;
	private double speed, followRange;
	private boolean used = false;

	public Golem(Game game, Player player, Team team, Location loc, double speed, double followRange) {
		super(game, player, team);
		this.loc = loc;
		this.speed = speed;
		this.followRange = followRange;
		game.registerSpecialItem(this);
	}

	public boolean use() {
		if (!used) {
			TeamColor color = TeamColor.fromApiColor(team.getColor());
			IronGolem golem = (IronGolem) loc.getWorld().spawnEntity(loc, EntityType.IRON_GOLEM);

			FileConfiguration config = Main.getConfigurator().config;
			String name = config.getString("specials.golem.name-format", "%teamcolor%%team% Golem");
			assert name != null;
			name = name.replace("%teamcolor%", color.chatColor.toString()).replace("%team%", team.getName());
			golem.setCustomName(name);
			golem.setCustomNameVisible(config.getBoolean("specials.golem.show-name", true));
			entity = golem;

			NMSUtils.makeMobAttackTarget(golem, speed, followRange, -1).attackNearestTarget(0, "EntityPlayer");

			Main.registerGameEntity(golem, (misat11.bw.game.Game) game);
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
	public double getSpeed() {
		return speed;
	}

	@Override
	public double getFollowRange() {
		return followRange;
	}

}
