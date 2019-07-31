package misat11.bw.special.listener;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEntityEvent;
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
import misat11.bw.special.TNTSheep;

public class TNTSheepListener implements Listener {

	public static final String TNT_SHEEP_PREFIX = "Module:TNTSheep:";

	@EventHandler
	public void onTNTSheepRegistered(BedwarsApplyPropertyToBoughtItem event) {
		if (event.getPropertyName().equalsIgnoreCase("tntsheep")) {
			ItemStack stack = event.getStack();

			String specialString = TNT_SHEEP_PREFIX + event.getDoubleProperty("speed") + ":"
					+ event.getDoubleProperty("follow");

			APIUtils.hashIntoInvisibleString(stack, specialString);
		}

	}

	@EventHandler
	public void onTNTSheepUsed(PlayerInteractEvent event) {
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
					String unhidden = APIUtils.unhashFromInvisibleStringStartsWith(stack, TNT_SHEEP_PREFIX);
					if (unhidden != null) {
						event.setCancelled(true);
						String[] splitted = unhidden.split(":");
						double speed = Double.parseDouble(splitted[2]);
						double follow = Double.parseDouble(splitted[3]);
						Location startLocation;
						if (event.getClickedBlock() == null) {
							startLocation = eventPlayer.getLocation(); // TODO do something better :D
						} else {
							startLocation = event.getClickedBlock().getRelative(BlockFace.UP).getLocation();
						}
						TNTSheep sheep = new TNTSheep(game, eventPlayer, game.getTeamOfPlayer(eventPlayer),
								startLocation, speed, follow);
						if (sheep.use()) {
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
								// F*cking 1.8
								eventPlayer.setItemInHand(replace);
							}
						}
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onTNTSheepDamge(EntityDamageByEntityEvent event) {
		if (event.isCancelled()) {
			return;
		}

		if (event.getCause().equals(DamageCause.CUSTOM) || event.getCause().equals(DamageCause.VOID)
				|| event.getCause().equals(DamageCause.FALL)) {
			return;
		}

		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			if (Main.isPlayerInGame(player)) {
				GamePlayer gamePlayer = Main.getPlayerGameProfile(player);
				Game game = gamePlayer.getGame();
				if (event.getDamager() instanceof TNTPrimed && !game.getOriginalOrInheritedFriendlyfire()) {
					TNTPrimed tnt = (TNTPrimed) event.getDamager();
					List<SpecialItem> sheeps = game.getActivedSpecialItems(TNTSheep.class);
					for (SpecialItem item : sheeps) {
						if (item instanceof TNTSheep) {
							TNTSheep sheep = (TNTSheep) item;
							if (tnt.equals(sheep.getTNT())) {
								if (sheep.getTeam() == game.getTeamOfPlayer(player)) {
									event.setCancelled(true);
								}
								return;
							}
						}
					}
				}
			}
		} else if (event.getEntity() instanceof Creature) {
			Creature mob = (Creature) event.getEntity();
			for (String name : Main.getGameNames()) {
				Game game = Main.getGame(name);
				if (game.getStatus() == GameStatus.RUNNING && mob.getWorld().equals(game.getGameWorld())) {
					List<SpecialItem> sheeps = game.getActivedSpecialItems(TNTSheep.class);
					for (SpecialItem item : sheeps) {
						if (item instanceof TNTSheep) {
							TNTSheep sheep = (TNTSheep) item;
							if (mob.equals(sheep.getEntity())) {
								event.setDamage(0.0);
								return;
							}
						}
					}
				}
			}
		}

	}

	@EventHandler
	public void onTNTSheepInteractOtherUser(PlayerInteractEntityEvent event) {
		// TODO think about if it's needed
		if (event.getPlayer() == null) {
			return;
		}

		Player player = event.getPlayer();
		if (Main.isPlayerInGame(player)) {
			GamePlayer gamePlayer = Main.getPlayerGameProfile(player);
			Game game = gamePlayer.getGame();

			if (event.getRightClicked() == null) {
				return;
			}
			Entity rightClicked = event.getRightClicked();
			Entity vehicle = rightClicked.getVehicle();

			List<SpecialItem> sheeps = game.getActivedSpecialItems(TNTSheep.class);
			for (SpecialItem item : sheeps) {
				if (item instanceof TNTSheep) {
					TNTSheep sheep = (TNTSheep) item;
					if (sheep.getEntity().equals(rightClicked) || sheep.getEntity().equals(vehicle)) {
						event.setCancelled(true);
						return;
					}
				}
			}
		}
	}

}
