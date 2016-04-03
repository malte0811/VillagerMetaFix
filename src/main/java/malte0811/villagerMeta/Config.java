package malte0811.villagerMeta;

import net.minecraftforge.common.config.Configuration;

public class Config {
	private static boolean metaOnByDefault = true;
	private static boolean nbtOnByDefault = false;
	private static boolean gui = true;
	public static Configuration config;
	public static void init() {
		config.load();
		metaOnByDefault = config.getBoolean("checkMetaByDefault", "general", true, "Whether villagers check the metadata of the given items by default (defaul:true)");
		nbtOnByDefault = config.getBoolean("checkNbtByDefault", "general", false, "Whether villagers check the NBT tag of the given items by default (defaul:false)");
		gui = config.getBoolean("guiIcons", "general", true, "Set to false to disable the icons in the villager trading GUI indicating whether a trade is NBT/meta-sensitive");
		config.save();
	}
	public static boolean useMetaOnDefault() {
		return metaOnByDefault;
	}
	public static boolean useNbtOnDefault() {
		return nbtOnByDefault;
	}
	public static boolean enableGuiIcons() {
		return gui;
	}
}
