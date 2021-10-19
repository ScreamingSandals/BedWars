package org.screamingsandals.bedwars.lobby;

import lombok.RequiredArgsConstructor;
import org.screamingsandals.lib.event.OnEvent;
import org.screamingsandals.lib.event.player.SPlayerJoinEvent;
import org.screamingsandals.lib.event.player.SPlayerLeaveEvent;
import org.screamingsandals.lib.npc.NPCManager;
import org.screamingsandals.lib.npc.event.NPCInteractEvent;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.utils.annotations.methods.OnPostEnable;
import org.screamingsandals.lib.utils.annotations.methods.OnPreDisable;
import org.screamingsandals.lib.utils.annotations.parameters.ConfigFile;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.util.ArrayList;
import java.util.List;

@Service(dependsOn = {
        NPCManager.class
})
@RequiredArgsConstructor
public class LobbyNPCManager {
    @ConfigFile(value = "database/npcdb.yml")
    private final YamlConfigurationLoader loader;

    private final List<LobbyNPC> npcs = new ArrayList<>();

    @OnPostEnable
    public void onPostEnable() {

    }

    @OnPreDisable
    public void onPreDisable() {

    }

    @OnEvent
    public void onPlayerJoin(SPlayerJoinEvent event) {

    }

    @OnEvent
    public void onPlayerLeave(SPlayerLeaveEvent event) {

    }

    @OnEvent
    public void onNPCInteract(NPCInteractEvent event) {

    }
}
