package misat11.bw.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Debugger {
    public static void info(String debug) {
        Logger logger = Logger.getLogger("BedWars");
        logger.info(debug);
    }

    public static void warn(String debug) {
        Logger logger = Logger.getLogger("BedWars");
        logger.log(Level.WARNING, debug);
    }
}
