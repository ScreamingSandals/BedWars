package org.screamingsandals.bedwars.commands;

import org.screamingsandals.bedwars.api.Permissions;
import org.screamingsandals.lib.commands.common.CommandBuilder;
import org.screamingsandals.lib.commands.common.RegisterCommand;
import org.screamingsandals.lib.commands.common.interfaces.ScreamingCommand;

import java.util.List;

@RegisterCommand
public class BedWarsCommand implements ScreamingCommand {

    @Override
    public void register() {
        CommandBuilder.bukkitCommand().create("sbw", Permissions.BASE_COMMAND, List.of("bedwars", "bw", "screamingbedwars"))
                .setDescription("Base command for the ScreamingBedWars plugin")
                .setUsage("/sbw")
                .handlePlayerCommand((player, args) -> {
                    player.sendMessage("Will send help commands here :(");
                })
                .handleConsoleCommand((console, args) -> {
                    console.sendMessage("Will send help commands here :(");
                })
                .register();
    }
}
