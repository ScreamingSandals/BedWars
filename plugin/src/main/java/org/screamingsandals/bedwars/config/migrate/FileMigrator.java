package org.screamingsandals.bedwars.config.migrate;

import java.io.File;
import java.util.concurrent.CompletableFuture;

public interface FileMigrator {
    void migrate(File file) throws Exception;

    default CompletableFuture<Void> migrateAsynchronously(File file) {
        return CompletableFuture.runAsync(() -> {
            try {
                migrate(file);
            } catch (Exception e) {
                throw new RuntimeException("Could not migrate file!", e);
            }
        });
    }
}
