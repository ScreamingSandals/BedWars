package org.screamingsandals.bedwars.commands.admin;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.commands.AdminCommand;
import org.screamingsandals.lib.sender.CommandSenderWrapper;

import static org.screamingsandals.bedwars.lib.lang.I.i18n;

public class RemoveCommand extends BaseAdminSubCommand {
    public RemoveCommand(CommandManager<CommandSenderWrapper> manager, Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        super(manager, commandSenderWrapperBuilder, "remove");
    }

    @Override
    public void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        manager.command(
                commandSenderWrapperBuilder
                .handler(commandContext -> {
                    String gameName = commandContext.get("game");
                    var sender = commandContext.getSender();

                    if (Main.isGameExists(gameName)) {
                        if (!AdminCommand.gc.containsKey(gameName)) {
                            sender.sendMessage(i18n("arena_must_be_in_edit_mode"));
                        } else {
                            AdminCommand.gc.remove(gameName);
                            var game = Main.getGame(gameName);
                            var file = game.getFile();
                            if (file != null) {
                                file.delete();
                            }
                            Main.removeGame(game);
                            sender.sendMessage(i18n("arena_removed"));
                        }
                    } else if (AdminCommand.gc.containsKey(gameName)) {
                        AdminCommand.gc.remove(gameName);
                        sender.sendMessage(i18n("arena_removed"));
                    } else {
                        sender.sendMessage(i18n("no_arena_found"));
                    }
                })
        );
    }
}
