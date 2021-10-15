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
public class RemoveCommand extends BaseAdminSubCommand {
    public RemoveCommand() {
        super();
    }

    @Override
    public void construct(CommandManager<CommandSenderWrapper> manager, Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        manager.command(
                commandSenderWrapperBuilder
                .handler(commandContext -> {
                    String gameName = commandContext.get("game");
                    var sender = commandContext.getSender();

                    GameManagerImpl.getInstance().getGame(gameName).ifPresentOrElse(game -> {
                        if (!AdminCommand.gc.containsKey(gameName)) {
                            sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_ERROR_ARENA_MUST_BE_IN_EDIT_MODE).defaultPrefix());
                        } else {
                            AdminCommand.gc.remove(gameName);
                            var file = game.getFile();
                            if (file != null) {
                                file.delete();
                            }
                            GameManagerImpl.getInstance().removeGame(game);
                            sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_SUCCESS_REMOVED).defaultPrefix());
                        }
                    }, () -> {
                        if (AdminCommand.gc.containsKey(gameName)) {
                            AdminCommand.gc.remove(gameName);
                            sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_SUCCESS_REMOVED).defaultPrefix());
                        } else {
                            sender.sendMessage(Message.of(LangKeys.IN_GAME_ERRORS_GAME_NOT_FOUND).defaultPrefix());
                        }
                    });
                })
        );
    }
}
