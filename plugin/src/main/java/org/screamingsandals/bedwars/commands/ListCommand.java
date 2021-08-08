package org.screamingsandals.bedwars.commands;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.game.GameManagerImpl;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.sender.CommandSenderWrapper;
import org.screamingsandals.lib.utils.annotations.Service;

@Service
public class ListCommand extends BaseCommand {
    public ListCommand() {
        super("list", BedWarsPermission.LIST_PERMISSION, true);
    }

    @Override
    protected void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder, CommandManager<CommandSenderWrapper> manager) {
        manager.command(
                commandSenderWrapperBuilder
                        .handler(commandContext -> {
                            var sender = commandContext.getSender();
                            sender.sendMessage(Message.of(LangKeys.LIST_HEADER).defaultPrefix());
                            GameManagerImpl.getInstance().getGames().forEach(game ->
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
