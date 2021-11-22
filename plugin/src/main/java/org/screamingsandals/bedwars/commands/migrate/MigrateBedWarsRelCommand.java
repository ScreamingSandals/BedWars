package org.screamingsandals.bedwars.commands.migrate;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import lombok.RequiredArgsConstructor;
import org.screamingsandals.bedwars.config.migrate.FileMigrator;
import org.screamingsandals.bedwars.config.migrate.bwrel.BedWarsRelArenaMigrator;
import org.screamingsandals.bedwars.config.migrate.bwrel.BedWarsRelConfigurationMigrator;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.sender.CommandSenderWrapper;
import org.screamingsandals.lib.utils.annotations.Service;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Service(dependsOn = {
        BedWarsRelConfigurationMigrator.class,
        BedWarsRelArenaMigrator.class
})
@RequiredArgsConstructor
public class MigrateBedWarsRelCommand extends MigrateCommand {
    private final FileMigrator configurationMigrator;
    private final FileMigrator arenaMigrator;

    @Override
    protected void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder, CommandManager<CommandSenderWrapper> manager) {
        manager.command(
                commandSenderWrapperBuilder
                        .literal("BedWarsRel", "bwrel", "BWRel", "bedwarsrel")
                        .handler(commandContext -> {
                            final CompletableFuture<?>[] arenaMigrators = Arrays.stream(
                                            Objects.requireNonNull(
                                                    Paths.get(MiscUtils.getPluginsFolder("BedwarsRel").toString(), "arenas").toFile().listFiles(File::isDirectory),
                                                    "'plugins/BedwarsRel/arenas' must be a directory!"
                                            )
                                    )
                                    .filter(File::isDirectory)
                                    .map(arenaMigrator::migrateAsynchronously)
                                    .toArray(CompletableFuture[]::new);
                            CompletableFuture.allOf(
                                    configurationMigrator.migrateAsynchronously(
                                            Paths.get(MiscUtils.getPluginsFolder("BedwarsRel").toString(), "config.yml").toFile()
                                    ),
                                    CompletableFuture.allOf(arenaMigrators)
                            )
                            .whenComplete((result, ex) -> {
                                if (ex != null) {
                                    ex.printStackTrace();
                                    Message.of(LangKeys.MIGRATE_FAILURE)
                                            .send(commandContext.getSender());
                                }
                            })
                            .thenRun(() -> Message.of(LangKeys.MIGRATE_SUCCESS)
                                    .placeholder("plugin", "BedWarsRel")
                                    .send(commandContext.getSender())
                            );
                        })
        );
    }
}
