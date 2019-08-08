package misat11.bw.special.listener;


import misat11.bw.Main;
import misat11.bw.api.APIUtils;
import misat11.bw.api.Game;
import misat11.bw.api.GameStatus;
import misat11.bw.api.events.BedwarsApplyPropertyToBoughtItem;
import misat11.bw.api.special.SpecialItem;
import misat11.bw.game.GamePlayer;
import misat11.bw.special.RescuePlatform;
import misat11.bw.utils.MiscUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

import static misat11.lib.lang.I18n.i18n;
import static misat11.lib.lang.I18n.i18nonly;

public class RescuePlatformListener implements Listener {
	private static final String RESCUE_PLATFORM_PREFIX = "Module:RescuePlatform:";
	private boolean isUsable = true;
	private int delay;

	@EventHandler
	public void onRescuePlatformRegistered(BedwarsApplyPropertyToBoughtItem event) {
		if (event.getPropertyName().equalsIgnoreCase("rescueplatform")) {
			ItemStack stack = event.getStack();
			APIUtils.hashIntoInvisibleString(stack, applyProperty(event));
		}

	}

	@EventHandler
	public void onPlayerUseItem(PlayerInteractEvent event) {
		if (event.isCancelled() && event.getAction() != Action.RIGHT_CLICK_AIR) {
			return;
		}

		if (!Main.isPlayerInGame(event.getPlayer())) {
			return;
		}

		GamePlayer gPlayer = Main.getPlayerGameProfile(event.getPlayer());
		Game game = gPlayer.getGame();
		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (game.getStatus() == GameStatus.RUNNING && !gPlayer.isSpectator) {
				if (event.getItem() != null) {
					ItemStack stack = event.getItem();
					String unhidden = APIUtils.unhashFromInvisibleStringStartsWith(stack, RESCUE_PLATFORM_PREFIX);

					if (unhidden != null) {
						event.setCancelled(true);

						boolean isBreakable = Boolean.parseBoolean(unhidden.split(":")[2]);
						int breakTime = Integer.parseInt(unhidden.split(":")[4]);
						int distance = Integer.parseInt(unhidden.split(":")[5]);
						Material material = MiscUtils.getMaterialFromString(unhidden.split(":")[6], "GLASS");

						RescuePlatform platform = new RescuePlatform(game, event.getPlayer(),
								game.getTeamOfPlayer(event.getPlayer()), stack);

						if (event.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN)
								.getType() != Material.AIR) {
							event.getPlayer().sendMessage(i18n("specials_rescue_platform_not_in_air"));
							return;
						}

						if (isUsable) {
							platform.createPlatform(isBreakable, breakTime, distance, material);
							delay = Integer.parseInt(unhidden.split(":")[3]);
							runCountdown();
						} else {
							MiscUtils.sendActionBarMessage(event.getPlayer(), i18nonly("special_item_delay").replace("%time%", String.valueOf(delay)));
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onFallDamage(EntityDamageEvent event) {
		Entity entity = event.getEntity();

		if (event.isCancelled()) {
			return;
		}

		if (!(entity instanceof Player)) {
			return;
		}

		Player player = ((Player) entity).getPlayer();

		if (!Main.isPlayerInGame(player)) {
			return;
		}

		GamePlayer gPlayer = Main.getPlayerGameProfile(player);
		Game game = gPlayer.getGame();

		if (gPlayer.isSpectator) {
			return;
		}

		RescuePlatform rescuePlatform = (RescuePlatform) game.getFirstActivedSpecialItemOfPlayer(player, RescuePlatform.class);
		if (rescuePlatform != null && event.getCause() == EntityDamageEvent.DamageCause.FALL) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();

		if (event.isCancelled()) {
			return;
		}

		if (!Main.isPlayerInGame(player)) {
			return;
		}

		GamePlayer gPlayer = Main.getPlayerGameProfile(player);
		Game game = gPlayer.getGame();

		if (gPlayer.isSpectator) {
			return;
		}

		Block block = event.getBlock();
		for (RescuePlatform checkedPlatform : getCreatedPlatforms(game, player)) {
			if (checkedPlatform != null) {
				for (Block platformBlock : checkedPlatform.getPlatformBlocks()) {
					if (platformBlock.equals(block) && !checkedPlatform.canBreak()) {
						event.setCancelled(true);
					}

				}
			}
		}
	}

	private ArrayList<RescuePlatform> getCreatedPlatforms(Game game, Player player) {
		ArrayList<RescuePlatform> createdPlatforms = new ArrayList<>();
		for (SpecialItem specialItem : game.getActivedSpecialItemsOfPlayer(player)) {
			if (specialItem instanceof RescuePlatform) {
				RescuePlatform platform = (RescuePlatform) specialItem;
				createdPlatforms.add(platform);
			}
		}
		return createdPlatforms;
	}

	private void runCountdown() {
		if (delay > 0) {
			isUsable = false;
			new BukkitRunnable() {

				@Override
				public void run() {
					delay--;
					if (delay == 0) {
						isUsable = true;
						this.cancel();
					}
				}
			}.runTaskTimer(Main.getInstance(), 20L, 20L);
		}
	}

	private String applyProperty(BedwarsApplyPropertyToBoughtItem event) {
		return RESCUE_PLATFORM_PREFIX
				+ MiscUtils.getBooleanFromProperty(
				"is-breakable", "specials.rescue-platform.is-breakable", event) + ":"
				+ MiscUtils.getIntFromProperty(
				"delay", "specials.protection-wall.delay", event) + ":"
				+ MiscUtils.getIntFromProperty(
				"break-time", "specials.rescue-platform.break-time", event) + ":"
				+ MiscUtils.getIntFromProperty(
				"distance", "specials.rescue-platform.distance", event) + ":"
				+ MiscUtils.getMaterialFromProperty(
				"material", "specials.rescue-platform.material", event);
	}
}
