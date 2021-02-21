package org.screamingsandals.bedwars.commands.admin;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.IntegerArgument;
import org.screamingsandals.lib.sender.CommandSenderWrapper;

import static org.screamingsandals.bedwars.lib.lang.I.i18n;

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

                            if (time >= 10 && time <= 3600) {
                                game.setGameTime(time);
                                sender.sendMessage(i18n("admin_command_gametime_setted").replace("%time%", Integer.toString(time)));
                                return;
                            }
                            sender.sendMessage(i18n("admin_command_invalid_countdown2"));
                        }))
        );
    }
}
