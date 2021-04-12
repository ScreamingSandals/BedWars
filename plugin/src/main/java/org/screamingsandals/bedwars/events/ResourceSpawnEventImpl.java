package org.screamingsandals.bedwars.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;
import org.screamingsandals.bedwars.api.events.ResourceSpawnEvent;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.bedwars.game.ItemSpawner;
import org.screamingsandals.bedwars.game.ItemSpawnerType;
import org.screamingsandals.lib.event.CancellableAbstractEvent;
import org.screamingsandals.lib.material.Item;
import org.screamingsandals.lib.material.builder.ItemFactory;
import org.screamingsandals.lib.world.LocationHolder;
import org.screamingsandals.lib.world.LocationMapper;

@EqualsAndHashCode(callSuper = true)
@Data
public class ResourceSpawnEventImpl extends CancellableAbstractEvent implements ResourceSpawnEvent<Game, ItemSpawner, ItemSpawnerType, Item, LocationHolder> {
    private final Game game;
    private final ItemSpawner itemSpawner;
    private final ItemSpawnerType type;
    @NotNull
    private Item resource;

    @Override
    public LocationHolder getLocation() {
        return LocationMapper.wrapLocation(itemSpawner.getLocation()); // TODO
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
