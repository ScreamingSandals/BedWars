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

import java.util.Optional;

@Service
public class JoinCommand extends BaseCommand {
    public JoinCommand() {
        super("join", BedWarsPermission.JOIN_PERMISSION, false);
    }

    @Override
    protected void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder, CommandManager<CommandSenderWrapper> manager) {
        manager.command(
                commandSenderWrapperBuilder
                        .argument(manager
                                .argumentBuilder(String.class, "game")
                                .withSuggestionsProvider((c, s) -> GameManager.getInstance().getGameNames())
                                .asOptional()
                        )
                        .handler(commandContext -> {
                            Optional<String> game = commandContext.getOptional("game");

                            var sender = commandContext.getSender().as(PlayerWrapper.class);
                            // TODO: Use Wrapper (bedwars changes needed)
                            var player = sender.as(Player.class);
                            ;
                            if (PlayerManager.getInstance().isPlayerInGame(sender)) {
                                sender.sendMessage(Message.of(LangKeys.IN_GAME_ERRORS_ALREADY_IN_GAME).defaultPrefix());
                                return;
                            }

                            if (game.isPresent()) {
                                var arenaN = game.get();
                                GameManager.getInstance().getGame(arenaN).ifPresentOrElse(
                                        game1 -> game1.joinToGame(player),
                                        () -> sender.sendMessage(Message.of(LangKeys.IN_GAME_ERRORS_GAME_NOT_FOUND).defaultPrefix())
                                );
                            } else {
                                GameManager.getInstance().getGameWithHighestPlayers().ifPresentOrElse(
                                        game1 -> game1.joinToGame(player),
                                        () -> sender.sendMessage(Message.of(LangKeys.IN_GAME_ERRORS_GAME_NOT_FOUND).defaultPrefix())
                                );
                            }
                        })
        );
    }
}
