package org.screamingsandals.bedwars.commands.admin;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.IntegerArgument;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.sender.CommandSenderWrapper;
import org.screamingsandals.lib.utils.annotations.Service;

@Service
public class PausecountdownCommand extends BaseAdminSubCommand {
    public PausecountdownCommand() {
        super();
    }

    @Override
    public void construct(CommandManager<CommandSenderWrapper> manager, Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
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
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_LOBBY_COUNTDOWN_SET).placeholder("countdown", countdown).defaultPrefix());
                                return;
                            }
                            sender.sendMessage(Message
                                    .of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_INVALID_COUNTDOWN)
                                    .defaultPrefix()
                                    .placeholder("lowest", 10)
                                    .placeholder("highest", 600)
                            );
                        }))
        );
    }
}
