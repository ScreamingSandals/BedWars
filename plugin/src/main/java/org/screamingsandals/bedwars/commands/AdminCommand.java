package org.screamingsandals.bedwars.commands;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.commands.admin.*;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.lib.sender.CommandSenderWrapper;

import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AdminCommand extends BaseCommand {

    public static HashMap<String, Game> gc;

    public AdminCommand(CommandManager<CommandSenderWrapper> manager) {
        super(manager, "admin", BedWarsPermission.ADMIN_PERMISSION, false);
        gc = new HashMap<>();
    }

    @Override
    protected void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        var builder = commandSenderWrapperBuilder
                .argument(manager
                        .argumentBuilder(String.class, "game")
                        .withSuggestionsProvider((c, s) ->
                            Stream.concat(Main.getGameNames().stream(), gc.keySet().stream()).distinct().collect(Collectors.toList())
                        )
                );

        new AddCommand(manager, builder);
        new ArenaTimeCommand(manager, builder);
        new ArenaWeatherCommand(manager, builder);
        new ConfigCommand(manager, builder);
        new CustomPrefixCommand(manager, builder);
        new EditCommand(manager, builder);
        new GameBossBarColorCommand(manager, builder);
        new InfoCommand(manager, builder);
        new JoinTeamCommand(manager, builder);
        new LobbyBossBarColorCommand(manager, builder);
        new LobbyCommand(manager, builder);
        new MinPlayersCommand(manager, builder);
        new PausecountdownCommand(manager, builder);
        new Pos1Command(manager, builder);
        new Pos2Command(manager, builder);
        new PostGameWaitingCommand(manager, builder);
        new RemoveCommand(manager, builder);
        new SaveCommand(manager, builder);
        new SpawnerCommand(manager, builder);
        new SpecCommand(manager, builder);
        new StoreCommand(manager, builder);
        new TeamCommand(manager, builder);
        new TimeCommand(manager, builder);
    }
}
