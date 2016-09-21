package net.nexustools.chesty.entity.passive;

import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIFleeSun;
import net.minecraft.entity.ai.EntityAIFollowOwner;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIOwnerHurtByTarget;
import net.minecraft.entity.ai.EntityAIOwnerHurtTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
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
import net.nexustools.chesty.inventory.ContainerChesty;

/**
 *
 * @author Steve4448
 */
public class EntityChesty extends EntityTameable implements IInventory {
	public static final int SPECIAL_SLOTS_SIZE = 6;
	public static final int DEFAULT_ACTUAL_INVENTORY_SIZE = 18;
	private int itemInUseCount;

	public String inventoryTitle;
	private int slotsCount;
	private ItemStack[] inventoryContents;
	public float prevLidAngle = 0;
	public float lidAngle = 0;
	public int ticksSinceSync = 0;
	public int numUsingPlayers = 0;

	public EntityChesty(World world) {
		super(world);
		texture = "/net/nexustools/chesty/client/model/ModelChesty.png";
		moveSpeed = 0.3F;
		setSize(0.8F, 1F);
		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(1, new EntityAIFleeSun(this, moveSpeed * 1.5F));
		tasks.addTask(2, new EntityAIFollowOwner(this, 1, 10, 20));
		tasks.addTask(3, new EntityAILeapAtTarget(this, 0.6F));
		tasks.addTask(4, new EntityAIAttackOnCollide(this, EntityPlayer.class, moveSpeed * 1.5F, false));
		tasks.addTask(5, new EntityAIWatchClosest(this, EntityPlayer.class, 24.0F));
		tasks.addTask(6, new EntityAIWander(this, moveSpeed));
		tasks.addTask(7, new EntityAILookIdle(this));
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
		if(!worldObj.isRemote && numUsingPlayers != 0 && ++ticksSinceSync % 60 == 0) {
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
			setTamed(true); //Todo: Proper way to tame Chesty, for now just automatically tamed whenever you first interact.
			this.setPathToEntity((PathEntity)null);
			this.setAttackTarget((EntityLiving)null);
			this.setOwner(par1EntityPlayer.username);
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
		return !isTamed();
	}

	public int getItemInUseCount() {
		return itemInUseCount;
	}

	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
		super.readFromNBT(par1NBTTagCompound);
		String version = par1NBTTagCompound.getString("version");
		System.out.println("Chesty version is " + version);
		NBTTagList var2 = par1NBTTagCompound.getTagList("Items");
		slotsCount = Math.max(DEFAULT_ACTUAL_INVENTORY_SIZE+SPECIAL_SLOTS_SIZE, var2.tagCount());
		inventoryContents = new ItemStack[slotsCount+1];
		for(int var3 = 0; var3 < var2.tagCount(); ++var3) {
			NBTTagCompound var4 = (NBTTagCompound) var2.tagAt(var3);
			int var5 = var4.getByte("Slot") & 255;
			setInventorySlotContents(var5, ItemStack.loadItemStackFromNBT(var4));
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
		super.writeToNBT(par1NBTTagCompound);
		par1NBTTagCompound.setString("version", Chesty.version);
		NBTTagList var2 = new NBTTagList();

		for(int var3 = 0; var3 < getSizeInventory(); ++var3) {
			if(getStackInSlot(var3) != null) {
				NBTTagCompound var4 = new NBTTagCompound();
				var4.setByte("Slot", (byte) var3);
				getStackInSlot(var3).writeToNBT(var4);
				var2.appendTag(var4);
			}
		}

		par1NBTTagCompound.setTag("Items", var2);
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
}
