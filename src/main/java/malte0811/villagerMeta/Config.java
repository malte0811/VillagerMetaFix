package malte0811.villagerMeta;

import net.minecraftforge.common.config.Configuration;

public class Config {
	private static boolean metaOnByDefault = true;
	private static boolean nbtOnByDefault = false;
	public static Configuration config;
	public static void init() {
		config.load();
		metaOnByDefault = config.getBoolean("checkMetaByDefault", "general", true, "Whether villagers check the metadata of the given items by default (defaul:true)");
		nbtOnByDefault = config.getBoolean("checkNbtByDefault", "general", false, "Whether villagers check the NBT tag of the given items by default (defaul:false)");
		config.save();
	}
	public static boolean useMetaOnDefault() {
		return metaOnByDefault;
	}
	public static boolean useNbtOnDefault() {
		return nbtOnByDefault;
	}
	
}
