package org.screamingsandals.bedwars.commands.admin;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import org.bukkit.entity.Player;
import org.screamingsandals.lib.sender.CommandSenderWrapper;

import static org.screamingsandals.bedwars.lib.lang.I.i18n;

public class LobbyCommand extends BaseAdminSubCommand {
    public LobbyCommand(CommandManager<CommandSenderWrapper> manager, Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        super(manager, commandSenderWrapperBuilder, "lobby");
    }

    @Override
    public void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        manager.command(
                commandSenderWrapperBuilder
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            var player = sender.as(Player.class);
                            var loc = player.getLocation();

                            game.setLobbySpawn(loc);
                            sender.sendMessage(
                                    i18n("admin_command_lobby_spawn_setted")
                                            .replace("%x%", Double.toString(loc.getX()))
                                            .replace("%y%", Double.toString(loc.getY()))
                                            .replace("%z%", Double.toString(loc.getZ()))
                                            .replace("%yaw%", Float.toString(loc.getYaw()))
                                            .replace("%pitch%", Float.toString(loc.getPitch()))
                            );
                        }))
        );
    }
}
