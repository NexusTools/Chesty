package net.nexustools.chesty.entity.passive;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIFollowOwner;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAIOwnerHurtByTarget;
import net.minecraft.entity.ai.EntityAIOwnerHurtTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.nexustools.chesty.Chesty;
import net.nexustools.chesty.entity.ai.EntityAIWanderWhenChestClosed;
import net.nexustools.chesty.inventory.ContainerChesty;
import net.nexustools.chesty.item.ItemChestySceptre;

public class EntityChesty extends EntityTameable implements IInventory {
	public static final int SPECIAL_SLOTS_SIZE = 6;
	public static final int DEFAULT_ACTUAL_INVENTORY_SIZE = 18;
	private int itemInUseCount;

	public String inventoryTitle;
	public int slotsCount;
	public ItemStack[] inventoryContents;
	public float prevLidAngle = 0;
	public float lidAngle = 0;
	public int ticksSinceSync = 59;
	public int numUsingPlayers = 0;
	public ItemStack chestySceptre;
	public boolean spawnParticles = false;

	public EntityChesty(World world) {
		super(world);
		texture = "/net/nexustools/chesty/client/model/ModelChesty.png";
		moveSpeed = 0.5F;
		setSize(0.8F, 1F);
		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(1, new EntityAIFollowOwner(this, moveSpeed, 4, 6));
		tasks.addTask(2, new EntityAIWatchClosest(this, EntityPlayer.class, 24.0F));
		tasks.addTask(3, new EntityAIWanderWhenChestClosed(this, moveSpeed));
		targetTasks.addTask(1, new EntityAIOwnerHurtByTarget(this));
		targetTasks.addTask(2, new EntityAIOwnerHurtTarget(this));
		targetTasks.addTask(3, new EntityAIHurtByTarget(this, true));
		inventoryTitle = "entity.Chesty.Chesty.inventory_description";
		slotsCount = SPECIAL_SLOTS_SIZE+DEFAULT_ACTUAL_INVENTORY_SIZE;
		inventoryContents = new ItemStack[slotsCount+1];
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if(!worldObj.isRemote && ++ticksSinceSync % 60 == 0) {
			if(chestySceptre == null || getOwner() == null || getOwner().worldObj != worldObj || getOwner().dimension != dimension) {
				setDead();
				if(chestySceptre != null) {
					chestySceptre.getTagCompound().removeTag("ChestyEntity");
				}
				return;
			} else {
				EntityPlayer owner = (EntityPlayer)getOwner();
				if(findChestySceptreOnPlayer(owner, chestySceptre) == null) {
					setDead();
					chestySceptre.getTagCompound().removeTag("ChestyEntity");
					return;
				}
			}
			if(numUsingPlayers != 0) {
				numUsingPlayers = 0;
				float var1 = 5.0F;
				List var2 = worldObj.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getAABBPool().addOrModifyAABBInPool((double) ((float) posX - var1), (double) ((float) posY - var1), (double) ((float) posZ - var1), (double) ((float) (posX + 1) + var1), (double) ((float) (posY + 1) + var1), (double) ((float) (posZ + 1) + var1)));
				Iterator var3 = var2.iterator();

				while(var3.hasNext()) {
					EntityPlayer var4 = (EntityPlayer) var3.next();
					if(var4.openContainer instanceof ContainerChesty) {
						if(((ContainerChesty) var4.openContainer).chesty == this) {
							++numUsingPlayers;
						}
					}
				}
			}
		} else if(!spawnParticles && worldObj.isRemote) {
			spawnParticles = true;
			for(int i = 0; i < 100; i++) {
				worldObj.spawnParticle("enchantmenttable", posX + (rand.nextDouble() * 2.5) - (rand.nextDouble() * 2.5), posY + (rand.nextDouble() * 2.5) - (rand.nextDouble() * 2.5), posZ + (rand.nextDouble() * 2.5) - (rand.nextDouble() * 2.5), (rand.nextDouble() * 2.5) - (rand.nextDouble() * 2.5), (rand.nextDouble() * 2.5) - (rand.nextDouble() * 2.5), (rand.nextDouble() * 2.5) - (rand.nextDouble() * 2.5));
			}
		}

