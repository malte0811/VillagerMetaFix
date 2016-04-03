package malte0811.villagerMeta.client;

import java.util.Arrays;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.client.event.GuiScreenEvent;

public class ClientEventHandler {
	public static boolean[] nbt;
	public static boolean[] meta;
	public static boolean[] current;//0 is Meta, 1 is NBT
	private static ResourceLocation tex = new ResourceLocation("villagermetafix:textures/misc/MetaNBT.png");
	@SubscribeEvent
	public void onDrawGuiPost(GuiScreenEvent.DrawScreenEvent.Post event) {
		if (!event.isCanceled()&&(event.gui instanceof GuiMerchant)&&nbt!=null&&meta!=null) {
			GuiMerchant gm = (GuiMerchant) event.gui;
			if (current==null) {
				int i = gm.field_147041_z;
				current = new boolean[]{ClientEventHandler.meta[i], ClientEventHandler.nbt[i]};
			}
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_BLEND);
			OpenGlHelper.glBlendFunc(770, 771, 1, 0);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			Minecraft.getMinecraft().getTextureManager().bindTexture(tex);
			for (int i = 0;i<2;i++) {
				gm.drawTexturedModalRect(gm.guiLeft+10+19*i, gm.guiTop+4, (current[i]?0:18), 18*i, 18, 18);
			}
			GL11.glEnable(GL11.GL_LIGHTING);
			// tooltips
			int x = event.mouseX;
			int y = event.mouseY;
			if (y>gm.guiTop+4&&y<gm.guiTop+22) {
				if (x>gm.guiLeft+10&&x<gm.guiLeft+28) {//meta
					gm.func_146283_a(Arrays.asList(new String[]{StatCollector.translateToLocal("gui.villagerMetaFix."+(current[0]?"checkM":"ignoreM"))}), x, y);
				} else if (x>gm.guiLeft+29&&x<gm.guiLeft+47) {//nbt
					gm.func_146283_a(Arrays.asList(new String[]{StatCollector.translateToLocal("gui.villagerMetaFix."+(current[1]?"checkNBT":"ignoreNBT"))}), x, y);
				}
			}
		}
	}

	@SubscribeEvent
	public void onGuiOpened(GuiScreenEvent.InitGuiEvent.Pre event) {
				if (!event.isCanceled()&&event.gui instanceof GuiMerchant) {
					current = null;
//					nbt = null;
//					meta = null;
				}
	}
	@SubscribeEvent
	public void onGuiAction(GuiScreenEvent.ActionPerformedEvent.Post event) {
		if (!event.isCanceled()&&event.gui instanceof GuiMerchant) {
			GuiMerchant gm = (GuiMerchant) event.gui;
			if (event.button==gm.field_147042_y||event.button==gm.field_147043_x) {
				current = null;
			}
		}
	}
}
