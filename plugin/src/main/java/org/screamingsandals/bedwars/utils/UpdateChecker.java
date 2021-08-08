package org.screamingsandals.bedwars.utils;

import lombok.experimental.UtilityClass;
import org.screamingsandals.bedwars.BedWarsPlugin;
import org.screamingsandals.bedwars.commands.BedWarsPermission;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.lib.event.EventManager;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.player.PlayerMapper;
import org.screamingsandals.lib.event.player.SPlayerJoinEvent;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.utils.annotations.methods.OnPostEnable;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.gson.GsonConfigurationLoader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service(dependsOn = {
        MainConfig.class,
        PlayerMapper.class
})
@UtilityClass
public class UpdateChecker {
    @OnPostEnable
    public void run(MainConfig mainConfig) {
        if (mainConfig.node("update-checker", "console").getBoolean()
                || mainConfig.node("update-checker", "admins").getBoolean()) {
            HttpClient.newHttpClient().sendAsync(HttpRequest.newBuilder()
                    .uri(URI.create("https://screamingsandals.org/bedwars-zero-update-checker.php?version=" + BedWarsPlugin.getVersion()))
                    .build(), HttpResponse.BodyHandlers.ofInputStream())
                    .thenAccept(inputStreamHttpResponse -> {
                        var loader = GsonConfigurationLoader.builder()
                                .source(() -> new BufferedReader(new InputStreamReader(inputStreamHttpResponse.body())))
                                .build();
                        try {
                            var result = loader.load();

                            if ("ok".equalsIgnoreCase(result.node("status").getString())) {
                                if (result.node("zero_update").getBoolean()) {
                                    if (mainConfig.node("update-checker", "console").getBoolean()) {
                                        Message
                                                .of(LangKeys.UPDATE_NEW_RELEASE)
                                                .defaultPrefix()
                                                .placeholder("version", result.node("version").getString())
                                                .placeholder("url", result.node("zero_download_url").getString())
                                                .send(PlayerMapper.getConsoleSender());
                                    }
                                    if (mainConfig.node("update-checker", "admins").getBoolean()) {
                                        EventManager.getDefaultEventManager().register(SPlayerJoinEvent.class, event -> {
                                            var player = event.getPlayer();
                                            if (player.hasPermission(BedWarsPermission.ADMIN_PERMISSION.asPermission())) {
                                                if (mainConfig.node("update-checker", "admins").getBoolean() && result.node("zero_update").getBoolean()) {
                                                    Message
                                                            .of(LangKeys.UPDATE_NEW_RELEASE)
                                                            .defaultPrefix()
                                                            .placeholder("version", result.node("version").getString())
                                                            .placeholder("url", result.node("zero_download_url").getString())
                                                            .send(player);
                                                }
                                            }
                                        });
                                    }
                                }
                            }
                        } catch (ConfigurateException e) {
                            e.printStackTrace();
                        }
                    });
        }
    }
}
