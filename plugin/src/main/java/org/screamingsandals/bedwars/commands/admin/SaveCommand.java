package org.screamingsandals.bedwars.commands.admin;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.commands.AdminCommand;
import org.screamingsandals.lib.sender.CommandSenderWrapper;

import static org.screamingsandals.bedwars.lib.lang.I.i18n;

public class SaveCommand extends BaseAdminSubCommand {
    public SaveCommand(CommandManager<CommandSenderWrapper> manager, Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        super(manager, commandSenderWrapperBuilder, "save");
    }

    @Override
    public void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        manager.command(
                commandSenderWrapperBuilder
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            for (var team : game.getTeams()) {
                                if (team.bed == null) {
                                    sender.sendMessage(i18n("admin_command_set_bed_for_team_before_save").replace("%team%", team.name));
                                    return;
                                } else if (team.spawn == null) {
                                    sender.sendMessage(i18n("admin_command_set_spawn_for_team_before_save").replace("%team%", team.name));
                                    return;
                                }
                            }
                            if (game.getTeams().size() < 2) {
                                sender.sendMessage(i18n("admin_command_need_min_2_teems"));
                            } else if (game.getPos1() == null || game.getPos2() == null) {
                                sender.sendMessage(i18n("admin_command_set_pos1_pos2_before_save"));
                            } else if (game.getLobbySpawn() == null) {
                                sender.sendMessage(i18n("admin_command_set_lobby_before_save"));
                            } else if (game.getSpecSpawn() == null) {
                                sender.sendMessage(i18n("admin_command_set_spec_before_save"));
                            } else if (game.getGameStoreList().isEmpty()) {
                                sender.sendMessage(i18n("admin_command_set_stores_before_save"));
                            } else if (game.getSpawners().isEmpty()) {
                                sender.sendMessage(i18n("admin_command_set_spawners_before_save"));
                            } else {
                                game.saveToConfig();
                                Main.addGame(game);
                                game.start();
                                sender.sendMessage(i18n("admin_command_game_saved_and_started"));
                                AdminCommand.gc.remove(commandContext.<String>get("game"));
                            }
                        }))
        );
    }
}
