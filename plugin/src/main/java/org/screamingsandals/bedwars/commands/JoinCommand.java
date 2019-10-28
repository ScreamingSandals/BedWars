package org.screamingsandals.bedwars.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.Main;

import java.util.List;

import static misat11.lib.lang.I.m;

public class JoinCommand extends BaseCommand {

    public JoinCommand() {
        super("join", null, false);
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        Player player = (Player) sender;
        if (Main.isPlayerInGame(player)) {
            m("commands.join.already_in_game").send(player);
            return true;
        }

        if (args.size() >= 1) {
            String arenaN = args.get(0);
            if (Main.isGameExists(arenaN)) {
                Main.getGame(arenaN).joinToGame(player);
            } else {
                m("commands.join.not_found").send(player);
            }
        } else {
            Main.getInstance().getGameWithHighestPlayers().joinToGame(player);
            return true;
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
