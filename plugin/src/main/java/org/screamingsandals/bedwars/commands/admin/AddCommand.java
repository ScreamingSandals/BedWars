package org.screamingsandals.bedwars.commands.admin;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import org.screamingsandals.bedwars.commands.AdminCommand;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.bedwars.game.GameManager;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.sender.CommandSenderWrapper;
import org.screamingsandals.lib.utils.annotations.Service;

@Service
public class AddCommand extends BaseAdminSubCommand {
    public AddCommand() {
        super("add");
    }

    @Override
    public void construct(CommandManager<CommandSenderWrapper> manager, Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        manager.command(
                commandSenderWrapperBuilder
                        .handler(commandContext -> {
                            String gameName = commandContext.get("game");
                            var sender = commandContext.getSender();

                            if (GameManager.getInstance().hasGame(gameName)) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_ERROR_ALREADY_EXISTS).defaultPrefix());
                            } else if (AdminCommand.gc.containsKey(gameName)) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_ERROR_ALREADY_WORKING_ON_IT).defaultPrefix());
                            } else {
                                var creator = Game.createGame(gameName);
                                AdminCommand.gc.put(gameName, creator);
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_SUCCESS_ADDED).defaultPrefix());
                            }
                        })
        );
    }
}
