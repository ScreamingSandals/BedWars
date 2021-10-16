package org.screamingsandals.bedwars.commands;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.StringArgument;
import org.screamingsandals.bedwars.inventories.GamesInventory;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.player.PlayerWrapper;
import org.screamingsandals.lib.sender.CommandSenderWrapper;
import org.screamingsandals.lib.utils.annotations.Service;

@Service
public class GamesInventoryCommand extends BaseCommand {
    private final GamesInventory gamesInventory;

    public GamesInventoryCommand(GamesInventory gamesInventory) {
        super("gamesinv", BedWarsPermission.GAMES_INVENTORY_PERMISSION, false);
        this.gamesInventory = gamesInventory;
    }

    @Override
    protected void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder, CommandManager<CommandSenderWrapper> manager) {
        manager.command(
                commandSenderWrapperBuilder
                        .argument(StringArgument
                                .<CommandSenderWrapper>newBuilder("inventory")
                                .withSuggestionsProvider((objectCommandContext, s) -> gamesInventory.getInventoriesNames())
                        )
                        .handler(commandContext -> {
                            var player = commandContext.getSender().as(PlayerWrapper.class);
                            String inventory = commandContext.get("inventory");

                            if (!gamesInventory.openForPlayer(player, inventory)) {
                                player.sendMessage(Message.of(LangKeys.GAMES_INVENTORY_UNKNOWN_INVENTORY).defaultPrefix().placeholder("type", inventory));
                            }
                        })
        );
    }
}
