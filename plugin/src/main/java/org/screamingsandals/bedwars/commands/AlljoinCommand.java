package org.screamingsandals.bedwars.commands;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import org.screamingsandals.bedwars.game.GameManagerImpl;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.player.PlayerManagerImpl;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.player.PlayerMapper;
import org.screamingsandals.lib.sender.CommandSenderWrapper;
import org.screamingsandals.lib.utils.annotations.Service;

import java.util.Optional;

@Service
public class AlljoinCommand extends BaseCommand {
    public AlljoinCommand() {
        super("alljoin", BedWarsPermission.ALL_JOIN_PERMISSION, true);
    }

    @Override
    protected void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder, CommandManager<CommandSenderWrapper> manager) {
        manager.command(
                commandSenderWrapperBuilder
                        .argument(manager
                                .argumentBuilder(String.class, "game")
                                .withSuggestionsProvider((c, s) -> GameManagerImpl.getInstance().getGameNames())
                                .asOptional()
                        )
                        .handler(commandContext -> {
                            Optional<String> gameName = commandContext.getOptional("game");

                            var sender = commandContext.getSender();
                            var game = gameName
                                    .flatMap(GameManagerImpl.getInstance()::getGame)
                                    .or(GameManagerImpl.getInstance()::getGameWithHighestPlayers);

                            if (game.isEmpty()) {
                                sender.sendMessage(Message.of(LangKeys.IN_GAME_ERRORS_GAME_NOT_FOUND).defaultPrefix());
                                return;
                            }

                            PlayerMapper.getPlayers().forEach(player -> {
                                if (player.hasPermission(BedWarsPermission.DISABLE_ALL_JOIN_PERMISSION.asPermission())) {
                                    return;
                                }

                                if (PlayerManagerImpl.getInstance().isPlayerInGame(player)) {
                                    PlayerManagerImpl.getInstance().getPlayerOrCreate(player).getGame().leaveFromGame(player);
                                }
                                game.get().joinToGame(player);
                            });
                        })
        );
    }
}
