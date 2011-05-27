package com.lhs;

public enum GunMessage {
	GOTSHOT("gotshot"),
	FIREDSHOT("fired"),
	HEALTHUPDATE("healthupdate"),
	SHIELDUPDATE("shieldupdate"),			//player stats
	DISCONNECT("disconnect"),
	CONNECTED("connected");

	private String text;

	GunMessage(String text){
		this.text = text;
	}

	public String getText() {
		return this.text;
	}

	public static GunMessage fromString(String text) {
		if (text != null) {
			for (GunMessage b : GunMessage.values()) {
				if (text.equalsIgnoreCase(b.text)) {
					return b;
				}
			}
			return null;
		}
		return null;
	}
}
