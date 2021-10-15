package org.screamingsandals.bedwars.commands.admin;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.player.PlayerWrapper;
import org.screamingsandals.lib.sender.CommandSenderWrapper;
import org.screamingsandals.lib.utils.annotations.Service;

@Service
public class LobbyPos1Command extends BaseAdminSubCommand {
    public LobbyPos1Command() {
        super();
    }

    @Override
    public void construct(CommandManager<CommandSenderWrapper> manager, Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        manager.command(
                commandSenderWrapperBuilder
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            var loc = sender.as(PlayerWrapper.class).getLocation();
                            var lobbyWorld = game.getLobbyWorld();

                            if (lobbyWorld != null && (!game.getLobbyWorld().equals(loc.getWorld()))) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_MUST_BE_IN_SAME_WORLD).defaultPrefix());
                                return;
                            }
                            if (game.getLobbyPos2() != null) {
                                if (Math.abs(game.getLobbyPos2().getBlockY() - loc.getBlockY()) <= 5) {
                                    sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_INVALID_BOUNDS).defaultPrefix());
                                    return;
                                }
                            }
                            game.setLobbyPos1(loc);
                            sender.sendMessage(
                                    Message
                                    .of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_LOBBY_POS1_SET)
                                    .defaultPrefix()
                                    .placeholder("arena", game.getName())
                                    .placeholder("x", loc.getBlockX())
                                    .placeholder("y", loc.getBlockY())
                                    .placeholder("z", loc.getBlockZ())
                            );
                        }))
        );
    }
}
