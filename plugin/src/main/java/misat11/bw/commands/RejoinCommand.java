package misat11.bw.commands;

import static misat11.lib.lang.I18n.i18n;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import misat11.bw.Main;

public class RejoinCommand extends BaseCommand {
	
	public RejoinCommand() {
		super("rejoin", null, false);
	}

	@Override
	public boolean execute(CommandSender sender, List<String> args) {
		Player player = (Player) sender;
		if (Main.isPlayerInGame(player)) {
			player.sendMessage(i18n("you_are_already_in_some_game"));
			return true;
		}
		
		String name = null;
		if (Main.isPlayerGameProfileRegistered(player)) {
			name = Main.getPlayerGameProfile(player).getLatestGameName();
		}
		if (name == null) {
			player.sendMessage(i18n("you_are_not_in_game_yet"));
		} else {
			if (Main.isGameExists(name)) {
				Main.getGame(name).joinToGame(player);
			} else {
				player.sendMessage(i18n("game_is_gone"));
			}
		}
		return true;
	}

	@Override
	public void completeTab(List<String> completion, CommandSender sender, List<String> args) {
		// Nothing to add.
	}

}
