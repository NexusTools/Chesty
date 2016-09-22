package net.nexustools.chesty.inventory;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.nexustools.chesty.Chesty;
import net.nexustools.chesty.entity.passive.EntityChesty;
import net.nexustools.chesty.item.ItemChestySceptre;

public class ContainerChesty extends Container {

	public final EntityChesty chesty;

	public ContainerChesty(InventoryPlayer inventoryPlayer, EntityChesty chesty) {
		this.chesty = chesty;
		chesty.openChest();
		this.addSlotToContainer(new SlotChestyFood(chesty, 0, 8, 18));
		this.addSlotToContainer(new SlotChestyFood(chesty, 1, 26, 18));
		for(int i = 0; i < 4; i++) {
			this.addSlotToContainer(new SlotChestyArmor(chesty, 2 + i, 98 + (i * 18), 18, i));
		}
		for(int var3 = 0; var3 < chesty.getSizeInventory() / 9; ++var3) {
			for(int var4 = 0; var4 < 9; ++var4) {
				this.addSlotToContainer(new SlotChesty(chesty, var4 + var3 * 9 + EntityChesty.SPECIAL_SLOTS_SIZE, 8 + var4 * 18, 40 + var3 * 18));
			}
		}

		for(int var3 = 0; var3 < 3; ++var3) {
			for(int var4 = 0; var4 < 9; ++var4) {
				this.addSlotToContainer(new Slot(inventoryPlayer, var4 + var3 * 9 + 9, 8 + var4 * 18, 90 + var3 * 18));
			}
		}

		for(int var3 = 0; var3 < 9; ++var3) {
			this.addSlotToContainer(new Slot(inventoryPlayer, var3, 8 + var3 * 18, 148));
		}
	}

	@Override
	public void addCraftingToCrafters(ICrafting par1ICrafting) {
		super.addCraftingToCrafters(par1ICrafting);
	}

	/**
	 * Looks for changes made in the container, sends them to every listener.
	 */
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		for(int var1 = 0; var1 < this.crafters.size(); ++var1) {
			ICrafting var2 = (ICrafting) this.crafters.get(var1);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void updateProgressBar(int par1, int par2) {
	}

	@Override
	public boolean canInteractWith(EntityPlayer par1EntityPlayer) {
		return this.chesty.isUseableByPlayer(par1EntityPlayer);
	}

	@Override
	public ItemStack slotClick(int par1, int par2, int par3, EntityPlayer par4EntityPlayer) {
		if(chesty == null || chesty.isDead) {
			return null;
		}
		if(!chesty.worldObj.isRemote && par1 >= 0 && getSlot(par1).getHasStack() && !(getSlot(par1).getStack().getItem() instanceof ItemChestySceptre)) {
			if(chesty.chestySceptre == null || chesty.chestySceptre.getTagCompound() == null || chesty.getOwner() == null || !(chesty.getOwner() instanceof EntityPlayer)) {
				return null;
			} else {
				EntityPlayer player = (EntityPlayer) chesty.getOwner();
				ItemStack actualSceptre = EntityChesty.findChestySceptreOnPlayer(player, chesty.chestySceptre);
				if(actualSceptre == null) {
					return null;
				}
			}
		}
		return super.slotClick(par1, par2, par3, par4EntityPlayer);
	}

	/**
	 * Called when a player shift-clicks on a slot. You must override this or
	 * you will crash when someone does that.
	 */
	@Override
	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int slot) {
		ItemStack var3 = null;
		Slot var4 = (Slot) this.inventorySlots.get(slot);
		if(var4 != null && var4.getHasStack()) {
			ItemStack var5 = var4.getStack();
			if(var5.getItem() == Chesty.chestySceptre) {
				return null;
			}

			var3 = var5.copy();
			boolean canSortInventory = true;
			if(slot > EntityChesty.SPECIAL_SLOTS_SIZE - 1) {
				for(int i = 0; i < 4; i++) {
					if(var5.getItem().isValidArmor(var5, i)) {
						canSortInventory = !this.mergeItemStack(var5, 2 + i, 3 + i, false);
						if(!canSortInventory) {
							var4.putStack((ItemStack) null);
						}
						break;
					}
				}
				if(var5.getItem() instanceof ItemFood) {
					canSortInventory = !this.mergeItemStack(var5, 0, 2, false);
					if(!canSortInventory) {
						var4.putStack((ItemStack) null);
					}
				}
			}
			if(canSortInventory) { //TODO: This can easily dupe items, should test this more.
				if(slot < EntityChesty.SPECIAL_SLOTS_SIZE) { //Chesty's Special Slots
					//From Chesty's special slots to any available inventory
					if(!this.mergeItemStack(var5, chesty.getSizeInventory() - 1, chesty.getSizeInventory() - 1 + 27 + 9, false)) {
						//Player inventory is full, try to move to chesty's inventory instead.
						if(!this.mergeItemStack(var5, EntityChesty.SPECIAL_SLOTS_SIZE, chesty.getSizeInventory() - 1, false)) {
							return null;
						}
					}
				} else if(slot >= EntityChesty.SPECIAL_SLOTS_SIZE && slot < chesty.getSizeInventory() - 1) { //Chesty's Inventory
					//From chesty inventory to any available playey inventory slot
					if(!this.mergeItemStack(var5, chesty.getSizeInventory() - 1, chesty.getSizeInventory() - 1 + 27 + 9, false)) {
						return null;
					}
				} else if(slot >= chesty.getSizeInventory() - 1 && slot < chesty.getSizeInventory() - 1 + 27) { //Player's Inventory
					//Player inventory to any available chesty slot
					if(!this.mergeItemStack(var5, EntityChesty.SPECIAL_SLOTS_SIZE, chesty.getSizeInventory() - 1, false)) {
						//Chesty slots were full, transfer to player hotbar instead
						if(!this.mergeItemStack(var5, chesty.getSizeInventory() - 1 + 27, chesty.getSizeInventory() - 1 + 27 + 9, false)) {
							return null;
						}
					}
				} else if(slot >= chesty.getSizeInventory() - 1 + 27 && slot < chesty.getSizeInventory() - 1 + 27 + 9) { //Player's Hotbar
					//Player hotbar to any available chesty slot
					if(!this.mergeItemStack(var5, EntityChesty.SPECIAL_SLOTS_SIZE, chesty.getSizeInventory() - 1 + 27, false)) {
						//Chesty slots were full, transfer to player inventory instead
						if(!this.mergeItemStack(var5, chesty.getSizeInventory() - 1 + 27, chesty.getSizeInventory() - 1 + 27, false)) {
							return null;
						}
					}
				}

				if(var5.stackSize == 0) {
					var4.putStack((ItemStack) null);
				} else {
					var4.onSlotChanged();
				}
			}

			if(var5.stackSize == var3.stackSize) {
				return null;
			}

			var4.onPickupFromSlot(par1EntityPlayer, var5);
		}
		return var3;
	}

	@Override
	public void onCraftGuiClosed(EntityPlayer par1EntityPlayer) {
		super.onCraftGuiClosed(par1EntityPlayer);
		chesty.closeChest();
	}
}
