package misat11.bw.special.listener;


import misat11.bw.Main;
import misat11.bw.api.APIUtils;
import misat11.bw.api.Game;
import misat11.bw.api.GameStatus;
import misat11.bw.api.events.BedwarsApplyPropertyToBoughtItem;
import misat11.bw.game.GamePlayer;
import misat11.bw.special.ArrowBlocker;
import misat11.bw.utils.MiscUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import static misat11.lib.lang.I18n.i18n;

public class ArrowBlockerListener implements Listener {
	private static final String ARROW_BLOCKER_PREFIX = "Module:ArrowBlocker:";
	
	@EventHandler
	public void onArrowBlockerRegistered(BedwarsApplyPropertyToBoughtItem event) {
		if (event.getPropertyName().equalsIgnoreCase("arrowblocker")) {
			ItemStack stack = event.getStack();
			APIUtils.hashIntoInvisibleString(stack, applyProperty(event));
		}
	}

	@EventHandler
	public void onPlayerUseItem(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (event.isCancelled() || event.getAction() != Action.RIGHT_CLICK_AIR) {
			return;
		}

		if (!Main.isPlayerInGame(player)) {
			return;
		}

		GamePlayer gPlayer = Main.getPlayerGameProfile(player);
		Game game = gPlayer.getGame();

		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (game.getStatus() == GameStatus.RUNNING && !gPlayer.isSpectator && event.getItem() != null) {
				ItemStack stack = event.getItem();
				String unhidden = APIUtils.unhashFromInvisibleStringStartsWith(stack, ARROW_BLOCKER_PREFIX);

				if (unhidden != null) {
					event.setCancelled(true);
					int delay = Integer.parseInt(unhidden.split(":")[2]);
					ArrowBlocker arrowBlocker = new ArrowBlocker(game, event.getPlayer(),
							game.getTeamOfPlayer(event.getPlayer()), stack, delay);

					if (arrowBlocker.isActivated()) {
						event.getPlayer().sendMessage(i18n("specials_arrow_blocker_already_activated"));
						return;
					}

					arrowBlocker.activate();
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onDamage(EntityDamageEvent event) {
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

		ArrowBlocker arrowBlocker = (ArrowBlocker) game.getFirstActivedSpecialItemOfPlayer(player, ArrowBlocker.class);
		if (arrowBlocker != null && event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
			event.setCancelled(true);
		}
	}

	private String applyProperty(BedwarsApplyPropertyToBoughtItem event) {
		return ARROW_BLOCKER_PREFIX
				+ MiscUtils.getBooleanFromProperty(
				"protection-time", "specials.arrow-blocker.protection-time", event) + ":"
				+ MiscUtils.getIntFromProperty(
				"delay", "specials.arrow-blocker.delay", event);
	}
}
