/*
 * Copyright (C) 2023 ScreamingSandals
 *
 * This file is part of Screaming BedWars.
 *
 * Screaming BedWars is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Screaming BedWars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Screaming BedWars. If not, see <https://www.gnu.org/licenses/>.
 */

package org.screamingsandals.bedwars.utils;

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

public class CitizensUtils {
    public static LivingEntity spawnNPC(GameStore store) {
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

    public static GameStore getFromNPC(Entity entity) {
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

    public static void remove(Entity entity) {
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
