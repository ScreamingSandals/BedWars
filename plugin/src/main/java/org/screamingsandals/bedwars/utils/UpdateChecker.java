package org.screamingsandals.bedwars.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.commands.BaseCommand;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static misat11.lib.lang.I.*;

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

                if ("ok".equalsIgnoreCase(result.status)) {
                    UpdateListener updateListener = null;
                    if (result.isUpdateAvailable) {
                        if (Main.getConfigurator().config.getBoolean("update-checker.zero.console")) {
                            mpr("update_checker_zero").replace("version", result.currentZeroVersion).send(Bukkit.getConsoleSender());
                            mpr("update_checker_zero_second").replace("url", result.download).send(Bukkit.getConsoleSender());
                        }
                        if (Main.getConfigurator().config.getBoolean("update-checker.zero.admins")) {
                            updateListener = new UpdateListener(result);
                            Bukkit.getPluginManager().registerEvents(updateListener, Main.getInstance());
                        }
                    }
                    if (result.isOneAvailable) {
                        float javaVer = Float.parseFloat(System.getProperty("java.class.version"));
                        if (Main.getConfigurator().config.getBoolean("update-checker.one.console")) {
                            mpr("update_checker_one").replace("url", result.oneWebsite).send(Bukkit.getConsoleSender());
                            if (javaVer < 55.0F) {
                                mpr("update_checker_one_second_bad").send(Bukkit.getConsoleSender());
                            } else {
                                mpr("update_checker_one_second_good").send(Bukkit.getConsoleSender());
                            }
                        }
                        if (Main.getConfigurator().config.getBoolean("update-checker.one.admins")) {
                            if (updateListener == null) {
                                updateListener = new UpdateListener(result);
                                Bukkit.getPluginManager().registerEvents(updateListener, Main.getInstance());
                            }
                            updateListener.javaVer = javaVer;
                        }
                    }
                }
            } catch (Exception ignored) {
            }
        });
    }

    public static class Result {
        public String status;
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

        @Override
        public String toString() {
            return "Result{" +
                    "status=" + status +
                    ", currentZeroVersion='" + currentZeroVersion + '\'' +
                    ", isUpdateAvailable=" + isUpdateAvailable +
                    ", isOneAvailable=" + isOneAvailable +
                    ", oneWebsite='" + oneWebsite + '\'' +
                    ", download='" + download + '\'' +
                    '}';
        }
    }

    public static class UpdateListener implements Listener {
        public float javaVer;
        private Result result;

        public UpdateListener(Result result) {
            this.result = result;
        }

        @EventHandler
        public void onPlayerJoin(PlayerJoinEvent event) {
            Player player = event.getPlayer();
            if (BaseCommand.hasPermission(player, BaseCommand.ADMIN_PERMISSION, false)) {
                if (Main.getConfigurator().config.getBoolean("update-checker.zero.admins") && result.isUpdateAvailable) {
                    mpr("update_checker_zero").replace("version", result.currentZeroVersion).send(player);
                    mpr("update_checker_zero_second").replace("url", result.download).send(player);
                }

                if (Main.getConfigurator().config.getBoolean("update-checker.one.admins") && result.isOneAvailable) {
                    mpr("update_checker_one").replace("url", result.oneWebsite).send(player);
                    if (javaVer < 55.0F) {
                        mpr("update_checker_one_second_bad").send(player);
                    } else {
                        mpr("update_checker_one_second_good").send(player);
                    }
                }
            }

        }
    }
}
