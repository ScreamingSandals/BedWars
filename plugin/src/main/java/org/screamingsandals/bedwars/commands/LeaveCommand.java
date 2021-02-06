package org.screamingsandals.bedwars.commands;

import org.screamingsandals.bedwars.Main;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static org.screamingsandals.bedwars.lib.lang.I18n.i18n;

public class LeaveCommand extends BaseCommand {

    public LeaveCommand() {
        super("leave", LEAVE_PERMISSION, false, Main.getConfigurator().node("default-permissions", "leave").getBoolean());
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        Player player = (Player) sender;
        if (Main.isPlayerInGame(player)) {
            Main.getPlayerGameProfile(player).changeGame(null);
        } else {
            player.sendMessage(i18n("you_arent_in_game"));
        }
        return true;
    }

    @Override
    public void completeTab(List<String> completion, CommandSender sender, List<String> args) {
    }

}
