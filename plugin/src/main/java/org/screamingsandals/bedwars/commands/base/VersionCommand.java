package org.screamingsandals.bedwars.commands.base;

import org.bukkit.command.CommandSender;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.Permissions;
import org.screamingsandals.lib.commands.common.RegisterCommand;
import org.screamingsandals.lib.commands.common.SubCommandBuilder;
import org.screamingsandals.lib.commands.common.interfaces.ScreamingCommand;

import java.util.Collections;
import java.util.List;

import static org.screamingsandals.lib.lang.I.mpr;

@RegisterCommand(subCommand = true)
public class VersionCommand implements ScreamingCommand {

    @Override
    public void register() {
        SubCommandBuilder.bukkitSubCommand().createSubCommand("sbw", "version", Permissions.BASE_VERSION_COMMAND, Collections.emptyList())
                .handleSubPlayerCommand(this::handle)
                .handleSubConsoleCommand(this::handle);
    }

    private void handle(CommandSender commandSender, List<String> args) {
        //todo: check if you have latest version (latest jenkins version and latest spigot-release)
        boolean version = false;

        mpr("commands.base.version.header")
                .replace("%version%", Main.getInstance().getDescription().getVersion())
                .send(commandSender);
        if (version) {
            mpr("commands.base.version.latest").send(commandSender);
        } else {
            mpr("commands.base.version.behind")
                    .replace("%count%", 5) //TODO
                    .send(commandSender);
        }
    }
}