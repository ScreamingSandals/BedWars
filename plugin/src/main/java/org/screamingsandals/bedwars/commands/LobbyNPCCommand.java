package org.screamingsandals.bedwars.commands;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.StringArgument;
import org.screamingsandals.bedwars.game.GameManagerImpl;
import org.screamingsandals.bedwars.inventories.GamesInventory;
import org.screamingsandals.bedwars.lobby.LobbyNPCManager;
import org.screamingsandals.lib.sender.CommandSenderWrapper;
import org.screamingsandals.lib.utils.annotations.Service;

@Service
public class LobbyNPCCommand extends BaseCommand {
    private final LobbyNPCManager lobbyNPCManager;

    public LobbyNPCCommand(LobbyNPCManager lobbyNPCManager) {
        super("lobbyNPC", BedWarsPermission.ADMIN_PERMISSION, false);
        this.lobbyNPCManager = lobbyNPCManager;
    }

    @Override
    protected void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder, CommandManager<CommandSenderWrapper> manager) {
        manager.command(
                commandSenderWrapperBuilder
                        .literal("add")
                        .handler(commandContext -> {
                            // TODO
                        })
        );

        manager.command(
                commandSenderWrapperBuilder
                        .literal("remove")
                        .handler(commandContext -> {
                            // TODO
                        })
        );

        manager.command(
                commandSenderWrapperBuilder
                        .literal("action")
                        .literal("join")
                        .argument(StringArgument
                                .<CommandSenderWrapper>newBuilder("game")
                                .withSuggestionsProvider((c, s) -> GameManagerImpl.getInstance().getGameNames())
                                .asOptional()
                        )
                        .handler(commandContext -> {
                            // TODO
                        })
        );

        manager.command(
                commandSenderWrapperBuilder
                        .literal("action")
                        .literal("gamesinv")
                        .argument(StringArgument
                                .<CommandSenderWrapper>newBuilder("inventory")
                                .withSuggestionsProvider((objectCommandContext, s) -> GamesInventory.getInstance().getInventoriesNames())
                        )
                        .handler(commandContext -> {
                            // TODO
                        })
        );
    }
}
