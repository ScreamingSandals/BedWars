package org.screamingsandals.bedwars.api.config;

import org.jetbrains.annotations.ApiStatus;

/**
 * Saves and retrieves configuration object
 *
 * @param <T> type of saved object
 * @author ScreamingSandals
 * @since 0.3.0
 */
@ApiStatus.NonExtendable
public interface Configuration<T> {
    /**
     * Gets current value, can be inherited from another configuration
     *
     * @return Current value
     */
    T get();

    /**
     * Check if current configuration object contains custom value
     *
     * @return true if value is set
     */
    boolean isSet();

    /**
     * Sets new value for that configuration
     *
     * @param value Value you wish to save
     */
    void set(T value);

    /**
     * Resets current value of this configuration
     */
    void clear();
}
