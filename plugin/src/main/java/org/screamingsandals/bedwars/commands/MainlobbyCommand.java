package org.screamingsandals.bedwars.commands;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import org.screamingsandals.lib.sender.CommandSenderWrapper;

public class MainlobbyCommand extends BaseCommand {
    public MainlobbyCommand(CommandManager<CommandSenderWrapper> manager) {
        super(manager, "mainlobby", BedWarsPermission.ADMIN_PERMISSION, false);
    }

    @Override
    protected void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {

    }
}