		prevLidAngle = lidAngle;
		float var1 = 0.1F;

		if(numUsingPlayers > 0 && lidAngle == 0.0F) {
			worldObj.playSoundEffect((double) posX + 0.5D, (double) posY + 0.5D, (double) posZ + 0.5D, "random.chestopen", 0.5F, worldObj.rand.nextFloat() * 0.1F + 0.9F);
		}

		if(numUsingPlayers == 0 && lidAngle > 0.0F || numUsingPlayers > 0 && lidAngle < 1.0F) {
			float var9 = lidAngle;

			if(lidAngle < 1.0F && numUsingPlayers > 0) {
				lidAngle += var1;

				if(lidAngle > 1.0F) {
					lidAngle = 1.0F;
				}
			} else if(numUsingPlayers == 0 && lidAngle > 0F) {
				lidAngle -= var1;
				if(lidAngle < 0.0F) {
					lidAngle = 0.0F;
				}
			}

			float var10 = 0.3F;

			if(lidAngle < var10 && var9 >= var10) {

				worldObj.playSoundEffect((double) posX + 0.5D, (double) posY + 0.5D, (double) posZ + 0.5D, "random.chestclosed", 0.5F, worldObj.rand.nextFloat() * 0.1F + 0.9F);
			}
		}
	}

	@Override
	public boolean interact(EntityPlayer par1EntityPlayer) {
		super.interact(par1EntityPlayer);
		if(worldObj.isRemote) {
			Chesty.setLastInteract(this);
			return true;
		} else {
			Chesty.setLastInteractRemote(this);
			if(isTamed() && getOwnerName().equals(par1EntityPlayer.username)) {
				par1EntityPlayer.openGui(Chesty.instance(), 0, worldObj, (int) posX, (int) posY, (int) posZ);
			} else {
				par1EntityPlayer.sendChatToPlayer(par1EntityPlayer.getTranslator().translateKey("entity.Chesty.Chesty.not_yours"));
				return false;
			}
			return true;
		}
	}

	@Override
	protected boolean isAIEnabled() {
		return true;
	}

	@Override
	public EntityAgeable createChild(EntityAgeable var1) {
		return null;
	}

	@Override
	public int getMaxHealth() {
		return 20;
	}

	public int getAttackStrength(Entity entity) {
		return 4;
	}

	@Override
	public boolean attackEntityFrom(DamageSource damageSource, int damage) {
		if(isEntityInvulnerable()) {
			return false;
		} else {
			Entity damageSourceEntity = damageSource.getEntity();
			aiSit.setSitting(false);

			if(damageSourceEntity != null && !(damageSourceEntity instanceof EntityPlayer) && !(damageSourceEntity instanceof EntityArrow)) {
				damage = (damage + 1) / 2;
			}

			return super.attackEntityFrom(damageSource, damage);
		}
	}

	@Override
	public boolean attackEntityAsMob(Entity entity) {
		return entity.attackEntityFrom(DamageSource.causeMobDamage(this), getAttackStrength(entity));
	}

	@Override
	protected boolean canDespawn() {
		return !isTamed() || getOwner() == null;
	}
	
	@Override
	public void setDead() {
		super.setDead();
		if(worldObj.isRemote)
			for(int i = 0; i < 100; i++) {
				worldObj.spawnParticle("enchantmenttable", posX + (rand.nextDouble() * 2.5) - (rand.nextDouble() * 2.5), posY + (rand.nextDouble() * 2.5) - (rand.nextDouble() * 2.5), posZ + (rand.nextDouble() * 2.5) - (rand.nextDouble() * 2.5), (rand.nextDouble() * 2.5) - (rand.nextDouble() * 2.5), (rand.nextDouble() * 2.5) - (rand.nextDouble() * 2.5), (rand.nextDouble() * 2.5) - (rand.nextDouble() * 2.5));
			}
	}

	public int getItemInUseCount() {
		return itemInUseCount;
	}

	/**
	 * Returns the stack in slot i
	 */
	@Override
	public ItemStack getStackInSlot(int par1) {
		return inventoryContents[par1];
	}

	/**
	 * Removes from an inventory slot (first arg) up to a specified number
	 * (second arg) of items and returns them in a new stack.
	 */
	@Override
	public ItemStack decrStackSize(int par1, int par2) {
		if(inventoryContents[par1] != null) {
			ItemStack var3;

			if(inventoryContents[par1].stackSize <= par2) {
				var3 = inventoryContents[par1];
				inventoryContents[par1] = null;
				onInventoryChanged();
				return var3;
			} else {
				var3 = inventoryContents[par1].splitStack(par2);

				if(inventoryContents[par1].stackSize == 0) {
					inventoryContents[par1] = null;
				}

				onInventoryChanged();
				return var3;
			}
		} else {
			return null;
		}
	}

	/**
	 * When some containers are closed they call this on each slot, then drop
	 * whatever it returns as an EntityItem - like when you close a workbench
	 * GUI.
	 */
	@Override
	public ItemStack getStackInSlotOnClosing(int par1) {
		if(inventoryContents[par1] != null) {
			ItemStack var2 = inventoryContents[par1];
			inventoryContents[par1] = null;
			return var2;
		} else {
			return null;
		}
	}

	/**
	 * Sets the given item stack to the specified slot in the inventory (can be
	 * crafting or armor sections).
	 */
	@Override
	public void setInventorySlotContents(int par1, ItemStack par2ItemStack) {
		inventoryContents[par1] = par2ItemStack;

		if(par2ItemStack != null && par2ItemStack.stackSize > getInventoryStackLimit()) {
			par2ItemStack.stackSize = getInventoryStackLimit();
		}
		onInventoryChanged();
	}

	/**
	 * Returns the number of slots in the inventory.
	 */
	@Override
	public int getSizeInventory() {
		return inventoryContents.length;
	}
	
	public int getActualSizeInventory() {
		return slotsCount-SPECIAL_SLOTS_SIZE;
	}

	/**
	 * Returns the name of the inventory.
	 */
	@Override
	public String getInvName() {
		return inventoryTitle;
	}

	/**
	 * Returns the maximum stack size for a inventory slot. Seems to always be
	 * 64, possibly will be extended. *Isn't this more of a set than a get?*
	 */
	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	/**
	 * Called when an the contents of an Inventory change, usually
	 */
	@Override
	public void onInventoryChanged() {
		if(chestySceptre != null && !worldObj.isRemote && getOwner() != null && getOwner() instanceof EntityPlayer) {
			ItemStack actualRod = findChestySceptreOnPlayer((EntityPlayer)getOwner(), chestySceptre);
			if(actualRod == null) {
				setDead();
				return;
			}
			NBTTagList var2 = new NBTTagList();
			for(int var3 = 0; var3 < getSizeInventory(); ++var3) {
				if(getStackInSlot(var3) != null) {
					NBTTagCompound var4 = new NBTTagCompound();
					var4.setByte("Slot", (byte) var3);
					getStackInSlot(var3).writeToNBT(var4);
					var2.appendTag(var4);
				}
			}
			actualRod.getTagCompound().setTag("ChestyItems", var2);
		}
	}

	/**
	 * Do not make give this method the name canInteractWith because it clashes
	 * with Container
	 */
	@Override
	public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer) {
		return getOwnerName().equals(par1EntityPlayer.username);
	}

	@Override
	public void openChest() {
		++numUsingPlayers;
		
	}

	@Override
	public void closeChest() {
		--numUsingPlayers;
	}
	
	public static ItemStack findChestySceptreOnPlayer(EntityPlayer player, ItemStack chestySceptre) {
		if(player == null || chestySceptre == null || !(chestySceptre.getItem() instanceof ItemChestySceptre) || chestySceptre.getTagCompound() == null)
			return null;
		ArrayList<ItemStack> items = new ArrayList<ItemStack>(Arrays.asList(player.inventory.mainInventory));
		items.add(player.inventory.getItemStack());
		for(ItemStack mainInventoryItem : items) {
			if(mainInventoryItem == null || !mainInventoryItem.hasTagCompound())
				continue;
			if(chestySceptre.getTagCompound().getInteger("ChestyEntity") == mainInventoryItem.getTagCompound().getInteger("ChestyEntity")) {
				return mainInventoryItem;
			}
		}
		return null;
	}
}
