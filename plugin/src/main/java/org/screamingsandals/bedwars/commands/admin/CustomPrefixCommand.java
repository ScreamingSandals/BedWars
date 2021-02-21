package org.screamingsandals.bedwars.commands.admin;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.StringArgument;
import org.screamingsandals.lib.sender.CommandSenderWrapper;

import static org.screamingsandals.bedwars.lib.lang.I.i18n;

public class CustomPrefixCommand extends BaseAdminSubCommand {
    public CustomPrefixCommand(CommandManager<CommandSenderWrapper> manager, Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        super(manager, commandSenderWrapperBuilder, "customprefix");
    }

    @Override
    public void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        manager.command(
                commandSenderWrapperBuilder
                        .argument(StringArgument.of("customPrefix", StringArgument.StringMode.GREEDY))
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            String customPrefix = commandContext.get("customPrefix");
                            if (customPrefix.trim().equalsIgnoreCase("off")) {
                                game.setCustomPrefix(null);
                                sender.sendMessage(i18n("admin_command_customprefix_disabled"));
                            } else {
                                game.setCustomPrefix(customPrefix);
                                sender.sendMessage(i18n("admin_command_customprefix_enabled").replace("%prefix%", customPrefix));
                            }
                        }))
        );
    }
}
