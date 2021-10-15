package org.screamingsandals.bedwars.commands.admin;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.StringArgument;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.sender.CommandSenderWrapper;
import org.screamingsandals.lib.utils.annotations.Service;

@Service
public class CustomPrefixCommand extends BaseAdminSubCommand {
    public CustomPrefixCommand() {
        super();
    }

    @Override
    public void construct(CommandManager<CommandSenderWrapper> manager, Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        manager.command(
                commandSenderWrapperBuilder
                        .argument(StringArgument.of("customPrefix", StringArgument.StringMode.GREEDY))
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            String customPrefix = commandContext.get("customPrefix");
                            if (customPrefix.trim().equalsIgnoreCase("off")) {
                                game.setCustomPrefix(null);
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_CUSTOM_PREFIX_DISABLED).defaultPrefix());
                            } else {
                                game.setCustomPrefix(customPrefix);
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_CUSTOM_PREFIX_ENABLED).placeholder("prefix", customPrefix).defaultPrefix());
                            }
                        }))
        );
    }
}
