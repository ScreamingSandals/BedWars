package org.screamingsandals.bedwars.commands;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.lib.sender.CommandSenderWrapper;

import java.util.Optional;

import static org.screamingsandals.bedwars.lib.lang.I.i18n;

public class JoinCommand extends BaseCommand {
    public JoinCommand(CommandManager<CommandSenderWrapper> manager) {
        super(manager, "join", BedWarsPermission.JOIN_PERMISSION, false);
    }

    @Override
    protected void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        manager.command(
                commandSenderWrapperBuilder
                        .argument(manager
                                .argumentBuilder(String.class, "game")
                                .withSuggestionsProvider((c, s) -> Main.getGameNames())
                                .asOptional()
                        )
                        .handler(commandContext -> {
                            Optional<String> game = commandContext.getOptional("game");

                            // TODO: Use Wrapper (bedwars changes needed)
                            var player = commandContext.getSender().as(Player.class);
                            if (Main.isPlayerInGame(player)) {
                                player.sendMessage(i18n("you_are_already_in_some_game"));
                                return;
                            }

                            if (game.isPresent()) {
                                var arenaN = game.get();
                                if (Main.isGameExists(arenaN)) {
                                    Main.getGame(arenaN).joinToGame(player);
                                } else {
                                    player.sendMessage(i18n("no_arena_found"));
                                }
                            } else {
                                Main.getInstance().getGameWithHighestPlayers().joinToGame(player);
                            }
                        })
        );
    }
}
