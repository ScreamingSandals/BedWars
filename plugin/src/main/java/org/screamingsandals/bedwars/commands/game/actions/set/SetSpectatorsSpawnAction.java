package org.screamingsandals.bedwars.commands.game.actions.set;

import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.commands.game.actions.Action;
import org.screamingsandals.bedwars.game.GameBuilder;

import java.util.Collections;
import java.util.List;

import static org.screamingsandals.lib.gamecore.language.GameLanguage.mpr;

public class SetSpectatorsSpawnAction implements Action {

    @Override
    public void handleCommand(GameBuilder gameBuilder, Player player, List<String> args) {
        if (gameBuilder.setSpectatorsSpawn(player)) {
            mpr("commands.admin.actions.set.spectators-spawn.done").send(player);
        }
    }

    @Override
    public List<String> handleTab(GameBuilder gameBuilder, Player player, List<String> args) {
        return Collections.emptyList();
    }
}
