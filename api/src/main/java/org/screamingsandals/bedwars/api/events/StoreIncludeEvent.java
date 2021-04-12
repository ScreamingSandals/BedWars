package org.screamingsandals.bedwars.api.events;

import java.nio.file.Path;

public interface StoreIncludeEvent extends BWCancellable {
    String getName();

    Path getPath();

    boolean isUseParent();

    // CategoryBuilder getCategoryBuilder() - just in class form, not interface
}
