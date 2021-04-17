package org.screamingsandals.bedwars.commands.admin;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import org.screamingsandals.bedwars.commands.AdminCommand;
import org.screamingsandals.bedwars.game.GameManager;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.sender.CommandSenderWrapper;
import org.screamingsandals.lib.utils.annotations.Service;

@Service
public class EditCommand extends BaseAdminSubCommand {
    public EditCommand() {
        super("edit");
    }

    @Override
    public void construct(CommandManager<CommandSenderWrapper> manager, Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        manager.command(
                commandSenderWrapperBuilder
                        .handler(commandContext -> {
                            String gameName = commandContext.get("game");
                            var sender = commandContext.getSender();

                            GameManager.getInstance().getGame(gameName).ifPresentOrElse(game -> {
                                game.stop();
                                AdminCommand.gc.put(gameName, game);
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_SUCCESS_EDIT_MODE).defaultPrefix());
                            }, () -> sender.sendMessage(Message.of(LangKeys.IN_GAME_ERRORS_GAME_NOT_FOUND).defaultPrefix()));
                        })
        );
    }
}
