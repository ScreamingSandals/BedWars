package org.screamingsandals.bedwars.commands.game.actions.add;

import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.commands.game.actions.Action;
import org.screamingsandals.bedwars.game.GameBuilder;

import java.util.List;

public class AddSpawnerAction implements Action {

    @Override
    public void handleCommand(GameBuilder gameBuilder, Player player, List<String> args) {

    }

    @Override
    public List<String> handleTab(GameBuilder gameBuilder, Player player, List<String> args) {
        return null;
    }
}
