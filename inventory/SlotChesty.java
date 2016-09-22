package net.nexustools.chesty.inventory;

import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.nexustools.chesty.entity.passive.EntityChesty;
import net.nexustools.chesty.item.ItemChestySceptre;

public class SlotChesty extends Slot {
	public SlotChesty(EntityChesty par2IInventory, int par3, int par4, int par5) {
		super(par2IInventory, par3, par4, par5);
	}

	@Override
	public boolean isItemValid(ItemStack par1ItemStack) {
		return par1ItemStack != null && !(par1ItemStack.getItem() instanceof ItemChestySceptre);
	}
}
