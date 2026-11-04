/*
 * Copyright (C) 2023 ScreamingSandals
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

package org.screamingsandals.bedwars.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.VersionInfo;
import org.screamingsandals.bedwars.commands.BaseCommand;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.screamingsandals.bedwars.lib.lang.I.*;

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
                    if (result.isUpdateAvailable || result.hasAdvisory || !result.isBranchSupported || (result.warningMessage != null && !result.warningMessage.isEmpty())) {
                        if (Main.getConfigurator().config.getBoolean("update-checker.zero.console")) {
                            CommandSender console = Bukkit.getConsoleSender();

                            if (!result.isBranchSupported) {
                                mpr("update_checker_branch_unsupported")
                                        .replace("branch", VersionInfo.VERSION.replaceFirst("^(\\d+\\.\\d+)\\..*$", "$1.x"))
                                        .send(console);
                            }
                            if (result.isUpdateAvailable) {
                                mpr("update_checker_zero").replace("version", result.currentZeroVersion).send(console);
                                mpr("update_checker_zero_second").replace("url", result.download).send(console);
                                if (result.hasAdvisory) {
                                    mpr("update_checker_advisory").send(console);
                                    mpr("update_checker_advisory_part2").send(console);
                                }
                            }
                            if (result.warningMessage != null && !result.warningMessage.isEmpty()) {
                                console.sendMessage(i18n("update_checker_note") + result.warningMessage);
                            }
                        }
                        if (Main.getConfigurator().config.getBoolean("update-checker.zero.admins")) {
                            updateListener = new UpdateListener(result);
                            Bukkit.getPluginManager().registerEvents(updateListener, Main.getInstance());
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
        @SerializedName("zero_download_url")
        public String download;
        @SerializedName("branch_support")
        public boolean isBranchSupported;
        @SerializedName("advisory")
        public boolean hasAdvisory;
        @SerializedName("warning_message")
        public String warningMessage;

        @Override
        public String toString() {
            return "Result{" +
                    "status=" + status +
                    ", currentZeroVersion='" + currentZeroVersion + '\'' +
                    ", isUpdateAvailable=" + isUpdateAvailable +
                    ", download='" + download + '\'' +
                    ", isBranchSupported=" + isBranchSupported +
                    ", hasAdvisory=" + hasAdvisory +
                    ", warningMessage='" + warningMessage + '\'' +
                    '}';
        }
    }

    public static class UpdateListener implements Listener {
        private Result result;

        public UpdateListener(Result result) {
            this.result = result;
        }

        @EventHandler
        public void onPlayerJoin(PlayerJoinEvent event) {
            Player player = event.getPlayer();
            if (BaseCommand.hasPermission(player, BaseCommand.ADMIN_PERMISSION, false)) {
                if (Main.getConfigurator().config.getBoolean("update-checker.zero.admins")) {
                    if (!result.isBranchSupported) {
                        mpr("update_checker_branch_unsupported")
                                .replace("branch", VersionInfo.VERSION.replaceFirst("^(\\d+\\.\\d+)\\..*$", "$1.x"))
                                .send(player);
                    }
                    if (result.isUpdateAvailable) {
                        mpr("update_checker_zero").replace("version", result.currentZeroVersion).send(player);
                        mpr("update_checker_zero_second").replace("url", result.download).send(player);
                        if (result.hasAdvisory) {
                            mpr("update_checker_advisory").send(player);
                            mpr("update_checker_advisory_part2").send(player);
                        }
                    }
                    if (result.warningMessage != null && !result.warningMessage.isEmpty()) {
                        player.sendMessage(i18n("update_checker_note") + result.warningMessage);
                    }
                }
            }

        }
    }
}
