package org.screamingsandals.bedwars.commands;

import org.bukkit.command.Command;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.utils.Permissions;
import org.screamingsandals.lib.screamingcommands.base.annotations.RegisterCommand;
import org.screamingsandals.lib.screamingcommands.base.interfaces.IBasicCommand;

import java.util.List;

import static misat11.lib.lang.I.m;
import static misat11.lib.lang.I.mpr;

@RegisterCommand(commandName = "bw", subCommandName = "join")
public class JoinCommand implements IBasicCommand {

    public JoinCommand(Main main) {
    }

    @Override
    public String getPermission() {
        return Permissions.BASE.permission;
    }

    @Override
    public String getDescription() {
        return "BedWars join command";
    }

    @Override
    public String getUsage() {
        return m("commands.help.join").get();
    }

    @Override
    public String getInvalidUsageMessage() {
        return "/bw join <arena>";
    }

    @Override
    public boolean onPlayerCommand(Player player, List<String> args) {
        if (Main.isPlayerInGame(player)) {
            mpr("commands.join.already_in_game").send(player);
            return true;
        }

        if (args.size() >= 1) {
            String arenaN = args.get(0);
            if (Main.isGameExists(arenaN)) {
                Main.getGame(arenaN).joinToGame(player);
            } else {
                mpr("commands.join.not_found").send(player);
            }
        } else {
            Main.getInstance().getGameWithHighestPlayers().joinToGame(player);
            return true;
        }
        return true;
    }

    @Override
    public boolean onConsoleCommand(ConsoleCommandSender consoleCommandSender, List<String> list) {
        return false;
    }

    @Override
    public void onPlayerTabComplete(Player player, Command command, List<String> completion, List<String> args) {
        if (args.size() == 1) {
            completion.addAll(Main.getGameNames());
        }
    }

    @Override
    public void onConsoleTabComplete(ConsoleCommandSender consoleCommandSender, Command command, List<String> list, List<String> list1) {

    }
}
