package net.nexustools.chesty.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.StatCollector;
import net.nexustools.chesty.inventory.ContainerChesty;
import net.nexustools.chesty.entity.passive.EntityChesty;
import org.lwjgl.opengl.GL11;

public class GuiChesty extends GuiContainer {
	private final int invRowsChest;
	private final int rowLength;
	public GuiChesty(InventoryPlayer inventoryPlayer, EntityChesty chesty) {
		super(new ContainerChesty(inventoryPlayer, chesty));
        this.allowUserInput = false;
		invRowsChest = (chesty.getActualSizeInventory()) / chesty.getRowLength();
		rowLength = chesty.getRowLength();
		if(rowLength == 9) {
			xSize = 176;
		} else if(rowLength == 12) {
			xSize = 230;
		}
		ySize = 16 + 18 + 4 + (invRowsChest * 18) + 14 + ((inventoryPlayer.getSizeInventory() / 9) * 18) + 4 + 18 + 7 - 17; // - 17 for some reason, not sure why.
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of
	 * the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		fontRenderer.drawString(StatCollector.translateToLocal("entity.EntityChesty.inventory.description"), 8, 6, 4210752);
		fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, this.ySize - 94, 4210752);
	}

	/**
	 * Draw the background layer for the GuiContainer (everything behind the
	 * items)
	 */
	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
		int var4 = mc.renderEngine.getTexture("/net/nexustools/chesty/client/gui/GuiChesty " + invRowsChest + " " + rowLength + ".png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(var4);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	}
}
