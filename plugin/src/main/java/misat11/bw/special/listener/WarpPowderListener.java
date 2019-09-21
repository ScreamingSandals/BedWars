package misat11.bw.special.listener;

import misat11.bw.Main;
import misat11.bw.api.APIUtils;
import misat11.bw.api.Game;
import misat11.bw.api.GameStatus;
import misat11.bw.api.events.BedwarsApplyPropertyToBoughtItem;
import misat11.bw.game.GamePlayer;
import misat11.bw.special.RescuePlatform;
import misat11.bw.special.WarpPowder;
import misat11.bw.utils.DelayFactory;
import misat11.bw.utils.MiscUtils;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import static misat11.lib.lang.I18n.i18nonly;

public class WarpPowderListener implements Listener {
	private static final String WARP_POWDER_PREFIX = "Module:WarpPowder:";

	@EventHandler
	public void onPowderItemRegister(BedwarsApplyPropertyToBoughtItem event) {
		if (event.getPropertyName().equalsIgnoreCase("warppowder")) {
			ItemStack stack = event.getStack();
			APIUtils.hashIntoInvisibleString(stack, applyProperty(event));
		}
	}

	@EventHandler
	public void onPlayerUseItem(PlayerInteractEvent event) {
		Player player = event.getPlayer();
<<<<<<< HEAD

		if (event.isCancelled() && event.getAction() != Action.RIGHT_CLICK_AIR) {
			return;
		}

		if (!Main.isPlayerInGame(player)) {
			return;
		}
=======
		if (!Main.isPlayerInGame(player)) {
			return;
		}

>>>>>>> master
		GamePlayer gPlayer = Main.getPlayerGameProfile(player);
		Game game = gPlayer.getGame();

		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (game.getStatus() == GameStatus.RUNNING && !gPlayer.isSpectator) {
				if (event.getItem() != null) {
<<<<<<< HEAD
					ItemStack stack = event.getItem();
					String unhidden = APIUtils.unhashFromInvisibleStringStartsWith(stack, WARP_POWDER_PREFIX);

					if (unhidden != null) {
						if (!game.isDelayActive(player, WarpPowder.class)) {
							event.setCancelled(true);

							int teleportTime = Integer.parseInt(unhidden.split(":")[1]);
							int delay = Integer.parseInt(unhidden.split(":")[2]);
							WarpPowder warpPowder = new WarpPowder(game, event.getPlayer(),
									game.getTeamOfPlayer(event.getPlayer()), stack, teleportTime);

							if (event.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN)
									.getType() == Material.AIR) {
								return;
							}

							if (delay > 0) {
								DelayFactory delayFactory = new DelayFactory(delay, warpPowder, player, game);
								game.registerDelay(delayFactory);
							}

							warpPowder.runTask();
						}
					} else {
						event.setCancelled(true);

						int delay = game.getActiveDelay(player, RescuePlatform.class).getRemainDelay();
						MiscUtils.sendActionBarMessage(player, i18nonly("special_item_delay").replace("%time%", String.valueOf(delay)));
=======
					if (!game.isDelayActive(player, WarpPowder.class)) {
						ItemStack stack = event.getItem();
						String unhidden = APIUtils.unhashFromInvisibleStringStartsWith(stack, WARP_POWDER_PREFIX);

						if (unhidden != null) {
							if (!game.isDelayActive(player, WarpPowder.class)) {
								event.setCancelled(true);

								int teleportTime = Integer.parseInt(unhidden.split(":")[2]);
								int delay = Integer.parseInt(unhidden.split(":")[3]);
								WarpPowder warpPowder = new WarpPowder(game, event.getPlayer(),
										game.getTeamOfPlayer(event.getPlayer()), stack, teleportTime);

								if (event.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN)
										.getType() == Material.AIR) {
									return;
								}

								if (delay > 0) {
									DelayFactory delayFactory = new DelayFactory(delay, warpPowder, player, game);
									game.registerDelay(delayFactory);
								}

								warpPowder.runTask();
							} else {
								event.setCancelled(true);

								int delay = game.getActiveDelay(player, RescuePlatform.class).getRemainDelay();
								MiscUtils.sendActionBarMessage(player, i18nonly("special_item_delay").replace("%time%", String.valueOf(delay)));
							}
						}
>>>>>>> master
					}
				}
			}
		}
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if (event.isCancelled() || !(event.getEntity() instanceof Player)) {
			return;
		}

		Player player = (Player) event.getEntity();

		if (!Main.isPlayerInGame(player)) {
			return;
		}

		GamePlayer gPlayer = Main.getPlayerGameProfile(player);
		Game game = gPlayer.getGame();

		if (gPlayer.isSpectator) {
			return;
		}

		WarpPowder warpPowder = (WarpPowder) game.getFirstActivedSpecialItemOfPlayer(player, WarpPowder.class);
		if (warpPowder != null) {
			warpPowder.cancelTeleport(true, true, false);
		}
	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
<<<<<<< HEAD
		if (event.isCancelled()) {
			return;
		}

		if (!Main.isPlayerInGame(player)) {
			return;
		}

=======
		if (event.isCancelled() || !Main.isPlayerInGame(player)) {
			return;
		}

>>>>>>> master
		GamePlayer gPlayer = Main.getPlayerGameProfile(player);
		Game game = gPlayer.getGame();
		if (gPlayer.isSpectator) {
			return;
		}

		WarpPowder warpPowder = (WarpPowder) game.getFirstActivedSpecialItemOfPlayer(player, WarpPowder.class);
		if (warpPowder != null) {
			if (warpPowder.getStack().equals(event.getItemDrop().getItemStack())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
<<<<<<< HEAD
		if (event.isCancelled()) {
			return;
		}

		if (!Main.isPlayerInGame(player)) {
=======
		if (event.isCancelled() || !Main.isPlayerInGame(player)) {
			return;
		}

		if (event.getFrom().getBlock().equals(event.getTo().getBlock())) {
>>>>>>> master
			return;
		}

		GamePlayer gPlayer = Main.getPlayerGameProfile(player);
		Game game = gPlayer.getGame();
		if (gPlayer.isSpectator) {
			return;
		}

		WarpPowder warpPowder = (WarpPowder) game.getFirstActivedSpecialItemOfPlayer(player, WarpPowder.class);
		if (warpPowder != null) {
			warpPowder.cancelTeleport(true, true, false);
		}
	}

	private String applyProperty(BedwarsApplyPropertyToBoughtItem event) {
		return WARP_POWDER_PREFIX
<<<<<<< HEAD
				+ MiscUtils.getBooleanFromProperty(
=======
				+ MiscUtils.getIntFromProperty(
>>>>>>> master
				"teleport-time", "specials.warp-powder.teleport-time", event) + ":"
				+ MiscUtils.getIntFromProperty(
				"delay", "specials.warp-powder.delay", event);
	}
}
