package org.screamingsandals.bedwars.commands;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.lib.sender.CommandSenderWrapper;

import static org.screamingsandals.bedwars.lib.lang.I.i18n;

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
                        player.sendMessage(i18n("you_arent_in_game"));
                    }
                })
        );
    }
}
