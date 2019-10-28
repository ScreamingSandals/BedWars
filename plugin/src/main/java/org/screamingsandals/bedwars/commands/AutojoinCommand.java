package org.screamingsandals.bedwars.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.game.Game;

import java.util.List;

import static misat11.lib.lang.I.m;

public class AutojoinCommand extends BaseCommand {

    public AutojoinCommand() {
        super("autojoin", null, false);
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        Player player = (Player) sender;
        if (Main.isPlayerInGame(player)) {
            m("commands.auto_join.already_in_game").send(player);
            return true;
        }

        Game game = Main.getInstance().getFirstWaitingGame();
        if (game == null) {
            m("commands.auto_join.no_empty_game").send(player);
        } else {
            game.joinToGame(player);
        }
        return true;
    }

    @Override
    public void completeTab(List<String> completion, CommandSender sender, List<String> args) {
        // Nothing to add.
    }

}
