package org.screamingsandals.bedwars.commands.game;

import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.Permissions;
import org.screamingsandals.bedwars.game.GameBuilder;
import org.screamingsandals.lib.commands.common.RegisterCommand;
import org.screamingsandals.lib.commands.common.SubCommandBuilder;
import org.screamingsandals.lib.commands.common.interfaces.ScreamingCommand;

import java.util.*;

@RegisterCommand(subCommand = true)
public class AdminCommand implements ScreamingCommand {
    private final Map<String, GameBuilder> gameBuilders = new HashMap<>();
    private final List<String> availableAdminCommands = new LinkedList<>();

    @Override
    public void register() {
        SubCommandBuilder.bukkitSubCommand().createSubCommand("sbw", "admin", Permissions.ADMIN_COMMAND, Collections.emptyList())
                .handleSubPlayerCommand((player, args) -> {
                    if (args.size() == 3) {
                        final var command = args.get(1);

                        if (command.equalsIgnoreCase("create")) {
                            final var name = args.get(2);
                            final GameBuilder gameBuilder = new GameBuilder();
                            gameBuilder.create(name);

                            gameBuilders.put(name, gameBuilder);
                            final var game = gameBuilder.get(player);

                            Main.getInstance().getGameManager().saveGame(game);
                            Main.getInstance().getGameManager().registerGame(name, gameBuilder.get(player));
                        }
                    }
                });
    }

    private void register(String command) {
        availableAdminCommands.add(command);
    }


}
