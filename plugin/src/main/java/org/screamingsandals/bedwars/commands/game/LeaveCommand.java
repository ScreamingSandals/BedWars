package org.screamingsandals.bedwars.commands.game;

import org.screamingsandals.bedwars.api.Permissions;
import org.screamingsandals.bedwars.commands.BedWarsCommand;
import org.screamingsandals.lib.commands.common.RegisterCommand;
import org.screamingsandals.lib.commands.common.SubCommandBuilder;
import org.screamingsandals.lib.commands.common.interfaces.ScreamingCommand;
import org.screamingsandals.lib.gamecore.GameCore;

import java.util.Collections;

import static org.screamingsandals.lib.gamecore.language.GameLanguage.mpr;

@RegisterCommand
public class LeaveCommand implements ScreamingCommand {

    @Override
    public void register() {
        SubCommandBuilder.bukkitSubCommand()
                .createSubCommand(BedWarsCommand.COMMAND_NAME, "leave", Permissions.LEAVE_COMMAND, Collections.emptyList())
                .handleSubPlayerCommand((player, args) -> {
                    final var gamePlayer = GameCore.getPlayerManager().getRegisteredPlayer(player);
                    if (!gamePlayer.isInGame()) {
                        System.out.println("what");
                        mpr("commands.leave.failed")
                                .game(gamePlayer.getActiveGame())
                                .send(player);
                        return;
                    }

                    gamePlayer.getActiveGame().leave(gamePlayer);
                });
    }
}
