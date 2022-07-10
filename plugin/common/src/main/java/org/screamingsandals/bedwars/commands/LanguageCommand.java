/*
 * Copyright (C) 2022 ScreamingSandals
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

package org.screamingsandals.bedwars.commands;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.BooleanArgument;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.lang.BedWarsLangService;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.sender.CommandSenderWrapper;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.utils.annotations.parameters.DataFolder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Objects;

@Service
public class LanguageCommand extends BaseCommand {
    private final BedWarsLangService bedWarsLangService;
    @DataFolder("languages")
    private final Path languageFolder;

    public LanguageCommand(BedWarsLangService bedWarsLangService, Path languageFolder) {
        super("lang", BedWarsPermission.ADMIN_PERMISSION, true);
        this.bedWarsLangService = bedWarsLangService;
        this.languageFolder = languageFolder;
    }

    @Override
    protected void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder, CommandManager<CommandSenderWrapper> manager) {
        manager.command(
                commandSenderWrapperBuilder
                        .argument(manager
                                .argumentBuilder(String.class, "language")
                                .withSuggestionsProvider((c, s) -> new ArrayList<>(bedWarsLangService.getInternalLanguageDefinition().getLanguages().keySet()))
                        )
                        .argument(BooleanArgument.optional("exportFile", false))
                    .handler(commandContext -> {
                        final var sender = commandContext.getSender();
                        try {
                            final String locale = commandContext.get("language");
                            final boolean exportFile = commandContext.get("exportFile");

                            if (exportFile) {
                                var file = bedWarsLangService.getInternalLanguageDefinition().getLanguages().get(locale);
                                if (file != null) {
                                    var nFile = languageFolder.resolve(file.substring(file.lastIndexOf("/") == -1 ? 0 : file.lastIndexOf("/") + 1));
                                    if (!Files.exists(nFile)) {
                                        var ins = LanguageCommand.class.getResourceAsStream("/" + file);
                                        if (ins != null) {
                                            Files.copy(ins, nFile);
                                        }
                                    }
                                }
                            }

                            if (Objects.requireNonNull(MainConfig.getInstance().node("locale").getString())
                                    .equalsIgnoreCase(locale)) {
                                sender.sendMessage(Message.of(LangKeys.LANGUAGE_ALREADY_SET).defaultPrefix()
                                        .placeholder("lang", locale));
                                return;
                            }

                            MainConfig.getInstance().node("locale").set(locale);
                            MainConfig.getInstance().saveConfig();
                            sender.sendMessage(Message.of(LangKeys.LANGUAGE_SUCCESS).defaultPrefix().placeholder("lang", locale));
                            ReloadCommand.reload(sender);
                        } catch (Exception e) {
                            sender.sendMessage(Message.of(LangKeys.LANGUAGE_USAGE_BW_LANG).defaultPrefix());
                        }
                    })
        );
    }
}
