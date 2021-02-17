package org.screamingsandals.bedwars.commands.admin;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.IntegerArgument;
import org.screamingsandals.lib.sender.CommandSenderWrapper;

import static org.screamingsandals.bedwars.lib.lang.I.i18n;

public class PausecountdownCommand extends BaseAdminSubCommand {
    public PausecountdownCommand(CommandManager<CommandSenderWrapper> manager, Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        super(manager, commandSenderWrapperBuilder, "pausecountdown");
    }

    @Override
    public void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        manager.command(
                commandSenderWrapperBuilder
                        .argument(IntegerArgument
                                .<CommandSenderWrapper>newBuilder("countdown")
                                .withMin(10)
                                .withMax(600)
                        )
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            int countdown = commandContext.get("countdown");

                            if (countdown >= 10 && countdown <= 600) {
                                game.setPauseCountdown(countdown);
                                sender.sendMessage(i18n("admin_command_pausecontdown_setted").replace("%countdown%", Integer.toString(countdown)));
                                return;
                            }
                            sender.sendMessage(i18n("admin_command_invalid_countdown"));
                        }))
        );
    }
}
