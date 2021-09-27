package org.screamingsandals.bedwars.commands;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import org.screamingsandals.bedwars.holograms.StatisticsHolograms;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.player.PlayerWrapper;
import org.screamingsandals.lib.sender.CommandSenderWrapper;
import org.screamingsandals.lib.utils.annotations.Service;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Service
public class RemoveHoloCommand extends BaseCommand {
    public static final List<UUID> PLAYERS_WITH_HOLOGRAM_REMOVER_IN_HAND = Collections.synchronizedList(new LinkedList<>());

    public RemoveHoloCommand() {
        super("removeholo", BedWarsPermission.ADMIN_PERMISSION, false);
    }

    @Override
    protected void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder, CommandManager<CommandSenderWrapper> manager) {
        manager.command(
                commandSenderWrapperBuilder
                    .handler(commandContext -> {
                        var player = commandContext.getSender().as(PlayerWrapper.class);
                        if (!StatisticsHolograms.isEnabled()) {
                            commandContext.getSender().sendMessage(Message.of(LangKeys.ADMIN_HOLO_NOT_ENABLED).defaultPrefix());
                        } else {
                            PLAYERS_WITH_HOLOGRAM_REMOVER_IN_HAND.add(player.getUuid());
                            commandContext.getSender().sendMessage(Message.of(LangKeys.ADMIN_HOLO_CLICK_TO_REMOVE).defaultPrefix());
                        }
                    })
        );
    }
}
