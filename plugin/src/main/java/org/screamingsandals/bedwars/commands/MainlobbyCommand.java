package org.screamingsandals.bedwars.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.bedwars.utils.Permissions;
import org.screamingsandals.lib.screamingcommands.base.annotations.RegisterCommand;
import org.screamingsandals.lib.screamingcommands.base.interfaces.IBasicCommand;

import java.util.Arrays;
import java.util.List;

import static misat11.lib.lang.I.mpr;

@RegisterCommand(commandName = "bw", subCommandName = "mainlobby")
public class MainlobbyCommand implements IBasicCommand {

    public MainlobbyCommand(Main main) {
    }

    @Override
    public String getPermission() {
        return Permissions.ADMIN_PERMISSIONS.permission;
    }

    @Override
    public String getDescription() {
        return "BedWars Mainlobby command";
    }

    @Override
    public String getUsage() {
        return "/bw mainlobby <enable|set>";
    }

    @Override
    public String getInvalidUsageMessage() {
        return mpr("commands.errors.unknown_usage").get();
    }

    @Override
    public boolean onPlayerCommand(Player player, List<String> args) {
        if (args.size() == 1) {
            if (args.contains("enable")) {
                Main.getConfigurator().config.set("mainlobby.enabled", true);
                Main.getConfigurator().saveConfig();

                mpr("commands.success.done.list.header").send(player);
                mpr("commands.admin.main_lobby.info").send(player);
                return true;
            } else if (args.contains("set")) {
                Location location = player.getLocation();

                Main.getConfigurator().config.set("mainlobby.location", MiscUtils.setLocationToString(location));
                Main.getConfigurator().config.set("mainlobby.world", location.getWorld().getName());
                Main.getConfigurator().saveConfig();

                mpr("commands.success.done.list.header").send(player);
                return true;
            }
        }
        mpr("commands.errors.unknown_usage").send(player);
        return true;
    }

    @Override
    public boolean onConsoleCommand(ConsoleCommandSender consoleCommandSender, List<String> list) {
        return false;
    }

    @Override
    public void onPlayerTabComplete(Player player, Command command, List<String> completion, List<String> args) {
        completion.addAll(Arrays.asList("enable", "set"));
    }

    @Override
    public void onConsoleTabComplete(ConsoleCommandSender consoleCommandSender, Command command, List<String> list, List<String> list1) {

    }
}
