/*
 * Copyright (C) 2024 ScreamingSandals
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

package org.screamingsandals.bedwars.lang;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.lib.lang.Lang;
import org.screamingsandals.lib.lang.LangService;
import org.screamingsandals.lib.spectator.Component;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.utils.annotations.ServiceDependencies;
import org.screamingsandals.lib.utils.annotations.methods.OnEnable;
import org.screamingsandals.lib.utils.annotations.parameters.DataFolder;
import org.screamingsandals.lib.utils.logger.LoggerWrapper;
import org.spongepowered.configurate.BasicConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.gson.GsonConfigurationLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@ServiceDependencies(dependsOn = {
        MainConfig.class
})
@RequiredArgsConstructor
public class BedWarsLangService extends LangService {
    public static final Pattern LANGUAGE_PATTERN = Pattern.compile("[a-z]{2}-[A-Z]{2}");
    public static final String MESSAGE_PLACEHOLDER_NAME = "bw-lang";

    private final MainConfig mainConfig;
    private final LoggerWrapper logger;
    @DataFolder("languages")
    private final Path languagesFolder;
    @Getter
    private LanguageDefinition internalLanguageDefinition;

    {
        Lang.initDefault(this);
    }

    @SneakyThrows
    @OnEnable
    public void onEnable() {
        // TODO: Multi language + Language updater
        Locale locale;
        try {
            locale = Locale.forLanguageTag(mainConfig.node("locale").getString("en_US").replace("_", "-"));
        } catch (IllegalArgumentException ex) {
            logger.error("Invalid locale specified in config, falling back to en_US!", ex);
            locale = Locale.US;
        }
        final var finalLocale = locale;
        var prefix = mainConfig.node("prefix").getString("[BW]");

        Lang.setDefaultPrefix(Component.fromLegacy(prefix));

        final var langDefinitionResource = BedWarsLangService.class.getResourceAsStream("/language_definition.json");
        if (langDefinitionResource != null) {
            internalLanguageDefinition = GsonConfigurationLoader.builder()
                    .source(() -> new BufferedReader(new InputStreamReader(langDefinitionResource)))
                    .build()
                    .load()
                    .get(LanguageDefinition.class);
        }

        if (internalLanguageDefinition == null) {
            logger.error("Can't load default language definition!");
            return;
        }

        var languages = internalLanguageDefinition
                .getLanguages()
                .entrySet()
                .stream()
                .map(entry -> {
                    try {
                        return Map.entry(Locale.forLanguageTag(entry.getKey()), entry.getValue());
                    } catch (IllegalArgumentException ex) {
                        logger.error("Invalid language definition: {}", ex.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        var us = languages
                .stream()
                .filter(entry -> entry.getKey().equals(Locale.US))
                .findFirst()
                .map(entry -> {
                    try {
                        final var translationResource = BedWarsLangService.class.getResourceAsStream("/" + entry.getValue());
                        if (translationResource == null) {
                            logger.error("Can't acquire base language file en_US!");
                            return null;
                        }
                        return LayeredTranslationContainer.of(
                                GsonConfigurationLoader
                                        .builder()
                                        .source(() -> new BufferedReader(new InputStreamReader(translationResource)))
                                        .build()
                                        .load()
                        );
                    } catch (ConfigurateException e) {
                        logger.error("Can't load base language file en_US!", e);
                        // e.printStackTrace();
                        return null;
                    }
                })
                .orElseGet(() -> LayeredTranslationContainer.of(BasicConfigurationNode.root()));

        if (us.isEmpty()) {
            logger.warn("Language definitions don't contain en_US file!");
        }

        if (!finalLocale.equals(Locale.US)) {
            fallbackContainer = languages
                    .stream()
                    .filter(entry -> entry.getKey().equals(finalLocale))
                    .findFirst()
                    .or(() -> languages
                                .stream()
                                .filter(entry -> entry.getKey().getLanguage().equals(finalLocale.getLanguage()))
                                .findFirst()
                    )
                    .map(entry -> {
                        try {
                            final var translationResource = BedWarsLangService.class.getResourceAsStream("/" + entry.getValue());
                            if (translationResource == null) {
                                logger.error("Can't acquire language file!");
                                return null;
                            }
                            return LayeredTranslationContainer.of(
                                    us,
                                    GsonConfigurationLoader
                                            .builder()
                                            .source(() -> new BufferedReader(new InputStreamReader(translationResource)))
                                            .build()
                                            .load(),
                                    BasicConfigurationNode.root(),
                                    BasicConfigurationNode.root()
                            );
                        } catch (ConfigurateException e) {
                            logger.error("Can't load language file!", e);
                            // e.printStackTrace();
                            return null;
                        }
                    })
                    .orElse(us);
        } else {
            fallbackContainer = us;
        }

        if (Files.exists(languagesFolder)) {
            try (var stream = Files.walk(languagesFolder.toAbsolutePath())) {
                stream.filter(Files::isRegularFile)
                        .forEach(file -> {
                            var name = file.getFileName().toString();
                            if (Files.exists(file) && Files.isRegularFile(file) && name.toLowerCase().endsWith(".json")) {
                                var matcher = LANGUAGE_PATTERN.matcher(name);
                                if (matcher.find()) {
                                    try {
                                        var locale1 = Locale.forLanguageTag(matcher.group());

                                        if (finalLocale.equals(locale1)) {
                                            ((LayeredTranslationContainer) fallbackContainer)
                                                    .setCustomNode(GsonConfigurationLoader
                                                            .builder()
                                                            .path(file)
                                                            .build()
                                                            .load()
                                                    );
                                        } else if (Locale.US.equals(locale1)) {
                                            us.setCustomNode(GsonConfigurationLoader
                                                    .builder()
                                                    .path(file)
                                                    .build()
                                                    .load()
                                            );
                                        }
                                    } catch (IllegalArgumentException | ConfigurateException ex) {
                                        logger.warn("Invalid language file in languages directory: " + name, ex);
                                    }
                                }
                            }
                        });
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Files.createDirectory(languagesFolder);
        }
    }

    @Override
    @Nullable
    public String getMessagePlaceholderName() {
        return MESSAGE_PLACEHOLDER_NAME;
    }
}
