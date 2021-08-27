package org.screamingsandals.bedwars.commands.admin;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.player.PlayerWrapper;
import org.screamingsandals.lib.sender.CommandSenderWrapper;
import org.screamingsandals.lib.utils.annotations.Service;

@Service
public class LobbyCommand extends BaseAdminSubCommand {
    public LobbyCommand() {
        super("lobby");
    }

    @Override
    public void construct(CommandManager<CommandSenderWrapper> manager, Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        manager.command(
                commandSenderWrapperBuilder
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            var player = sender.as(PlayerWrapper.class);
                            var loc = player.getLocation();

                            game.setLobbySpawn(loc);
                            Message
                                    .of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_LOBBY_SPAWN_SET)
                                    .defaultPrefix()
                                    .placeholder("x", loc.getX(), 2)
                                    .placeholder("y", loc.getY(), 2)
                                    .placeholder("z", loc.getZ(), 2)
                                    .placeholder("yaw", loc.getYaw(), 5)
                                    .placeholder("pitch", loc.getPitch(), 5)
                                    .send(sender);
                        }))
        );
    }
}
