package misat11.bw.special;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import misat11.bw.Main;
import misat11.bw.api.Game;
import misat11.bw.api.Team;
import misat11.bw.utils.SpawnEffects;

import static misat11.bw.utils.I18n.i18n;

public class WarpPowder extends SpecialItem implements misat11.bw.api.special.WarpPowder {

	private int fullTeleportingTime;
	private BukkitTask teleportingTask = null;
	private int teleportingTime;

	private ItemStack item;

	public WarpPowder(Game game, Player player, Team team, ItemStack item, int delay) {
		super(game, player, team);
		this.item = item;
		this.fullTeleportingTime = delay;
		this.teleportingTime = delay;
	}

	@Override
	public void cancelTeleport(boolean removeSpecial, boolean showMessage, boolean decrementStack) {
		try {
			teleportingTask.cancel();
		} catch (Exception e) {

		}

		if (removeSpecial) {
			game.unregisterSpecialItem(this);
		}

		if (showMessage) {
			player.sendMessage(i18n("specials_warp_powder_canceled"));
		}

		if (decrementStack) {
			item.setAmount(item.getAmount() - 1);

			player.updateInventory();
		}
	}

	@Override
	public ItemStack getStack() {
		return item;
	}

	@Override
	public void runTask() {
		game.registerSpecialItem(this);

		teleportingTime = fullTeleportingTime;

		player.sendMessage(i18n("specials_warp_powder_started").replace("%time%", Double.toString(teleportingTime)));

		teleportingTask = new BukkitRunnable() {

			@Override
			public void run() {
				if (teleportingTime == 0) {
					cancelTeleport(true, false, true);
					player.teleport(team.getTeamSpawn());
				} else {
					SpawnEffects.spawnEffect(game, player, "game-effects.warppowdertick");
					teleportingTime--;
				}
			}
		}.runTaskTimer(Main.getInstance(), 0L, 20L);
	}

	@Override
	public void setStackAmount(int amount) {
		item.setAmount(amount);
	}

}
