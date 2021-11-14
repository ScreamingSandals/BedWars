package org.screamingsandals.bedwars.commands;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import lombok.Getter;
import org.screamingsandals.bedwars.commands.admin.*;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.game.GameManagerImpl;
import org.screamingsandals.lib.sender.CommandSenderWrapper;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.utils.annotations.methods.Provider;

import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service(initAnother = {
        AddCommand.class,
        ArenaTimeCommand.class,
        ArenaWeatherCommand.class,
        ConfigCommand.class,
        CustomPrefixCommand.class,
        EditCommand.class,
        GameBossBarColorCommand.class,
        InfoCommand.class,
        JoinTeamCommand.class,
        LobbyBossBarColorCommand.class,
        LobbyCommand.class,
        MinPlayersCommand.class,
        PausecountdownCommand.class,
        Pos1Command.class,
        Pos2Command.class,
        LobbyPos1Command.class,
        LobbyPos2Command.class,
        PostGameWaitingCommand.class,
        RemoveCommand.class,
        SaveCommand.class,
        SpawnerCommand.class,
        SpecCommand.class,
        StoreCommand.class,
        TeamCommand.class,
        TimeCommand.class,
        DisplayNameCommand.class,
        FeeCommand.class
})
public class AdminCommand extends BaseCommand {

    public static HashMap<String, GameImpl> gc;
    @Getter(onMethod_ = @Provider(level = Provider.Level.POST_ENABLE))
    private Command.Builder<CommandSenderWrapper> builder;

    public AdminCommand() {
        super("admin", BedWarsPermission.ADMIN_PERMISSION, false);
        gc = new HashMap<>();
    }

    @Override
    protected void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder, CommandManager<CommandSenderWrapper> manager) {
        builder = commandSenderWrapperBuilder
                .argument(manager
                        .argumentBuilder(String.class, "game")
                        .withSuggestionsProvider((c, s) ->
                            Stream.concat(GameManagerImpl.getInstance().getGameNames().stream(), gc.keySet().stream()).distinct().collect(Collectors.toList())
                        )
                );
    }
}
