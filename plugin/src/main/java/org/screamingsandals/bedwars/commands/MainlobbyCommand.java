package org.screamingsandals.bedwars.commands;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.utils.MiscUtils;

import java.util.Arrays;
import java.util.List;

import static misat11.lib.lang.I.m;

public class MainlobbyCommand extends BaseCommand {

    public MainlobbyCommand() {
        super("mainlobby", ADMIN_PERMISSION, false);
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        Player player = (Player) sender;

        if (args.size() == 1) {
            if (args.contains("enable")) {
                Main.getConfigurator().config.set("mainlobby.enabled", true);
                Main.getConfigurator().saveConfig();

                m("commands.success.done.list.header").send(sender);
                m("commands.admin.main_lobby.info").send(sender);
                return true;
            } else if (args.contains("set")) {
                Location location = player.getLocation();

                Main.getConfigurator().config.set("mainlobby.location", MiscUtils.setLocationToString(location));
                Main.getConfigurator().config.set("mainlobby.world", location.getWorld().getName());
                Main.getConfigurator().saveConfig();

                m("commands.success.done.list.header").send(sender);
                return true;
            }
        }
        m("commands.errors.unknown_usage").send(sender);
        return true;
    }

    @Override
    public void completeTab(List<String> completion, CommandSender sender, List<String> args) {
        completion.addAll(Arrays.asList("enable", "set"));
    }
}
