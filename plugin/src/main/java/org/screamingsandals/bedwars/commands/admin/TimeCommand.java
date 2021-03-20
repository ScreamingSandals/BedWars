package org.screamingsandals.bedwars.commands.admin;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.IntegerArgument;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.sender.CommandSenderWrapper;

public class TimeCommand extends BaseAdminSubCommand {
    public TimeCommand(CommandManager<CommandSenderWrapper> manager, Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        super(manager, commandSenderWrapperBuilder, "time");
    }

    @Override
    public void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        manager.command(
                commandSenderWrapperBuilder
                        .argument(IntegerArgument
                                .<CommandSenderWrapper>newBuilder("time")
                                .withMin(10)
                                .withMax(3600)
                        )
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            int time = commandContext.get("time");

                            if (time >= 10 && time <= 7200) {
                                game.setGameTime(time);
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_GAME_TIME_SET).defaultPrefix().placeholder("time", time));
                                return;
                            }
                            sender.sendMessage(Message
                                    .of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_INVALID_COUNTDOWN)
                                    .defaultPrefix()
                                    .placeholder("lowest", 10)
                                    .placeholder("highest", 7200)
                            );
                        }))
        );
    }
}
