package org.screamingsandals.bedwars.commands.game.actions;

import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.game.GameBuilder;

import java.util.List;

public class LobbyAction implements Action {

    @Override
    public void handleCommand(GameBuilder gameBuilder, Player player, List<String> args) {

    }

    @Override
    public List<String> handleTab(GameBuilder gameBuilder, Player player, List<String> args) {
        return null;
    }
}
