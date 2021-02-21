package org.screamingsandals.bedwars.commands;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.lib.sender.CommandSenderWrapper;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.List;

import static org.screamingsandals.bedwars.lib.lang.I.i18n;

public class MainlobbyCommand extends BaseCommand {
    public MainlobbyCommand(CommandManager<CommandSenderWrapper> manager) {
        super(manager, "mainlobby", BedWarsPermission.ADMIN_PERMISSION, false);
    }

    @Override
    protected void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        manager.command(
                commandSenderWrapperBuilder
                        .argument(manager
                                .argumentBuilder(String.class, "action")
                                .withSuggestionsProvider((c, s) -> List.of("enable", "set"))
                        )
                .handler(commandContext -> {
                    String action = commandContext.get("action");
                    var sender = commandContext.getSender();

                    if (action.contains("enable")) {
                        try {
                            Main.getConfigurator().node("mainlobby", "enabled").set(true);
                            Main.getConfigurator().saveConfig();

                            sender.sendMessage(i18n("admin_command_success"));
                            sender.sendMessage(i18n("admin_command_mainlobby_info"));
                        } catch (SerializationException e) {
                            e.printStackTrace();
                        }
                    } else if (action.contains("set")) {
                        var location = sender.as(Player.class).getLocation();

                        try {
                            Main.getConfigurator().node("mainlobby", "location").set(MiscUtils.setLocationToString(location));
                            Main.getConfigurator().node("mainlobby", "world").set(location.getWorld().getName());
                            Main.getConfigurator().saveConfig();

                            sender.sendMessage(i18n("admin_command_success"));
                        } catch (SerializationException e) {
                            e.printStackTrace();
                        }
                    }
                })
        );
    }
}
