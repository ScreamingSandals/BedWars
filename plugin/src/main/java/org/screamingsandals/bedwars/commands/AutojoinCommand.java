package org.screamingsandals.bedwars.commands;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.game.GameManager;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.sender.CommandSenderWrapper;
import org.screamingsandals.lib.utils.AdventureHelper;

public class AutojoinCommand extends BaseCommand {
    public AutojoinCommand(CommandManager<CommandSenderWrapper> manager) {
        super(manager, "autojoin", BedWarsPermission.AUTOJOIN_PERMISSION, false);
    }

    @Override
    protected void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        manager.command(
                commandSenderWrapperBuilder
                        .handler(commandContext -> {
                            // TODO: Use Wrapper (bedwars changes needed)
                            var player = commandContext.getSender().as(Player.class);
                            if (Main.isPlayerInGame(player)) {
                                commandContext.getSender().sendMessage(AdventureHelper.toLegacy(Message.of(LangKeys.IN_GAME_ERRORS_ALREADY_IN_GAME).defaultPrefix().asComponent()));
                                return;
                            }

                            GameManager.getInstance().getFirstWaitingGame().ifPresentOrElse(
                                    game -> game.joinToGame(player),
                                    () -> commandContext.getSender().sendMessage(AdventureHelper.toLegacy(Message.of(LangKeys.IN_GAME_ERRORS_THERE_IS_NO_EMPTY_GAME).defaultPrefix().asComponent()))
                            );
                        })
        );
    }
}
