package org.screamingsandals.bedwars.player;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.screamingsandals.lib.item.Item;
import org.screamingsandals.lib.item.meta.PotionEffectHolder;
import org.screamingsandals.lib.player.gamemode.GameModeHolder;
import org.screamingsandals.lib.world.LocationHolder;

import java.util.Collection;

@Getter
@Setter
public class StoredInventory {
    private Item[] armor;
    private Component displayName;
    private Collection<PotionEffectHolder> effects;
    private int foodLevel = 0;
    private Item[] inventory;
    private LocationHolder leftLocation;
    private int level = 0;
    private Component listName;
    private GameModeHolder mode;
    private float xp;
}
