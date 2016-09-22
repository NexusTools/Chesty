package net.nexustools.chesty.inventory;

import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.nexustools.chesty.entity.passive.EntityChesty;

public class SlotChestyFood extends SlotChesty {
	public SlotChestyFood(EntityChesty par2IInventory, int par3, int par4, int par5) {
		super(par2IInventory, par3, par4, par5);
	}

	@Override
	public boolean isItemValid(ItemStack par1ItemStack) {
		return par1ItemStack != null && par1ItemStack.getItem() instanceof ItemFood;
	}
}
