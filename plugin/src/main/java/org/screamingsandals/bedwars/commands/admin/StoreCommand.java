package org.screamingsandals.bedwars.commands.admin;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.BooleanArgument;
import cloud.commandframework.arguments.standard.StringArgument;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.game.GameStore;
import org.screamingsandals.bedwars.utils.ArenaUtils;
import org.screamingsandals.lib.sender.CommandSenderWrapper;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.screamingsandals.bedwars.lib.lang.I.i18n;
import static org.screamingsandals.bedwars.lib.lang.I.i18nonly;

public class StoreCommand extends BaseAdminSubCommand {
    public StoreCommand(CommandManager<CommandSenderWrapper> manager, Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        super(manager, commandSenderWrapperBuilder, "store");
    }

    @Override
    public void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        manager.command(
                commandSenderWrapperBuilder
                        .literal("add")
                        .argument(StringArgument
                                .<CommandSenderWrapper>newBuilder("name")
                                .asOptional()
                                .withSuggestionsProvider((c,s) -> List.of("Villager_shop", "Dealer", "Seller", "&a&lVillager_shop", "&4Barter"))
                        )
                        .argument(StringArgument.optional("file"))
                        .argument(BooleanArgument.optional("useParent", true))
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            var name = commandContext.<String>getOptional("name")
                                    .map(s -> ChatColor.translateAlternateColorCodes('&', s));
                            var file = commandContext.<String>getOptional("file");
                            boolean useParent = commandContext.get("useParent");
                            var loc = sender.as(Player.class).getLocation();

                            if (game.getPos1() == null || game.getPos2() == null) {
                                sender.sendMessage(i18n("admin_command_set_pos1_pos2_first"));
                                return;
                            }
                            if (game.getWorld() != loc.getWorld()) {
                                sender.sendMessage(i18n("admin_command_must_be_in_same_world"));
                                return;
                            }
                            if (!ArenaUtils.isInArea(loc, game.getPos1(), game.getPos2())) {
                                sender.sendMessage(i18n("admin_command_spawn_must_be_in_area"));
                                return;
                            }
                            var store = game.getGameStoreList()
                                    .stream()
                                    .filter(gameStore -> gameStore.getStoreLocation().getBlock().equals(loc.getBlock()))
                                    .findFirst();

                            if (store.isPresent()) {
                                sender.sendMessage(i18n("admin_command_store_already_exists"));
                                return;
                            }
                            game.getGameStoreList().add(new GameStore(loc, file.orElse(null), useParent, name.orElse(null), name.isPresent(), false));
                            sender.sendMessage(
                                    i18n("admin_command_store_added")
                                            .replace("%x%", Double.toString(loc.getX()))
                                            .replace("%y%", Double.toString(loc.getY()))
                                            .replace("%z%", Double.toString(loc.getZ()))
                                            .replace("%yaw%", Float.toString(loc.getYaw()))
                                            .replace("%pitch%", Float.toString(loc.getPitch()))
                            );
                        }))
        );

        manager.command(
                commandSenderWrapperBuilder
                        .literal("remove")
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            var loc = sender.as(Player.class).getLocation();

                            if (game.getWorld() != loc.getWorld()) {
                                sender.sendMessage(i18n("admin_command_must_be_in_same_world"));
                                return;
                            }
                            var store = game.getGameStoreList()
                                    .stream()
                                    .filter(gameStore -> gameStore.getStoreLocation().getBlock().equals(loc.getBlock()))
                                    .findFirst();

                            if (store.isEmpty()) {
                                sender.sendMessage(i18n("admin_command_store_not_exists"));
                                return;
                            }
                            game.getGameStoreList().remove(store.get());
                            sender.sendMessage(
                                    i18n("admin_command_store_removed")
                                            .replace("%x%", Double.toString(loc.getX()))
                                            .replace("%y%", Double.toString(loc.getY()))
                                            .replace("%z%", Double.toString(loc.getZ()))
                                            .replace("%yaw%", Float.toString(loc.getYaw()))
                                            .replace("%pitch%", Float.toString(loc.getPitch()))
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
                                        i18n("admin_command_store_child_state")
                                                .replace("%value%", i18nonly("arena_info_config_true"))
                                );
                                return;
                            }

                            sender.sendMessage(i18n("admin_command_store_not_exists"));
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
                                        i18n("admin_command_store_child_state")
                                                .replace("%value%", i18nonly("arena_info_config_false"))
                                );
                                return;
                            }

                            sender.sendMessage(i18n("admin_command_store_not_exists"));
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
                                        sender.sendMessage(i18n("admin_command_npc_must_have_skinname"));
                                        return;
                                    }

                                    store.get().setEntityTypeNPC(splitted[1]);
                                    sender.sendMessage(i18n("admin_command_store_living_entity_type_set").replace("%type%", splitted[1]));
                                    return;
                                }

                                if (t == null) {
                                    sender.sendMessage(i18n("admin_command_wrong_living_entity_type"));
                                    return;
                                }

                                store.get().setEntityType(t);

                                sender.sendMessage(i18n("admin_command_store_living_entity_type_set").replace("%type%", t.toString()));
                                return;
                            }

                            sender.sendMessage(i18n("admin_command_store_not_exists"));
                        }))
        );
    }
}
