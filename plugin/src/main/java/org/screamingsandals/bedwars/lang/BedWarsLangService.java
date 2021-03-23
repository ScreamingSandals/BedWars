package org.screamingsandals.bedwars.lang;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang.LocaleUtils;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.lib.lang.Lang;
import org.screamingsandals.lib.lang.LangService;
import org.screamingsandals.lib.lang.container.TranslationContainer;
import org.screamingsandals.lib.utils.AdventureHelper;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.utils.annotations.methods.OnEnable;
import org.screamingsandals.lib.utils.logger.LoggerWrapper;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.gson.GsonConfigurationLoader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service(dependsOn = {
        MainConfig.class
})
@RequiredArgsConstructor
public class BedWarsLangService extends LangService {
    private final MainConfig mainConfig;
    private final LoggerWrapper logger;

    {
        Lang.initDefault(this);
    }

    @SneakyThrows
    @OnEnable
    public void onEnable() {
        Locale locale;
        try {
            locale = LocaleUtils.toLocale(mainConfig.node("locale").getString("en_US").replace("-", "_"));
        } catch (IllegalArgumentException ex) {
            logger.error("invalid locale specified in config, fallback to en_US!", ex);
            locale = Locale.US;
        }
        final var finalLocale = locale;
        var prefix = mainConfig.node("prefix").getString("[BW]");

        Lang.setDefaultPrefix(AdventureHelper.toComponent(prefix));

        var internalLanguageDefinition = new Gson().fromJson(new InputStreamReader(BedWarsLangService.class.getResourceAsStream("/language_definition.json")), LanguageDefinition.class);

        if (internalLanguageDefinition == null) {
            logger.error("Can't load default Language Definition for Screaming BedWars!");
        }

        var languages = internalLanguageDefinition
                .getLanguages()
                .entrySet()
                .stream()
                .map(entry -> {
                    try {
                        return Map.entry(LocaleUtils.toLocale(entry.getKey().replace("-", "_")), entry.getValue());
                    } catch (IllegalArgumentException ex) {
                        logger.error("Invalid language definition: ", ex);
                        ex.printStackTrace();
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
                        return TranslationContainer.of(
                                GsonConfigurationLoader
                                        .builder()
                                        .source(() -> new BufferedReader(new InputStreamReader(BedWarsLangService.class.getResourceAsStream("/" + entry.getValue()))))
                                        .build()
                                        .load(),
                                null);
                    } catch (ConfigurateException e) {
                        logger.error("Can't load base language file en_US!", e);
                        e.printStackTrace();
                        return null;
                    }
                })
                .orElse(null);

        if (us == null) {
            logger.warn("Language definitions don't contain en_US file!");
        }

        if (!locale.equals(Locale.US)) {
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
                            return TranslationContainer.of(
                                    GsonConfigurationLoader
                                            .builder()
                                            .source(() -> new BufferedReader(new InputStreamReader(BedWarsLangService.class.getResourceAsStream("/" + entry.getValue()))))
                                            .build()
                                            .load(),
                                    us);
                        } catch (ConfigurateException e) {
                            logger.error("Can't load language file!", e);
                            e.printStackTrace();
                            return null;
                        }
                    })
                    .orElse(us);
        } else {
            fallbackContainer = us;
        }

        // TODO updater and custom translations

    }
}
