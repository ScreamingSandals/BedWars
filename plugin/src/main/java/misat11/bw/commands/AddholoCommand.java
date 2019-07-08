package misat11.bw.commands;

import static misat11.lib.lang.I18n.i18n;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import misat11.bw.Main;

public class AddholoCommand extends BaseCommand {
	
	public AddholoCommand() {
		super("addholo", ADMIN_PERMISSION, false);
	}

	@Override
	public boolean execute(CommandSender sender, List<String> args) {
		Player player = (Player) sender;
		if (!Main.isHologramsEnabled()) {
			player.sendMessage(i18n("holo_not_enabled"));
		} else {
			Main.getHologramInteraction().addHologramLocation(player.getEyeLocation());
			Main.getHologramInteraction().updateHolograms();
			player.sendMessage(i18n("holo_added"));
		}
		return true;
	}

	@Override
	public void completeTab(List<String> completion, CommandSender sender, List<String> args) {
		// TODO Auto-generated method stub
		
	}

}
