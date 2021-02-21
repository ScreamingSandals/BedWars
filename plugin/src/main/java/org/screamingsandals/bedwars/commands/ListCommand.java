package org.screamingsandals.bedwars.commands;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.game.GameManager;
import org.screamingsandals.lib.sender.CommandSenderWrapper;

import static org.screamingsandals.bedwars.lib.lang.I.i18n;

public class ListCommand extends BaseCommand {
    public ListCommand(CommandManager<CommandSenderWrapper> manager) {
        super(manager, "list", BedWarsPermission.LIST_PERMISSION, true);
    }

    @Override
    protected void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        manager.command(
                commandSenderWrapperBuilder
                        .handler(commandContext -> {
                            var sender = commandContext.getSender();
                            sender.sendMessage(i18n("list_header"));
                            GameManager.getInstance().getGames().forEach(game ->
                                    sender.sendMessage(Component
                                            .text(game.getName())
                                            .color(game.getStatus() == GameStatus.DISABLED ? NamedTextColor.RED : NamedTextColor.GREEN)
                                            .append(Component
                                                    .text(" " + game.countPlayers())
                                                    .color(NamedTextColor.WHITE)
                                            )
                                    )
                            );
                        })
        );
    }
}
