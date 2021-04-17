package org.screamingsandals.bedwars.commands;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.game.GameManager;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.player.PlayerManager;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.player.PlayerWrapper;
import org.screamingsandals.lib.sender.CommandSenderWrapper;
import org.screamingsandals.lib.utils.annotations.Service;

@Service
public class AutojoinCommand extends BaseCommand {
    public AutojoinCommand() {
        super("autojoin", BedWarsPermission.AUTOJOIN_PERMISSION, false);
    }

    @Override
    protected void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder, CommandManager<CommandSenderWrapper> manager) {
        manager.command(
                commandSenderWrapperBuilder
                        .handler(commandContext -> {
                            var player = commandContext.getSender().as(PlayerWrapper.class);
                            if (PlayerManager.getInstance().isPlayerInGame(player)) {
                                player.sendMessage(Message.of(LangKeys.IN_GAME_ERRORS_ALREADY_IN_GAME).defaultPrefix());
                                return;
                            }

                            GameManager.getInstance().getFirstWaitingGame().ifPresentOrElse(
                                    game -> game.joinToGame(player.as(Player.class)),// TODO: Use Wrapper (bedwars changes needed)
                                    () -> player.sendMessage(Message.of(LangKeys.IN_GAME_ERRORS_THERE_IS_NO_EMPTY_GAME).defaultPrefix())
                            );
                        })
        );
    }
}
