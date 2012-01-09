
package me.merdril.RandomBattle;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
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
	RandomBattle	                plugin;
	protected static int	        stageHeight;
	protected static int	        stageWidth;
	protected static int	        stageLength;
	private Location	            startPoint;
	private Location	            currentPoint;
	private int	                    side	     = 1;
	private boolean	                noGoodStage	 = false;
	private static ArrayList<Block>	editedBlocks	= new ArrayList<Block>();
	
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
		startPoint = playerLocation;
		startPoint.setY(stageHeight);
		currentPoint = startPoint.getBlock().getLocation();
		findSafeStage(currentPoint);
		// Check that a stage was set
		if (noGoodStage)
			plugin.getServer()
			        .getConsoleSender()
			        .sendMessage(
			                "[RandomBattle] Failed to set stage for " + player.getDisplayName()
			                        + " at location " + player.getLocation().toString() + ".");
		player.sendMessage("[RandomBattle] Stage set.");
	}
	
	private void findSafeStage(Location loc)
	{
		outer:
		for (int i = 0; i < stageWidth; i++)
		{
			for (int j = 0; j < stageLength; j++)
			{
				// 5 for the maximum height of the battle field
				for (int k = 0; k < 5; k++)
				{
					if (!loc.getBlock().getType().equals(Material.AIR))
					{
						removeBlocks();
						if (side == 1)
						{
							if (currentPoint.getBlockX() - startPoint.getBlockX() >= stageWidth)
							{
								side = 2;
								currentPoint = startPoint.getBlock().getLocation();
								findSafeStage(currentPoint);
								break outer;
							}
							currentPoint.add(1, 0, 0);
							findSafeStage(currentPoint);
							break outer;
						}
						else if (side == 2)
						{
							if (startPoint.getBlockX() - currentPoint.getBlockX() >= stageWidth)
							{
								side = 3;
								currentPoint = startPoint.getBlock().getLocation();
								findSafeStage(currentPoint);
								break outer;
							}
							currentPoint.subtract(1, 0, 0);
							findSafeStage(currentPoint);
							break outer;
						}
						else if (side == 3)
						{
							if (startPoint.getBlockZ() - currentPoint.getBlockZ() >= stageLength)
							{
								side = 4;
								currentPoint = startPoint.getBlock().getLocation();
								findSafeStage(currentPoint);
								break outer;
							}
							currentPoint.add(0, 0, 1);
							findSafeStage(currentPoint);
							break outer;
						}
						else if (side == 4)
						{
							if (currentPoint.getBlockZ() - startPoint.getBlockZ() >= stageLength)
							{
								noGoodStage = true;
								break outer;
							}
							currentPoint.subtract(0, 0, 1);
							findSafeStage(currentPoint);
							break outer;
						}
					}
					if (k == 0)
					{
						editedBlocks.add(loc.getBlock());
						loc.getBlock().setType(Material.GRASS);
					}
					loc = loc.add(0, 1, 0);
				}
				loc.setY(stageHeight);
				loc.add(0, 0, 1);
			}
			loc.setZ(startPoint.getBlockZ() + i + 1);
			loc.add(1, 0, 0);
		}
	}
	
	public void removeBlocks()
	{
		for (Block block : editedBlocks)
		{
			block.setType(Material.AIR);
			editedBlocks.remove(block);
		}
	}
}
