package org.screamingsandals.bedwars.commands;

import org.bukkit.command.Command;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.utils.Permissions;
import org.screamingsandals.lib.screamingcommands.base.annotations.RegisterCommand;
import org.screamingsandals.lib.screamingcommands.base.interfaces.IBasicCommand;

import java.util.List;

import static misat11.lib.lang.I.*;

@RegisterCommand(commandName = "bw", subCommandName = "hologram")
public class HologramCommand implements IBasicCommand {

    public HologramCommand(Main main) {
    }

    @Override
    public String getPermission() {
        return Permissions.ADMIN.permission;
    }

    @Override
    public String getDescription() {
        return "BedWars hologram command";
    }

    @Override
    public String getUsage() {
        return "/bw hologram <add|create|remove|delete>";
    }

    @Override
    public String getInvalidUsageMessage() {
        return mpr("commands.errors.unknown_usage").get();
    }

    @Override
    public boolean onPlayerCommand(Player player, List<String> args) {
        if (args.size() == 1) {
            if (args.get(0).equalsIgnoreCase("create") || args.get(0).equalsIgnoreCase("add")) {
                if (!Main.isHologramsEnabled()) {
                    mpr("holograms.not_enabled").send(player);
                } else {
                    Main.getHologramInteraction().addHologramLocation(player.getEyeLocation());
                    Main.getHologramInteraction().updateHolograms();
                    mpr("commands.holograms.added").send(player);
                }
                return true;
            } else if (args.get(0).equalsIgnoreCase("remove") || args.get(0).equalsIgnoreCase("delete")) {
                if (!Main.isHologramsEnabled()) {
                    mpr("holograms.not_enabled").send(player);
                } else {
                    player.setMetadata("bw-remove-holo", new FixedMetadataValue(Main.getInstance(), true));
                    mpr("commands.holograms.click_to_remove").send(player);
                }
                return true;
            }
        } else {
            mpr("commands.errors.unknown_usage").send(player);
        }
        return true;
    }

    @Override
    public boolean onConsoleCommand(ConsoleCommandSender consoleCommandSender, List<String> list) {
        return false;
    }

    @Override
    public void onPlayerTabComplete(Player player, Command command, List<String> list, List<String> list1) {

    }

    @Override
    public void onConsoleTabComplete(ConsoleCommandSender consoleCommandSender, Command command, List<String> list, List<String> list1) {

    }
}
