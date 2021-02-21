package org.screamingsandals.bedwars.commands.admin;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.EnumArgument;
import org.screamingsandals.bedwars.api.ArenaTime;
import org.screamingsandals.lib.sender.CommandSenderWrapper;

import static org.screamingsandals.bedwars.lib.lang.I.i18n;

public class ArenaTimeCommand extends BaseAdminSubCommand {
    public ArenaTimeCommand(CommandManager<CommandSenderWrapper> manager, Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        super(manager, commandSenderWrapperBuilder, "arenatime");
    }

    @Override
    public void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        manager.command(
                commandSenderWrapperBuilder
                        .argument(EnumArgument.of(ArenaTime.class, "arenaTime"))
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            ArenaTime arenaTime = commandContext.get("arenaTime");

                            game.setArenaTime(arenaTime);

                            sender.sendMessage(i18n("admin_command_arena_time_set").replace("%time%", arenaTime.name()));
                        }))
        );
    }
}
