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

package org.screamingsandals.bedwars.commands;

import gs.mclo.api.MclogsClient;
import gs.mclo.api.response.UploadLogResponse;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.VersionInfo;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.screamingsandals.bedwars.lib.lang.I.i18n;

public class DumplogsCommand extends BaseCommand {
    private static final Pattern BUKKIT_VERSION_PATTERN = Pattern.compile("(.*)-R\\d+\\.\\d+.*");

    public DumplogsCommand() {
        super("dumplogs", ADMIN_PERMISSION, true, false);
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            try {
                UploadLogResponse response = new MclogsClient("ScreamingBedWars", VersionInfo.VERSION, getMinecraftVersion())
                        .uploadLog(Paths.get("./logs/latest.log"));

                if (response.isSuccess()) {
                    if (Main.isSpigot() && sender instanceof Player) {
                        try {
                            TextComponent msg1 = new TextComponent(response.getUrl());
                            msg1.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, response.getUrl()));
                            msg1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("").append("Open this link").create()));

                            ((Player) sender).spigot().sendMessage(new ComponentBuilder("")
                                    .append(TextComponent.fromLegacyText(i18n("dump_logs_success") + ChatColor.GRAY))
                                    .append(msg1)
                                    .create());
                        } catch (Throwable ignored) {
                            sender.sendMessage(i18n("dump_logs_success") + ChatColor.GRAY + response.getUrl());
                        }
                    } else {
                        sender.sendMessage(i18n("dump_logs_success") + ChatColor.GRAY + response.getUrl());
                    }
                } else {
                    sender.sendMessage(i18n("dump_failed"));
                }
            } catch (IOException e) {
                e.printStackTrace();
                sender.sendMessage(i18n("dump_failed"));
            }
        });

        return true;
    }

    public String getMinecraftVersion() {
        String bukkitVersion = Bukkit.getBukkitVersion();

        Matcher matcher = BUKKIT_VERSION_PATTERN.matcher(bukkitVersion);
        if (matcher.matches()) {
            return matcher.group(1);
        }

        return bukkitVersion;
    }

    @Override
    public void completeTab(List<String> completion, CommandSender sender, List<String> args) {
    }

}