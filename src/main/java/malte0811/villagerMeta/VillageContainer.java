package malte0811.villagerMeta;

import java.util.ArrayList;
import java.util.List;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.versioning.ArtifactVersion;
import cpw.mods.fml.common.versioning.VersionParser;
import cpw.mods.fml.common.versioning.VersionRange;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import net.minecraftforge.common.config.Configuration;
@MCVersion(value="1.7.10")
public class VillageContainer extends DummyModContainer
{

	public static void loadConfig() {

	}

	public VillageContainer() {
		super(new ModMetadata());
		getMetadata().modId="VillagerMetaFix";
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
}