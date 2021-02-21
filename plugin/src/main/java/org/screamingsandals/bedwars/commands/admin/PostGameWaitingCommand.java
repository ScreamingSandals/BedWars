package org.screamingsandals.bedwars.commands.admin;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.IntegerArgument;
import org.screamingsandals.lib.sender.CommandSenderWrapper;

import static org.screamingsandals.bedwars.lib.lang.I.i18n;

public class PostGameWaitingCommand extends BaseAdminSubCommand {
    public PostGameWaitingCommand(CommandManager<CommandSenderWrapper> manager, Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        super(manager, commandSenderWrapperBuilder, "postgamewaiting");
    }

    @Override
    public void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        manager.command(
                commandSenderWrapperBuilder
                        .argument(IntegerArgument
                                .<CommandSenderWrapper>newBuilder("time")
                                .withMin(0)
                        )
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            int time = commandContext.get("time");

                            if (time >= 0) {
                                game.setPostGameWaiting(time);
                                sender.sendMessage(i18n("admin_command_post_game_waiting").replace("%number%", String.valueOf(time)));
                                return;
                            }
                            sender.sendMessage(i18n("admin_command_invalid_time").replace("%number%", String.valueOf(time)));
                        }))
        );
    }
}
