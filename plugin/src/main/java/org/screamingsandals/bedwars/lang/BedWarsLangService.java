package org.screamingsandals.bedwars.lang;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.lib.lang.Lang;
import org.screamingsandals.lib.lang.LangService;
import org.screamingsandals.lib.lang.container.TranslationContainer;
import org.screamingsandals.lib.utils.AdventureHelper;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.utils.annotations.methods.OnEnable;
import org.spongepowered.configurate.gson.GsonConfigurationLoader;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Service(dependsOn = {
        MainConfig.class
})
@RequiredArgsConstructor
public class BedWarsLangService extends LangService {
    private final MainConfig mainConfig;

    {
        Lang.initDefault(this);
    }

    @SneakyThrows
    @OnEnable
    public void onEnable() {
        var locale = mainConfig.node("locale").getString("en-US");
        var prefix = mainConfig.node("prefix").getString("[BW]");

        Lang.setDefaultPrefix(AdventureHelper.toComponent(prefix));

        fallbackContainer = TranslationContainer.of(
                GsonConfigurationLoader
                        .builder()
                        .source(() -> new BufferedReader(new InputStreamReader(BedWarsLangService.class.getResourceAsStream("language_en-US.yml"))))
                        .build()
                        .load(),
                null);

        // TODO download and load other translations

    }
}
