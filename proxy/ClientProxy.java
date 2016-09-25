package net.nexustools.chesty.proxy;

import cpw.mods.fml.client.registry.RenderingRegistry;
import net.nexustools.chesty.client.renderer.entity.RenderChesty;
import net.nexustools.chesty.entity.passive.EntityChesty;

public class ClientProxy extends Proxy {
	
	@Override
	public void loadRenderers() {
		RenderingRegistry.registerEntityRenderingHandler(EntityChesty.class, new RenderChesty());
	}
}
