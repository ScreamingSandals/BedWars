package org.screamingsandals.bedwars.commands;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.lib.sender.CommandSenderWrapper;

import static org.screamingsandals.bedwars.lib.lang.I.i18n;

public class ListCommand extends BaseCommand {
    public ListCommand(CommandManager<CommandSenderWrapper> manager) {
        super(manager, "list", BedWarsPermission.LIST_PERMISSION, true);
    }

    @Override
    protected void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        manager.command(
                commandSenderWrapperBuilder
                    .handler(commandContext -> {
                        var sender = commandContext.getSender();
                        sender.sendMessage(i18n("list_header"));
                        Main.sendGameListInfo(sender);
                    })
        );
    }
}
