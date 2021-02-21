package org.screamingsandals.bedwars.commands;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.game.GameManager;
import org.screamingsandals.lib.sender.CommandSenderWrapper;

import static org.screamingsandals.bedwars.lib.lang.I.i18n;

public class RejoinCommand extends BaseCommand {
    public RejoinCommand(CommandManager<CommandSenderWrapper> manager) {
        super(manager, "rejoin", BedWarsPermission.REJOIN_PERMISSION, false);
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

                        String name = null;
                        if (Main.isPlayerGameProfileRegistered(player)) {
                            name = Main.getPlayerGameProfile(player).getLatestGameName();
                        }
                        if (name == null) {
                            player.sendMessage(i18n("you_are_not_in_game_yet"));
                        } else {
                            GameManager.getInstance().getGame(name)
                                    .ifPresentOrElse(
                                            game -> game.joinToGame(player),
                                            () -> player.sendMessage(i18n("game_is_gone"))
                                    );
                        }
                    })
        );
    }
}
