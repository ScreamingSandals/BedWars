package org.screamingsandals.bedwars.commands;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.lib.sender.CommandSenderWrapper;

import java.io.File;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;

import static org.screamingsandals.bedwars.lib.lang.I.i18n;

public class LanguageCommand extends BaseCommand {

    private static final List<String> languages = List.of("af", "ar", "bs", "ca", "cs", "da", "de",
            "el", "en", "en-UD", "es", "fi", "fr", "he", "hr", "hu", "id", "it", "ja", "ko", "lv",
            "nl", "no", "pl", "pt", "pt-BR", "ro", "ru", "sk", "sl", "sr", "sr-CS", "sv", "th",
            "tr", "uk", "vi", "zh", "zh-CN");

    public LanguageCommand(CommandManager<CommandSenderWrapper> manager) {
        super(manager, "lang", BedWarsPermission.ADMIN_PERMISSION, true);
    }

    @Override
    protected void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
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
                            var file = Main.getInstance().getPluginDescription().getDataFolder().resolve("languages").resolve("language_" + locale + ".yml").toFile();

                            if (file.exists()) {
                                config.load(file);
                            } else {
                                final var ins = Main.class.getResourceAsStream("languages/language_" + locale + ".yml");
                                config.load(new InputStreamReader(ins));
                            }
                            final var langName = Objects.requireNonNull(config.getString("lang_name"));

                            if (Objects.requireNonNull(MainConfig.getInstance().node("locale").getString())
                                    .equalsIgnoreCase(locale)) {
                                sender.sendMessage(i18n("language_already_set")
                                        .replace("%lang%", langName));
                                return;
                            }
                            MainConfig.getInstance().node("locale").set(locale);
                            MainConfig.getInstance().saveConfig();
                            Bukkit.getServer().getPluginManager().disablePlugin(Main.getInstance().getPluginDescription().as(JavaPlugin.class));
                            Bukkit.getServer().getPluginManager().enablePlugin(Main.getInstance().getPluginDescription().as(JavaPlugin.class));
                            sender.sendMessage(i18n("language_success")
                                    .replace("%lang%", langName));
                        } catch (Exception e) {
                            sender.sendMessage(i18n("usage_bw_lang"));
                        }
                    })
        );
    }
}
