package org.screamingsandals.bedwars.lib.lang;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Message {
	private String key;
	private ITranslateContainer container;
	private String def;
	private boolean prefix;
	private String prefixTranslate = null;
	private Map<String, MessageReplacement> replaces = new HashMap<>();
	
	public Message(String key, ITranslateContainer container) {
		this(key, container, null, false);
	}
	
	public Message(String key, ITranslateContainer container, boolean prefix) {
		this(key, container, null, prefix);
	}
	
	public Message(String key, ITranslateContainer container, String def) {
		this(key, container, def, false);
	}

	public Message(String key, ITranslateContainer container, String def, boolean prefix) {
		this.key = key;
		this.container = container;
		this.def = def;
		this.prefix = prefix;
	}
	
	public Message replace(String placeholder, MessageReplacement replacement) {
		if (placeholder.startsWith("%")) {
			placeholder = placeholder.substring(1);
		}
		if (placeholder.endsWith("%")) {
			placeholder = placeholder.substring(0, placeholder.length() - 1);
		}
		replaces.put(placeholder, replacement);
		return this;
	}
	
	public Message replace(String placeholder, String replacement) {
		return replace(placeholder, receiver -> replacement);
	}
	
	public Message replace(String placeholder, byte replacement) {
		return replace(placeholder, receiver -> String.valueOf(replacement));
	}
	
	public Message replace(String placeholder, short replacement) {
		return replace(placeholder, receiver -> String.valueOf(replacement));
	}
	
	public Message replace(String placeholder, int replacement) {
		return replace(placeholder, receiver -> String.valueOf(replacement));
	}
	
	public Message replace(String placeholder, long replacement) {
		return replace(placeholder, receiver -> String.valueOf(replacement));
	}
	
	public Message replace(String placeholder, float replacement) {
		return replace(placeholder, receiver -> String.valueOf(replacement));
	}
	
	public Message replace(String placeholder, double replacement) {
		return replace(placeholder, receiver -> String.valueOf(replacement));
	}
	
	public Message replace(String placeholder, boolean replacement) {
		return replace(placeholder, receiver -> String.valueOf(replacement));
	}
	
	public Message replace(String placeholder, char replacement) {
		return replace(placeholder, receiver -> String.valueOf(replacement));
	}
	
	public Message key(String key) {
		this.key = key;
		return this;
	}
	
	public Message def(String def) {
		this.def = def;
		return this;
	}
	
	public Message clearReplaces() {
		this.replaces.clear();
		return this;
	}
	
	public Message reset(String key) {
		return reset(key, null, false);
	}
	
	public Message reset(String key, String def) {
		return reset(key, def, false);
	}
	
	public Message reset(String key, String def, boolean prefix) {
		this.key = key;
		this.def = def;
		this.prefix = prefix;
		
		return clearReplaces();
	}
	
	public Message prefix() {
		return prefix(true);
	}
	
	public Message prefix(boolean prefix) {
		this.prefix = prefix;
		return this;
	}

	public Message prefix(String prefixTranslate) {
		this.prefixTranslate = prefixTranslate;
		this.prefix = true;
		return this;
	}
	
	public Message unprefix() {
		return prefix(false);
	}
	
	public Message container(ITranslateContainer container) {
		this.container = container;
		return this;
	}
	
	public String toString() {
		return get();
	}
	
	public String get() {
		return container.translate(key, def, prefix);
	}

	public Message send(Object sender) {
		if (sender instanceof Collection) {
			for (Object rec : (Collection<?>) sender) {
				send(rec);
			}
			return this;
		}
		
		String message = get(sender);
		
		try {
			sender.getClass().getMethod("sendMessage", String.class).invoke(sender, message);
		} catch (Throwable t) {
		}
		return this;
	}
	
	public String get(Object sender) {
		String name = "";

		try {
			name = (String) sender.getClass().getMethod("getName").invoke(sender);
		} catch (Throwable t) {
		}
		
		MessageReceiver receiver = new MessageReceiver(sender, name);
		
		return get(receiver);
	}
	
	public String get(MessageReceiver receiver) {
		String message = container.translate(key, def, prefix, prefixTranslate);
		
		for (Map.Entry<String, MessageReplacement> replace : replaces.entrySet()) {
			message = message.replaceAll("%" + replace.getKey() + "%", replace.getValue().replace(receiver));
		}
		
		return message;
	}
}
