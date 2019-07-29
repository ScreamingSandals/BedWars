package misat11.bw.special;

import misat11.bw.Main;
import misat11.bw.api.Game;
import misat11.bw.api.Team;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import static misat11.lib.lang.I18n.i18n;

public class ArrowBlocker extends SpecialItem implements misat11.bw.api.special.ArrowBlocker {
	private Game game;
	private Player player;

	private int protectionTime;
	private int usedTime;
	private int delayTime;
	private int delay;

	private boolean isActivated;
	private boolean canUse;

	private ItemStack item;

	public ArrowBlocker(Game game, Player player, Team team, ItemStack item, int delay) {
		super(game, player, team);
		this.game = game;
		this.player = player;
		this.item = item;
		this.delay = delay;

		canUse = true;
	}

	@Override
	public int getProtectionTime() {
		return protectionTime;
	}

	@Override
	public int getUsedTime() {
		return usedTime;
	}

	public int getDelayTime() {
		return delayTime;
	}

	@Override
	public boolean isActivated() {
		return isActivated;
	}

	public boolean canUse() {
		return canUse;
	}

	@Override
	public void runTask() {
		new BukkitRunnable() {

			@Override
			public void run() {
				usedTime++;

				if (usedTime == protectionTime) {
					isActivated = false;
					player.sendMessage(i18n("specials_arrow_blocker_ended"));

					game.unregisterSpecialItem(ArrowBlocker.this);
					this.cancel();
				}
			}
		}.runTaskTimer(Main.getInstance(), 0L, 20L);
	}

	public void activate() {
		protectionTime = Main.getConfigurator().config.getInt("specials.arrow-blocker.protection-time", 10);
		delayTime = delay;
		canUse = true;

		if (protectionTime > 0) {
			game.registerSpecialItem(this);
			runTask();

			item.setAmount(item.getAmount() - 1);
			player.updateInventory();

			player.sendMessage(i18n("specials_arrow_blocker_started").replace("%time%", Integer.toString(protectionTime)));
			canUse = false;
		}
	}
}
