package misat11.bw.commands;

import misat11.bw.Main;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static misat11.lib.lang.I18n.i18n;

public class LeaveCommand extends BaseCommand {

    public LeaveCommand() {
        super("leave", null, false);
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        Player player = (Player) sender;
        if (Main.isPlayerInGame(player)) {
            Main.getPlayerGameProfile(player).changeGame(null);
        } else {
            player.sendMessage(i18n("you_arent_in_game"));
        }
        return true;
    }

    @Override
    public void completeTab(List<String> completion, CommandSender sender, List<String> args) {
        // TODO Auto-generated method stub

    }

}
