package org.screamingsandals.bedwars.commands.game.actions;

import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.game.GameBuilder;

import java.util.List;

public interface Action {

    void handleCommand(GameBuilder gameBuilder, Player player, List<String> args);

    List<String> handleTab(GameBuilder gameBuilder, Player player, List<String> args);
}
