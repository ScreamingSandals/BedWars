package org.screamingsandals.bedwars.commands.admin;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.context.CommandContext;
import lombok.RequiredArgsConstructor;
import org.screamingsandals.bedwars.commands.AdminCommand;
import org.screamingsandals.bedwars.commands.CommandService;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.sender.CommandSenderWrapper;
import org.screamingsandals.lib.utils.annotations.ServiceDependencies;
import org.screamingsandals.lib.utils.annotations.methods.OnPostEnable;
import org.screamingsandals.lib.utils.annotations.parameters.ProvidedBy;

import java.util.function.BiConsumer;

@ServiceDependencies(dependsOn = {
        AdminCommand.class
})
@RequiredArgsConstructor
public abstract class BaseAdminSubCommand {

    private final String name;

    @OnPostEnable
    public void onPostEnable(@ProvidedBy(CommandService.class) CommandManager<CommandSenderWrapper> manager, @ProvidedBy(AdminCommand.class) Command.Builder<CommandSenderWrapper> builder) {
        construct(manager, builder.literal(name));
    }

    public abstract void construct(CommandManager<CommandSenderWrapper> manager, Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder);

    protected void editMode(CommandContext<CommandSenderWrapper> commandContext, BiConsumer<CommandSenderWrapper, GameImpl> handler) {
        String gameName = commandContext.get("game");
        var sender = commandContext.getSender();

        if (AdminCommand.gc.containsKey(gameName)) {
            handler.accept(sender, AdminCommand.gc.get(gameName));
        } else {
            sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_ERROR_ARENA_NOT_IN_EDIT).defaultPrefix());
        }
    }
}
