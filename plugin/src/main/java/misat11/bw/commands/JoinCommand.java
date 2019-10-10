package misat11.bw.commands;

import misat11.bw.Main;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static misat11.lib.lang.I18n.i18n;

public class JoinCommand extends BaseCommand {

    public JoinCommand() {
        super("join", null, false);
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        Player player = (Player) sender;
        if (args.size() >= 1) {
            if (Main.isPlayerInGame(player)) {
                player.sendMessage(i18n("you_are_already_in_some_game"));
                return true;
            }
            String arenaN = args.get(0);
            if (Main.isGameExists(arenaN)) {
                Main.getGame(arenaN).joinToGame(player);
            } else {
                player.sendMessage(i18n("no_arena_found"));
            }
        } else {
            player.sendMessage(i18n("usage_bw_join"));
        }
        return true;
    }

    @Override
    public void completeTab(List<String> completion, CommandSender sender, List<String> args) {
        if (args.size() == 1) {
            completion.addAll(Main.getGameNames());
        }
    }

}
