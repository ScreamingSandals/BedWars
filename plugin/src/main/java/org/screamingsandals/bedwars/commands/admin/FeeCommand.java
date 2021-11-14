package org.screamingsandals.bedwars.commands.admin;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.IntegerArgument;
import org.screamingsandals.bedwars.commands.AdminCommand;
import org.screamingsandals.bedwars.game.GameManagerImpl;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.sender.CommandSenderWrapper;
import org.screamingsandals.lib.utils.annotations.Service;

@Service
public class FeeCommand extends BaseAdminSubCommand {
    public FeeCommand() {
        super("fee");
    }

    @Override
    public void construct(CommandManager<CommandSenderWrapper> manager, Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        manager.command(
                commandSenderWrapperBuilder
                        .argument(IntegerArgument.of("fee"))
                        .handler(commandContext -> editMode(commandContext, (commandSenderWrapper, game) -> {
                            final int fee = commandContext.get("fee");

                            game.setFee(fee);
                            Message.of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_FEE_SET)
                                    .placeholder("fee", fee)
                                    .defaultPrefix()
                                    .send(commandSenderWrapper);
                        }))
        );
    }
}
