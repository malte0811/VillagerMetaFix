package malte0811.villagerMeta;

import java.util.HashMap;
import java.util.Iterator;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;
import cpw.mods.fml.relauncher.Side;
import malte0811.villagerMeta.api.VillagerHelper;
import malte0811.villagerMeta.network.MessageTradeSync;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraftforge.event.entity.player.EntityInteractEvent;

public class EventHandler {
	private static final HashMap<EntityPlayerMP, EntityVillager> startingTrades = new HashMap<>();
	@SubscribeEvent
	public void onEntityInteract(EntityInteractEvent e) {
		if (e.isCanceled()||!(e.target instanceof EntityVillager)||!(e.entityPlayer instanceof EntityPlayerMP)) {
			return;
		}
		EntityPlayerMP player = (EntityPlayerMP)e.entityPlayer;
		EntityVillager villager = (EntityVillager) e.target;
		ItemStack itemstack = player.inventory.getCurrentItem();
		boolean flag = itemstack != null && itemstack.getItem() == Items.spawn_egg;
		
		if (!flag && villager.isEntityAlive() && !villager.isTrading() && !villager.isChild() && !player.isSneaking()) {
			startingTrades.put(player, villager);
		}
	}
	@SubscribeEvent
	public void onWorldTick(WorldTickEvent e) {
		if (e.phase!=Phase.END||e.side!=Side.SERVER) {
			return;
		}
		Iterator<EntityPlayerMP> it = startingTrades.keySet().iterator();
		while (it.hasNext()) {
			EntityPlayerMP p = it.next();
			MerchantRecipeList l = startingTrades.get(p).getRecipes(p);
			if (l==null) {
				continue;
			}
			int length = l.size();
			boolean[] meta = new boolean[length];
			boolean[] nbt = new boolean[length];
			for (int i = 0;i<length;i++) {
				meta[i] = VillagerHelper.isMetaSensitive((MerchantRecipe)l.get(i));
				nbt[i] = VillagerHelper.isNbtSensitive((MerchantRecipe)l.get(i));
			}
			MessageTradeSync mess = new MessageTradeSync(nbt, meta);
			VillageContainer.network.sendTo(mess, p);
			it.remove();
		}
	}
}
