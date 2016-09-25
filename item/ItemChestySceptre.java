package net.nexustools.chesty.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.logging.Level;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.nexustools.chesty.Chesty;
import net.nexustools.chesty.entity.passive.EntityChesty;
import net.nexustools.chesty.support.IronChestEntry;
import net.nexustools.chesty.support.IronChestSupport;

public class ItemChestySceptre extends Item {

	public ItemChestySceptre(int par1) {
		super(par1);
		maxStackSize = 1;
		setFull3D();
		setItemName("chestySceptre");
		setCreativeTab(CreativeTabs.tabMisc);
		setTextureFile("/net/nexustools/chesty/client/gui/items.png");
		setIconCoord(0, 0);
	}
	
	@Override
	public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int par4, int par5, int par6, int par7, float par8, float par9, float par10) {
		if(!world.isRemote) {
			if(!itemStack.hasTagCompound()) {
				itemStack.setTagCompound(new NBTTagCompound());
			}
			if(itemStack.getTagCompound().hasKey("ChestyEntity")) {
				int chestyEntityId = itemStack.getTagCompound().getInteger("ChestyEntity");
				Entity foundEntity = null;
				for(World w : DimensionManager.getWorlds()) { //Is this really what I have to do...
					if((foundEntity = w.getEntityByID(chestyEntityId)) != null) {
						if(foundEntity instanceof EntityChesty && (foundEntity.worldObj != world || foundEntity.dimension != player.dimension)) {
							//This Chesty is in another world. Re-make.
							foundEntity.setDead();
							itemStack.getTagCompound().removeTag("ChestyEntity");
							break;
						} else if(!(foundEntity instanceof EntityChesty)) {
							//This entity id that we had stored isn't even a chesty. Re-make.
							itemStack.getTagCompound().removeTag("ChestyEntity");
							break;
						}
						EntityChesty chesty = (EntityChesty) foundEntity;
						if(!(itemStack.getTagCompound().getString("ChestyOwner").equals(player.username)) || !chesty.getOwnerName().equals(itemStack.getTagCompound().getString("ChestyOwner"))) {
							//Name mismatches. This player could've found one or killed a player for one. He now owns it!
							itemStack.getTagCompound().setString("ChestyOwner", player.username);
							chesty.setOwner(player.username);
						}
						if(player.isSneaking()) {
							chesty.setDead();
							itemStack.getTagCompound().removeTag("ChestyEntity");
						}
						break;
					}
				}
				if(foundEntity == null) { //Chesty not found. Re-make.
					itemStack.getTagCompound().removeTag("ChestyEntity");
				}
			}
			if(!player.isSneaking() && !itemStack.getTagCompound().hasKey("ChestyEntity")) {
				itemStack.getTagCompound().setString("ChestyOwner", player.username);
				
				EntityChesty entityChesty = new EntityChesty(world);
				entityChesty.setPosition((double)par4 + (entityChesty.width / 2), (double)par5+entityChesty.height, (double)par6 + (entityChesty.width / 2)); //Todo: Find a valid block somewhere nearby.
				entityChesty.setTamed(true);
				entityChesty.setOwner(player.username);
				
				if(Chesty.ironChestExists && itemStack.getTagCompound().hasKey("ChestyIronChestSubType")) {
					entityChesty.getDataWatcher().updateObject(EntityChesty.DATA_WATCHER_SUBTYPE, new Byte((byte)(itemStack.getTagCompound().getInteger("ChestyIronChestSubType")+1)));
					IronChestEntry entry = IronChestSupport.getIronChestEntry(itemStack.getTagCompound().getInteger("ChestyIronChestSubType"));
					entityChesty.slotsCount = (EntityChesty.SPECIAL_SLOTS_SIZE + entry.size - (entry.rowLength*3));
					entityChesty.rowLength = entry.rowLength;
					entityChesty.inventoryContents = new ItemStack[entityChesty.slotsCount+1];
				}
				
				world.spawnEntityInWorld(entityChesty);
				itemStack.getTagCompound().setInteger("ChestyEntity", entityChesty.entityId);
				
				NBTTagList var2 = itemStack.getTagCompound().getTagList("ChestyItems");
				for(int var3 = 0; var3 < var2.tagCount(); ++var3) {
					if(var3 >= entityChesty.inventoryContents.length) {
						Chesty.getLogger().log(Level.WARNING, "Too many items trying to be loaded, not loading anymore.");
						break;
					}
					NBTTagCompound var4 = (NBTTagCompound) var2.tagAt(var3);
					int var5 = var4.getByte("Slot") & 255;
					entityChesty.setInventorySlotContents(var5, ItemStack.loadItemStackFromNBT(var4));
				}
				return true;
			}
		}
        return false;
    }

	@SideOnly(Side.CLIENT)
	@Override
	public boolean hasEffect(ItemStack par1ItemStack) {
		return par1ItemStack.hasTagCompound() && par1ItemStack.getTagCompound().hasKey("ChestyEntity");
	}

	/**
	 * Return an item rarity from EnumRarity
	 */
	@Override
	public EnumRarity getRarity(ItemStack par1ItemStack) {
		return EnumRarity.rare;
	}
	
	/*@Override
    public int getIconFromDamage(final int i) {
        return 0 + i;
    }*/

	@Override
	public boolean onDroppedByPlayer(ItemStack item, EntityPlayer player) {
		if(!player.worldObj.isRemote && item.hasTagCompound()) {
			Entity chesty;
			if((chesty = player.worldObj.getEntityByID(item.getTagCompound().getInteger("ChestyEntity"))) != null && chesty instanceof EntityChesty && item.getTagCompound().getString("ChestyOwner").equals(player.username)) {
				chesty.setDead();
				item.getTagCompound().removeTag("ChestyEntity");
			}
		}
		return true;
	}
}
