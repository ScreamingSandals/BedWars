package org.screamingsandals.bedwars.commands;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import org.screamingsandals.bedwars.game.GameManagerImpl;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.player.PlayerManagerImpl;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.player.PlayerWrapper;
import org.screamingsandals.lib.sender.CommandSenderWrapper;
import org.screamingsandals.lib.utils.annotations.Service;

@Service
public class RejoinCommand extends BaseCommand {
    public RejoinCommand() {
        super("rejoin", BedWarsPermission.REJOIN_PERMISSION, false);
    }

    @Override
    protected void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder, CommandManager<CommandSenderWrapper> manager) {
        manager.command(
                commandSenderWrapperBuilder
                    .handler(commandContext -> {
                        var playerManager = PlayerManagerImpl.getInstance();
                        var player = commandContext.getSender().as(PlayerWrapper.class);
                        if (playerManager.isPlayerInGame(player)) {
                            commandContext.getSender().sendMessage(Message.of(LangKeys.IN_GAME_ERRORS_ALREADY_IN_GAME).defaultPrefix());
                            return;
                        }

                        String name = null;
                        if (playerManager.isPlayerRegistered(player)) {
                            name = playerManager.getPlayer(player).orElseThrow().getLatestGameName();
                        }
                        if (name == null) {
                            commandContext.getSender().sendMessage(Message.of(LangKeys.IN_GAME_ERRORS_YOU_ARE_NOT_IN_GAME).defaultPrefix());
                        } else {
                            GameManagerImpl.getInstance().getGame(name)
                                    .ifPresentOrElse(
                                            game -> game.joinToGame(PlayerManagerImpl.getInstance().getPlayerOrCreate(player)),
                                            () -> player.sendMessage(Message.of(LangKeys.IN_GAME_ERRORS_GAME_IS_GONE).defaultPrefix())
                                    );
                        }
                    })
        );
    }
}
