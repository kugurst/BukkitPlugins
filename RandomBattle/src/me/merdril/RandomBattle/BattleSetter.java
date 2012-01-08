
package me.merdril.RandomBattle;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ComplexLivingEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
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
	RandomBattle	     plugin;
	protected static int	stageHeight;
	protected static int	stageWidth;
	protected static int	stageLength;
	
	/**
	 * 
	 */
	public BattleSetter(RandomBattle instance, SpoutPlayer player, Monster monster,
	        int stageHeight, int stageWidth, int stageLength)
	{
		plugin = instance;
		setStage(player, monster);
		if (stageHeight > 124)
			BattleSetter.stageHeight = 124;
		else
			BattleSetter.stageHeight = stageHeight;
		BattleSetter.stageLength = stageLength;
		BattleSetter.stageWidth = stageLength;
	}
	
	public BattleSetter(RandomBattle instance, SpoutPlayer player, ComplexLivingEntity dragon)
	{
		// plugin = instance;
		// if (monster instanceof ComplexEntityPart)
		// {
		// ComplexEntityPart dragonPart = (ComplexEntityPart) monster;
		// LivingEntity dragon = dragonPart.getParent();
		// setStage(player, dragon);
		// }
	}
	
	private void setStage(SpoutPlayer player, LivingEntity monster)
	{
		// Get the location of the two entities
		Location playerLocation = player.getLocation();
		Location monsterLocation = monster.getLocation();
		// Try to make a stage for the monsters
		Location startPoint = playerLocation;
		startPoint.setY(stageHeight);
		startPoint = startPoint.getBlock().getLocation();
		Location endPoint =
		        new Location(startPoint.getWorld(), startPoint.getBlockX(), startPoint.getBlockY(),
		                startPoint.getBlockZ());
		for (int i = 0; i < stageWidth; i++)
		{
			for (int j = 0; j < stageLength; j++)
			{
				// 5 for the maximum height of the battle field
				for (int k = 0; k < 5; k++)
				{
					if (endPoint.getBlock().getType().compareTo(Material.AIR) != 0)
						if (k == 0)
						{	

						}
						else
						{	

						}
				}
			}
		}
		player.sendMessage("[RandomBattle] Stage set.");
	}
	
	private Location findSafeStage(Location startPoint)
	{
		startPoint.setY(stageHeight);
		startPoint = startPoint.getBlock().getLocation();
		return startPoint;
	}
}
