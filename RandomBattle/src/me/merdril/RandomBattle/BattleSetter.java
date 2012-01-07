
package me.merdril.RandomBattle;

import org.bukkit.entity.ComplexEntityPart;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
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
	
	/**
	 * 
	 */
	public BattleSetter(RandomBattle instance, SpoutPlayer player, LivingEntity monster)
	{
		plugin = instance;
		setStage(player, monster);
	}
	
	public BattleSetter(RandomBattle instance, SpoutPlayer player, Entity monster)
	{
		plugin = instance;
		if (monster instanceof ComplexEntityPart)
		{
			ComplexEntityPart dragonPart = (ComplexEntityPart) monster;
			LivingEntity dragon = dragonPart.getParent();
			setStage(player, dragon);
		}
	}
	
	public static void setStage(SpoutPlayer player, LivingEntity monster)
	{
		player.sendMessage("[RandomBattle] Stage set.");
	}
}
