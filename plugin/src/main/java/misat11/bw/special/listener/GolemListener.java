package misat11.bw.special.listener;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import misat11.bw.Main;
import misat11.bw.api.APIUtils;
import misat11.bw.api.Game;
import misat11.bw.api.GameStatus;
import misat11.bw.api.events.BedwarsApplyPropertyToBoughtItem;
import misat11.bw.api.special.SpecialItem;
import misat11.bw.game.GamePlayer;
import misat11.bw.special.Golem;

public class GolemListener implements Listener {

	public static final String GOLEM_PREFIX = "Module:Golem:";

	@EventHandler
	public void onGolemRegistered(BedwarsApplyPropertyToBoughtItem event) {
		if (event.getPropertyName().equalsIgnoreCase("golem")) {
			ItemStack stack = event.getStack();

			String specialString = GOLEM_PREFIX + event.getDoubleProperty("speed") + ":"
					+ event.getDoubleProperty("follow");

			APIUtils.hashIntoInvisibleString(stack, specialString);
		}

	}

	@EventHandler
	public void onGolemUsed(PlayerInteractEvent event) {
		if (event.isCancelled() && event.getAction() != Action.RIGHT_CLICK_AIR) {
			return;
		}

		if (!Main.isPlayerInGame(event.getPlayer())) {
			return;
		}

		Player eventPlayer = event.getPlayer();
		GamePlayer gamePlayer = Main.getPlayerGameProfile(eventPlayer);
		Game game = gamePlayer.getGame();
		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (game.getStatus() == GameStatus.RUNNING && !gamePlayer.isSpectator) {
				if (event.getItem() != null) {
					ItemStack stack = event.getItem();
					String unhidden = APIUtils.unhashFromInvisibleStringStartsWith(stack, GOLEM_PREFIX);
					if (unhidden != null) {
						event.setCancelled(true);
						String[] splitted = unhidden.split(":");
						double speed = Double.parseDouble(splitted[2]);
						double follow = Double.parseDouble(splitted[3]);
						Location startLocation;
						if (event.getClickedBlock() == null) {
							startLocation = eventPlayer.getLocation();
						} else {
							startLocation = event.getClickedBlock().getRelative(BlockFace.UP)
									.getLocation().add(0.5, 0.5, 0.5);
						}
						Golem golem = new Golem(game, eventPlayer, game.getTeamOfPlayer(eventPlayer),
								startLocation, speed, follow);
						if (golem.use()) {
							ItemStack replace = null;
							if (event.getItem().getAmount() > 1) {
								replace = event.getItem().clone();
								replace.setAmount(event.getItem().getAmount() - 1);
							}
							try {
								if (event.getHand() == EquipmentSlot.HAND) {
									eventPlayer.getInventory().setItemInMainHand(replace);
								} else {
									eventPlayer.getInventory().setItemInOffHand(replace);
								}
							} catch (Throwable t) {
								eventPlayer.setItemInHand(replace);
							}
						}
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onGolemDamage(EntityDamageByEntityEvent event) {
		if (event.isCancelled()) {
			return;
		}

		if (event.getCause().equals(DamageCause.CUSTOM) || event.getCause().equals(DamageCause.VOID)) {
			return;
		}

		if (!(event.getEntity() instanceof IronGolem)) {
			return;
		}

		if (event.getDamager() instanceof Player) {
			Player player = (Player) event.getDamager();
			if (Main.isPlayerInGame(player)) {
				GamePlayer gamePlayer = Main.getPlayerGameProfile(player);
				Game game = gamePlayer.getGame();
				if (!game.getOriginalOrInheritedFriendlyfire()) {
					for (SpecialItem item : game.getActivedSpecialItems(Golem.class)) {
						if (item instanceof Golem) {
							if (((Golem) item).getEntity().equals(event.getEntity())) {
								if (item.getTeam() == game.getTeamOfPlayer(player)) {
									event.setCancelled(true);
								}
								return;
							}
						}
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onGolemTarget(EntityTargetEvent event) {
		if (event.isCancelled()) {
			return;
		}

		if (!(event.getEntity() instanceof IronGolem)) {
			return;
		}

		if (event.getReason().equals(TargetReason.CUSTOM)) {
			return;
		}

		// When a *golem* targets an entity
		IronGolem ironGolem = (IronGolem) event.getEntity();
		for (String name : Main.getGameNames()) {
			Game game = Main.getGame(name);
			if (game.getStatus() == GameStatus.RUNNING && ironGolem.getWorld().equals(game.getGameWorld())) {
				List<SpecialItem> golems = game.getActivedSpecialItems(Golem.class);
				for (SpecialItem item : golems) {
					if (item instanceof Golem) {
						Golem golem = (Golem) item;
						if (golem.getEntity().equals(ironGolem)) {
							// Target enemy players only
							if (event.getTarget() instanceof Player) {
								Player player = (Player) event.getTarget();
								if (Main.isPlayerInGame(player)) {
									if (golem.getTeam() != game.getTeamOfPlayer(player)) {
										return;
									}
								}
							}
							// Don't target anything except an enemy player
							event.setCancelled(true);
							return;
						}
					}
				}
			}
		}
	}

}
