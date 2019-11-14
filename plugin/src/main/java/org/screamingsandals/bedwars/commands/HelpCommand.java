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

@RegisterCommand(commandName = "bw", subCommandName = "help")
public class HelpCommand implements IBasicCommand {

    public HelpCommand(Main main) {
    }

    @Override
    public String getPermission() {
        return Permissions.BASE_PERMISSION.permission;
    }

    @Override
    public String getDescription() {
        return "BedWars help command";
    }

    @Override
    public String getUsage() {
        return "/bw help";
    }

    @Override
    public String getInvalidUsageMessage() {
        return ""; //How could you use this in invalid way :<
    }

    @Override
    public boolean onPlayerCommand(Player player, List<String> list) {
        m("commands.admin.help.header")
                .replace("%version%", Main.getVersion())
                .send(player);
        m("commands.admin.help.join").send(player);
        m("commands.admin.help.leave").send(player);
        m("commands.admin.help.rejoin").send(player);
        m("commands.admin.help.auto_join").send(player);
        m("commands.admin.help.list").send(player);

        if (player.hasPermission(Permissions.ADMIN_PERMISSIONS.permission)
                || player.hasPermission(Permissions.SEE_OTHER_STATS.permission)) {
            m("commands.admin.help.stats_others").send(player);
        } else {
            m("commands.admin.help.stats").send(player);
        }

        if (player.hasPermission(Permissions.ADMIN_PERMISSIONS.permission)) {
            m("commands.admin.help.admin.addholo").send(player);
            m("commands.admin.help.admin.removeholo").send(player);
            m("commands.admin.help.admin.mainlobby").send(player);
            m("commands.admin.help.admin.info").send(player);
            m("commands.admin.help.admin.add").send(player);
            m("commands.admin.help.admin.lobby").send(player);
            m("commands.admin.help.admin.spec").send(player);
            m("commands.admin.help.admin.pos1").send(player);
            m("commands.admin.help.admin.pos2").send(player);
            m("commands.admin.help.admin.pausecountdown").send(player);
            m("commands.admin.help.admin.minplayers").send(player);
            m("commands.admin.help.admin.time").send(player);
            m("commands.admin.help.admin.team_add").send(player);
            m("commands.admin.help.admin.team_color").send(player);
            m("commands.admin.help.admin.team_maxplayers").send(player);
            m("commands.admin.help.admin.team_spawn").send(player);
            m("commands.admin.help.admin.team_bed").send(player);
            m("commands.admin.help.admin.join_team").send(player);
            m("commands.admin.help.admin.spawner_add").send(player);
            m("commands.admin.help.admin.spawner_reset").send(player);
            m("commands.admin.help.admin.store_add").send(player);
            m("commands.admin.help.admin.store_remove").send(player);
            m("commands.admin.help.admin.config").send(player);
            m("commands.admin.help.admin.arena_time").send(player);
            m("commands.admin.help.admin.arena_weather").send(player);
            m("commands.admin.help.admin.remove").send(player);
            m("commands.admin.help.admin.edit").send(player);
            m("commands.admin.help.admin.save").send(player);
            m("commands.admin.help.admin.reload").send(player);
        }
        return true;
    }

    @Override
    public boolean onConsoleCommand(ConsoleCommandSender consoleCommandSender, List<String> list) {
        m("commands.admin.help.header_console")
                .replace("%version%", Main.getVersion())
                .send(consoleCommandSender);
        m("commands.admin.help.list").send(consoleCommandSender);
        m("commands.admin.help.stats_others").send(consoleCommandSender);
        m("commands.admin.help.admin.reload").send(consoleCommandSender);
        return true;
    }

    @Override
    public void onPlayerTabComplete(Player player, Command command, List<String> list, List<String> list1) {

    }

    @Override
    public void onConsoleTabComplete(ConsoleCommandSender consoleCommandSender, Command command, List<String> list, List<String> list1) {

    }
}
