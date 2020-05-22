package org.screamingsandals.bedwars.commands.base;

import org.bukkit.command.CommandSender;
import org.screamingsandals.bedwars.api.Permissions;
import org.screamingsandals.bedwars.commands.BedWarsCommand;
import org.screamingsandals.lib.commands.common.RegisterCommand;
import org.screamingsandals.lib.commands.common.SubCommandBuilder;
import org.screamingsandals.lib.commands.common.interfaces.ScreamingCommand;
import org.screamingsandals.lib.gamecore.GameCore;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.screamingsandals.lib.lang.I.m;
import static org.screamingsandals.lib.lang.I.mpr;

@RegisterCommand(subCommand = true)
public class ListCommand implements ScreamingCommand {

    @Override
    public void register() {
        SubCommandBuilder.bukkitSubCommand().createSubCommand(BedWarsCommand.COMMAND_NAME, "list", Permissions.BASE_LIST_COMMAND, Collections.emptyList())
                .handleSubPlayerCommand(this::handle)
                .handleSubConsoleCommand(this::handle);
    }

    private void handle(CommandSender commandSender, List<String> args) {
        final var games = GameCore.getGameManager().getRegisteredGamesMap();
        final Collection<String> names = games.keySet();
        final int size = games.values().size();
        if (size > 0) {
            mpr("commands.base.list.arenas_list_header")
                    .replace("%count%", size)
                    .send(commandSender);
            names.forEach(name -> mpr("commands.base.list.arenas_list_text")
                    .replace("%game_name%", name)
                    .replace("%game_state%", m("game.state." + games.get(name).getActiveState().getName()).get())
                    .send(commandSender));
        } else {
            mpr("commands.base.list.no_arenas_found").send(commandSender);
        }
    }
}
