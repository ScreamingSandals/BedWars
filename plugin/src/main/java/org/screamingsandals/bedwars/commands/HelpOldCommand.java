/*
 * Copyright (C) 2022 ScreamingSandals
 *
 * This file is part of Screaming BedWars.
 *
 * Screaming BedWars is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Screaming BedWars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Screaming BedWars. If not, see <https://www.gnu.org/licenses/>.
 */

package org.screamingsandals.bedwars.commands;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import org.screamingsandals.bedwars.BedWarsPlugin;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.sender.CommandSenderWrapper;
import org.screamingsandals.lib.utils.annotations.Service;

@Service
public class HelpOldCommand extends BaseCommand {
    public HelpOldCommand() {
        super("help-old", null, true);
    }

    @Override
    protected void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder, CommandManager<CommandSenderWrapper> manager) {
        manager.command(commandSenderWrapperBuilder
                .hidden()
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

                        if (sender.hasPermission(BedWarsPermission.GAMES_INVENTORY_PERMISSION.asPermission())) {
                            Message.of(LangKeys.HELP_BW_GAMESINV).send(sender);
                        }

                        if (sender.hasPermission(BedWarsPermission.ALL_JOIN_PERMISSION.asPermission())) {
                            Message.of(LangKeys.HELP_BW_ALLJOIN).send(sender);
                        }

                        if (sender.hasPermission(BedWarsPermission.ADMIN_PERMISSION.asPermission())) {
                            if (MainConfig.getInstance().node("enable-cheat-command-for-admins").getBoolean()) {
                                Message.of(LangKeys.HELP_BW_CHEAT_GIVE)
                                        .join(LangKeys.HELP_BW_CHEAT_KILL)
                                        .join(LangKeys.HELP_BW_CHEAT_BUILD_POP_UP_TOWER)
                                        .join(LangKeys.HELP_BW_CHEAT_REBUILD_REGION)
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
                                    .join(LangKeys.HELP_BW_ADMIN_DISPLAY_NAME)
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
                                    .join(LangKeys.HELP_BW_ADMIN_CONFIG_SET)
                                    .join(LangKeys.HELP_BW_ADMIN_CONFIG_RESET)
                                    .join(LangKeys.HELP_BW_ADMIN_CONFIG_LIST_ADD)
                                    .join(LangKeys.HELP_BW_ADMIN_CONFIG_LIST_SET)
                                    .join(LangKeys.HELP_BW_ADMIN_CONFIG_LIST_REMOVE)
                                    .join(LangKeys.HELP_BW_ADMIN_CONFIG_LIST_CLEAR)
                                    .join(LangKeys.HELP_BW_ADMIN_FEE)
                                    .join(LangKeys.HELP_BW_ADMIN_ARENA_WEATHER)
                                    .join(LangKeys.HELP_BW_ADMIN_REMOVE)
                                    .join(LangKeys.HELP_BW_ADMIN_EDIT)
                                    .join(LangKeys.HELP_BW_ADMIN_SAVE)
                                    .join(LangKeys.HELP_BW_RELOAD)
                                    .join(LangKeys.HELP_BW_DUMP)
                                    .join(LangKeys.HELP_BW_MIGRATE)
                                    .send(sender);
                        }
                    } else {
                        Message
                                .of(LangKeys.HELP_TITLE_CONSOLE)
                                .placeholder("version", BedWarsPlugin.getVersion())
                                .join(LangKeys.HELP_BW_LIST)
                                .join(LangKeys.HELP_BW_STATS_OTHER)
                                .join(LangKeys.HELP_BW_ALLJOIN)
                                .join(LangKeys.HELP_BW_MIGRATE)
                                .join(LangKeys.HELP_BW_RELOAD)
                                .send(sender);
                    }
                })
        );
    }
}
