package org.screamingsandals.bedwars.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.Main;

import java.util.List;

import static misat11.lib.lang.I.m;

public class RejoinCommand extends BaseCommand {

    public RejoinCommand() {
        super("rejoin", null, false);
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        Player player = (Player) sender;
        if (Main.isPlayerInGame(player)) {
            m("commands.rejoin.already_in_game").send(player);
            return true;
        }

        String name = null;
        if (Main.isPlayerGameProfileRegistered(player)) {
            name = Main.getPlayerGameProfile(player).getLatestGameName();
        }
        if (name == null) {
            m("commands.rejoin.no_game_found").send(player);
        } else {
            if (Main.isGameExists(name)) {
                Main.getGame(name).joinToGame(player);
            } else {
                m("commands.rejoin.no_game_found").send(player);
            }
        }
        return true;
    }

    @Override
    public void completeTab(List<String> completion, CommandSender sender, List<String> args) {
        // Nothing to add.
    }

}
