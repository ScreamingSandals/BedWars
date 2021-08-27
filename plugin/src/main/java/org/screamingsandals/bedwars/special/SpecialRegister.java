package org.screamingsandals.bedwars.special;

import lombok.experimental.UtilityClass;
import org.screamingsandals.bedwars.special.listener.*;
import org.screamingsandals.lib.utils.annotations.Service;

@Service(initAnother = {
        ArrowBlockerListener.class,
        AutoIgniteableTNTListener.class,
        GolemListener.class,
        LuckyBlockAddonListener.class,
        MagnetShoesListener.class,
        PermaItemListener.class,
        ProtectionWallListener.class,
        RescuePlatformListener.class,
        TeamChestListener.class,
        ThrowableFireballListener.class,
        TNTSheepListener.class,
        TrackerListener.class,
        TrapListener.class,
        WarpPowderListener.class,
        BridgeEggListener.class
})
@UtilityClass
public class SpecialRegister {

}
