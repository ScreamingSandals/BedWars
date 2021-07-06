package org.screamingsandals.bedwars.utils;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;

public class SpawnEffectsFlattening {
    public static Object convert(Particle particle, Object object) {
        if (particle.getDataType().equals(BlockData.class)) {
            return Bukkit.createBlockData(object.toString());
        }
        return object;
    }
}
