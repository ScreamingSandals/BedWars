package org.screamingsandals.bedwars.listener;

import org.screamingsandals.lib.block.BlockHolder;

public class Player116ListenerUtils {
    public static boolean isAnchorEmpty(BlockHolder anchor) {
        var charges = anchor.getType().get("charges");
        return charges.map("0"::equals).orElse(true);
    }
}
