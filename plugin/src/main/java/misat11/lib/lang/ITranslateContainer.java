package misat11.lib.lang;

public interface ITranslateContainer {
	String getLocaleCode();

	default String getLanguage() {
		return translate("lang_name");
	}

	default String getFallbackLanguage() {
		return getFallbackContainer() != null ? getFallbackContainer().getLanguage() : null;
	}

	default String translate(String key) {
		return translate(key, null);
	}

	String translate(String key, String def);

	default String translate(String key, String def, boolean prefix) {
		if (prefix) {
			return translateWithPrefix(key, def);
		} else {
			return translate(key, def);
		}
	}

	default String translate(String key, boolean prefix) {
		return translate(key, null, prefix);
	}

	default String getPrefix() {
		return translate("prefix", "");
	}

	default String translateWithPrefix(String key) {
		return translateWithPrefix(key, null);
	}

	default String translateWithPrefix(String key, String def) {
		String value = "";
		String prefix = getPrefix();
		if (prefix != null && prefix.length() > 0) {
			value += prefix + " ";
		}
		value += translate(key, def);
		return value;
	}

	ITranslateContainer getFallbackContainer();
}
