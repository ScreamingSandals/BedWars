package org.screamingsandals.bedwars.utils;

import lombok.experimental.UtilityClass;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.SpawnReason;
import net.citizensnpcs.api.npc.MemoryNPCDataStore;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.screamingsandals.bedwars.game.GameStore;

@UtilityClass
public class CitizensUtils {
    public LivingEntity spawnNPC(GameStore store) {
        if (Bukkit.getPluginManager().isPluginEnabled("Citizens")) {
            try {
                NPCRegistry npcRegistry = CitizensAPI.getNamedNPCRegistry(CitizensUtils.class.getName());
                if (npcRegistry == null) {
                    npcRegistry = CitizensAPI.createNamedNPCRegistry(CitizensUtils.class.getName(), new MemoryNPCDataStore());
                }
                NPC npc = npcRegistry.createNPC(EntityType.PLAYER, store.getShopCustomName());
                npc.data().set("bedwarsStore", store);
                if (store.getSkinName() != null) {
                    npc.addTrait(SkinTrait.class);
                    npc.getTraitNullable(SkinTrait.class).setSkinName(store.getSkinName());
                }
                npc.spawn(store.getStoreLocation(), SpawnReason.PLUGIN);
                return (LivingEntity) npc.getEntity();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public GameStore getFromNPC(Entity entity) {
        if (entity.hasMetadata("NPC") && Bukkit.getPluginManager().isPluginEnabled("Citizens")) {
            try {
                NPCRegistry npcRegistry = CitizensAPI.getNamedNPCRegistry(CitizensUtils.class.getName());
                if (npcRegistry != null) {
                    NPC npc = npcRegistry.getNPC(entity);
                    if (npc != null) {
                        Object object = npc.data().get("bedwarsStore");
                        if (object instanceof GameStore) {
                            return (GameStore) object;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void remove(Entity entity) {
        if (entity.hasMetadata("NPC") && Bukkit.getPluginManager().isPluginEnabled("Citizens")) {
            try {
                NPCRegistry npcRegistry = CitizensAPI.getNamedNPCRegistry(CitizensUtils.class.getName());
                if (npcRegistry != null) {
                    NPC npc = npcRegistry.getNPC(entity);
                    if (npc != null) {
                        npc.destroy();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
