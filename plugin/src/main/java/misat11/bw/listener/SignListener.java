package misat11.bw.listener;

import static misat11.lib.lang.I18n.i18n;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import misat11.bw.Main;
import misat11.bw.commands.BwCommand;
import misat11.bw.game.Game;
import misat11.bw.utils.GameSign;

public class SignListener implements Listener {

	public static final List<String> BEDWARS_SIGN_PREFIX = Arrays.asList("[bedwars]", "[bwgame]");

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (event.getClickedBlock().getState() instanceof Sign) {
				if (Main.isSignRegistered(event.getClickedBlock().getLocation())) {
					GameSign sign = Main.getSign(event.getClickedBlock().getLocation());
					if (sign.getGameName().equalsIgnoreCase("leave")) {
						if (Main.isPlayerInGame(event.getPlayer())) {
							Main.getPlayerGameProfile(event.getPlayer()).changeGame(null);
						}
					} else {
						Game game = sign.getGame();
						if (game != null) {
							game.joinToGame(event.getPlayer());
						} else {
							event.getPlayer().sendMessage(i18n("sign_game_not_exists"));
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.isCancelled()) {
			return;
		}

		if (event.getBlock().getState() instanceof Sign) {
			Location loc = event.getBlock().getLocation();
			if (Main.isSignRegistered(loc)) {
				if (event.getPlayer().hasPermission(BwCommand.ADMIN_PERMISSION)) {
					Main.unregisterSign(loc);
				} else {
					event.getPlayer().sendMessage(i18n("sign_can_not_been_destroyed"));
					event.setCancelled(true);
				}
			}
		}

	}

	@EventHandler
	public void onChangeSign(SignChangeEvent event) {
		if (event.isCancelled()) {
			return;
		}
		if (event.getBlock().getState() instanceof Sign) {
			Location loc = event.getBlock().getLocation();
			if (BEDWARS_SIGN_PREFIX.contains(event.getLine(0).toLowerCase())) {
				if (event.getPlayer().hasPermission(BwCommand.ADMIN_PERMISSION)) {
					if (Main.registerSign(loc, event.getLine(1))) {
						event.getPlayer().sendMessage(i18n("sign_successfully_created"));
					} else {
						event.getPlayer().sendMessage(i18n("sign_can_not_been_created"));
						event.setCancelled(true);
						event.getBlock().breakNaturally();
					}
				}
			}
		}
	}
}
