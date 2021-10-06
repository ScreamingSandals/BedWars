package org.screamingsandals.bedwars.commands.admin;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.StringArgument;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.sender.CommandSenderWrapper;
import org.screamingsandals.lib.utils.AdventureHelper;
import org.screamingsandals.lib.utils.annotations.Service;

@Service
public class DisplayNameCommand extends BaseAdminSubCommand {
    public DisplayNameCommand() {
        super("displayName");
    }

    @Override
    public void construct(CommandManager<CommandSenderWrapper> manager, Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        manager.command(
                commandSenderWrapperBuilder
                        .argument(StringArgument.of("displayName", StringArgument.StringMode.GREEDY))
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            String displayName = commandContext.get("displayName");
                            if (displayName.trim().equalsIgnoreCase("off") || displayName.trim().isEmpty()) {
                                game.setDisplayName(null);
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_DISPLAY_NAME_DISABLED).defaultPrefix());
                            } else {
                                game.setDisplayName(AdventureHelper.translateAlternateColorCodes(LegacyComponentSerializer.AMPERSAND_CHAR, displayName));
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_DISPLAY_NAME_ENABLED).placeholder("display_name", displayName).defaultPrefix());
                            }
                        }))
        );
    }
}
