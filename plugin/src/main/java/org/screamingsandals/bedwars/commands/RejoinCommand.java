package org.screamingsandals.bedwars.commands;

import org.screamingsandals.bedwars.Main;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static misat11.lib.lang.I18n.i18n;

public class RejoinCommand extends BaseCommand {

    public RejoinCommand() {
        super("rejoin", REJOIN_PERMISSION, false, Main.getConfigurator().config.getBoolean("default-permissions.rejoin"));
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
