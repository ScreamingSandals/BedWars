package org.screamingsandals.bedwars.commands.admin;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.commands.AdminCommand;
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

                        if (Main.isGameExists(gameName)) {
                            var game = Main.getGame(gameName);
                            game.stop();
                            AdminCommand.gc.put(gameName, game);
                            sender.sendMessage(i18n("arena_switched_to_edit"));
                        } else {
                            sender.sendMessage(i18n("no_arena_found"));
                        }
                    })
        );
    }
}
