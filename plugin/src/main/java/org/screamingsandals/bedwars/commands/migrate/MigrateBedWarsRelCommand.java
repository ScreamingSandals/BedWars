package org.screamingsandals.bedwars.commands.migrate;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import org.screamingsandals.bedwars.config.migrate.FileMigrator;
import org.screamingsandals.bedwars.config.migrate.bwrel.BedWarsRelConfigurationMigrator;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.sender.CommandSenderWrapper;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.utils.annotations.methods.OnEnable;

import java.nio.file.Paths;

@Service(dependsOn = {
        BedWarsRelConfigurationMigrator.class
})
public class MigrateBedWarsRelCommand extends MigrateCommand {
    private FileMigrator configurationMigrator;

    @OnEnable
    public void enable(BedWarsRelConfigurationMigrator configurationMigrator) {
        this.configurationMigrator = configurationMigrator;
    }

    @Override
    protected void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder, CommandManager<CommandSenderWrapper> manager) {
        manager.command(
                commandSenderWrapperBuilder
                        .literal("BedWarsRel", "bwrel", "BWRel", "bedwarsrel")
                        .handler(commandContext ->
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
                                )
                        )
        );
    }
}
