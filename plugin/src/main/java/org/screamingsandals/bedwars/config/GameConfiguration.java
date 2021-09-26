package org.screamingsandals.bedwars.config;

import lombok.AllArgsConstructor;
import org.screamingsandals.bedwars.api.config.Configuration;

@AllArgsConstructor
public class GameConfiguration<T> implements Configuration<T> {
    private final Class<T> type;
    private final GameConfigurationContainer configurationContainer;
    private final String key;
    private final T implicitValue;

    @Override
    public T get() {
        return !isSet() ? implicitValue : configurationContainer.getSaved(key, type);
    }

    @Override
    public boolean isSet() {
        return configurationContainer.has(key);
    }

    @Override
    public void set(T value) {
        configurationContainer.update(key, value);
    }

    @Override
    public void clear() {
        configurationContainer.remove(key);
    }
}
