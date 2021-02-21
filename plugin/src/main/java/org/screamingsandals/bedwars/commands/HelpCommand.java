package org.screamingsandals.bedwars.commands;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.lib.sender.CommandSenderWrapper;

import static org.screamingsandals.bedwars.lib.lang.I.i18nonly;

public class HelpCommand extends BaseCommand {
    public HelpCommand(CommandManager<CommandSenderWrapper> manager) {
        super(manager, "help", null, true);
    }

    @Override
    protected void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        manager.command(commandSenderWrapperBuilder
                .handler(commandContext -> {
                    // TODO: use more generic way
                    var sender = commandContext.getSender();
                    if (sender.getType() == CommandSenderWrapper.Type.PLAYER) {
                        sender.sendMessage(i18nonly("help_title").replace("%version%", Main.getVersion()));
                        if (sender.hasPermission(BedWarsPermission.JOIN_PERMISSION.asPermission())) {
                            sender.sendMessage(i18nonly("help_bw_join"));
                        }
                        if (sender.hasPermission(BedWarsPermission.LEAVE_PERMISSION.asPermission())) {
                            sender.sendMessage(i18nonly("help_bw_leave"));
                        }
                        if (sender.hasPermission(BedWarsPermission.REJOIN_PERMISSION.asPermission())) {
                            sender.sendMessage(i18nonly("help_bw_rejoin"));
                        }
                        if (sender.hasPermission(BedWarsPermission.AUTOJOIN_PERMISSION.asPermission())) {
                            sender.sendMessage(i18nonly("help_bw_autojoin"));
                        }
                        if (sender.hasPermission(BedWarsPermission.LIST_PERMISSION.asPermission())) {
                            sender.sendMessage(i18nonly("help_bw_list"));
                        }
                        if (sender.hasPermission(BedWarsPermission.LEADERBOARD_PERMISSION.asPermission())) {
                            sender.sendMessage(i18nonly("help_bw_leaderboard"));
                        }

                        if ((sender.hasPermission(BedWarsPermission.STATS_PERMISSION.asPermission()))) {
                            if (sender.hasPermission(BedWarsPermission.ADMIN_PERMISSION.asPermission()) || sender.hasPermission(BedWarsPermission.OTHER_STATS_PERMISSION.asPermission())) {
                                sender.sendMessage(i18nonly("help_bw_stats_other"));
                            } else {
                                sender.sendMessage(i18nonly("help_bw_stats"));
                            }
                        }

                        if (sender.hasPermission("bw.admin.alljoin")) {
                            sender.sendMessage(i18nonly("help_bw_alljoin"));
                        }

                        if (sender.hasPermission(BedWarsPermission.ADMIN_PERMISSION.asPermission())) {
                            sender.sendMessage(i18nonly("help_bw_addholo"));
                            sender.sendMessage(i18nonly("help_bw_removeholo"));
                            sender.sendMessage(i18nonly("help_bw_mainlobby"));

                            sender.sendMessage(i18nonly("help_bw_admin_info"));
                            sender.sendMessage(i18nonly("help_bw_admin_add"));
                            sender.sendMessage(i18nonly("help_bw_admin_lobby"));
                            sender.sendMessage(i18nonly("help_bw_admin_spec"));
                            sender.sendMessage(i18nonly("help_bw_admin_pos1"));
                            sender.sendMessage(i18nonly("help_bw_admin_pos2"));
                            sender.sendMessage(i18nonly("help_bw_admin_pausecountdown"));
                            sender.sendMessage(i18nonly("help_bw_admin_post_game_waiting"));
                            sender.sendMessage(i18nonly("help_bw_admin_customprefix"));
                            sender.sendMessage(i18nonly("help_bw_admin_minplayers"));
                            sender.sendMessage(i18nonly("help_bw_admin_time"));
                            sender.sendMessage(i18nonly("help_bw_admin_team_add"));
                            sender.sendMessage(i18nonly("help_bw_admin_team_color"));
                            sender.sendMessage(i18nonly("help_bw_admin_team_maxplayers"));
                            sender.sendMessage(i18nonly("help_bw_admin_team_spawn"));
                            sender.sendMessage(i18nonly("help_bw_admin_team_bed"));
                            sender.sendMessage(i18nonly("help_bw_admin_jointeam"));
                            sender.sendMessage(i18nonly("help_bw_admin_spawner_add"));
                            sender.sendMessage(i18nonly("help_bw_admin_spawner_remove"));
                            sender.sendMessage(i18nonly("help_bw_admin_spawner_reset"));
                            sender.sendMessage(i18nonly("help_bw_admin_store_add"));
                            sender.sendMessage(i18nonly("help_bw_admin_store_type"));
                            sender.sendMessage(i18nonly("help_bw_admin_store_child"));
                            sender.sendMessage(i18nonly("help_bw_admin_store_adult"));
                            sender.sendMessage(i18nonly("help_bw_admin_store_remove"));
                            sender.sendMessage(i18nonly("help_bw_admin_config"));
                            sender.sendMessage(i18nonly("help_bw_admin_arena_time"));
                            sender.sendMessage(i18nonly("help_bw_admin_arena_weather"));
                            sender.sendMessage(i18nonly("help_bw_admin_remove"));
                            sender.sendMessage(i18nonly("help_bw_admin_edit"));
                            sender.sendMessage(i18nonly("help_bw_admin_save"));
                            sender.sendMessage(i18nonly("help_bw_reload"));
                            sender.sendMessage(i18nonly("help_bw_dump"));
                        }
                    } else {
                        sender.sendMessage(i18nonly("help_title_console").replace("%version%", Main.getVersion()));
                        sender.sendMessage(i18nonly("help_bw_list"));
                        sender.sendMessage(i18nonly("help_bw_stats_other"));
                        sender.sendMessage(i18nonly("help_bw_alljoin"));
                        sender.sendMessage(i18nonly("help_bw_reload"));
                    }
                })
        );
    }

    /*
     * Special case, only for help command
     */
    @Override
    public void construct() {
        var builder = manager.commandBuilder("bw");
        construct(builder);

        super.construct();
    }
}
