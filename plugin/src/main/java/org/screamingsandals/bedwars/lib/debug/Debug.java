package org.screamingsandals.bedwars.lib.debug;

import org.screamingsandals.lib.utils.AdventureHelper;

import java.util.logging.Level;
import java.util.logging.Logger;

@Deprecated
public class Debug {
    private static Logger logger;
    private static boolean isDebug = false;
    private static String fallbackName = "Fallback";

    public static void init(String pluginName) {
        logger = Logger.getLogger(pluginName);
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

    public static void debug(String debug, Level level, boolean forceDebug) {
        if (isDebug || forceDebug) {
            log(level, translateColor(debug));
        }
    }

    public static void setDebug(boolean debug) {
        isDebug = debug;
    }

    private static String translateColor(String string) {
        if (string == null) {
            return "";
        }

        return AdventureHelper.translateAlternateColorCodes('&', string);
    }

    private static void log(Level level, String message) {

        if (logger != null) {
            logger.log(level, message);
        } else {
            Logger.getLogger(fallbackName).log(level, message);
        }
    }
}