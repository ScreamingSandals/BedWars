package org.screamingsandals.bedwars.commands.admin;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.context.CommandContext;
import org.screamingsandals.bedwars.commands.AdminCommand;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.lib.sender.CommandSenderWrapper;

import static org.screamingsandals.bedwars.lib.lang.I.i18n;

public abstract class BaseAdminSubCommand {

    protected final CommandManager<CommandSenderWrapper> manager;

    public BaseAdminSubCommand(CommandManager<CommandSenderWrapper> manager, Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder, String name) {
        this.manager = manager;
        construct(commandSenderWrapperBuilder.literal(name));
    }

    public abstract void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder);

    protected void editMode(CommandContext<CommandSenderWrapper> commandContext, EditModeHandler handler) {
        String gameName = commandContext.get("game");
        var sender = commandContext.getSender();

        if (AdminCommand.gc.containsKey(gameName)) {
            handler.handle(sender, AdminCommand.gc.get(gameName));
        } else {
            sender.sendMessage(i18n("arena_not_in_edit"));
        }
    }

    protected interface EditModeHandler {
        void handle(CommandSenderWrapper sender, Game game);
    }
}
