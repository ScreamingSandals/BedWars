package org.screamingsandals.bedwars.commands;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.screamingsandals.bedwars.BedWarsPlugin;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.sender.CommandSenderWrapper;
import org.screamingsandals.lib.utils.annotations.Service;

import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;

// TODO: Rewrite this command
@Service
public class LanguageCommand extends BaseCommand {

    private static final List<String> languages = List.of("en-US");

    public LanguageCommand() {
        super("lang", BedWarsPermission.ADMIN_PERMISSION, true);
    }

    @Override
    protected void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder, CommandManager<CommandSenderWrapper> manager) {
        manager.command(
                commandSenderWrapperBuilder
                        .argument(manager
                                .argumentBuilder(String.class, "language")
                                .withSuggestionsProvider((c, s) -> languages)
                        )
                    .handler(commandContext -> {
                        final var sender = commandContext.getSender();
                        try {
                            final String locale = commandContext.get("language");
                            final var config = new YamlConfiguration();
                            var file = BedWarsPlugin.getInstance().getPluginDescription().getDataFolder().resolve("languages").resolve("language_" + locale + ".yml").toFile();

                            if (file.exists()) {
                                config.load(file);
                            } else {
                                final var ins = BedWarsPlugin.class.getResourceAsStream("languages/language_" + locale + ".yml");
                                config.load(new InputStreamReader(ins));
                            }
                            final var langName = Objects.requireNonNull(config.getString("lang_name"));

                            if (Objects.requireNonNull(MainConfig.getInstance().node("locale").getString())
                                    .equalsIgnoreCase(locale)) {
                                sender.sendMessage(Message.of(LangKeys.LANGUAGE_ALREADY_SET).defaultPrefix()
                                        .placeholder("lang", langName));
                                return;
                            }
                            MainConfig.getInstance().node("locale").set(locale);
                            MainConfig.getInstance().saveConfig();
                            Bukkit.getServer().getPluginManager().disablePlugin(BedWarsPlugin.getInstance().getPluginDescription().as(JavaPlugin.class));
                            Bukkit.getServer().getPluginManager().enablePlugin(BedWarsPlugin.getInstance().getPluginDescription().as(JavaPlugin.class));
                            sender.sendMessage(Message.of(LangKeys.LANGUAGE_SUCCESS).defaultPrefix().placeholder("lang", langName));
                        } catch (Exception e) {
                            sender.sendMessage(Message.of(LangKeys.LANGUAGE_USAGE_BW_LANG).defaultPrefix());
                        }
                    })
        );
    }
}
