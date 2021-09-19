package org.screamingsandals.bedwars.commands.admin;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import org.screamingsandals.bedwars.commands.AdminCommand;
import org.screamingsandals.bedwars.game.GameManagerImpl;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.sender.CommandSenderWrapper;
import org.screamingsandals.lib.utils.annotations.Service;

@Service
public class SaveCommand extends BaseAdminSubCommand {
    public SaveCommand() {
        super("save");
    }

    @Override
    public void construct(CommandManager<CommandSenderWrapper> manager, Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        manager.command(
                commandSenderWrapperBuilder
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            for (var team : game.getTeams()) {
                                if (team.getTargetBlock() == null) {
                                    sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_SET_TARGET_BLOCK_FOR_TEAM_BEFORE_SAVE).defaultPrefix().placeholder("team", team.getName()));
                                    return;
                                } else if (team.getTeamSpawn() == null) {
                                    sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_SET_SPAWN_FOR_TEAM_BEFORE_SAVE).defaultPrefix().placeholder("team", team.getName()));
                                    return;
                                }
                            }
                            if (game.getTeams().size() < 2) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_NEED_2_TEAMS).defaultPrefix());
                            } else if (game.getPos1() == null || game.getPos2() == null) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_SET_BOUNDS_BEFORE_SAVE).defaultPrefix());
                            } else if (game.getLobbySpawn() == null) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_SET_LOBBY_BEFORE_SAVE).defaultPrefix());
                            } else if (game.getSpecSpawn() == null) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_SET_SPEC_BEFORE_SAVE).defaultPrefix());
                            } else if (game.getGameStoreList().isEmpty()) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_SET_STORES_BEFORE_SAVE).defaultPrefix());
                            } else if (game.getSpawners().isEmpty()) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_SET_SPAWNERS_BEFORE_SAVE).defaultPrefix());
                            } else {
                                game.saveToConfig();
                                GameManagerImpl.getInstance().addGame(game);
                                game.start();
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_SAVED_AND_STARTED).defaultPrefix());
                                AdminCommand.gc.remove(commandContext.<String>get("game"));
                            }
                        }))
        );
    }
}
