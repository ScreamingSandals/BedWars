package org.screamingsandals.bedwars.commands.admin;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.IntegerArgument;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.sender.CommandSenderWrapper;

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
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_INVALID_MIN_PLAYERS).defaultPrefix());
                                return;
                            }
                            game.setMinPlayers(minPlayers);
                            sender.sendMessage(
                                    Message.of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_MIN_PLAYERS_SET)
                                            .placeholder("win", minPlayers)
                                            .defaultPrefix()
                            );
                        }))
        );
    }
}
