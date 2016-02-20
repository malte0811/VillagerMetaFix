package malte0811.villagerMeta;

import java.util.Map;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

public class VillagerMetaFix implements IFMLLoadingPlugin{

	@Override
	public String[] getASMTransformerClass() {
		return new String[]{"malte0811.villagerMeta.VillageTransformer"};
	}

	@Override
	public String getModContainerClass() {
		return "malte0811.villagerMeta.VillageContainer";
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getAccessTransformerClass() {
		return "malte0811.villagerMeta.VillageTransformer";
	}


}
