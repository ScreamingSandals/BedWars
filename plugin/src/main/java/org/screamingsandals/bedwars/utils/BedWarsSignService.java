package org.screamingsandals.bedwars.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.game.GameManager;
import org.screamingsandals.bedwars.lib.signmanager.ClickableSign;
import org.screamingsandals.bedwars.lib.signmanager.SignManagerNew;
import org.screamingsandals.lib.player.PlayerWrapper;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.utils.annotations.parameters.ConfigFile;
import org.screamingsandals.lib.world.BlockMapper;
import org.screamingsandals.lib.world.state.BlockStateMapper;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.util.Optional;

@Service(dependsOn = {
        BlockMapper.class,
        BlockStateMapper.class,
        MainConfig.class,
        GameManager.class
})
public class BedWarsSignService extends SignManagerNew {
    public BedWarsSignService(@ConfigFile(value = "database/sign.yml", old = "sign.yml") YamlConfigurationLoader loader) {
        super(loader);
    }

    @Override
    protected boolean isAllowedToUse(PlayerWrapper player) {
        return player.isOnline();
    }

    @Override
    protected boolean isAllowedToEdit(PlayerWrapper player) {
        return false;
    }

    @Override
    protected Optional<String> normalizeKey(Component key) {
        var key2 = PlainComponentSerializer.plain().serialize(key);
        if (GameManager.getInstance().hasGame(key2)) {
            return Optional.of(key2);
        }
        return Optional.empty();
    }

    @Override
    protected void updateSign(ClickableSign sign) {

    }

    @Override
    protected void onClick(PlayerWrapper playerWrapper, ClickableSign sign) {

    }

    @Override
    protected boolean isFirstLineValid(Component firstLine) {
        return false;
    }

    @Override
    protected Component signCreatedMessage(PlayerWrapper player) {
        return null;
    }

    @Override
    protected Component signCannotBeCreatedMessage(PlayerWrapper player) {
        return null;
    }

    @Override
    protected Component signCannotBeDestroyedMessage(PlayerWrapper player) {
        return null;
    }
}
