package org.screamingsandals.bedwars.commands.admin;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.EnumArgument;
import org.screamingsandals.bedwars.api.ArenaTime;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.sender.CommandSenderWrapper;
import org.screamingsandals.lib.utils.annotations.Service;

@Service
public class ArenaTimeCommand extends BaseAdminSubCommand {
    public ArenaTimeCommand() {
        super("arenatime");
    }

    @Override
    public void construct(CommandManager<CommandSenderWrapper> manager, Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        manager.command(
                commandSenderWrapperBuilder
                        .argument(EnumArgument.of(ArenaTime.class, "arenaTime"))
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            ArenaTime arenaTime = commandContext.get("arenaTime");

                            game.setArenaTime(arenaTime);

                            sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_ARENA_TIME_SET).defaultPrefix().placeholder("time", arenaTime.name()));
                        }))
        );
    }
}
