package net.nexustools.chesty.support;

import java.lang.reflect.Field;
import java.util.ArrayList;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.nexustools.chesty.Chesty;

public class IronChestSupport {
	public static Item ironChestItem;
	public static IronChestEntry[] ironChestEntries;
	
	public static void init() {
		try {
			ArrayList<IronChestEntry> tempIronChestEntries = new ArrayList<IronChestEntry>();
			Class ironChest = Class.forName("cpw.mods.ironchest.IronChest");
			if(ironChest == null)
				throw new Exception("Could not get IronChest");
			
			Field blockIronChest = ironChest.getDeclaredField("ironChestBlock");
			if(blockIronChest == null)
				throw new Exception("Could not get IronChestBlock from IronChest");
			
			int ironChestItemId = ((Block)blockIronChest.get(blockIronChest)).blockID;
			ironChestItem = Item.itemsList[ironChestItemId];
			if(ironChestItem == null)
				throw new Exception("Could not get IronChestItem");
			else if(!ironChestItem.getClass().getName().equals("cpw.mods.ironchest.ItemIronChest"))
				throw new Exception("Class " + ironChestItem.getClass().getName() + " != cpw.mods.ironchest.ItemIronChest");
			
			Class ironChestType = Class.forName("cpw.mods.ironchest.IronChestType");
			Field sizeField = ironChestType.getDeclaredField("size");
			sizeField.setAccessible(true); //Could doing this in certain minecraft enviornments throw security exceptions?
			Field rowLengthField = ironChestType.getDeclaredField("rowLength");
			rowLengthField.setAccessible(true);
			Field friendlyNameField = ironChestType.getDeclaredField("friendlyName");
			friendlyNameField.setAccessible(true);
			
			if(sizeField == null || rowLengthField == null || friendlyNameField == null)
				throw new Exception("One or more fields required are still null. Wasn't able to succesfully reflect fields?");
			
			Object[] ironChests = (Object[])ironChestType.getMethod("values").invoke(ironChestType);
			for(Object o : ironChests) {
				Enum oe = (Enum)o;
				String name = oe.name();
				tempIronChestEntries.add(new IronChestEntry(name, (String)friendlyNameField.get(o), "/net/nexustools/chesty/client/model/ModelChesty " + name + ".png", oe.ordinal(), sizeField.getInt(o), rowLengthField.getInt(o)));
				
				ItemStack testItemIsProper = new ItemStack(ironChestItem, 1, oe.ordinal());
				if(!testItemIsProper.getItemName().equals(name)) {
					throw new Exception(name + " != " + testItemIsProper.getItemName());
				}
			}
			ironChestEntries = new IronChestEntry[tempIronChestEntries.size()];
			tempIronChestEntries.toArray(ironChestEntries);
			Chesty.getLogger().info("Successfully added " + ironChestEntries.length + " IronChest variants.");
		} catch(Exception e) {
			e.printStackTrace();
			Chesty.getLogger().severe("Error while reflecting into IronChest mod. Disabling IronChest support and continuing.");
			Chesty.ironChestSupportEnabled = false;
		}
	}
	
	public static IronChestEntry getIronChestEntry(int subType) {
		for(IronChestEntry entry : ironChestEntries) {
			if(entry.subType == subType)
				return entry;
		}
		return null;
	}
	
	public static boolean isIronChestSupported() {
		return Chesty.ironChestExists && Chesty.ironChestSupportForcedEnabled && (Chesty.ironChestSupportEnabled || Chesty.ironChestSupportForcedEnabled);
	}
}
