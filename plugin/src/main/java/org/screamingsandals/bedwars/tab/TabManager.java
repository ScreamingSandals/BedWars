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

package org.screamingsandals.bedwars.tab;

import org.bukkit.ChatColor;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.game.GamePlayer;
import org.screamingsandals.bedwars.lib.nms.accessors.IChatBaseComponent_i_ChatSerializerAccessor;
import org.screamingsandals.bedwars.lib.nms.accessors.PacketPlayOutPlayerListHeaderFooterAccessor;

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
                    headerComponent = getMethod(getCorrectSerializingMethod())
                            .invokeStatic("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', String.join("\n", translate(player, header))) + "\"}");
                } else {
                    headerComponent = getMethod(getCorrectSerializingMethod())
                            .invokeStatic("{\"text\": \"\"}");
                }

                Object footerComponent;
                if (footer != null) {
                    footerComponent = getMethod(getCorrectSerializingMethod())
                            .invokeStatic("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', String.join("\n", translate(player, footer))) + "\"}");
                } else {
                    footerComponent = getMethod(getCorrectSerializingMethod())
                            .invokeStatic("{\"text\": \"\"}");
                }

                Object packet;
                if (PacketPlayOutPlayerListHeaderFooterAccessor.getConstructor1() != null) {
                    packet = PacketPlayOutPlayerListHeaderFooterAccessor.getConstructor1().newInstance(headerComponent, footerComponent);
                } else {
                    packet = PacketPlayOutPlayerListHeaderFooterAccessor.getConstructor0().newInstance();
                    setField(packet, PacketPlayOutPlayerListHeaderFooterAccessor.getFieldHeader(), headerComponent);
                    setField(packet, PacketPlayOutPlayerListHeaderFooterAccessor.getFieldFooter(), footerComponent);
                }
                sendPacket(player.player, packet);
            } catch (Exception ignored) {

            }
        }
    }

    public void clear(GamePlayer player) {
        if (player.player.isOnline() && (header != null || footer != null)) {
            try {
                Object blankComponent = getMethod(getCorrectSerializingMethod())
                        .invokeStatic("{\"text\": \"\"}");
                Object packet;
                if (PacketPlayOutPlayerListHeaderFooterAccessor.getConstructor1() != null) {
                    packet = PacketPlayOutPlayerListHeaderFooterAccessor.getConstructor1().newInstance(blankComponent, blankComponent);
                } else {
                    packet = PacketPlayOutPlayerListHeaderFooterAccessor.getConstructor0().newInstance();
                    setField(packet, PacketPlayOutPlayerListHeaderFooterAccessor.getFieldHeader(), blankComponent);
                    setField(packet, PacketPlayOutPlayerListHeaderFooterAccessor.getFieldFooter(), blankComponent);
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
                .replace("%spectating%", String.valueOf(gamePlayer.getGame().countSpectating()))
                .replace("%spectators%", String.valueOf(gamePlayer.getGame().countSpectators()))
                .replace("%respawnable%", String.valueOf(gamePlayer.getGame().countRespawnable()))
                .replace("%max%", String.valueOf(gamePlayer.getGame().getMaxPlayers()))
                .replace("%map%", gamePlayer.getGame().getName())));
        return list;
    }

    public static Method getCorrectSerializingMethod() {
        if (IChatBaseComponent_i_ChatSerializerAccessor.getMethodFunc_150699_a1() != null) {
            return IChatBaseComponent_i_ChatSerializerAccessor.getMethodFunc_150699_a1();
        }
        return IChatBaseComponent_i_ChatSerializerAccessor.getMethodFunc_240643_a_1();
    }
}
