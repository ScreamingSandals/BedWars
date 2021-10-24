package org.screamingsandals.bedwars.lobby;

import lombok.RequiredArgsConstructor;
import org.screamingsandals.bedwars.inventories.GamesInventory;
import org.screamingsandals.lib.event.OnEvent;
import org.screamingsandals.lib.event.player.SPlayerJoinEvent;
import org.screamingsandals.lib.event.player.SPlayerLeaveEvent;
import org.screamingsandals.lib.npc.NPCManager;
import org.screamingsandals.lib.npc.event.NPCInteractEvent;
import org.screamingsandals.lib.plugin.ServiceManager;
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
        NPCManager.class
})
@RequiredArgsConstructor
public class LobbyNPCManager {
    @ConfigFile(value = "database/npcdb.json")
    private final GsonConfigurationLoader loader;
    private final GamesInventory gamesInventory;

    private final List<LobbyNPC> npcs = new ArrayList<>();

    public static LobbyNPCManager getInstance() {
        return ServiceManager.get(LobbyNPCManager.class);
    }

    @OnPostEnable
    public void onPostEnable() {
        try {
            var node = loader.load();

            node.childrenList().forEach(npcNode -> {
                try {
                    var npc = npcNode.get(LobbyNPC.class);
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
        if (npcs.isEmpty()) {
            return;
        }

        var node = loader.createNode();

        npcs.forEach(lobbyNPC -> {
            try {
                lobbyNPC.destroy();
                node.appendListNode().set(lobbyNPC);
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
        npcs.forEach(lobbyNPC -> {
            var npc = lobbyNPC.getNpc();
            if (npc != null) {
                npc.addViewer(event.getPlayer());
            }
        });
    }

    @OnEvent
    public void onPlayerLeave(SPlayerLeaveEvent event) {
        npcs.forEach(lobbyNPC -> {
            var npc = lobbyNPC.getNpc();
            if (npc != null) {
                npc.removeViewer(event.getPlayer());
            }
        });
    }

    @OnEvent
    public void onNPCInteract(NPCInteractEvent event) {
        npcs.stream()
                .filter(lobbyNPC1 -> lobbyNPC1.getNpc().equals(event.getVisual()))
                .findFirst()
                .ifPresent(npc -> npc.handleClick(event.getPlayer(), event.getInteractType()));
    }
}
