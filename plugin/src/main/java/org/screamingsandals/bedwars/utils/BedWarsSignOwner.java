package org.screamingsandals.bedwars.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.commands.AdminCommand;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.lib.signmanager.SignBlock;
import org.screamingsandals.lib.signmanager.SignOwner;

import static misat11.lib.lang.I.*;

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
			updateLeaveSign(sign);
		}
	}

	private void updateLeaveSign(SignBlock sign) {
		List<String> texts = new ArrayList<>(Main.getConfigurator().config.getStringList("sign"));

		for (int i = 0; i < texts.size(); i++) {
			String text = texts.get(i);
			texts.set(i, text.replaceAll("%arena%", i18n("leave_from_game_item")).replaceAll("%status%", "")
					.replaceAll("%players%", ""));
		}

		if (sign.getLocation().getChunk().isLoaded()) {
			Block block = sign.getLocation().getBlock();
			if (block.getState() instanceof Sign) {
				Sign state = (Sign) block.getState();
				for (int i = 0; i < texts.size() && i < 4; i++) {
					state.setLine(i, texts.get(i));
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
	public String getSignCreationPermission() {
		return AdminCommand.ADMIN_PERMISSION;
	}

	@Override
	public String returnTranslate(String key) {
		return i18n(key);
	}

}
