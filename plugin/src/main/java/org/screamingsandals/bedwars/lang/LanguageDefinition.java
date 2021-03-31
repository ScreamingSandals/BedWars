package org.screamingsandals.bedwars.lang;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class LanguageDefinition {
    private final String branch;
    private final String version;
    private final Map<String, String> languages = new HashMap<>();
}
