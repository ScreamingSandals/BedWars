package org.screamingsandals.bedwars.api.events;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public interface BWCancellable {
    boolean isCancelled();

    void setCancelled(boolean cancelled);
}
