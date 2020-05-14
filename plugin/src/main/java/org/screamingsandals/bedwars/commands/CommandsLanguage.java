package org.screamingsandals.bedwars.commands;

import static org.screamingsandals.lib.gamecore.language.GameLanguage.mpr;

public class CommandsLanguage extends org.screamingsandals.lib.commands.common.language.CommandsLanguage {

    public CommandsLanguage() {
        super();
        load();
    }

    private void load() {
        getLanguages().put(Key.NO_PERMISSIONS, mpr("commands.errors.no_permissions").get());
        getLanguages().put(Key.COMMAND_DOES_NOT_EXISTS, mpr("commands.errors.command_does_not_exists").get());
        getLanguages().put(Key.SOMETHINGS_FUCKED, mpr("commands.errors.somethings_fucked").get());
        getLanguages().put(Key.NOT_FOR_CONSOLE, mpr("commands.errors.not_for_console").get());
    }
}
