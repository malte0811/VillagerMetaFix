package malte0811.villagerMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import cpw.mods.fml.client.FMLFileResourcePack;
import cpw.mods.fml.client.FMLFolderResourcePack;
import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.versioning.ArtifactVersion;
import cpw.mods.fml.common.versioning.VersionParser;
import cpw.mods.fml.common.versioning.VersionRange;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import cpw.mods.fml.relauncher.Side;
import malte0811.villagerMeta.client.ClientEventHandler;
import malte0811.villagerMeta.network.MessageTradeSync;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
@MCVersion(value="1.7.10")
public class VillageContainer extends DummyModContainer {
	public static final String MODID = "VillagerMetaFix";
	public static final SimpleNetworkWrapper network = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
	public static void loadConfig() {

	}

	public VillageContainer() {
		super(new ModMetadata());
		getMetadata().modId=MODID;
		ArrayList<String> l = new ArrayList<>();
		l.add("malte0811");
		getMetadata().authorList = l;
		getMetadata().description="A small core mod that makes villagers check that they are buying items with the correct metadata";
		getMetadata().name="VillagerMetaFix";
		getMetadata().version="0.1";
		
	}

	@Override
	public boolean registerBus(EventBus bus, LoadController controller) {
		bus.register(this);
		return true;
	}
	@Override
	public List<ArtifactVersion> getDependants() {
		ArrayList<ArtifactVersion> ret = new ArrayList<>();
		return ret;
	}


	@Override
	public VersionRange acceptableMinecraftVersionRange() {
		return VersionParser.parseRange("1.7.10");
	}
	@Override
	public Disableable canBeDisabled() {
		return Disableable.NEVER;
	}
	@Subscribe
	public void preInit(FMLPreInitializationEvent ev) {
		Config.config = new Configuration(ev.getSuggestedConfigurationFile());
		Config.init();
	}

	@Subscribe
	public void init(FMLInitializationEvent ev) {
		if (Config.enableGuiIcons()) {
			MinecraftForge.EVENT_BUS.register(new EventHandler());
			FMLCommonHandler.instance().bus().register(new EventHandler());
			if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
				MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
			}
			int id = 0;
			network.registerMessage(MessageTradeSync.Handler.class, MessageTradeSync.class, id++, Side.CLIENT);
		}
	}
	@Override
	public Class<?> getCustomResourcePackClass() {
		return VillagerMetaFix.location.isDirectory()?FMLFolderResourcePack.class:FMLFileResourcePack.class;
	}
	@Override
	public File getSource() {
		return VillagerMetaFix.location;
	}
}