
package me.merdril.RandomBattle;

import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;
import org.getspout.spoutapi.player.SpoutPlayer;

/**
 * 
 */

/**
 * @author mark
 * 
 */
public class BattleSetter
{
	RandomBattle	plugin;
	private Vector	zeroVector	= new Vector(0, 0, 0);
	
	/**
	 * 
	 */
	public BattleSetter(RandomBattle instance, SpoutPlayer player, LivingEntity monster)
	{
		plugin = instance;
		stopEntities(player, monster);
	}
	
	public void stopEntities(LivingEntity entity1, LivingEntity entity2)
	{
		entity1.setVelocity(zeroVector);
		entity2.setVelocity(zeroVector);
	}
	
}
