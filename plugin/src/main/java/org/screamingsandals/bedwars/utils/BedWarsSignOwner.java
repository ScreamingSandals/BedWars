package org.screamingsandals.bedwars.utils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.commands.AdminCommand;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.bedwars.lib.signmanager.SignBlock;
import org.screamingsandals.bedwars.lib.signmanager.SignOwner;
import org.spongepowered.configurate.ConfigurationNode;

import static org.screamingsandals.bedwars.lib.lang.I.*;

public class BedWarsSignOwner implements SignOwner {

	@Override
	public boolean isNameExists(String name) {
		return Main.isGameExists(name) || name.equalsIgnoreCase("leave");
	}

	@Override
	public void updateSign(SignBlock sign) {
		String name = sign.getName();
		if (Main.isGameExists(name)) {
			new BukkitRunnable() {
				@Override
				public void run() {
					Main.getGame(name).updateSigns();
				}
			}.runTask(Main.getInstance());

		} else if ("leave".equalsIgnoreCase(name)) {
			new BukkitRunnable() {
				@Override
				public void run() {
					updateLeaveSign(sign);
				}
			}.runTask(Main.getInstance());
		}
	}

	private void updateLeaveSign(SignBlock sign) {
		List<String> texts = Main.getConfigurator().node("sign", "lines").childrenList().stream().map(ConfigurationNode::getString).collect(Collectors.toList());

		var opt = sign.getLocation().asOptional(Location.class);
		if (opt.isPresent()) {
			Block block = opt.get().getBlock();
			if (block.getState() instanceof Sign) {
				Sign state = (Sign) block.getState();

				for (int i = 0; i < texts.size(); i++) {
					String text = texts.get(i);
					state.setLine(i, text.replaceAll("%arena%", i18nonly("leave_from_game_item")).replaceAll("%status%", "")
							.replaceAll("%players%", ""));
				}

				state.update();
			}
		}
	}

	@Override
	public List<String> getSignPrefixes() {
		return Arrays.asList("[bedwars]", "[bwgame]");
	}

	@Override
	public void onClick(Player player, SignBlock sign) {
		if (sign.getName().equalsIgnoreCase("leave")) {
            if (Main.isPlayerInGame(player)) {
                Main.getPlayerGameProfile(player).changeGame(null);
            }
        } else {
            Game game = Main.getGame(sign.getName());
            if (game != null) {
                game.joinToGame(player);
            } else {
                m("sign_game_not_exists").send(player);
            }
        }
	}

	@Override
	public List<String> getSignCreationPermissions() {
		return AdminCommand.ADMIN_PERMISSION;
	}

	@Override
	public String returnTranslate(String key) {
		return i18n(key);
	}

}
