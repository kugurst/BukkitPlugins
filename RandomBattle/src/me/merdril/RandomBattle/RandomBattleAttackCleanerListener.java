/**
 * 
 */

package me.merdril.RandomBattle;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;

/**
 * @author mark
 * 
 */
public class RandomBattleAttackCleanerListener extends EntityListener
{
	RandomBattle	plugin;
	
	/**
	 * 
	 */
	public RandomBattleAttackCleanerListener(RandomBattle instance)
	{
		plugin = instance;
	}
	
	@Override
	public void onEntityDeath(EntityDeathEvent event)
	{
		UUID searchID = event.getEntity().getUniqueId();
		ArrayList<ArrayList<UUID>> encounteredEntities =
		        new ArrayList<ArrayList<UUID>>(
		                RandomBattleAttackListener.alreadyEncountered.values());
	}
}
