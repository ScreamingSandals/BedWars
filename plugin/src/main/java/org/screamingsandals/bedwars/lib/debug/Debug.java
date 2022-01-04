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

package org.screamingsandals.bedwars.lib.debug;

import org.bukkit.ChatColor;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Debug {
    private static Logger logger;
    private static Logger aLogger;
    private static boolean isDebug = false;
    private static String fallbackName = "Fallback";

    public static void init(String pluginName) {
        logger = Logger.getLogger(pluginName);
        aLogger = Logger.getAnonymousLogger();
    }

    public static void info(String debug) {
        if (isDebug) {
            log(Level.INFO, translateColor(debug));
        }
    }

    public static void info(String debug, boolean forceDebug) {
        if (isDebug || forceDebug) {
            log(Level.INFO, translateColor(debug));
        }
    }

    public static void infoA(String prefix, String debug) {
        if (isDebug) {
            aLog(Level.INFO, translateColor(prefix) + " " + translateColor(debug));
        }
    }

    public static void infoA(String prefix, String debug, boolean forceDebug) {
        if (isDebug || forceDebug) {
            aLog(Level.INFO, translateColor(prefix) + " " + translateColor(debug));
        }
    }

    public static void warn(String debug) {
        if (isDebug) {
            log(Level.WARNING, translateColor(debug));
        }
    }

    public static void warn(String debug, boolean forceDebug) {
        if (isDebug || forceDebug) {
            log(Level.WARNING, translateColor(debug));
        }
    }

    public static void warnA(String prefix, String debug) {
        if (isDebug) {
            aLog(Level.WARNING, translateColor(prefix) + "> " + translateColor(debug));
        }
    }

    public static void warnA(String prefix, String debug, boolean forceDebug) {
        if (isDebug || forceDebug) {
            aLog(Level.WARNING, translateColor(prefix) + "> " + translateColor(debug));
        }
    }

    public static void debug(String debug, Level level, boolean forceDebug) {
        if (isDebug || forceDebug) {
            log(level, translateColor(debug));
        }
    }

    public static void setDebug(boolean debug) {
        isDebug = debug;
    }

    public static void setFallbackName(String name) {
        fallbackName = name;
    }

    private static String translateColor(String string) {
        if (string == null) {
            return "";
        }

        if (isBukkit()) {
            return ChatColor.translateAlternateColorCodes('&', string);
        } else {
            return net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', string);
        }
    }

    private static void log(Level level, String message) {

        if (logger != null) {
            logger.log(level, message);
        } else {
            Logger.getLogger(fallbackName).log(level, message);
        }
    }

    private static void aLog(Level level, String message) {
        if (aLogger != null) {
            aLogger.log(level, message);
        } else {
            Logger.getAnonymousLogger(fallbackName).log(level, message);
        }
    }

    private static boolean isBukkit() {
        try {
            Class.forName("org.bukkit.ChatColor");
            return true;
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }
}