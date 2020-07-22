package org.screamingsandals.bedwars.commands;

import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.game.Game;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static misat11.lib.lang.I18n.i18n;

public class AutojoinCommand extends BaseCommand {

    public AutojoinCommand() {
        super("autojoin", AUTOJOIN_PERMISSION, false, Main.getConfigurator().config.getBoolean("default-permissions.autojoin"));
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        Player player = (Player) sender;
        if (Main.isPlayerInGame(player)) {
            player.sendMessage(i18n("you_are_already_in_some_game"));
            return true;
        }

        Game game = Main.getInstance().getFirstWaitingGame();
        if (game == null) {
            player.sendMessage(i18n("there_is_no_empty_game"));
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
