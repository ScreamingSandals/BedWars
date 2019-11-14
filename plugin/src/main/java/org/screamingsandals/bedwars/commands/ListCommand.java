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

@RegisterCommand(commandName = "bw", subCommandName = "list")
public class ListCommand implements IBasicCommand {

    public ListCommand(Main main) {
    }

    @Override
    public String getPermission() {
        return Permissions.BASE.permission;
    }

    @Override
    public String getDescription() {
        return "BedWars list command";
    }

    @Override
    public String getUsage() {
        return "/bw list";
    }

    @Override
    public String getInvalidUsageMessage() {
        return "";
    }

    @Override
    public boolean onPlayerCommand(Player player, List<String> list) {
        mpr("commands.list.header").send(player);
        Main.sendGameListInfo(player);
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
