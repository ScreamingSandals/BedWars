package org.screamingsandals.bedwars.lobby;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.screamingsandals.bedwars.commands.NPCCommand;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.lib.Server;
import org.screamingsandals.lib.event.OnEvent;
import org.screamingsandals.lib.event.player.SPlayerJoinEvent;
import org.screamingsandals.lib.event.player.SPlayerLeaveEvent;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.npc.event.NPCInteractEvent;
import org.screamingsandals.lib.plugin.ServiceManager;
import org.screamingsandals.lib.tasker.Tasker;
import org.screamingsandals.lib.tasker.TaskerTime;
import org.screamingsandals.lib.utils.InteractType;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.utils.annotations.methods.OnPostEnable;
import org.screamingsandals.lib.utils.annotations.methods.OnPreDisable;
import org.screamingsandals.lib.utils.annotations.parameters.ConfigFile;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.gson.GsonConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.ArrayList;
import java.util.List;

@Service(dependsOn = {
        org.screamingsandals.lib.npc.NPCManager.class
})
@RequiredArgsConstructor
public class NPCManager {
    @ConfigFile(value = "database/npcdb.json")
    private final GsonConfigurationLoader loader;
    @Getter
    @Setter
    private boolean modified;

    @Getter
    private final List<BedWarsNPC> npcs = new ArrayList<>();

    public static NPCManager getInstance() {
        return ServiceManager.get(NPCManager.class);
    }

    @OnPostEnable
    public void onPostEnable() {
        try {
            var node = loader.load();

            node.childrenList().forEach(npcNode -> {
                try {
                    var npc = npcNode.get(BedWarsNPC.class);
                    if (npc == null) {
                        return;
                    }
                    npc.spawn();
                    npcs.add(npc);
                } catch (ConfigurateException e) {
                    e.printStackTrace();
                }
            });
        } catch (ConfigurateException e) {
            e.printStackTrace();
        }

    }

    @OnPreDisable
    public void onPreDisable() {
        if (npcs.isEmpty() || !modified) {
            return;
        }

        var node = loader.createNode();

        npcs.forEach(bedWarsNPC -> {
            try {
                bedWarsNPC.destroy();
                node.appendListNode().set(bedWarsNPC);
            } catch (SerializationException e) {
                e.printStackTrace();
            }
        });

        try {
            loader.save(node);
        } catch (ConfigurateException e) {
            e.printStackTrace();
        }
    }

    @OnEvent
    public void onPlayerJoin(SPlayerJoinEvent event) {
        npcs.forEach(bedWarsNPC -> {
            var npc = bedWarsNPC.getNpc();
            if (npc != null) {
                Tasker.build(() -> {
                            if (event.getPlayer().isOnline()) {
                                npc.addViewer(event.getPlayer());
                            }
                        })
                        .delay(10, TaskerTime.TICKS)
                        .start();
            }
        });
    }

    @OnEvent
    public void onPlayerLeave(SPlayerLeaveEvent event) {
        npcs.forEach(bedWarsNPC -> {
            var npc = bedWarsNPC.getNpc();
            if (npc != null) {
                npc.removeViewer(event.getPlayer());
            }
        });
    }

    @OnEvent
    public void onNPCInteract(NPCInteractEvent event) {
        npcs.stream()
                .filter(bedWarsNPC1 -> bedWarsNPC1.getNpc().equals(event.getVisual()))
                .findFirst()
                .ifPresent(npc -> {
                    if (event.getInteractType() == InteractType.RIGHT_CLICK && NPCCommand.SELECTING_NPC.contains(event.getPlayer().getUuid())) {
                        NPCCommand.SELECTING_NPC.remove(event.getPlayer().getUuid());
                        NPCCommand.NPCS_IN_HAND.put(event.getPlayer().getUuid(), npc);
                        event.getPlayer().sendMessage(Message.of(LangKeys.ADMIN_NPC_EDITING)
                                .defaultPrefix()
                                .placeholder("x", npc.getLocation().getX(), 2)
                                .placeholder("y", npc.getLocation().getY(), 2)
                                .placeholder("z", npc.getLocation().getZ(), 2)
                                .placeholder("yaw", npc.getLocation().getYaw(), 5)
                                .placeholder("pitch", npc.getLocation().getPitch(), 5));
                    } else {
                        npc.handleClick(event.getPlayer(), event.getInteractType());
                    }
                });
    }
}
