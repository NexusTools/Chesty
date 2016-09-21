package net.nexustools.chesty;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.entity.EntityEggInfo;
import net.minecraft.entity.EntityList;
import net.minecraftforge.common.Configuration;
import net.nexustools.chesty.entity.passive.EntityChesty;
import net.nexustools.chesty.proxy.Proxy;

@Mod(modid = "Chesty", name = "Chesty", version = "0.1")
@NetworkMod(clientSideRequired = true)
public class Chesty {
	public static String version;
	public static int chestyNpcId = 0;
	@SidedProxy(clientSide = "net.nexustools.chesty.proxy.ClientProxy", serverSide = "net.nexustools.chesty.proxy.Proxy")
	public static Proxy proxy;
	private static Chesty instance;
	
	private static EntityChesty lastInteractChesty = null;
	private static EntityChesty lastInteractChestyRemote = null;
	
	@Mod.PreInit
	public void preload(FMLPreInitializationEvent iEvent) {
		instance = this;
		version = FMLCommonHandler.instance().findContainerFor(this).getVersion(); //Is there a better way to obtain our mod's version without duplicate version variables?
		Configuration conf = new Configuration(iEvent.getSuggestedConfigurationFile());
		conf.load();
		chestyNpcId = conf.get(Configuration.CATEGORY_GENERAL, "chestyNpcId", EntityRegistry.findGlobalUniqueEntityId()).getInt();
		conf.save();
	}

	@Mod.Init
	public void load(FMLInitializationEvent IEvent) {
		if(FMLCommonHandler.instance().getSide().isClient()) {
			proxy.loadRenderers();
		}
		
		NetworkRegistry.instance().registerGuiHandler(this, proxy);
		
		EntityRegistry.registerModEntity(EntityChesty.class, "Chesty", chestyNpcId, this, 80, 3, true);
		//EntityRegistry.addSpawn(EntityMiniMe.class, 100, 1, 6, EnumCreatureType.creature, BiomeGenBase.beach, BiomeGenBase.forest, BiomeGenBase.plains, BiomeGenBase.jungle);
		
		LanguageRegistry.instance().addStringLocalization("entity.Chesty.Chesty.name", "Chesty");
		LanguageRegistry.instance().addStringLocalization("entity.Chesty.Chesty.inventory_description", "Chesty's Inventory");
		LanguageRegistry.instance().addStringLocalization("entity.Chesty.Chesty.not_yours", "This Chesty does not belong to you.");
		
		EntityList.IDtoClassMapping.put(chestyNpcId, EntityChesty.class);
		EntityList.entityEggs.put(chestyNpcId, new EntityEggInfo(chestyNpcId, 0xFFFFFF, 0x000000));
	}
	
	public static void setLastInteract(EntityChesty chesty) {
		lastInteractChesty = chesty;
	}
	
	public static EntityChesty getLastInteract() {
		return lastInteractChesty;
	}
	
	public static void setLastInteractRemote(EntityChesty chesty) {
		lastInteractChestyRemote = chesty;
	}
	
	public static EntityChesty getLastInteractRemote() {
		return lastInteractChestyRemote;
	}
	
	public static Chesty instance() {
		return instance;
	}
}
