package misat11.bw.commands;

import static misat11.lib.lang.I18n.i18n;

import java.util.List;

import org.bukkit.command.CommandSender;

import misat11.bw.Main;

public class ListCommand extends BaseCommand {
	
	public ListCommand() {
		super("list", null, true);
	}

	@Override
	public boolean execute(CommandSender sender, List<String> args) {
		sender.sendMessage(i18n("list_header"));
		Main.sendGameListInfo(sender);
		return true;
	}

	@Override
	public void completeTab(List<String> completion, CommandSender sender, List<String> args) {
		// TODO Auto-generated method stub
		
	}

}
