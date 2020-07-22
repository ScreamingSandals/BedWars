package org.screamingsandals.bedwars.commands;

import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

import static misat11.lib.lang.I18n.i18n;

public class MainlobbyCommand extends BaseCommand {

    public MainlobbyCommand() {
        super("mainlobby", ADMIN_PERMISSION, false, false);
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        Player player = (Player) sender;

        if (args.size() == 1) {
            if (args.contains("enable")) {
                Main.getConfigurator().config.set("mainlobby.enabled", true);
                Main.getConfigurator().saveConfig();

                player.sendMessage(i18n("admin_command_success"));
                player.sendMessage(i18n("admin_command_mainlobby_info"));
                return true;
            } else if (args.contains("set")) {
                Location location = player.getLocation();

                Main.getConfigurator().config.set("mainlobby.location", MiscUtils.setLocationToString(location));
                Main.getConfigurator().config.set("mainlobby.world", location.getWorld().getName());
                Main.getConfigurator().saveConfig();

                player.sendMessage(i18n("admin_command_success"));
                return true;
            }
        }
        player.sendMessage(i18n("unknown_usage"));
        return true;
    }

    @Override
    public void completeTab(List<String> completion, CommandSender sender, List<String> args) {
        completion.addAll(Arrays.asList("enable", "set"));
    }
}
