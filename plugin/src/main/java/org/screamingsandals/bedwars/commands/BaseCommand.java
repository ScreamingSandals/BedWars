package org.screamingsandals.bedwars.commands;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.keys.SimpleCloudKey;
import cloud.commandframework.permission.PredicatePermission;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.screamingsandals.lib.player.PlayerWrapper;
import org.screamingsandals.lib.sender.CommandSenderWrapper;
import org.screamingsandals.lib.utils.annotations.ServiceDependencies;
import org.screamingsandals.lib.utils.annotations.methods.OnPostEnable;
import org.screamingsandals.lib.utils.annotations.parameters.ProvidedBy;

@RequiredArgsConstructor
@Getter
@ServiceDependencies(dependsOn = {
        CommandService.class
})
public abstract class BaseCommand {

    protected final String name;
    protected final BedWarsPermission possiblePermission;
    protected final boolean allowConsole;

    protected abstract void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder, CommandManager<CommandSenderWrapper> manager);

    @OnPostEnable
    public void construct(@ProvidedBy(CommandService.class) CommandManager<CommandSenderWrapper> manager) {
        var builder = manager.commandBuilder("bw")
                .literal(name);
        if (possiblePermission != null) {
            builder = builder.permission(
                    PredicatePermission.of(SimpleCloudKey.of(name), perm ->
                            perm.getType() == CommandSenderWrapper.Type.CONSOLE || possiblePermission.asPermission().hasPermission(perm)
                    )
            );
        }
        if (!allowConsole) {
            builder = builder.senderType(PlayerWrapper.class);
        }
        construct(builder, manager);
    }
}
