
package me.merdril.RandomBattle;

import org.bukkit.entity.Entity;
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
	static RandomBattle	  plugin;
	private static Vector	zeroVector	= new Vector(0, 0, 0);
	
	/**
	 * 
	 */
	public BattleSetter(RandomBattle instance, SpoutPlayer player, LivingEntity monster)
	{
		plugin = instance;
		stopEntities(player, monster);
	}
	
	public BattleSetter(RandomBattle instance, SpoutPlayer player, Entity monster)
	{
		plugin = instance;
		stopEntities(player, monster);
	}
	
	public static void stopEntities(LivingEntity entity1, LivingEntity entity2)
	{
		SpoutPlayer player = (SpoutPlayer) entity1;
		player.sendMessage("[RandomBattle] Vector sent.");
	}
	
	public static void stopEntities(LivingEntity entity1, Entity entity2)
	{
		
		SpoutPlayer player = (SpoutPlayer) entity1;
		player.sendMessage("[RandomBattle] Vector sent.");
	}
}
