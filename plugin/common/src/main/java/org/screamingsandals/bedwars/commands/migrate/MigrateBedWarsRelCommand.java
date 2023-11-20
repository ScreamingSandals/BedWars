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
import org.screamingsandals.bedwars.config.migrate.bwrel.BedWarsRelArenaMigrator;
import org.screamingsandals.bedwars.config.migrate.bwrel.BedWarsRelConfigurationMigrator;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.sender.CommandSender;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.utils.annotations.ServiceDependencies;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@ServiceDependencies(dependsOn = {
        BedWarsRelConfigurationMigrator.class,
        BedWarsRelArenaMigrator.class
})
@RequiredArgsConstructor
public class MigrateBedWarsRelCommand extends MigrateCommand {
    private final FileMigrator configurationMigrator;
    private final FileMigrator arenaMigrator;

    @Override
    protected void construct(Command.Builder<CommandSender> commandSenderWrapperBuilder, CommandManager<CommandSender> manager) {
        manager.command(
                commandSenderWrapperBuilder
                        .literal("BedWarsRel", "bwrel", "BWRel", "bedwarsrel")
                        .handler(commandContext -> {
                            final var failure = new AtomicBoolean(false);
                            Arrays.stream(
                                    Objects.requireNonNull(
                                            Paths.get(MiscUtils.getPluginsFolder("BedwarsRel").toString(), "arenas").toFile().listFiles(File::isDirectory),
                                            "'plugins/BedwarsRel/arenas' must be a directory!"
                                    )
                            ).forEach(arena -> {
                                try {
                                    arenaMigrator.migrate(arena);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    failure.set(true);
                                }
                            });
                            if (failure.get()) {
                                Message.of(LangKeys.MIGRATE_FAILURE)
                                        .send(commandContext.getSender());
                                return;
                            }
                            configurationMigrator.migrateAsynchronously(
                                    Paths.get(MiscUtils.getPluginsFolder("BedwarsRel").toString(), "config.yml").toFile()
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
