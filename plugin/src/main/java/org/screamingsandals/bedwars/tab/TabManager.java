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

package org.screamingsandals.bedwars.tab;

import org.bukkit.ChatColor;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.game.GamePlayer;
import org.screamingsandals.bedwars.lib.nms.accessors.ClientboundTabListPacketAccessor;
import org.screamingsandals.bedwars.lib.nms.accessors.Component$SerializerAccessor;
import org.screamingsandals.bedwars.lib.nms.accessors.RegistryAccessAccessor;
import org.screamingsandals.bedwars.lib.nms.utils.ClassStorage;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.screamingsandals.bedwars.lib.nms.utils.ClassStorage.*;

public class TabManager {
    private List<String> header;
    private List<String> footer;

    public TabManager() {
        if (Main.getConfigurator().config.getBoolean("tab.header.enabled")) {
            header = Main.getConfigurator().config.getStringList("tab.header.contents");
        }
        if (Main.getConfigurator().config.getBoolean("tab.footer.enabled")) {
            footer = Main.getConfigurator().config.getStringList("tab.footer.contents")
                    .stream()
                    .map(content -> ChatColor.translateAlternateColorCodes('&', content))
                    .collect(Collectors.toList());
        }
    }

    public void modifyForPlayer(GamePlayer player) {
        if (player.player.isOnline() && (header != null || footer != null)) {
            try {
                Object headerComponent;
                if (header != null) {
                    headerComponent = serialize("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', String.join("\n", translate(player, header))) + "\"}");
                } else {
                    headerComponent = serialize("{\"text\": \"\"}");
                }

                Object footerComponent;
                if (footer != null) {
                    footerComponent = serialize("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', String.join("\n", translate(player, footer))) + "\"}");
                } else {
                    footerComponent = serialize("{\"text\": \"\"}");
                }

                Object packet;
                if (ClientboundTabListPacketAccessor.CONSTRUCTOR_1.get() != null) {
                    packet = ClientboundTabListPacketAccessor.CONSTRUCTOR_1.get().newInstance(headerComponent, footerComponent);
                } else {
                    packet = ClientboundTabListPacketAccessor.CONSTRUCTOR_0.get().newInstance();
                    setField(packet, ClientboundTabListPacketAccessor.FIELD_HEADER.get(), headerComponent);
                    setField(packet, ClientboundTabListPacketAccessor.FIELD_FOOTER.get(), footerComponent);
                }
                sendPacket(player.player, packet);
            } catch (Exception ignored) {

            }
        }
    }

    public void clear(GamePlayer player) {
        if (player.player.isOnline() && (header != null || footer != null)) {
            try {
                String clearString;
                if (Main.getVersionNumber() >= 115) {
                    clearString = "{\"text\": \"\"}";
                } else {
                    clearString = "{\"translate\": \"\"}";
                }
                Object blankComponent = serialize(clearString);
                Object packet;
                if (ClientboundTabListPacketAccessor.CONSTRUCTOR_1.get() != null) {
                    packet = ClientboundTabListPacketAccessor.CONSTRUCTOR_1.get().newInstance(blankComponent, blankComponent);
                } else {
                    packet = ClientboundTabListPacketAccessor.CONSTRUCTOR_0.get().newInstance();
                    setField(packet, ClientboundTabListPacketAccessor.FIELD_HEADER.get(), blankComponent);
                    setField(packet, ClientboundTabListPacketAccessor.FIELD_FOOTER.get(), blankComponent);
                }
                sendPacket(player.player, packet);
            } catch (Exception ignored) {
            }
        }
    }

    public List<String> translate(GamePlayer gamePlayer, List<String> origin) {
        List<String> list = new ArrayList<>();
        origin.forEach(a -> list.add(a.replace("%players%", String.valueOf(gamePlayer.getGame().countPlayers()))
                .replace("%alive%", String.valueOf(gamePlayer.getGame().countAlive()))
                .replace("%spectating%", String.valueOf(gamePlayer.getGame().getStatus() != GameStatus.WAITING ? gamePlayer.getGame().countSpectating() : (gamePlayer.getGame().countPlayers() - gamePlayer.getGame().countRespawnable())))
                .replace("%spectators%", String.valueOf(gamePlayer.getGame().getStatus() != GameStatus.WAITING ? gamePlayer.getGame().countSpectators() : (gamePlayer.getGame().countPlayers() - gamePlayer.getGame().countRespawnable())))
                .replace("%respawnable%", String.valueOf(gamePlayer.getGame().countRespawnable()))
                .replace("%max%", String.valueOf(gamePlayer.getGame().getMaxPlayers()))
                .replace("%map%", gamePlayer.getGame().getName())));
        return list;
    }

    public static Object serialize(String text) {
        if (Component$SerializerAccessor.METHOD_FROM_JSON.get() != null) {
            return ClassStorage.getMethod(Component$SerializerAccessor.METHOD_FROM_JSON.get()).invokeStatic(text);
        }
        if (Component$SerializerAccessor.METHOD_FROM_JSON_1.get() != null) {
            return ClassStorage.getMethod(Component$SerializerAccessor.METHOD_FROM_JSON_1.get()).invokeStatic(text);
        }
        return ClassStorage.getMethod(Component$SerializerAccessor.METHOD_FROM_JSON_LENIENT.get()).invokeStatic(text, RegistryAccessAccessor.FIELD_EMPTY.get());
    }
}
