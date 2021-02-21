package org.screamingsandals.bedwars.commands.admin;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.utils.ArenaUtils;
import org.screamingsandals.lib.sender.CommandSenderWrapper;

import static org.screamingsandals.bedwars.lib.lang.I.i18n;

public class SpecCommand extends BaseAdminSubCommand {
    public SpecCommand(CommandManager<CommandSenderWrapper> manager, Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        super(manager, commandSenderWrapperBuilder, "spec");
    }

    @Override
    public void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        manager.command(
                commandSenderWrapperBuilder
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            var loc = sender.as(Player.class).getLocation();

                            if (game.getPos1() == null || game.getPos2() == null) {
                                sender.sendMessage(i18n("admin_command_set_pos1_pos2_first"));
                                return;
                            }
                            if (game.getWorld() != loc.getWorld()) {
                                sender.sendMessage(i18n("admin_command_must_be_in_same_world"));
                                return;
                            }
                            if (!ArenaUtils.isInArea(loc, game.getPos1(), game.getPos2())) {
                                sender.sendMessage(i18n("admin_command_spawn_must_be_in_area"));
                                return;
                            }
                            game.setSpecSpawn(loc);
                            sender.sendMessage(
                                    i18n("admin_command_spec_spawn_setted")
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
