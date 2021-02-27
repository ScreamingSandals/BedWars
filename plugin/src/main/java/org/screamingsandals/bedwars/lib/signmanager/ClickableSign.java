package org.screamingsandals.bedwars.lib.signmanager;

import lombok.Data;
import org.screamingsandals.bedwars.utils.PreparedLocation;

@Data
public class ClickableSign {
    private final PreparedLocation location;
    private final String key;
}
