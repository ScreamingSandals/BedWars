package org.screamingsandals.bedwars.commands.admin;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.BooleanArgument;
import cloud.commandframework.arguments.standard.StringArgument;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.game.GameStore;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.utils.ArenaUtils;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.sender.CommandSenderWrapper;
import org.screamingsandals.lib.utils.annotations.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StoreCommand extends BaseAdminSubCommand {
    public StoreCommand() {
        super("store");
    }

    @Override
    public void construct(CommandManager<CommandSenderWrapper> manager, Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        manager.command(
                commandSenderWrapperBuilder
                        .literal("add")
                        .argument(StringArgument
                                .<CommandSenderWrapper>newBuilder("name")
                                .asOptional()
                                .withSuggestionsProvider((c,s) -> List.of("Villager_shop", "Dealer", "Seller", "&a&lVillager_shop", "&4Barter"))
                        )
                        .argument(StringArgument.optional("file"))
                        .argument(BooleanArgument.optional("useParent"))
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            var name = commandContext.<String>getOptional("name")
                                    .map(s -> ChatColor.translateAlternateColorCodes('&', s));
                            var file = commandContext.<String>getOptional("file");
                            boolean useParent = commandContext.getOrDefault("useParent", true);
                            var loc = sender.as(Player.class).getLocation();

                            if (game.getPos1() == null || game.getPos2() == null) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_SET_BOUNDS_FIRST).defaultPrefix());
                                return;
                            }
                            if (game.getWorld() != loc.getWorld()) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_MUST_BE_IN_SAME_WORLD).defaultPrefix());
                                return;
                            }
                            if (!ArenaUtils.isInArea(loc, game.getPos1(), game.getPos2())) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_MUST_BE_IN_BOUNDS).defaultPrefix());
                                return;
                            }
                            var store = game.getGameStoreList()
                                    .stream()
                                    .filter(gameStore -> gameStore.getStoreLocation().getBlock().equals(loc.getBlock()))
                                    .findFirst();

                            if (store.isPresent()) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_STORE_ALREADY_EXIST).defaultPrefix());
                                return;
                            }
                            game.getGameStoreList().add(new GameStore(loc, file.orElse(null), useParent, name.orElse(null), name.isPresent(), false));
                            sender.sendMessage(
                                    Message
                                    .of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_STORE_ADDED)
                                    .defaultPrefix()
                                    .placeholder("x", loc.getX(), 2)
                                    .placeholder("y", loc.getY(), 2)
                                    .placeholder("z", loc.getZ(), 2)
                                    .placeholder("yaw", loc.getYaw(), 5)
                                    .placeholder("pitch", loc.getPitch(), 5)
                            );
                        }))
        );

        manager.command(
                commandSenderWrapperBuilder
                        .literal("remove")
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            var loc = sender.as(Player.class).getLocation();

                            if (game.getWorld() != loc.getWorld()) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_MUST_BE_IN_SAME_WORLD).defaultPrefix());
                                return;
                            }
                            var store = game.getGameStoreList()
                                    .stream()
                                    .filter(gameStore -> gameStore.getStoreLocation().getBlock().equals(loc.getBlock()))
                                    .findFirst();

                            if (store.isEmpty()) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_STORE_DOES_NOT_EXIST).defaultPrefix());
                                return;
                            }
                            game.getGameStoreList().remove(store.get());
                            sender.sendMessage(
                                    Message
                                            .of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_STORE_REMOVED)
                                            .defaultPrefix()
                                            .placeholder("x", loc.getX(), 2)
                                            .placeholder("y", loc.getY(), 2)
                                            .placeholder("z", loc.getZ(), 2)
                                            .placeholder("yaw", loc.getYaw(), 5)
                                            .placeholder("pitch", loc.getPitch(), 5)
                            );
                        }))
        );

        manager.command(
                commandSenderWrapperBuilder
                        .literal("child")
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            var loc = sender.as(Player.class).getLocation();

                            var store = game.getGameStoreList()
                                    .stream()
                                    .filter(gameStore -> gameStore.getStoreLocation().getBlock().equals(loc.getBlock()))
                                    .findFirst();

                            if (store.isPresent()) {
                                store.get().setBaby(true);

                                sender.sendMessage(
                                        Message
                                                .of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_CHILD_STATE)
                                                .defaultPrefix()
                                                .placeholder("value", Message.of(LangKeys.ADMIN_INFO_CONSTANT_TRUE))
                                );
                                return;
                            }

                            sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_STORE_DOES_NOT_EXIST).defaultPrefix());
                        }))
        );

        manager.command(
                commandSenderWrapperBuilder
                        .literal("adult")
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            var loc = sender.as(Player.class).getLocation();

                            var store = game.getGameStoreList()
                                    .stream()
                                    .filter(gameStore -> gameStore.getStoreLocation().getBlock().equals(loc.getBlock()))
                                    .findFirst();

                            if (store.isPresent()) {
                                store.get().setBaby(false);

                                sender.sendMessage(
                                        Message
                                                .of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_CHILD_STATE)
                                                .defaultPrefix()
                                                .placeholder("value", Message.of(LangKeys.ADMIN_INFO_CONSTANT_FALSE))
                                );
                                return;
                            }

                            sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_STORE_DOES_NOT_EXIST).defaultPrefix());
                        }))
        );

        manager.command(
                commandSenderWrapperBuilder
                        .literal("type")
                        .argument(StringArgument
                                .<CommandSenderWrapper>newBuilder("type")
                                .withSuggestionsProvider((c, s) -> Arrays.stream(EntityType.values()).map(EntityType::name).collect(Collectors.toList()))
                        )
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            String type = commandContext.get("type");
                            var loc = sender.as(Player.class).getLocation();

                            var store = game.getGameStoreList()
                                    .stream()
                                    .filter(gameStore -> gameStore.getStoreLocation().getBlock().equals(loc.getBlock()))
                                    .findFirst();

                            if (store.isPresent()) {
                                EntityType t = null;
                                try {
                                    t = EntityType.valueOf(type.split(":", 2)[0].toUpperCase());
                                    if (!t.isAlive()) {
                                        t = null;
                                    }
                                } catch (Exception e) {
                                }

                                if (t == EntityType.PLAYER) {
                                    String[] splitted = type.split(":", 2);
                                    if (splitted.length != 2 || splitted[1].trim().equals("")) {
                                        sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_NPC_MUST_HAVE_SKIN_NAME).defaultPrefix());
                                        return;
                                    }

                                    store.get().setEntityTypeNPC(splitted[1]);
                                    sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_STORE_ENTITY_TYPE_SET).defaultPrefix().placeholder("type", splitted[1]));
                                    return;
                                }

                                if (t == null) {
                                    sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_INVALID_ENTITY_TYPE).defaultPrefix());
                                    return;
                                }

                                store.get().setEntityType(t);

                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_STORE_ENTITY_TYPE_SET).defaultPrefix().placeholder("type", t.toString()));
                                return;
                            }

                            sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_STORE_DOES_NOT_EXIST).defaultPrefix());
                        }))
        );
    }
}
