package org.screamingsandals.bedwars.commands.game.admin.actions;

import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.game.GameBuilder;
import org.screamingsandals.lib.gamecore.GameCore;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class EditGameAction implements Action {

    @Override
    public void handleCommand(GameBuilder gameBuilder, Player player, List<String> args) {
        final var argsSize = args.size();
    }

    @Override
    public List<String> handleTab(GameBuilder gameBuilder, Player player, List<String> args) {
        final var toReturn = new LinkedList<String>();
        final var argsSize = args.size();

        if (argsSize == 0) {
            return toReturn;
        }

        if (argsSize == 1) {
            final var typed = args.get(0);
            final Set<String> available = GameCore.getGameManager().getRegisteredGamesNames();

            for (var found : available) {
                if (found.startsWith(typed)) {
                    toReturn.add(found);
                }
                return toReturn;
            }
        }
        return toReturn;
    }
}
