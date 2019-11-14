package org.screamingsandals.bedwars.commands;

import org.bukkit.command.Command;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.utils.Permissions;
import org.screamingsandals.lib.screamingcommands.base.annotations.RegisterCommand;
import org.screamingsandals.lib.screamingcommands.base.interfaces.IBasicCommand;

import java.util.List;

import static misat11.lib.lang.I.mpr;

@RegisterCommand(commandName = "bw", subCommandName = "leave")
public class LeaveCommand implements IBasicCommand {

    public LeaveCommand(Main main) {
    }

    @Override
    public String getPermission() {
        return Permissions.BASE.permission;
    }

    @Override
    public String getDescription() {
        return "BedWars leave command";
    }

    @Override
    public String getUsage() {
        return "/bw leave";
    }

    @Override
    public String getInvalidUsageMessage() {
        return "";
    }

    @Override
    public boolean onPlayerCommand(Player player, List<String> list) {
        if (Main.isPlayerInGame(player)) {
            Main.getPlayerGameProfile(player).changeGame(null);
        } else {
            mpr("commands.leave.not_in_game").send(player);
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
