package org.screamingsandals.bedwars.utils;

import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.screamingsandals.bedwars.Main;
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
		return OldAdminCommand.ADMIN_PERMISSION;
	}

	@Override
	public String returnTranslate(String key) {
		return i18n(key);
	}

}
