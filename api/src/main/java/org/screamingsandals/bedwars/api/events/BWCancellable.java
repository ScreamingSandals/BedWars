package org.screamingsandals.bedwars.api.events;

public interface BWCancellable {
    boolean isCancelled();

    void setCancelled(boolean cancelled);
}
