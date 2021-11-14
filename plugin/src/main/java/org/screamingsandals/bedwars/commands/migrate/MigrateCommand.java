package org.screamingsandals.bedwars.commands.migrate;

import org.screamingsandals.bedwars.commands.BaseCommand;
import org.screamingsandals.bedwars.commands.BedWarsPermission;

public abstract class MigrateCommand extends BaseCommand {
    public MigrateCommand() {
        super("migrate", BedWarsPermission.ADMIN_PERMISSION, true);
    }
}
