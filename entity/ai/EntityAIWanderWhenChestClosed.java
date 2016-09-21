package net.nexustools.chesty.entity.ai;

import net.minecraft.entity.ai.EntityAIWander;
import net.nexustools.chesty.entity.passive.EntityChesty;

public class EntityAIWanderWhenChestClosed extends EntityAIWander {
	private final EntityChesty chesty;
	public EntityAIWanderWhenChestClosed(EntityChesty par1EntityCreature, float par2) {
        super(par1EntityCreature, par2);
		chesty = par1EntityCreature;
    }
	
	@Override
	public boolean shouldExecute() {
		return chesty.numUsingPlayers == 0 && super.shouldExecute();
	}
}
