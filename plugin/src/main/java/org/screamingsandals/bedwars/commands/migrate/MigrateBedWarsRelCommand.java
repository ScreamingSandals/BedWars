package org.screamingsandals.bedwars.commands.migrate;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import org.screamingsandals.bedwars.config.migrate.Migrator;
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
    private Migrator configurationMigrator;

    @OnEnable
    public void enable(BedWarsRelConfigurationMigrator configurationMigrator) {
        this.configurationMigrator = configurationMigrator;
    }

    @Override
    protected void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder, CommandManager<CommandSenderWrapper> manager) {
        manager.command(
                commandSenderWrapperBuilder
                        .literal("BedWarsRel", "bwrel", "BWRel", "bedwarsrel")
                        .handler(commandContext -> {
                            final var migrationResult = configurationMigrator.migrate();
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
