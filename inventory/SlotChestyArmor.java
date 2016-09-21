package net.nexustools.chesty.inventory;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SlotChestyArmor extends Slot {

	final int armorType;
	
	public SlotChestyArmor(IInventory par2IInventory, int par3, int par4, int par5, int par6) {
		super(par2IInventory, par3, par4, par5);
		this.armorType = par6;
	}

	@Override
	public int getSlotStackLimit() {
		return 1;
	}

	@Override
	public boolean isItemValid(ItemStack par1ItemStack) {
		Item item = (par1ItemStack == null ? null : par1ItemStack.getItem());
		return item != null && item.isValidArmor(par1ItemStack, armorType);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getBackgroundIconIndex() {
		return 15 + this.armorType * 16;
	}
}
