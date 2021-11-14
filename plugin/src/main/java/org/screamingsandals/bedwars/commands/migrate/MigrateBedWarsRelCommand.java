package org.screamingsandals.bedwars.commands.migrate;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import org.screamingsandals.bedwars.config.migrate.ConfigurationMigrator;
import org.screamingsandals.bedwars.config.migrate.bwrel.BedWarsRelConfigurationMigrator;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.sender.CommandSenderWrapper;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.utils.annotations.methods.OnEnable;

@Service(dependsOn = {
        BedWarsRelConfigurationMigrator.class
})
public class MigrateBedWarsRelCommand extends MigrateCommand {
    private ConfigurationMigrator migrator;

    @OnEnable
    public void enable(BedWarsRelConfigurationMigrator migrator) {
        this.migrator = migrator;
    }

    @Override
    protected void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder, CommandManager<CommandSenderWrapper> manager) {
        manager.command(
                commandSenderWrapperBuilder
                        .literal("BedWarsRel", "bwrel", "BWRel", "bedwarsrel")
                        .handler(commandContext -> {
                            final var migrationResult = migrator.migrate();
                            if (migrationResult) {
                                Message.of(LangKeys.MIGRATE_SUCCESS)
                                        .placeholder("plugin", "BedWarsRel")
                                        .send(commandContext.getSender());
                            } else {
                                Message.of(LangKeys.MIGRATE_FAILURE)
                                        .send(commandContext.getSender());
                            }
                        })
        );
    }
}
