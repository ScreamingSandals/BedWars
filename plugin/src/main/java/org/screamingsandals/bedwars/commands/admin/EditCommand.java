package org.screamingsandals.bedwars.commands.admin;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import org.screamingsandals.bedwars.commands.AdminCommand;
import org.screamingsandals.bedwars.game.GameManager;
import org.screamingsandals.lib.sender.CommandSenderWrapper;

import static org.screamingsandals.bedwars.lib.lang.I.i18n;

public class EditCommand extends BaseAdminSubCommand {
    public EditCommand(CommandManager<CommandSenderWrapper> manager, Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        super(manager, commandSenderWrapperBuilder, "edit");
    }

    @Override
    public void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        manager.command(
                commandSenderWrapperBuilder
                        .handler(commandContext -> {
                            String gameName = commandContext.get("game");
                            var sender = commandContext.getSender();

                            GameManager.getInstance().getGame(gameName).ifPresentOrElse(game -> {
                                game.stop();
                                AdminCommand.gc.put(gameName, game);
                                sender.sendMessage(i18n("arena_switched_to_edit"));
                            }, () -> sender.sendMessage(i18n("no_arena_found")));
                        })
        );
    }
}
