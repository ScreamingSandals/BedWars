package org.screamingsandals.bedwars.commands;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.BooleanArgument;
import cloud.commandframework.arguments.standard.EnumArgument;
import cloud.commandframework.arguments.standard.StringArgument;
import org.screamingsandals.bedwars.game.GameManagerImpl;
import org.screamingsandals.bedwars.inventories.GamesInventory;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.lobby.BedWarsNPC;
import org.screamingsandals.bedwars.lobby.NPCManager;
import org.screamingsandals.bedwars.utils.SerializableLocation;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.player.PlayerWrapper;
import org.screamingsandals.lib.sender.CommandSenderWrapper;
import org.screamingsandals.lib.utils.annotations.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NPCCommand extends BaseCommand {
    private final NPCManager lobbyNPCManager;

    public static final Map<UUID, BedWarsNPC> NPCS_IN_HAND = new ConcurrentHashMap<>();
    public static final List<UUID> SELECTING_NPC = Collections.synchronizedList(new ArrayList<>());

    public NPCCommand(NPCManager lobbyNPCManager) {
        super("npc", BedWarsPermission.ADMIN_PERMISSION, false);
        this.lobbyNPCManager = lobbyNPCManager;
    }

    @Override
    protected void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder, CommandManager<CommandSenderWrapper> manager) {
        manager.command(
                commandSenderWrapperBuilder
                        .literal("add")
                        .handler(commandContext -> {
                            var player = commandContext.getSender().as(PlayerWrapper.class);
                            var loc = player.getLocation();

                            var npc = new BedWarsNPC();
                            npc.setLocation(new SerializableLocation(loc));
                            npc.spawn();
                            lobbyNPCManager.setModified(true);
                            lobbyNPCManager.getNpcs().add(npc);
                            NPCS_IN_HAND.put(player.getUuid(), npc);
                            player.sendMessage(Message.of(LangKeys.ADMIN_NPC_ADDED)
                                    .defaultPrefix()
                                    .placeholder("x", loc.getX(), 2)
                                    .placeholder("y", loc.getY(), 2)
                                    .placeholder("z", loc.getZ(), 2)
                                    .placeholder("yaw", loc.getYaw(), 5)
                                    .placeholder("pitch", loc.getPitch(), 5));
                        })
        );

        manager.command(
                commandSenderWrapperBuilder
                        .literal("select")
                        .handler(commandContext -> {
                            var player = commandContext.getSender().as(PlayerWrapper.class);
                            player.sendMessage(Message.of(LangKeys.ADMIN_NPC_NOW_CLICK).defaultPrefix());
                            NPCS_IN_HAND.remove(player.getUuid());
                            SELECTING_NPC.add(player.getUuid());
                        })
        );

        manager.command(
                commandSenderWrapperBuilder
                        .literal("quit")
                        .handler(commandContext -> {
                            var player = commandContext.getSender().as(PlayerWrapper.class);
                            if (!NPCS_IN_HAND.containsKey(player.getUuid())) {
                                player.sendMessage(Message.of(LangKeys.ADMIN_NPC_NOT_EDITING).defaultPrefix());
                                return;
                            }
                            NPCS_IN_HAND.remove(player.getUuid());
                            player.sendMessage(Message.of(LangKeys.ADMIN_NPC_NO_LONGER_EDITING).defaultPrefix());
                        })
        );

        manager.command(
                commandSenderWrapperBuilder
                        .literal("shouldLookAtPlayer")
                        .argument(BooleanArgument.of("shouldLookAtPlayer"))
                        .handler(commandContext -> {
                            var player = commandContext.getSender().as(PlayerWrapper.class);
                            boolean shouldLookAtPlayer = commandContext.get("shouldLookAtPlayer");
                            if (!NPCS_IN_HAND.containsKey(player.getUuid())) {
                                player.sendMessage(Message.of(LangKeys.ADMIN_NPC_NOT_EDITING).defaultPrefix());
                                return;
                            }

                            var npc = NPCS_IN_HAND.get(player.getUuid());
                            npc.setShouldLookAtPlayer(shouldLookAtPlayer);
                            npc.getNpc().setShouldLookAtPlayer(shouldLookAtPlayer);
                            lobbyNPCManager.setModified(true);

                            if (shouldLookAtPlayer) {
                                player.sendMessage(Message.of(LangKeys.ADMIN_NPC_SHOULD_LOOK).defaultPrefix());
                            } else {
                                player.sendMessage(Message.of(LangKeys.ADMIN_NPC_SHOULD_NOT_LOOK).defaultPrefix());
                            }
                        })
        );

        manager.command(
                commandSenderWrapperBuilder
                        .literal("remove")
                        .handler(commandContext -> {
                            var player = commandContext.getSender().as(PlayerWrapper.class);
                            if (!NPCS_IN_HAND.containsKey(player.getUuid())) {
                                player.sendMessage(Message.of(LangKeys.ADMIN_NPC_NOT_EDITING).defaultPrefix());
                                return;
                            }

                            var npc = NPCS_IN_HAND.get(player.getUuid());
                            NPCS_IN_HAND.remove(player.getUuid());
                            npc.destroy();
                            lobbyNPCManager.setModified(true);
                            lobbyNPCManager.getNpcs().remove(npc);
                            player.sendMessage(Message.of(LangKeys.ADMIN_NPC_REMOVED).defaultPrefix());
                        })
        );

        manager.command(
                commandSenderWrapperBuilder
                        .literal("action")
                        .argument(EnumArgument.of(BedWarsNPC.Action.class, "action"))
                        .argument(StringArgument
                                .<CommandSenderWrapper>newBuilder("value")
                                .greedy()
                                .withSuggestionsProvider((c, s) -> {
                                    switch (c.<BedWarsNPC.Action>get("action")) {
                                        case JOIN_GAME:
                                            return GameManagerImpl.getInstance().getGameNames();
                                        case OPEN_GAMES_INVENTORY:
                                            return GamesInventory.getInstance().getInventoriesNames();
                                        default:
                                            return List.of();
                                    }
                                })
                                .asOptional()
                        )
                        .handler(commandContext -> {
                            var player = commandContext.getSender().as(PlayerWrapper.class);
                            if (!NPCS_IN_HAND.containsKey(player.getUuid())) {
                                player.sendMessage(Message.of(LangKeys.ADMIN_NPC_NOT_EDITING).defaultPrefix());
                                return;
                            }
                            BedWarsNPC.Action action = commandContext.get("action");
                            String value = commandContext.getOrDefault("value", "");
                            if (value.isEmpty() && action.requireArguments()) {
                                player.sendMessage(Message.of(LangKeys.ADMIN_NPC_REQUIRES_VALUE).defaultPrefix());
                                return;
                            }

                            var npc = NPCS_IN_HAND.get(player.getUuid());
                            npc.setAction(action);
                            npc.setValue(value);
                            lobbyNPCManager.setModified(true);

                            player.sendMessage(Message.of(LangKeys.ADMIN_NPC_ACTION_SET).defaultPrefix()
                                    .placeholder("action", action.name())
                                    .placeholder("value", value));
                        })
        );
    }
}
