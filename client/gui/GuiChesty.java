package net.nexustools.chesty.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.StatCollector;
import net.nexustools.chesty.inventory.ContainerChesty;
import net.nexustools.chesty.entity.passive.EntityChesty;
import org.lwjgl.opengl.GL11;

public class GuiChesty extends GuiContainer {
	int inventoryRows;
	public GuiChesty(InventoryPlayer inventoryPlayer, EntityChesty chesty) {
		super(new ContainerChesty(inventoryPlayer, chesty));
        this.allowUserInput = false;
        short var3 = 172;
        int var4 = var3 - 108;
		inventoryRows = (chesty.getSizeInventory()) / 9;
		inventoryRows += (inventoryPlayer.getSizeInventory()) / 9;
        this.ySize = var4 + inventoryRows * 18;
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of
	 * the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		fontRenderer.drawString(StatCollector.translateToLocal("entity.Chesty.Chesty.inventory_description"), 8, 6, 4210752);
		fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
	}

	/**
	 * Draw the background layer for the GuiContainer (everything behind the
	 * items)
	 */
	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
		int var4 = mc.renderEngine.getTexture("/net/nexustools/chesty/client/gui/GuiChesty.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(var4);
        int var5 = (this.width - this.xSize) / 2;
        int var6 = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(var5, var6, 0, 0, this.xSize, this.inventoryRows * 18 + 18);
        this.drawTexturedModalRect(var5, var6 + this.inventoryRows * 18 + 18, 0, 126, this.xSize, 96);
	}
}
