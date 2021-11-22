package org.screamingsandals.bedwars.commands;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.player.PlayerWrapper;
import org.screamingsandals.lib.sender.CommandSenderWrapper;
import org.screamingsandals.lib.utils.annotations.Service;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.List;

@Service
public class MainlobbyCommand extends BaseCommand {
    public MainlobbyCommand() {
        super("mainlobby", BedWarsPermission.ADMIN_PERMISSION, false);
    }

    @Override
    protected void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder, CommandManager<CommandSenderWrapper> manager) {
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
                            MainConfig.getInstance().node("mainlobby", "enabled").set(true);
                            MainConfig.getInstance().saveConfig();

                            Message
                                    .of(LangKeys.SUCCESS)
                                    .join(LangKeys.ADMIN_MAINLOBBY_INFO)
                                    .defaultPrefix()
                                    .send(sender);
                        } catch (SerializationException e) {
                            e.printStackTrace();
                        }
                    } else if (action.contains("set")) {
                        var location = sender.as(PlayerWrapper.class).getLocation();

                        try {
                            MainConfig.getInstance().node("mainlobby", "location").set(MiscUtils.writeLocationToString(location));
                            MainConfig.getInstance().node("mainlobby", "world").set(location.getWorld().getName());
                            MainConfig.getInstance().saveConfig();

                            sender.sendMessage(Message.of(LangKeys.SUCCESS).defaultPrefix());
                        } catch (SerializationException e) {
                            e.printStackTrace();
                        }
                    }
                })
        );
    }
}
