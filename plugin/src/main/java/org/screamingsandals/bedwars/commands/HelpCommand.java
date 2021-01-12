package org.screamingsandals.bedwars.commands;

import org.screamingsandals.bedwars.Main;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

import static org.screamingsandals.bedwars.lib.lang.I18n.i18nonly;

public class HelpCommand extends BaseCommand {

    public HelpCommand() {
        super("help", Collections.EMPTY_LIST, true, true);
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
        console.sendMessage(i18nonly("help_title_console").replace("%version%", "Zero " + Main.getVersion()));
        console.sendMessage(i18nonly("help_bw_list"));
        console.sendMessage(i18nonly("help_bw_stats_other"));
        console.sendMessage(i18nonly("help_bw_alljoin"));
        console.sendMessage(i18nonly("help_bw_reload"));
    }

    public void sendHelp(Player player) {
        player.sendMessage(i18nonly("help_title").replace("%version%", "Zero " + Main.getVersion()));
        if (hasPermission(player, JOIN_PERMISSION, Main.getConfigurator().config.getBoolean("default-permissions.join"))) {
            player.sendMessage(i18nonly("help_bw_join"));
        }
        if (hasPermission(player, LEAVE_PERMISSION, Main.getConfigurator().config.getBoolean("default-permissions.leave"))) {
            player.sendMessage(i18nonly("help_bw_leave"));
        }
        if (hasPermission(player, REJOIN_PERMISSION, Main.getConfigurator().config.getBoolean("default-permissions.rejoin"))) {
            player.sendMessage(i18nonly("help_bw_rejoin"));
        }
        if (hasPermission(player, AUTOJOIN_PERMISSION, Main.getConfigurator().config.getBoolean("default-permissions.autojoin"))) {
            player.sendMessage(i18nonly("help_bw_autojoin"));
        }
        if (hasPermission(player, LIST_PERMISSION, Main.getConfigurator().config.getBoolean("default-permissions.list"))) {
            player.sendMessage(i18nonly("help_bw_list"));
        }
        if (hasPermission(player, LEADERBOARD_PERMISSION, Main.getConfigurator().config.getBoolean("default-permissions.leaderboard"))) {
            player.sendMessage(i18nonly("help_bw_leaderboard"));
        }

        if ((hasPermission(player, STATS_PERMISSION, Main.getConfigurator().config.getBoolean("default-permissions.stats")))) {
            if (hasPermission(player, ADMIN_PERMISSION, false) || hasPermission(player, OTHER_STATS_PERMISSION, false)) {
                player.sendMessage(i18nonly("help_bw_stats_other"));
            } else {
                player.sendMessage(i18nonly("help_bw_stats"));
            }
        }

        if (player.hasPermission("bw.admin.alljoin")) {
            player.sendMessage(i18nonly("help_bw_alljoin"));
        }

        if (hasPermission(player, ADMIN_PERMISSION, false)) {
            player.sendMessage(i18nonly("help_bw_addholo"));
            player.sendMessage(i18nonly("help_bw_removeholo"));
            player.sendMessage(i18nonly("help_bw_mainlobby"));

            player.sendMessage(i18nonly("help_bw_admin_info"));
            player.sendMessage(i18nonly("help_bw_admin_add"));
            player.sendMessage(i18nonly("help_bw_admin_lobby"));
            player.sendMessage(i18nonly("help_bw_admin_spec"));
            player.sendMessage(i18nonly("help_bw_admin_pos1"));
            player.sendMessage(i18nonly("help_bw_admin_pos2"));
            player.sendMessage(i18nonly("help_bw_admin_pausecountdown"));
            player.sendMessage(i18nonly("help_bw_admin_post_game_waiting"));
            player.sendMessage(i18nonly("help_bw_admin_customprefix"));
            player.sendMessage(i18nonly("help_bw_admin_minplayers"));
            player.sendMessage(i18nonly("help_bw_admin_time"));
            player.sendMessage(i18nonly("help_bw_admin_team_add"));
            player.sendMessage(i18nonly("help_bw_admin_team_color"));
            player.sendMessage(i18nonly("help_bw_admin_team_maxplayers"));
            player.sendMessage(i18nonly("help_bw_admin_team_spawn"));
            player.sendMessage(i18nonly("help_bw_admin_team_bed"));
            player.sendMessage(i18nonly("help_bw_admin_jointeam"));
            player.sendMessage(i18nonly("help_bw_admin_spawner_add"));
            player.sendMessage(i18nonly("help_bw_admin_spawner_remove"));
            player.sendMessage(i18nonly("help_bw_admin_spawner_reset"));
            player.sendMessage(i18nonly("help_bw_admin_store_add"));
            player.sendMessage(i18nonly("help_bw_admin_store_type"));
            player.sendMessage(i18nonly("help_bw_admin_store_child"));
            player.sendMessage(i18nonly("help_bw_admin_store_adult"));
            player.sendMessage(i18nonly("help_bw_admin_store_remove"));
            player.sendMessage(i18nonly("help_bw_admin_config"));
            player.sendMessage(i18nonly("help_bw_admin_arena_time"));
            player.sendMessage(i18nonly("help_bw_admin_arena_weather"));
            player.sendMessage(i18nonly("help_bw_admin_remove"));
            player.sendMessage(i18nonly("help_bw_admin_edit"));
            player.sendMessage(i18nonly("help_bw_admin_save"));
            player.sendMessage(i18nonly("help_bw_reload"));
            player.sendMessage(i18nonly("help_bw_dump"));
        }
    }

}
