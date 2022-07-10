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

package org.screamingsandals.bedwars.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.screamingsandals.bedwars.api.events.GameTickEvent;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.lib.event.SEvent;

@Data
@AllArgsConstructor
public class GameTickEventImpl implements GameTickEvent, SEvent {
    private final GameImpl game;
    private final int previousCountdown;
    private final GameStatus previousStatus;
    private final int countdown;
    private final GameStatus status;
    private final int originalNextCountdown;
    private final GameStatus originalNextStatus;
    private int nextCountdown;
    private GameStatus nextStatus;

    @Override
    public void preventContinuation(boolean prevent) {
        if (prevent) {
            this.nextCountdown = this.countdown;
            this.nextStatus = this.status;
        } else {
            this.nextCountdown = this.originalNextCountdown;
            this.nextStatus = this.originalNextStatus;
        }
    }

    @Override
    public boolean isNextCountdownChanged() {
        return this.nextCountdown != this.originalNextCountdown;
    }

    @Override
    public boolean isNextStatusChanged() {
        return this.nextStatus != this.originalNextStatus;
    }
}
