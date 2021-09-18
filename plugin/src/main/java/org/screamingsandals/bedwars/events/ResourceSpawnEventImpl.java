package org.screamingsandals.bedwars.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;
import org.screamingsandals.bedwars.api.events.ResourceSpawnEvent;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.game.ItemSpawnerImpl;
import org.screamingsandals.bedwars.game.ItemSpawnerTypeImpl;
import org.screamingsandals.lib.event.CancellableAbstractEvent;
import org.screamingsandals.lib.item.Item;
import org.screamingsandals.lib.item.builder.ItemFactory;
import org.screamingsandals.lib.world.LocationHolder;

@EqualsAndHashCode(callSuper = true)
@Data
public class ResourceSpawnEventImpl extends CancellableAbstractEvent implements ResourceSpawnEvent<GameImpl, ItemSpawnerImpl, ItemSpawnerTypeImpl, Item, LocationHolder> {
    private final GameImpl game;
    private final ItemSpawnerImpl itemSpawner;
    private final ItemSpawnerTypeImpl type;
    @NotNull
    private Item resource;

    @Override
    public LocationHolder getLocation() {
        return itemSpawner.getLocation();
    }

    @Override
    public void setResource(Object resource) {
        if (resource instanceof Item) {
            this.resource = (Item) resource;
        } else {
            this.resource = ItemFactory.build(resource).orElseThrow();
        }
    }
}
