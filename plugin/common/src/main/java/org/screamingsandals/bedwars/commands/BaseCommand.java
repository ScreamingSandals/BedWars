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

package org.screamingsandals.bedwars.commands;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.keys.SimpleCloudKey;
import cloud.commandframework.permission.PredicatePermission;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.screamingsandals.lib.player.Player;
import org.screamingsandals.lib.sender.CommandSender;
import org.screamingsandals.lib.utils.annotations.ServiceDependencies;
import org.screamingsandals.lib.utils.annotations.methods.OnPostEnable;
import org.screamingsandals.lib.utils.annotations.parameters.ProvidedBy;

@RequiredArgsConstructor
@Getter
@ServiceDependencies(dependsOn = {
        CommandService.class
})
public abstract class BaseCommand {

    protected final String name;
    protected final BedWarsPermission possiblePermission;
    protected final boolean allowConsole;

    protected abstract void construct(Command.Builder<CommandSender> commandSenderWrapperBuilder, CommandManager<CommandSender> manager);

    @OnPostEnable
    public void construct(@ProvidedBy(CommandService.class) CommandManager<CommandSender> manager) {
        var builder = manager.commandBuilder("bw")
                .literal(name);
        if (possiblePermission != null) {
            builder = builder.permission(
                    PredicatePermission.of(SimpleCloudKey.of(name), perm ->
                            perm.getType() == CommandSender.Type.CONSOLE || possiblePermission.asPermission().hasPermission(perm)
                    )
            );
        }
        if (!allowConsole) {
            builder = builder.senderType(Player.class);
        }
        construct(builder, manager);
    }
}
