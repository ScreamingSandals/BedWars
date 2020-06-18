package org.screamingsandals.bedwars.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import org.bukkit.Bukkit;
import org.screamingsandals.bedwars.Main;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateChecker {
    public static void run() {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            try {
                URL url = new URL("https://screamingsandals.org/bedwars-zero-update-checker.php?version=" + Main.getVersion());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("User-Agent", "SpigetResourceUpdater");
                JsonObject jsonObject = new JsonParser().parse(new InputStreamReader(connection.getInputStream()))
                        .getAsJsonObject();
                Result result = new Gson().fromJson(jsonObject, Result.class);

                if (result.status) {
                    if (result.isUpdateAvailable) {
                        if (Main.getConfigurator().config.getBoolean("update-checker.zero.console")) {

                        }
                    }
                    if (result.isOneAvailable) {
                        if (Main.getConfigurator().config.getBoolean("update-checker.one.console")) {

                        }
                    }
                }
            } catch (Exception ignored) {
            }
        });
    }

    public static class Result {
        public boolean status;
        @SerializedName("version")
        public String currentZeroVersion;
        @SerializedName("zero_update")
        public boolean isUpdateAvailable;
        @SerializedName("one_available")
        public boolean isOneAvailable;
        @SerializedName("one_page")
        public String oneWebsite;
        @SerializedName("zero_download_url")
        public String download;

    }
}
