package org.screamingsandals.bedwars.commands.migrate;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import lombok.RequiredArgsConstructor;
import org.screamingsandals.bedwars.config.migrate.FileMigrator;
import org.screamingsandals.bedwars.config.migrate.andrei.BedWars1058ArenaMigrator;
import org.screamingsandals.bedwars.config.migrate.andrei.BedWars1058ConfigurationMigrator;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.sender.CommandSenderWrapper;
import org.screamingsandals.lib.utils.annotations.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

@Service(dependsOn = {
        BedWars1058ConfigurationMigrator.class,
        BedWars1058ArenaMigrator.class
})
@RequiredArgsConstructor
public class MigrateBedWars1058Command extends MigrateCommand {
    private final FileMigrator configurationMigrator;
    private final FileMigrator arenaMigrator;

    @Override
    protected void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder, CommandManager<CommandSenderWrapper> manager) {
        manager.command(
                commandSenderWrapperBuilder
                        .literal("bw1058", "bedwars1058", "BedWars1058", "1058", "andrei1058")
                        .handler(commandContext -> {
                            CompletableFuture<?>[] arenaMigrators;
                            try {
                                arenaMigrators = Files.walk(Paths.get(MiscUtils.getPluginsFolder("BedWars1058").toString(), "Arenas"))
                                        .map(Path::toFile)
                                        .filter(File::isFile)
                                        .map(arenaMigrator::migrateAsynchronously)
                                        .toArray(CompletableFuture[]::new);
                            } catch (IOException e) {
                                e.printStackTrace();
                                Message.of(LangKeys.MIGRATE_FAILURE)
                                        .send(commandContext.getSender());
                                return;
                            }
                            CompletableFuture.allOf(
                                    configurationMigrator.migrateAsynchronously(
                                            Paths.get(MiscUtils.getPluginsFolder("BedWars1058").toString(), "config.yml").toFile()
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
                                    .placeholder("plugin", "BedWars1058")
                                    .send(commandContext.getSender())
                            );
                        })
        );
    }
}
