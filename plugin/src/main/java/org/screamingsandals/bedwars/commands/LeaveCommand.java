package org.screamingsandals.bedwars.commands;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.sender.CommandSenderWrapper;
import org.screamingsandals.lib.utils.AdventureHelper;

public class LeaveCommand extends BaseCommand {
    public LeaveCommand(CommandManager<CommandSenderWrapper> manager) {
        super(manager, "leave", BedWarsPermission.LEAVE_PERMISSION, false);
    }

    @Override
    protected void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        manager.command(
                commandSenderWrapperBuilder
                .handler(commandContext -> {
                    // TODO: Use Wrapper (bedwars changes needed)
                    var player = commandContext.getSender().as(Player.class);
                    if (Main.isPlayerInGame(player)) {
                        Main.getPlayerGameProfile(player).changeGame(null);
                    } else {
                        player.sendMessage(AdventureHelper.toLegacy(Message.of(LangKeys.IN_GAME_ERRORS_YOU_ARE_NOT_IN_GAME).defaultPrefix().asComponent()));
                    }
                })
        );
    }
}
