/**
 * 
 */

package me.merdril.RandomBattle;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.entity.ComplexLivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.inventory.ItemStack;

/**
 * @author mark
 * 
 */
public class RandomBattleAttackCleanerListener extends EntityListener
{
	RandomBattle	plugin;
	private UUID[]	requiredEntities;
	protected int	deadEntites	= 0;
	
	/**
	 * 
	 */
	public RandomBattleAttackCleanerListener(RandomBattle instance, int numReqEntities)
	{
		plugin = instance;
		requiredEntities = new UUID[numReqEntities];
	}
	
	/**
	 * <code>public void onEntityDeath(EntityDeathEvent event)</code> <br/>
	 * <br/>
	 * This is a rather expensive function. It's a good thing Bukkit establishes concurrency on
	 * listeners. Here that Bukkit maintainers? Don't change that. <br/>
	 * This function looks through the HashMap of encountered entities and removes those that have
	 * died after a certain number of entities have died.
	 * 
	 * 
	 * @param event
	 *            - The event that sparks the function call
	 */
	@Override
	public void onEntityDeath(EntityDeathEvent event)
	{
		boolean isMonster = event.getEntity() instanceof Monster;
		boolean isDragon = event.getEntity() instanceof ComplexLivingEntity;
		if (!isMonster && !isDragon)
			return;
		if (deadEntites < requiredEntities.length - 1)
		{
			requiredEntities[deadEntites] = event.getEntity().getUniqueId();
			deadEntites++;
		}
		else
		{
			requiredEntities[requiredEntities.length - 1] = event.getEntity().getUniqueId();
			deadEntites = 0;
			ArrayList<ArrayList<UUID>> allMonsters =
			        new ArrayList<ArrayList<UUID>>(
			                RandomBattleAttackListener.alreadyEncountered.values());
			if (allMonsters == null || allMonsters.size() == 0)
				return;
			for (ArrayList<UUID> playerList : allMonsters)
			{
				for (UUID entityID : requiredEntities)
					synchronized (RandomBattleAttackListener.alreadyEncountered)
					{
						playerList.remove(entityID);
					}
			}
		}
	}
	
	@Override
	public void onEntityExplode(EntityExplodeEvent event)
	{
		if (event.getEntity() instanceof Monster)
		{
			ArrayList<ItemStack> nullList = new ArrayList<ItemStack>(0);
			plugin.getServer().getPluginManager()
			        .callEvent(new EntityDeathEvent(event.getEntity(), nullList));
		}
	}
}
