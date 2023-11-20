/*
 * Copyright (C) 2023 ScreamingSandals
 *
 * This file is part of Screaming BedWars.
 *
 * Screaming BedWars is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Screaming BedWars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Screaming BedWars. If not, see <https://www.gnu.org/licenses/>.
 */

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
import org.screamingsandals.lib.sender.CommandSender;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.utils.annotations.ServiceDependencies;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

@Service
@ServiceDependencies(dependsOn = {
        BedWars1058ConfigurationMigrator.class,
        BedWars1058ArenaMigrator.class
})
@RequiredArgsConstructor
public class MigrateBedWars1058Command extends MigrateCommand {
    private final FileMigrator configurationMigrator;
    private final FileMigrator arenaMigrator;

    @Override
    protected void construct(Command.Builder<CommandSender> commandSenderWrapperBuilder, CommandManager<CommandSender> manager) {
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
