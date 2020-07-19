package org.screamingsandals.bedwars.commands;

import org.screamingsandals.bedwars.Main;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static misat11.lib.lang.I18n.i18n;

public class AddholoCommand extends BaseCommand {

    public AddholoCommand() {
        super("addholo", ADMIN_PERMISSION, false, false);
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        Player player = (Player) sender;
        if (!Main.isHologramsEnabled()) {
            player.sendMessage(i18n("holo_not_enabled"));
        } else {
            Main.getHologramInteraction().addHologramLocation(player.getEyeLocation());
            Main.getHologramInteraction().updateHolograms();
            player.sendMessage(i18n("holo_added"));
        }
        return true;
    }

    @Override
    public void completeTab(List<String> completion, CommandSender sender, List<String> args) {
    }

}
