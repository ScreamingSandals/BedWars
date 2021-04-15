package org.screamingsandals.bedwars.api.events;

import org.screamingsandals.bedwars.api.BedwarsAPI;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.player.BWPlayer;
import org.screamingsandals.bedwars.api.utils.EventUtils;
import org.screamingsandals.lib.utils.Wrapper;

import java.util.Map;
import java.util.function.Consumer;

public interface ApplyPropertyToItemEvent<G extends Game, P extends BWPlayer, I extends Wrapper> {
    G getGame();

    P getPlayer();

    I getStack();

    String getPropertyName();

    // TODO - Special wrapper for ConfigurationNodes
    Map<String, Object> getProperties();

    /**
     *
     * @param stack wrapper or platform item
     */
    void setStack(Object stack);

    @SuppressWarnings("unchecked")
    static void handle(Object plugin, Consumer<ApplyPropertyToItemEvent<Game, BWPlayer, Wrapper>> consumer) {
        BedwarsAPI.getInstance().getEventUtils().handle(plugin, ApplyPropertyToItemEvent.class, (Consumer) consumer);
    }

    default Object getProperty(String key) {
        return getProperties().get(key);
    }

    default boolean hasProperty(String key) {
        return getProperties().containsKey(key);
    }

    default String getStringProperty(String key) {
        return getProperties().get(key).toString();
    }

    default byte getByteProperty(String key) {
        return ((Number) getProperties().get(key)).byteValue();
    }

    default short getShortProperty(String key) {
        return ((Number) getProperties().get(key)).shortValue();
    }

    default int getIntProperty(String key) {
        return ((Number) getProperties().get(key)).intValue();
    }

    default long getLongProperty(String key) {
        return ((Number) getProperties().get(key)).longValue();
    }

    default float getFloatProperty(String key) {
        return ((Number) getProperties().get(key)).floatValue();
    }

    default double getDoubleProperty(String key) {
        return ((Number) getProperties().get(key)).doubleValue();
    }

    default boolean getBooleanProperty(String key) {
        return Boolean.parseBoolean(getProperties().get(key).toString());
    }

    default char getCharProperty(String key) {
        return (char) getProperties().get(key);
    }
}
