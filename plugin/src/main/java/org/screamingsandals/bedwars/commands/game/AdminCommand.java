package org.screamingsandals.bedwars.commands.game;

import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.Permissions;
import org.screamingsandals.bedwars.commands.BedWarsCommand;
import org.screamingsandals.bedwars.game.GameBuilder;
import org.screamingsandals.lib.commands.common.RegisterCommand;
import org.screamingsandals.lib.commands.common.SubCommandBuilder;
import org.screamingsandals.lib.commands.common.interfaces.ScreamingCommand;
import org.screamingsandals.lib.gamecore.core.cycle.GameCycle;

import java.util.*;

import static org.screamingsandals.lib.lang.I.mpr;

@RegisterCommand(subCommand = true)
public class AdminCommand implements ScreamingCommand {
    private GameBuilder activeBuilder;
    private final Map<String, GameBuilder> gameBuilders = new HashMap<>();
    private final List<String> availableAdminCommands = new LinkedList<>();
    private Player player;

    @Override
    public void register() {
        SubCommandBuilder.bukkitSubCommand()
                .createSubCommand(BedWarsCommand.commandName, "admin", Permissions.ADMIN_COMMAND, Collections.emptyList())
                .handleSubPlayerCommand(this::handleCommand)
                .handleSubPlayerTab(this::handleTab);
    }

    private void handleCommand(Player player, List<String> args) {
        final var argsSize = args.size();
        if (argsSize < 3) {
            mpr("commands.admin.errors.not_enough_arguments").send(player);
            return;
        }

        this.player = player;
        final var arenaName = args.get(1);
        final var command = args.get(2);

        switch (command.toLowerCase()) {
            case "create":
                handleCreateAction(arenaName, args.subList(3, argsSize));
                break;
            default:
                mpr("commands.admin.errors.invalid_action")
                        .replace("%action%", command)
                        .send(player);
        }

        System.out.println(arenaName);
        System.out.println(command);
    }

    private List<String> handleTab(Player player, List<String> args) {
        return Collections.emptyList();
    }

    private void handleCreateAction(String arenaName, List<String> args) {
        if (Main.getGameManager().isGameRegistered(arenaName)) {
            mpr("core.errors.game-already-created").send(player);
            return;
        }

        if (args.isEmpty()) {
            mpr("commands.admin.actions.create.invalid_game_type").send(player);
            return;
        }

        final var gameType = GameCycle.Type.valueOf(args.get(0));

        if (gameType == null) {
            mpr("commands.admin.actions.create.invalid_game_type").send(player);
        }
    }

    private void handleCreateTab() {

    }

    private void register(String command) {
        availableAdminCommands.add(command);
    }


}
