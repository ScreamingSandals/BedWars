# Language

## Using built-in languages

To use one of the built-in languages, update the `locale` value in your `config.yml` file to your desired language code. Below is a list of available language codes:  

```yaml
# Example: Set to Czech (Čeština)
locale: cs
```

| Language Code | Language Name            | Native Name              |
|---------------|--------------------------|--------------------------|
| `af`          | Afrikaans                | Afrikaans                |
| `ar`          | Arabic                   | العربية                  |
| `bg`          | Bulgarian                | Български                |
| `bs`          | Bosnian                  | Bosanski                 |
| `ca`          | Catalan                  | Català                   |
| `cs`          | Czech                    | Čeština                  |
| `da`          | Danish                   | Dansk                    |
| `de`          | German                   | Deutsch                  |
| `el`          | Greek                    | Ελληνικά                 |
| `en-UD`       | English (Upside Down)    | English (Upside Down)    |
| `en`          | English                  | English                  |
| `es`          | Spanish                  | Español                  |
| `fi`          | Finnish                  | Suomi                    |
| `fr`          | French                   | Français                 |
| `he`          | Hebrew                   | עברית                    |
| `hr`          | Croatian                 | Hrvatski                 |
| `hu`          | Hungarian                | Magyar                   |
| `id`          | Indonesian               | Bahasa Indonesia         |
| `it`          | Italian                  | Italiano                 |
| `ja`          | Japanese                 | 日本語                    |
| `ko`          | Korean                   | 한국어                    |
| `lv`          | Latvian                  | Latviešu                 |
| `nl`          | Dutch                    | Nederlands               |
| `no`          | Norwegian                | Norsk                    |
| `pl`          | Polish                   | Polski                   |
| `pt-BR`       | Portuguese (Brazil)      | Português (Brasil)       |
| `pt`          | Portuguese               | Português                |
| `ro`          | Romanian                 | Română                   |
| `ru`          | Russian                  | Русский                  |
| `sk`          | Slovak                   | Slovenčina               |
| `sl`          | Slovenian                | Slovenščina              |
| `sr-CS`       | Serbian (Cyrillic)       | Српски (ћирилица)        |
| `sr`          | Serbian (Latin)          | Srpski (latinica)        |
| `sv`          | Swedish                  | Svenska                  |
| `th`          | Thai                     | ภาษาไทย                  |
| `tr`          | Turkish                  | Türkçe                   |
| `uk`          | Ukrainian                | Українська               |
| `vi`          | Vietnamese               | Tiếng Việt               |
| `zh-CN`       | Chinese (Simplified)     | 简体中文                  |
| `zh`          | Chinese (Traditional)    | 繁體中文                  |

These translations are maintained by the community and may not always be fully complete. If you would like to help improve them, you can contribute on our [Weblate instance](https://weblate.screamingsandals.org/projects/bedwars/0-2-x/).

!!! note "Using Norwegian language"

    YAML treats the literal value `no` as `false`. To use `no` as the Norwegian language code, you must cast it to a string like this:  
    ```yaml
    locale: "no"
    ```

!!! note "Empty language folder"

    The `languages` folder is typically empty because the built-in languages are stored within the plugin jar itself. You do not need to download any files unless you plan to customize the translations.


These translations are maintained by the community and may not be 100% complete. You can help us translating them [on our Weblate instance](https://weblate.screamingsandals.org/projects/bedwars/0-2-x/).

## Customizing language files

If you wish to make changes to language files, follow these steps:

1. **Download the base language file**  
   You can download the base language files from our [GitHub repository](https://github.com/ScreamingSandals/BedWars/tree/ver/0.2.x/plugin/src/main/resources/languages).
2. **Add the file to your languages folder**  
   Place your modified language file in the `languages` folder inside your `BedWars` folder (located in the default `plugins` folder). For example, to modify the Czech language, create a file named `language_cs.yml`.
3. **Update `config.yml`**  
   Update the `locale` variable in your `config.yml` to match your file’s language code.  
   ```yaml
   # Example for Czech:
   locale: cs
   ```

!!! tip

    While you can download and modify the entire language file, it is better to create an empty language file and include only the lines you want to override.
    This ensures that any untranslated lines will still use the updated built-in versions. If you paste the unchanged lines into your custom file, they will override the built-in translations, even if the plugin updates them later.

## Creating a new language

To create a completely new language, follow these steps:  

1. Download the English language file from the [GitHub repository](https://github.com/ScreamingSandals/BedWars/tree/ver/0.2.x/plugin/src/main/resources/languages).  
2. Save the file as `language_myCode.yml`, where `myCode` is your desired language code.  
3. Translate the individual lines into your new language.  
4. Update the `locale` variable in your `config.yml` to your new language code:  
   ```yaml
   locale: myCode
   ```  

!!! tip

    If you create a new language, consider helping us add it to the plugin by contributing to our Weblate instance! This way, your language becomes available to everyone in future plugin updates. Visit our [Weblate instance](https://weblate.screamingsandals.org/projects/bedwars/0-2-x/) to get started.
