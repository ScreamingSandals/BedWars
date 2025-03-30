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

package org.screamingsandals.bedwars.api.events;

import org.jetbrains.annotations.ApiStatus;
import org.screamingsandals.bedwars.api.BedwarsAPI;
import org.screamingsandals.bedwars.api.game.LocalGame;
import org.screamingsandals.bedwars.api.game.GameStatus;

import java.util.function.Consumer;

@ApiStatus.NonExtendable
public interface GameTickEvent {

    LocalGame getGame();

    int getCountdown();

    GameStatus getStatus();

    int getNextCountdown();

    void setNextCountdown(int nextCountdown);

    GameStatus getNextStatus();

    void setNextStatus(GameStatus nextStatus);

    int getPreviousCountdown();

    GameStatus getPreviousStatus();


    void preventContinuation(boolean prevent);

    boolean isNextCountdownChanged();

    boolean isNextStatusChanged();

    default boolean isNextTickChanged() {
        return isNextCountdownChanged() || isNextStatusChanged();
    }

    static void handle(Object plugin, Consumer<GameTickEvent> consumer) {
        BedwarsAPI.getInstance().getEventUtils().handle(plugin, GameTickEvent.class, consumer);
    }
}
