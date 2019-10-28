package org.screamingsandals.bedwars.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.Main;

import java.util.List;

import static misat11.lib.lang.I.m;

public class HelpCommand extends BaseCommand {

    public HelpCommand() {
        super("help", null, true);
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        if (sender instanceof Player) {
            sendHelp((Player) sender);
        } else if (sender instanceof ConsoleCommandSender) {
            sendConsoleHelp((ConsoleCommandSender) sender);
        }
        return true;
    }

    @Override
    public void completeTab(List<String> completion, CommandSender sender, List<String> args) {
        // Nothing to add.
    }

    public void sendConsoleHelp(ConsoleCommandSender console) {
        m("commands.help.header_console")
                .replace("%version%", Main.getVersion())
                .send(console);
        m("commands.help.list").send(console);
        m("commands.help.stats_others").send(console);
        m("commands.help.admin.reload").send(console);
    }

    public void sendHelp(Player player) {
        m("commands.help.header")
                .replace("%version%", Main.getVersion())
                .send(player);
        m("commands.help.join").send(player);
        m("commands.help.leave").send(player);
        m("commands.help.rejoin").send(player);
        m("commands.help.auto_join").send(player);
        m("commands.help.list").send(player);

        if (player.hasPermission(ADMIN_PERMISSION) || player.hasPermission(OTHER_STATS_PERMISSION)) {
            m("commands.help.stats_others").send(player);
        } else {
            m("commands.help.stats").send(player);
        }

        if (player.hasPermission(ADMIN_PERMISSION)) {
            m("commands.help.admin.addholo").send(player);
            m("commands.help.admin.removeholo").send(player);
            m("commands.help.admin.mainlobby").send(player);
            m("commands.help.admin.info").send(player);
            m("commands.help.admin.add").send(player);
            m("commands.help.admin.lobby").send(player);
            m("commands.help.admin.spec").send(player);
            m("commands.help.admin.pos1").send(player);
            m("commands.help.admin.pos2").send(player);
            m("commands.help.admin.pausecountdown").send(player);
            m("commands.help.admin.minplayers").send(player);
            m("commands.help.admin.time").send(player);
            m("commands.help.admin.team_add").send(player);
            m("commands.help.admin.team_color").send(player);
            m("commands.help.admin.team_maxplayers").send(player);
            m("commands.help.admin.team_spawn").send(player);
            m("commands.help.admin.team_bed").send(player);
            m("commands.help.admin.join_team").send(player);
            m("commands.help.admin.spawner_add").send(player);
            m("commands.help.admin.spawner_reset").send(player);
            m("commands.help.admin.store_add").send(player);
            m("commands.help.admin.store_remove").send(player);
            m("commands.help.admin.config").send(player);
            m("commands.help.admin.arena_time").send(player);
            m("commands.help.admin.arena_weather").send(player);
            m("commands.help.admin.remove").send(player);
            m("commands.help.admin.edit").send(player);
            m("commands.help.admin.save").send(player);
            m("commands.help.admin.reload").send(player);
        }
    }

}
