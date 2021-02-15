package org.screamingsandals.bedwars.commands;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.lib.sender.CommandSenderWrapper;

import static org.screamingsandals.bedwars.lib.lang.I.i18n;

public class AutojoinCommand extends BaseCommand {
    public AutojoinCommand(CommandManager<CommandSenderWrapper> manager) {
        super(manager, "autojoin", BedWarsPermission.AUTOJOIN_PERMISSION, false);
    }

    @Override
    protected void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        manager.command(
                commandSenderWrapperBuilder
                    .handler(commandContext -> {
                        // TODO: Use Wrapper (bedwars changes needed)
                        var player = commandContext.getSender().as(Player.class);
                        if (Main.isPlayerInGame(player)) {
                            player.sendMessage(i18n("you_are_already_in_some_game"));
                            return;
                        }

                        var game = Main.getInstance().getFirstWaitingGame();
                        if (game == null) {
                            player.sendMessage(i18n("there_is_no_empty_game"));
                        } else {
                            game.joinToGame(player);
                        }
                    })
        );
    }
}
