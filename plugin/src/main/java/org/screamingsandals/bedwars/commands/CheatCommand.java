package org.screamingsandals.bedwars.commands;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.IntegerArgument;
import org.screamingsandals.bedwars.BedWarsPlugin;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.bedwars.player.PlayerManagerImpl;
import org.screamingsandals.lib.entity.EntityHuman;
import org.screamingsandals.lib.entity.EntityMapper;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.player.PlayerMapper;
import org.screamingsandals.lib.player.PlayerWrapper;
import org.screamingsandals.lib.sender.CommandSenderWrapper;
import org.screamingsandals.lib.utils.annotations.Service;

import java.util.stream.Collectors;

@Service
public class CheatCommand extends BaseCommand {
    private final PlayerManagerImpl playerManager;
    private final MainConfig mainConfig;

    public CheatCommand(PlayerManagerImpl playerManager, MainConfig mainConfig) {
        super("cheat", BedWarsPermission.ADMIN_PERMISSION, false);
        this.playerManager = playerManager;
        this.mainConfig = mainConfig;
    }

    @Override
    protected void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder, CommandManager<CommandSenderWrapper> manager) {
        if (!mainConfig.node("enable-cheat-command-for-admins").getBoolean()) {
            return;
        }

        manager.command(
                commandSenderWrapperBuilder
                .literal("give")
                .argument(manager
                        .argumentBuilder(String.class, "resource")
                        .withSuggestionsProvider((c, s) ->
                                BedWarsPlugin.getAllSpawnerTypes()
                        )
                )
                .argument(IntegerArgument.optional("amount", 1))
                .argument(manager
                        .argumentBuilder(String.class, "player")
                        .withSuggestionsProvider((c, s) ->
                                PlayerMapper.getPlayers().stream().map(PlayerWrapper::getName).collect(Collectors.toList())
                        )
                        .asOptional()
                )
                .handler(commandContext -> {
                    var player = commandContext.getSender().as(PlayerWrapper.class);

                    var game = playerManager.getGameOfPlayer(player);
                    if (game.isEmpty()) {
                        player.sendMessage(Message.of(LangKeys.IN_GAME_ERRORS_NOT_IN_ANY_GAME_YET).defaultPrefix());
                        return;
                    }
                    if (game.get().getStatus() != GameStatus.RUNNING) {
                        player.sendMessage(Message.of(LangKeys.IN_GAME_CHEAT_GAME_NOT_RUNNING).defaultPrefix());
                        return;
                    }

                    var resource = commandContext.<String>get("resource");
                    int amount = commandContext.get("amount");
                    var receiver = commandContext.<String>getOptional("player");
                    BedWarsPlayer bwPlayer;
                    if (receiver.isPresent()) {
                        var playerWrapper = receiver.flatMap(PlayerMapper::getPlayer);
                        if (playerWrapper.isEmpty() || !playerManager.isPlayerInGame(playerWrapper.get())) {
                            player.sendMessage(Message.of(LangKeys.IN_GAME_CHEAT_INVALID_PLAYER));
                            return;
                        }
                        bwPlayer = playerManager.getPlayer(playerWrapper.get()).orElseThrow();
                    } else {
                        bwPlayer = playerManager.getPlayer(player).orElseThrow();
                    }

                    if (bwPlayer.isSpectator) {
                        player.sendMessage(Message.of(LangKeys.IN_GAME_CHEAT_INVALID_PLAYER));
                        return;
                    }
                    var spawnerType = BedWarsPlugin.getSpawnerType(resource);
                    if (spawnerType == null) {
                        player.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_INVALID_SPAWNER_TYPE).defaultPrefix());
                        return;
                    }
                    var remaining = bwPlayer.getPlayerInventory().addItem(spawnerType.getItem(amount));
                    remaining.forEach(item ->
                        EntityMapper.dropItem(item, player.getLocation())
                    );
                    Message.of(LangKeys.IN_GAME_CHEAT_RECEIVED_GIVE)
                            .placeholder("player", player.getName())
                            .placeholder("amount", amount)
                            .placeholder("resource", spawnerType.getItemName())
                            .defaultPrefix()
                            .send(player);
                })
        );

        manager.command(
                commandSenderWrapperBuilder
                        .literal("kill")
                        .argument(manager
                                .argumentBuilder(String.class, "player")
                                .withSuggestionsProvider((c, s) ->
                                        PlayerMapper.getPlayers().stream().map(PlayerWrapper::getName).collect(Collectors.toList())
                                )
                                .asOptional())
                        .handler(commandContext -> {
                            var player = commandContext.getSender().as(PlayerWrapper.class);

                            var game = playerManager.getGameOfPlayer(player);
                            if (game.isEmpty()) {
                                player.sendMessage(Message.of(LangKeys.IN_GAME_ERRORS_NOT_IN_ANY_GAME_YET).defaultPrefix());
                                return;
                            }
                            if (game.get().getStatus() != GameStatus.RUNNING) {
                                player.sendMessage(Message.of(LangKeys.IN_GAME_CHEAT_GAME_NOT_RUNNING).defaultPrefix());
                                return;
                            }

                            var receiver = commandContext.<String>getOptional("player");
                            BedWarsPlayer bwPlayer;
                            if (receiver.isPresent()) {
                                var playerWrapper = receiver.flatMap(PlayerMapper::getPlayer);
                                if (playerWrapper.isEmpty() || !playerManager.isPlayerInGame(playerWrapper.get())) {
                                    player.sendMessage(Message.of(LangKeys.IN_GAME_CHEAT_INVALID_PLAYER));
                                    return;
                                }
                                bwPlayer = playerManager.getPlayer(playerWrapper.get()).orElseThrow();
                            } else {
                                bwPlayer = playerManager.getPlayer(player).orElseThrow();
                            }

                            if (bwPlayer.isSpectator) {
                                player.sendMessage(Message.of(LangKeys.IN_GAME_CHEAT_INVALID_PLAYER));
                                return;
                            }
                            bwPlayer.as(EntityHuman.class).setHealth(0);
                            Message.of(LangKeys.IN_GAME_CHEAT_RECEIVED_KILL)
                                    .placeholder("player", player.getName())
                                    .defaultPrefix()
                                    .send(player);
                        })
        );
    }
}
