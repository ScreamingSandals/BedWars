package org.screamingsandals.bedwars.commands;

import org.bukkit.command.CommandSender;
import org.screamingsandals.bedwars.Main;

import java.util.List;

import static misat11.lib.lang.I.m;

public class ListCommand extends BaseCommand {

    public ListCommand() {
        super("list", null, true);
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        m("commands.list.header").send(sender);
        Main.sendGameListInfo(sender);
        return true;
    }

    @Override
    public void completeTab(List<String> completion, CommandSender sender, List<String> args) {
    }

}
