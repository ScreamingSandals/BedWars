/*
 * Copyright (C) 2023 ScreamingSandals
 *
 * This file is part of Screaming BedWars.
 *
 * Screaming BedWars is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Screaming BedWars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Screaming BedWars. If not, see <https://www.gnu.org/licenses/>.
 */

package org.screamingsandals.bedwars.commands;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import lombok.Getter;
import org.screamingsandals.bedwars.commands.admin.*;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.game.GameManagerImpl;
import org.screamingsandals.lib.sender.CommandSender;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.utils.annotations.ServiceDependencies;
import org.screamingsandals.lib.utils.annotations.methods.Provider;

import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@ServiceDependencies(initAnother = {
        AddCommand.class,
        ArenaWeatherCommand.class,
        ConfigCommand.class,
        EditCommand.class,
        InfoCommand.class,
        JoinTeamCommand.class,
        LobbyCommand.class,
        MinPlayersCommand.class,
        LobbyCountdownCommand.class,
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
    private Command.Builder<CommandSender> builder;

    public AdminCommand() {
        super("admin", BedWarsPermission.ADMIN_PERMISSION, false);
        gc = new HashMap<>();
    }

    @Override
    protected void construct(Command.Builder<CommandSender> commandSenderWrapperBuilder, CommandManager<CommandSender> manager) {
        builder = commandSenderWrapperBuilder
                .argument(manager
                        .argumentBuilder(String.class, "game")
                        .withSuggestionsProvider((c, s) ->
                            Stream.concat(GameManagerImpl.getInstance().getGameNames().stream(), gc.keySet().stream()).distinct().collect(Collectors.toList())
                        )
                );
    }
}
