package org.screamingsandals.bedwars.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.Main;

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

    public LanguageCommand() {
        super("lang", ADMIN_PERMISSION, false, false);
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        final var player = (Player) sender;
        if (args.size() == 1) {
            try {
                final var locale = args.get(0);
                final var config = new YamlConfiguration();
                var file = new File(Main.getInstance().getDataFolder().toString()
                        + "/languages", "language_" + locale + ".yml");

                if (file.exists()) {
                    config.load(file);
                } else {
                    final var ins = Main.getInstance()
                            .getResource("languages/language_" + locale + ".yml");
                    config.load(new InputStreamReader(Objects.requireNonNull(ins)));
                }
                final var langName = Objects.requireNonNull(config.getString("lang_name"));

                if (Objects.requireNonNull(Main.getConfigurator().config.getString("locale"))
                        .equalsIgnoreCase(locale)) {
                    player.sendMessage(i18n("language_already_set")
                            .replace("%lang%", langName));
                    return true;
                }
                Main.getConfigurator().config.set("locale", locale);
                Main.getConfigurator().saveConfig();
                Bukkit.getServer().getPluginManager().disablePlugin(Main.getInstance());
                Bukkit.getServer().getPluginManager().enablePlugin(Main.getInstance());
                player.sendMessage(i18n("language_success")
                        .replace("%lang%", langName));
            } catch (Exception e) {
                player.sendMessage(i18n("usage_bw_lang"));
            }
        } else {
            player.sendMessage(i18n("usage_bw_lang"));
        }
        return true;
    }

    @Override
    public void completeTab(List<String> completion, CommandSender sender, List<String> args) {
        if (args.size() == 1) {
            args.addAll(languages);
        }
    }
}
