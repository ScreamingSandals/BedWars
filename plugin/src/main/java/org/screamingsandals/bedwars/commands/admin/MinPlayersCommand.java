package org.screamingsandals.bedwars.commands.admin;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.IntegerArgument;
import org.screamingsandals.lib.sender.CommandSenderWrapper;

import static org.screamingsandals.bedwars.lib.lang.I.i18n;

public class MinPlayersCommand extends BaseAdminSubCommand {
    public MinPlayersCommand(CommandManager<CommandSenderWrapper> manager, Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        super(manager, commandSenderWrapperBuilder, "minplayers");
    }

    @Override
    public void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        manager.command(
                commandSenderWrapperBuilder
                        .argument(IntegerArgument
                                    .<CommandSenderWrapper>newBuilder("minPlayers")
                                    .withMin(2)
                        )
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            int minPlayers = commandContext.get("minPlayers");

                            if (minPlayers < 2) {
                                sender.sendMessage(i18n("admin_command_invalid_min_players"));
                                return;
                            }
                            game.setMinPlayers(minPlayers);
                            sender.sendMessage(
                                    i18n("admin_command_min_players_set")
                                            .replace("%min%", Integer.toString(minPlayers))
                            );
                        }))
        );
    }
}
