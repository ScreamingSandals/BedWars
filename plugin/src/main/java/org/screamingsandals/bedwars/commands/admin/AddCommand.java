package org.screamingsandals.bedwars.commands.admin;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.commands.AdminCommand;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.lib.sender.CommandSenderWrapper;

import static org.screamingsandals.bedwars.lib.lang.I.i18n;

public class AddCommand extends BaseAdminSubCommand {
    public AddCommand(CommandManager<CommandSenderWrapper> manager, Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        super(manager, commandSenderWrapperBuilder, "add");
    }

    @Override
    public void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        manager.command(
                commandSenderWrapperBuilder
                        .handler(commandContext -> {
                            String gameName = commandContext.get("game");
                            var sender = commandContext.getSender();

                            if (Main.isGameExists(gameName)) {
                                sender.sendMessage(i18n("allready_exists"));
                            } else if (AdminCommand.gc.containsKey(gameName)) {
                                sender.sendMessage(i18n("allready_working_on_it"));
                            } else {
                                var creator = Game.createGame(gameName);
                                AdminCommand.gc.put(gameName, creator);
                                sender.sendMessage(i18n("arena_added"));
                            }
                        })
        );
    }
}
