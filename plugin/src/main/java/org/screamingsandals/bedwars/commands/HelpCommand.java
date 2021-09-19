package org.screamingsandals.bedwars.commands;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import org.screamingsandals.bedwars.BedWarsPlugin;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.sender.CommandSenderWrapper;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.utils.annotations.methods.OnPostEnable;
import org.screamingsandals.lib.utils.annotations.parameters.ProvidedBy;

@Service
public class HelpCommand extends BaseCommand {
    public HelpCommand() {
        super("help", null, true);
    }

    @Override
    protected void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder, CommandManager<CommandSenderWrapper> manager) {
        manager.command(commandSenderWrapperBuilder
                .handler(commandContext -> {
                    // TODO: use more generic way
                    var sender = commandContext.getSender();
                    if (sender.getType() == CommandSenderWrapper.Type.PLAYER) {
                        Message.of(LangKeys.HELP_TITLE).placeholder("version", BedWarsPlugin.getVersion()).send(sender);
                        if (sender.hasPermission(BedWarsPermission.JOIN_PERMISSION.asPermission())) {
                            Message.of(LangKeys.HELP_BW_JOIN).send(sender);
                        }
                        if (sender.hasPermission(BedWarsPermission.LEAVE_PERMISSION.asPermission())) {
                            Message.of(LangKeys.HELP_BW_LEAVE).send(sender);
                        }
                        if (sender.hasPermission(BedWarsPermission.REJOIN_PERMISSION.asPermission())) {
                            Message.of(LangKeys.HELP_BW_REJOIN).send(sender);
                        }
                        if (sender.hasPermission(BedWarsPermission.AUTOJOIN_PERMISSION.asPermission())) {
                            Message.of(LangKeys.HELP_BW_AUTOJOIN).send(sender);
                        }
                        if (sender.hasPermission(BedWarsPermission.LIST_PERMISSION.asPermission())) {
                            Message.of(LangKeys.HELP_BW_LIST).send(sender);
                        }
                        if (sender.hasPermission(BedWarsPermission.LEADERBOARD_PERMISSION.asPermission())) {
                            Message.of(LangKeys.HELP_BW_LEADERBOARD).send(sender);
                        }

                        if (sender.hasPermission(BedWarsPermission.STATS_PERMISSION.asPermission())) {
                            if (sender.hasPermission(BedWarsPermission.ADMIN_PERMISSION.asPermission()) || sender.hasPermission(BedWarsPermission.OTHER_STATS_PERMISSION.asPermission())) {
                                Message.of(LangKeys.HELP_BW_STATS_OTHER).send(sender);
                            } else {
                                Message.of(LangKeys.HELP_BW_STATS).send(sender);
                            }
                        }

                        if (sender.hasPermission(BedWarsPermission.ALL_JOIN_PERMISSION.asPermission())) {
                            Message.of(LangKeys.HELP_BW_ALLJOIN).send(sender);
                        }

                        if (sender.hasPermission(BedWarsPermission.ADMIN_PERMISSION.asPermission())) {
                            if (MainConfig.getInstance().node("enable-cheat-command-for-admins").getBoolean()) {
                                Message.of(LangKeys.HELP_BW_CHEAT_GIVE)
                                        .join(LangKeys.HELP_BW_CHEAT_KILL)
                                        .send(sender);
                            }

                            Message
                                    .of(LangKeys.HELP_BW_ADDHOLO)
                                    .join(LangKeys.HELP_BW_REMOVEHOLO)
                                    .join(LangKeys.HELP_BW_MAINLOBBY)
                                    .join(LangKeys.HELP_BW_ADMIN_INFO)
                                    .join(LangKeys.HELP_BW_ADMIN_ADD)
                                    .join(LangKeys.HELP_BW_ADMIN_LOBBY)
                                    .join(LangKeys.HELP_BW_ADMIN_SPEC)
                                    .join(LangKeys.HELP_BW_ADMIN_POS1)
                                    .join(LangKeys.HELP_BW_ADMIN_POS2)
                                    .join(LangKeys.HELP_BW_ADMIN_LOBBY_POS1)
                                    .join(LangKeys.HELP_BW_ADMIN_LOBBY_POS2)
                                    .join(LangKeys.HELP_BW_ADMIN_PAUSECOUNTDOWN)
                                    .join(LangKeys.HELP_BW_ADMIN_POST_GAME_WAITING)
                                    .join(LangKeys.HELP_BW_ADMIN_CUSTOMPREFIX)
                                    .join(LangKeys.HELP_BW_ADMIN_MINPLAYERS)
                                    .join(LangKeys.HELP_BW_ADMIN_TIME)
                                    .join(LangKeys.HELP_BW_ADMIN_TEAM_ADD)
                                    .join(LangKeys.HELP_BW_ADMIN_TEAM_COLOR)
                                    .join(LangKeys.HELP_BW_ADMIN_TEAM_MAXPLAYERS)
                                    .join(LangKeys.HELP_BW_ADMIN_TEAM_SPAWN)
                                    .join(LangKeys.HELP_BW_ADMIN_TEAM_BED)
                                    .join(LangKeys.HELP_BW_ADMIN_JOINTEAM)
                                    .join(LangKeys.HELP_BW_ADMIN_SPAWNER_ADD)
                                    .join(LangKeys.HELP_BW_ADMIN_SPAWNER_BASE_AMOUNT)
                                    .join(LangKeys.HELP_BW_ADMIN_SPAWNER_MAX_SPAWNED_RESOURCES)
                                    .join(LangKeys.HELP_BW_ADMIN_SPAWNER_LINKED_TEAM)
                                    .join(LangKeys.HELP_BW_ADMIN_SPAWNER_HOLOGRAM)
                                    .join(LangKeys.HELP_BW_ADMIN_SPAWNER_HOLOGRAM_TYPE)
                                    .join(LangKeys.HELP_BW_ADMIN_SPAWNER_FLOATING)
                                    .join(LangKeys.HELP_BW_ADMIN_SPAWNER_ROTATION_MODE)
                                    .join(LangKeys.HELP_BW_ADMIN_SPAWNER_CUSTOM_NAME)
                                    .join(LangKeys.HELP_BW_ADMIN_SPAWNER_CHANGE_TYPE)
                                    .join(LangKeys.HELP_BW_ADMIN_SPAWNER_REMOVE)
                                    .join(LangKeys.HELP_BW_ADMIN_SPAWNER_RESET)
                                    .join(LangKeys.HELP_BW_ADMIN_STORE_ADD)
                                    .join(LangKeys.HELP_BW_ADMIN_STORE_TYPE)
                                    .join(LangKeys.HELP_BW_ADMIN_STORE_CHILD)
                                    .join(LangKeys.HELP_BW_ADMIN_STORE_ADULT)
                                    .join(LangKeys.HELP_BW_ADMIN_STORE_REMOVE)
                                    .join(LangKeys.HELP_BW_ADMIN_CONFIG)
                                    .join(LangKeys.HELP_BW_ADMIN_ARENA_TIME)
                                    .join(LangKeys.HELP_BW_ADMIN_ARENA_WEATHER)
                                    .join(LangKeys.HELP_BW_ADMIN_REMOVE)
                                    .join(LangKeys.HELP_BW_ADMIN_EDIT)
                                    .join(LangKeys.HELP_BW_ADMIN_SAVE)
                                    .join(LangKeys.HELP_BW_RELOAD)
                                    .join(LangKeys.HELP_BW_DUMP)
                                    .send(sender);
                        }
                    } else {
                        Message
                                .of(LangKeys.HELP_TITLE_CONSOLE)
                                .placeholder("version", BedWarsPlugin.getVersion())
                                .join(LangKeys.HELP_BW_LIST)
                                .join(LangKeys.HELP_BW_STATS_OTHER)
                                .join(LangKeys.HELP_BW_ALLJOIN)
                                .join(LangKeys.HELP_BW_RELOAD)
                                .send(sender);
                    }
                })
        );
    }

    /*
     * Special case, only for help command
     */
    @Override
    @OnPostEnable
    public void construct(@ProvidedBy(CommandService.class) CommandManager<CommandSenderWrapper> manager) {
        var builder = manager.commandBuilder("bw");
        construct(builder, manager);

        super.construct(manager);
    }
}
