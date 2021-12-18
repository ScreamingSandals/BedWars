package org.screamingsandals.bedwars.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.screamingsandals.bedwars.api.events.ApplyPropertyToItemEvent;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.lib.event.SEvent;
import org.screamingsandals.lib.item.Item;
import org.screamingsandals.lib.item.builder.ItemFactory;

import java.util.Map;

@Data
@AllArgsConstructor
public class ApplyPropertyToItemEventImpl implements ApplyPropertyToItemEvent<GameImpl, BedWarsPlayer, Item>, SEvent {
    private final GameImpl game;
    private final BedWarsPlayer player;
    private final String propertyName;
    private final Map<String, Object> properties;
    private Item stack;

    @Override
    public void setStack(Object stack) {
        if (stack instanceof Item) {
            this.stack = (Item) stack;
        } else {
            this.stack = ItemFactory.build(stack).orElseThrow();
        }
    }
}
