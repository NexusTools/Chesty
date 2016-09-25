package net.nexustools.chesty;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import java.util.logging.Logger;
import net.minecraft.entity.EntityList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.Configuration;
import net.nexustools.chesty.entity.passive.EntityChesty;
import net.nexustools.chesty.item.ItemChestySceptre;
import net.nexustools.chesty.network.ConnectionHandlerChesty;
import net.nexustools.chesty.proxy.Proxy;
import net.nexustools.chesty.support.IronChestSupport;
import net.nexustools.chesty.network.PacketHandler;

@Mod(modid = "Chesty", name = "Chesty", dependencies = "after:IronChest")
@NetworkMod(clientSideRequired = true, channels = PacketHandler.PACKET_CHANNEL_NAME, packetHandler = PacketHandler.class)
public class Chesty {

	@Mod.Instance("Chesty")
	public static Chesty instance;

	public static String version;

	public static int chestyNpcId;
	public static int chestySceptreId;

	private static boolean spawnChestySceptreInDungeons;
	private static int chestySceptreSpawnChanceMin;
	private static int chestySceptreSpawnChanceMax;
	private static int chestySceptreSpawnWeight;

	private static boolean allowCraftingChestySceptre;

	public static Item chestySceptre;

	@SidedProxy(clientSide = "net.nexustools.chesty.proxy.ClientProxy", serverSide = "net.nexustools.chesty.proxy.Proxy")
	public static Proxy proxy;

	private static final Logger CHESTY_LOGGER = Logger.getLogger("Chesty");

	@Mod.PreInit
	public void preload(FMLPreInitializationEvent iEvent) {
		instance = this;
		CHESTY_LOGGER.setParent(FMLLog.getLogger());

		version = FMLCommonHandler.instance().findContainerFor(this).getVersion();

		Configuration conf = new Configuration(iEvent.getSuggestedConfigurationFile());
		conf.load();
		chestyNpcId = conf.get(Configuration.CATEGORY_GENERAL, "chestyNpcId", EntityRegistry.findGlobalUniqueEntityId(), "The NPC Id for Chesty. Shouldn't have to change, but it's available if needed.").getInt();
		allowCraftingChestySceptre = conf.get(Configuration.CATEGORY_GENERAL, "allowCraftingChestySceptre", false, "If ture, adds a crafting recipe for the Sceptre of Chests.").getBoolean(false);

		spawnChestySceptreInDungeons = conf.get(Configuration.CATEGORY_GENERAL, "spawnChestySceptreInDungeons", true, "If ture, adds the Sceptre of Chests to dungeon chests.").getBoolean(true);
		chestySceptreSpawnChanceMin = conf.get(Configuration.CATEGORY_GENERAL, "chestySceptreSpawnChanceMin", 1, "The minmium chance in percentage to spawn the Sceptre of Chests in dungeon chests.").getInt();
		chestySceptreSpawnChanceMax = conf.get(Configuration.CATEGORY_GENERAL, "chestySceptreSpawnChanceMax", 1, "The maximum chance in percentage to spawn the Sceptre of Chests in dungeon chests.").getInt();
		chestySceptreSpawnWeight = conf.get(Configuration.CATEGORY_GENERAL, "chestySceptreSpawnWeight", 70, "The overall spawn rarity weight to spawn the Sceptre of Chests in dungeon chests.").getInt();

		IronChestSupport.ironChestSupportForcedEnabled = IronChestSupport.ironChestSupportEnabled = conf.get(Configuration.CATEGORY_GENERAL, "ironChestSupportEnabled", true, "If ture, and you have a compitable version of IronChest installed, you will be able to upgrade Chesty with different chests from the IronChest mod. Warning: Disabling this can result in loss of items in previous Chesty saves.").getBoolean(true);

		chestySceptreId = conf.getItem("chestySceptreId", 16480).getInt();

		conf.save();
	}

	@Mod.Init
	public void load(FMLInitializationEvent IEvent) {
		IronChestSupport.init();

		if(FMLCommonHandler.instance().getSide().isClient()) {
			proxy.loadRenderers();
		}
		chestySceptre = new ItemChestySceptre(chestySceptreId);

		if(allowCraftingChestySceptre) {
			GameRegistry.addRecipe(
					new ItemStack(chestySceptre),
					new Object[]{ //S = Stick G = Gold Ingot E = Emerald O = Eye of Ender
						"XGO",
						"ESG",
						"SEX",
						'S',
						Item.stick,
						'G',
						Item.ingotGold,
						'E',
						Item.emerald,
						'O',
						Item.eyeOfEnder
					}
			);
		}
		if(spawnChestySceptreInDungeons) {
			ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(new WeightedRandomChestContent(new ItemStack(chestySceptre), chestySceptreSpawnChanceMin, chestySceptreSpawnChanceMax, chestySceptreSpawnWeight));
		}

		NetworkRegistry.instance().registerConnectionHandler((IConnectionHandler) new ConnectionHandlerChesty());

		EntityRegistry.registerModEntity(EntityChesty.class, "EntityChesty", chestyNpcId, this, 60, 3, true);
		EntityList.addMapping(EntityChesty.class, "EntityChesty", chestyNpcId); //Not sure if I should do this.

		LanguageRegistry.instance().addStringLocalization("item.chestySceptre.name", "Sceptre of Chests");

		LanguageRegistry.instance().addStringLocalization("entity.EntityChesty.name", "Chesty");
		LanguageRegistry.instance().addStringLocalization("entity.EntityChesty.inventory.description", "Chesty's Inventory");
		LanguageRegistry.instance().addStringLocalization("entity.EntityChesty.not_yours", "This Chesty does not belong to you.");
		LanguageRegistry.instance().addStringLocalization("entity.EntityChesty.please_empty", "Please empty Chesty before upgrading/downgrading.");
	}

	public static Logger getLogger() {
		return CHESTY_LOGGER;
	}
}
