package misat11.bw.special.listener;


import misat11.bw.Main;
import misat11.bw.api.APIUtils;
import misat11.bw.api.Game;
import misat11.bw.api.GameStatus;
import misat11.bw.api.events.BedwarsApplyPropertyToBoughtItem;
import misat11.bw.game.GamePlayer;
import misat11.bw.special.RescuePlatform;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import static misat11.lib.lang.I18n.i18n;

public class RescuePlatformListener implements Listener {

	public static final String RESCUE_PLATFORM_PREFIX = "Module:RescuePlatform:";

	@EventHandler
	public void onRescuePlatformRegistered(BedwarsApplyPropertyToBoughtItem event) {
		if (event.getPropertyName().equalsIgnoreCase("rescueplatform")) {
			String rescuePlatformString = RESCUE_PLATFORM_PREFIX + event.getIntProperty("delay");
			ItemStack stack = event.getStack();

			APIUtils.hashIntoInvisibleString(stack, rescuePlatformString);
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

		if (event.getItem() != null) {
			Material material = Main.getConfigurator().getDefinedMaterial("specials.rescue-platform.used-item", "BLAZE_ROD");

			if (event.getItem().getType() != material) {
				return;
			}
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
						int delay = Integer.parseInt(unhidden.split(":")[2]);
						RescuePlatform platform = new RescuePlatform(game, event.getPlayer(),
								game.getTeamOfPlayer(event.getPlayer()), stack, delay);

						RescuePlatform originalPlatform = (RescuePlatform) game
								.getFirstActivedSpecialItemOfPlayer(event.getPlayer(), RescuePlatform.class);

						if (originalPlatform != null) {
							if (originalPlatform.getStack().equals(platform.getStack())) {
								event.getPlayer().sendMessage(i18n("specials_rescue_platform_multiuse"));
								return;
							}
						}

						if (event.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN)
								.getType() != Material.AIR) {
							event.getPlayer().sendMessage(i18n("specials_rescue_platform_not_in_air"));
							return;
						}

						platform.createPlatform();
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
}
