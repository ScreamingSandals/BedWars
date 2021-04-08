package org.screamingsandals.bedwars.commands;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.player.PlayerManager;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.player.PlayerWrapper;
import org.screamingsandals.lib.sender.CommandSenderWrapper;

public class LeaveCommand extends BaseCommand {
    public LeaveCommand(CommandManager<CommandSenderWrapper> manager) {
        super(manager, "leave", BedWarsPermission.LEAVE_PERMISSION, false);
    }

    @Override
    protected void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        manager.command(
                commandSenderWrapperBuilder
                .handler(commandContext -> {
                    var player = commandContext.getSender().as(PlayerWrapper.class);
                    if (PlayerManager.getInstance().isPlayerInGame(player)) {
                        PlayerManager.getInstance().getPlayerOrCreate(player).changeGame(null);
                    } else {
                        commandContext.getSender().sendMessage(Message.of(LangKeys.IN_GAME_ERRORS_YOU_ARE_NOT_IN_GAME).defaultPrefix());
                    }
                })
        );
    }
}
