package misat11.bw.special;

import misat11.bw.Main;
import misat11.bw.api.Game;
import misat11.bw.api.Team;
import misat11.bw.game.TeamColor;
import misat11.bw.utils.MiscUtils;
import misat11.lib.nms.NMSUtils;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static misat11.lib.lang.I18n.i18nonly;

public class Golem extends SpecialItem implements misat11.bw.api.special.Golem {
	private LivingEntity entity;
	private Location loc;
	private ItemStack item;
	private double speed;
	private double followRange;
	private double health;
	private String name;
	private boolean showName;

	public Golem(Game game, Player player, Team team,
				 ItemStack item, Location loc, double speed, double followRange, double health,
				 String name, boolean showName) {
		super(game, player, team);
		this.loc = loc;
		this.item = item;
		this.speed = speed;
		this.followRange = followRange;
		this.health = health;
		this.name = name;
		this.showName = showName;
	}

	@Override
	public LivingEntity getEntity() {
		return entity;
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
		TeamColor color = TeamColor.fromApiColor(team.getColor());
		IronGolem golem = (IronGolem) loc.getWorld().spawnEntity(loc, EntityType.IRON_GOLEM);

		golem.setCustomName(name
				.replace("%teamcolor%", color.chatColor.toString())
				.replace("%team%", team.getName()));
		golem.setCustomNameVisible(showName);
		entity = golem;

		NMSUtils.makeMobAttackTarget(golem, speed, followRange, -1).attackNearestTarget(0, "EntityPlayer");

		game.registerSpecialItem(this);
		Main.registerGameEntity(golem, (misat11.bw.game.Game) game);
		MiscUtils.sendActionBarMessage(player, i18nonly("specials_golem_created"));

		item.setAmount(item.getAmount() - 1);
		player.updateInventory();
	}
}