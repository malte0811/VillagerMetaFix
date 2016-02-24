package malte0811.villagerMeta.api;

import java.lang.reflect.Field;

import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;

public class VillagerHelper {
	private static Field meta = null, nbt = null;
	public static boolean isMetaOrNbtInvalid(MerchantRecipe p0, ItemStack p1, ItemStack p2) {
		if (isMetaSensitive(p0))
		{
			if (p1!=null&&p0.getItemToBuy()!=null&&p1.getItemDamage()!=p0.getItemToBuy().getItemDamage())
				return true;
			if (p2!=null&&p0.getSecondItemToBuy()!=null&&p2.getItemDamage()!=p0.getSecondItemToBuy().getItemDamage())
				return true;
		}
		if (isNbtSensitive(p0))
		{
			if (!areTagsEqual(p1, p0.getItemToBuy()))
				return true;
			if (!areTagsEqual(p2, p0.getSecondItemToBuy()))
				return true;
		}
		return false;
	}
	public static boolean isMetaSensitive(MerchantRecipe r) {
		initMeta();
		try {
			return meta.getBoolean(r);
		} catch (IllegalArgumentException | IllegalAccessException | NullPointerException e) {
			return false;
		}
	}
	public static boolean isNbtSensitive(MerchantRecipe r) {
		initNbt();
		try {
			return nbt.getBoolean(r);
		} catch (IllegalArgumentException | IllegalAccessException | NullPointerException e) {
			return false;
		}
	}
	private static void initNbt() {
		if (nbt==null) {
			Class<MerchantRecipe> c = MerchantRecipe.class;
				try {
					nbt = c.getDeclaredField("checkNbt");
					if (!nbt.isAccessible()) {
						nbt.setAccessible(true);
					}
				} catch (NoSuchFieldException | SecurityException e) {
					e.printStackTrace();
				}
		}
	}
	private static void initMeta() {
		if (meta==null) {
			Class<MerchantRecipe> c = MerchantRecipe.class;
				try {
					meta = c.getDeclaredField("checkMeta");
					if (!meta.isAccessible()) {
						meta.setAccessible(true);
					}
				} catch (NoSuchFieldException | SecurityException e) {
					e.printStackTrace();
				}
		}
	}
	
	public static MerchantRecipe createRecipe(ItemStack b1, ItemStack b2, ItemStack sell, boolean metaSensitive, boolean nbtSensitive) {
		MerchantRecipe ret = new MerchantRecipe(b1, b2, sell);
		initMeta();
		initNbt();
		if (meta!=null&&nbt!=null) {
			try {
				meta.setBoolean(ret, metaSensitive);
				nbt.setBoolean(ret, nbtSensitive);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			} 
		}
		return ret;
	}
	public static boolean areTagsEqual(ItemStack p, ItemStack q) {
		if (p==null&&q==null) {
			return true;
		}
		if (p==null||q==null) {
			return false;
		}
		if (p.stackTagCompound==null&&q.stackTagCompound==null) {
			return true;
		}
		// an empty compound is as good as no compound at all
		if (p.stackTagCompound==null&&q.stackTagCompound.hasNoTags()) {
			return true;
		}
		if (q.stackTagCompound==null&&p.stackTagCompound.hasNoTags()) {
			return true;
		}
		return ItemStack.areItemStackTagsEqual(p, q);
	}
	public static boolean hasSameInputMetaNBT(MerchantRecipe p0, MerchantRecipe p1) {
		if (isMetaSensitive(p0)||isMetaSensitive(p1))
		{
			if (p1.getItemToBuy()!=null&&p0.getItemToBuy()!=null&&p1.getItemToBuy().getItemDamage()!=p0.getItemToBuy().getItemDamage())
				return false;
			if (p1.getSecondItemToBuy()!=null&&p0.getSecondItemToBuy()!=null&&p1.getSecondItemToBuy().getItemDamage()!=p0.getSecondItemToBuy().getItemDamage())
				return false;
		}
		if (isNbtSensitive(p0)||isNbtSensitive(p1))
		{
			if (!areTagsEqual(p1.getItemToBuy(), p0.getItemToBuy()))
				return false;
			if (!areTagsEqual(p1.getSecondItemToBuy(), p0.getSecondItemToBuy()))
				return false;
		}
		return true;
	}
}
