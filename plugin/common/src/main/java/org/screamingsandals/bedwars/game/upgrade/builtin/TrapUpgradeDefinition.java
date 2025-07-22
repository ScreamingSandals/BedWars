/*
 * Copyright (C) 2025 ScreamingSandals
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

package org.screamingsandals.bedwars.game.upgrade.builtin;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.api.game.upgrade.Upgradable;
import org.screamingsandals.bedwars.game.TeamImpl;
import org.screamingsandals.lib.item.meta.PotionEffect;
import org.screamingsandals.lib.spectator.sound.SoundStart;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class TrapUpgradeDefinition implements BuiltInUpgradeDefinition {
    private static final @NotNull String DETECTION_RANGE_KEY = "detection-range";
    private static final @NotNull String EFFECTS_KEY = "effects";
    private static final @NotNull String ENEMIES_KEY = "enemies";
    private static final @NotNull String TEAM_KEY = "team";
    private static final @NotNull String SINGULAR_USE_KEY = "singular-use";
    private static final @NotNull String MESSAGE_KEY = "message";
    private static final @NotNull String TEAM_TITLE_KEY = "team-title";
    private static final @NotNull String TEAM_SUBTITLE_KEY = "team-subtitle";
    private static final @NotNull String NAME_KEY = "name";
    private static final @NotNull String TRIGGER_SOUND_KEY = "trigger-sound";

    private final double detectionRange;
    private final @NotNull List<@NotNull PotionEffect> effects;
    private final boolean enemies;
    private final boolean team;
    private final boolean singularUse;
    private final @Nullable String message;
    private final @Nullable String teamTitle;
    private final @Nullable String teamSubtitle;
    private final @Nullable String name;
    private final @Nullable SoundStart triggerSound;

    @Override
    public double getInitialLevel() {
        return 0;
    }

    @Override
    public @Nullable Double getMaximalLevel() {
        return 1.0;
    }

    @Override
    public boolean isApplicable(@NotNull Upgradable upgradable) {
        return upgradable instanceof TeamImpl;
    }

    public static class Loader implements BuiltInUpgradeDefinition.Loader<TrapUpgradeDefinition> {
        public static final @NotNull Loader INSTANCE = new Loader();

        @Override
        public @NotNull TrapUpgradeDefinition load(@NotNull ConfigurationNode node) throws ConfigurateException {
            var list = node.node(EFFECTS_KEY).getList(PotionEffect.class);
            if (list == null || list.isEmpty()) {
                throw new ConfigurateException("No potion effects are defined");
            }

            return new TrapUpgradeDefinition(
                    node.node(DETECTION_RANGE_KEY).getDouble(7),
                    list,
                    node.node(ENEMIES_KEY).getBoolean(true),
                    node.node(TEAM_KEY).getBoolean(false),
                    node.node(SINGULAR_USE_KEY).getBoolean(true),
                    node.node(MESSAGE_KEY).getString(),
                    node.node(TEAM_TITLE_KEY).getString(),
                    node.node(TEAM_SUBTITLE_KEY).getString(),
                    node.node(NAME_KEY).getString(),
                    node.node(TRIGGER_SOUND_KEY).get(SoundStart.class)
            );
        }
    }
}
