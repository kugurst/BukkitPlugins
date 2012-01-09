
package me.merdril.RandomBattle;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
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
	RandomBattle	               plugin;
	private int	                   stageHeight;
	private int	                   stageWidth;
	private int	                   stageLength;
	private Location	           startPoint;
	private Location	           currentPoint;
	
	private int	                   side	            = 1;
	private boolean	               goodStage	    = true;
	private ArrayList<Block>	   editedBlocks	    = new ArrayList<Block>();
	public static ArrayList<Block>	allEditedBlocks	= new ArrayList<Block>();
	private CommandSender	       console;
	
	/**
	 * 
	 */
	public BattleSetter(RandomBattle instance, SpoutPlayer player, Monster monster,
	        int stageHeight, int stageWidth, int stageLength)
	{
		plugin = instance;
		console = plugin.getServer().getConsoleSender();
		if (stageHeight > 123)
			this.stageHeight = 123;
		else
			this.stageHeight = stageHeight;
		this.stageLength = stageLength;
		this.stageWidth = stageLength;
		setStage(player, monster);
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
		startPoint = startPoint.getBlock().getLocation();
		currentPoint = startPoint.getBlock().getLocation();
		console.sendMessage("[RandomBattle] Starting point: " + currentPoint);
		findSafeStage();
		// Check that a stage was set
		if (!goodStage)
			console.sendMessage("[RandomBattle] Failed to set stage for " + player.getDisplayName()
			        + " at location " + player.getLocation().toString() + ".");
		else
			player.sendMessage("[RandomBattle] Stage set.");
	}
	
	private void findSafeStage()
	{
		// Initially the same as startPoint, and doesn't change until a block that isn't air is
		// encountered
		Location changePoint = currentPoint.getBlock().getLocation();
		console.sendMessage("[RandomBattle] Mod location: " + changePoint);
		// 5 for the max height of the boundingBox
		outer:
		for (int i = 0; i <= 4; i++)
		{
			for (int j = 0; j <= stageWidth; j++)
			{
				for (int k = 0; k <= stageLength; k++)
				{
					if (!currentPoint.getBlock().getType().equals(Material.AIR))
					{
						removeBlocks(editedBlocks);
						goodStage = false;
						// console.sendMessage("[RandomBattle] Location: " + currentPoint
						// + " is not air on side: " + side + ".");
						// removeBlocks(editedBlocks);
						// if (side == 1)
						// {
						// if (changePoint.getBlockX() - startPoint.getBlockX() > stageWidth)
						// {
						// changePoint = startPoint.getBlock().getLocation();
						// changePoint.subtract(1, 0, 0);
						// currentPoint = changePoint.getBlock().getLocation();
						// side = 2;
						// }
						// else
						// {
						// changePoint.add(1, 0, 0);
						// currentPoint = changePoint.getBlock().getLocation();
						// }
						// }
						// else if (side == 2)
						// {
						// if (startPoint.getBlockX() - changePoint.getBlockX() > stageWidth)
						// {
						// changePoint = startPoint.getBlock().getLocation();
						// changePoint.add(0, 0, 1);
						// currentPoint = changePoint.getBlock().getLocation();
						// side = 3;
						// }
						// else
						// {
						// changePoint.subtract(1, 0, 0);
						// currentPoint = changePoint.getBlock().getLocation();
						// }
						// }
						// else if (side == 3)
						// {
						// if (changePoint.getBlockZ() - startPoint.getBlockZ() > stageLength)
						// {
						// changePoint = startPoint.getBlock().getLocation();
						// changePoint.subtract(0, 0, 1);
						// currentPoint = changePoint.getBlock().getLocation();
						// side = 4;
						// }
						// else
						// {
						// changePoint.add(0, 0, 1);
						// currentPoint = changePoint.getBlock().getLocation();
						// }
						// }
						// else if (side == 4)
						// {
						// if (startPoint.getBlockZ() - changePoint.getBlockZ() > stageLength)
						// {
						// goodStage = false;
						// break outer;
						// }
						// else
						// {
						// changePoint.subtract(0, 0, 1);
						// currentPoint = changePoint.getBlock().getLocation();
						// }
						// }
						// i = j = k = 0;
						// continue outer;
					}
					if (i == 0)
					{
						editedBlocks.add(currentPoint.getBlock());
						currentPoint.getBlock().setType(Material.GRASS);
					}
					else if (i == 1)
					{
						if (j % 3 == 0 && k % 3 == 0)
						{
							editedBlocks.add(currentPoint.getBlock());
							currentPoint.getBlock().setType(Material.GLOWSTONE);
						}
					}
					currentPoint.add(0, 0, 1);
				}
				currentPoint.setZ(startPoint.getBlockZ());
				currentPoint.add(1, 0, 0);
			}
			currentPoint.setX(startPoint.getBlockX());
			currentPoint.add(0, 1, 0);
		}
	}
	
	public static void removeBlocks(ArrayList<Block> blockList)
	{
		while (blockList.size() > 0)
		{
			blockList.get(0).setType(Material.AIR);
			allEditedBlocks.remove(blockList.get(0));
			blockList.remove(0);
		}
	}
}