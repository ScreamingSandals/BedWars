package org.screamingsandals.bedwars.commands;

import org.bukkit.command.Command;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.utils.Permissions;
import org.screamingsandals.lib.screamingcommands.base.annotations.RegisterCommand;
import org.screamingsandals.lib.screamingcommands.base.interfaces.IBasicCommand;

import java.util.List;

import static misat11.lib.lang.I.m;
import static misat11.lib.lang.I.mpr;

@RegisterCommand(commandName = "bw", subCommandName = "rejoin")
public class RejoinCommand implements IBasicCommand {

    public RejoinCommand(Main main) {
    }

    @Override
    public String getPermission() {
        return Permissions.BASE_PERMISSION.permission;
    }

    @Override
    public String getDescription() {
        return "BedWars Rejoin command";
    }

    @Override
    public String getUsage() {
        return "/bw rejoin";
    }

    @Override
    public String getInvalidUsageMessage() {
        return ""; //how
    }

    @Override
    public boolean onPlayerCommand(Player player, List<String> list) {
        if (Main.isPlayerInGame(player)) {
            m("commands.rejoin.already_in_game").send(player);
            return true;
        }

        String name = null;
        if (Main.isPlayerGameProfileRegistered(player)) {
            name = Main.getPlayerGameProfile(player).getLatestGameName();
        }
        if (name == null) {
            mpr("commands.rejoin.no_game_found").send(player);
        } else {
            if (Main.isGameExists(name)) {
                Main.getGame(name).joinToGame(player);
            } else {
                mpr("commands.rejoin.no_game_found").send(player);
            }
        }
        return true;
    }

    @Override
    public boolean onConsoleCommand(ConsoleCommandSender consoleCommandSender, List<String> list) {
        return false;
    }

    @Override
    public void onPlayerTabComplete(Player player, Command command, List<String> list, List<String> list1) {

    }

    @Override
    public void onConsoleTabComplete(ConsoleCommandSender consoleCommandSender, Command command, List<String> list, List<String> list1) {

    }
}
