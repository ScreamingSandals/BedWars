package org.screamingsandals.bedwars.commands.migrate;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import org.screamingsandals.bedwars.config.migrate.ConfigurationMigrator;
import org.screamingsandals.bedwars.config.migrate.andrei.BedWars1058ConfigurationMigrator;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.sender.CommandSenderWrapper;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.utils.annotations.methods.OnEnable;

@Service(dependsOn = {
        BedWars1058ConfigurationMigrator.class
})
public class MigrateBedWars1058Command extends MigrateCommand {
    private ConfigurationMigrator configurationMigrator;

    @OnEnable
    public void enable(BedWars1058ConfigurationMigrator configurationMigrator) {
        this.configurationMigrator = configurationMigrator;
    }

    @Override
    protected void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder, CommandManager<CommandSenderWrapper> manager) {
        manager.command(
                commandSenderWrapperBuilder
                        .literal("bw1058", "bedwars1058", "BedWars1058", "1058", "andrei1058")
                        .handler(commandContext -> {
                            final var migrationResult = configurationMigrator.migrate();
                            if (migrationResult) {
                                Message.of(LangKeys.MIGRATE_SUCCESS)
                                        .placeholder("plugin", "BedWars1058")
                                        .send(commandContext.getSender());
                            } else {
                                Message.of(LangKeys.MIGRATE_FAILURE)
                                        .send(commandContext.getSender());
                            }
                        })
        );
    }
}
