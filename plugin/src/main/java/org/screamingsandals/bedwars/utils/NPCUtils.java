package org.screamingsandals.bedwars.utils;

import lombok.experimental.UtilityClass;
import org.screamingsandals.bedwars.game.GameStore;
import org.screamingsandals.lib.npc.NPC;
import org.screamingsandals.lib.npc.NPCManager;
import org.screamingsandals.lib.npc.NPCSkin;
import org.screamingsandals.lib.utils.AdventureHelper;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.world.LocationMapper;
import org.spongepowered.configurate.gson.GsonConfigurationLoader;

import java.net.URL;
import java.util.List;

@Service(dependsOn = {
        NPCManager.class
})
@UtilityClass
public class NPCUtils {
    public NPC spawnNPC(GameStore store) {
        var npc = NPCManager
                .npc(LocationMapper.resolve(store.getStoreLocation()).orElseThrow())
                .setTouchable(true)
                .setShouldLookAtViewer(true)
                .setDisplayName(List.of(AdventureHelper.toComponentNullable(store.getShopCustomName())));

        var skin = retrieveSkin(store.getSkinName());
        if (skin != null) {
            npc.setSkin(skin);
        }

        return npc.show();
    }

    public NPCSkin retrieveSkin(String skinName) {
        try {
            var node = GsonConfigurationLoader.builder()
                    .url(new URL("https://api.mojang.com/users/profiles/minecraft/" + skinName))
                    .build()
                    .load();

            var userUUID = node.node("id").getString();

            var node2 = GsonConfigurationLoader.builder()
                    .url(new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + userUUID + "?unsigned=false"))
                    .build()
                    .load();

            var skinNode = node2.node("properties").childrenList()
                    .stream()
                    .filter(property ->
                            property.node("name").getString("").equals("textures")
                    )
                    .findFirst()
                    .orElseThrow();

            return new NPCSkin(skinNode.node("value").getString(), skinNode.node("signature").getString());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;
    }
}
