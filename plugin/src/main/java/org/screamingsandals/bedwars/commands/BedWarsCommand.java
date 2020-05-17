package org.screamingsandals.bedwars.commands;

import org.bukkit.command.CommandSender;
import org.screamingsandals.bedwars.api.Permissions;
import org.screamingsandals.lib.commands.common.CommandBuilder;
import org.screamingsandals.lib.commands.common.RegisterCommand;
import org.screamingsandals.lib.commands.common.interfaces.ScreamingCommand;

import java.util.List;

import static org.screamingsandals.lib.gamecore.language.GameLanguage.mpr;

@RegisterCommand
public class BedWarsCommand implements ScreamingCommand {
    public static String commandName = "sbw";

    @Override
    public void register() {
        CommandBuilder.bukkitCommand().create(commandName, Permissions.BASE_COMMAND, List.of("bedwars", "bw", "screamingbedwars"))
                .setDescription("Base command for the ScreamingBedWars plugin")
                .setUsage("/sbw")
                .handlePlayerCommand((player, args) -> execute(player))
                .handleConsoleCommand((console, args) -> execute(console))
                .register();
    }

    private void execute(CommandSender commandSender) {
        mpr("commands.main").sendList(commandSender);
    }
}
