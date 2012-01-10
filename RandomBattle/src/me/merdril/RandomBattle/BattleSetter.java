
package me.merdril.RandomBattle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ComplexLivingEntity;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Spider;
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
	RandomBattle	                     plugin;
	private int	                         stageHeight;
	private int	                         stageWidth;
	private int	                         stageLength;
	private Location	                 startPoint;
	private Location	                 currentPoint;
	
	// private int side = 1;
	private boolean	                     goodStage	     = true;
	private ArrayList<Block>	         editedBlocks	 = new ArrayList<Block>();
	public static ArrayList<Block>	     allEditedBlocks	= new ArrayList<Block>();
	private HashMap<Monster, Location[]>	field	     = new HashMap<Monster, Location[]>();
	private CommandSender	             console;
	
	/**
	 * 
	 */
	public BattleSetter(RandomBattle instance, SpoutPlayer player, Monster monster, int sH, int sW,
	        int sL)
	{
		plugin = instance;
		console = plugin.getServer().getConsoleSender();
		if (sH > 122)
			stageHeight = 122;
		else
			stageHeight = sH;
		if (sL < 10)
			stageLength = 10;
		else
			stageLength = sL;
		if (sW < 15)
			stageWidth = 15;
		else
			stageWidth = sW;
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
	
	private void setStage(SpoutPlayer player, Monster monster)
	{
		// Get the location of the two entities
		Location playerLocation = player.getLocation();
		// Try to make a stage for the monsters
		startPoint = playerLocation;
		startPoint.setY(stageHeight);
		startPoint = startPoint.getBlock().getLocation();
		currentPoint = startPoint.getBlock().getLocation();
		console.sendMessage("[RandomBattle] Starting point: " + currentPoint);
		findSafeStage();
		// Check that a stage was set
		if (!goodStage)
		{
			console.sendMessage("[RandomBattle] Failed to set stage for " + player.getDisplayName()
			        + " at location " + player.getLocation().toString() + ".");
			return;
		}
		player.sendMessage("[RandomBattle] Stage set. Teleporting...");
		field = makeBoundingBoxes(player, monster);
		teleportPlayer(player);
		teleportMonster();
	}
	
	/**
	 * <code>private HashMap<Monster, Location[]> makeBoundingBoxes(SpoutPlayer player, Monster monster)</code>
	 * <br/>
	 * <br/>
	 * Returns a mapping of monsters to the corners of their bounding box. The first location is the
	 * lower corner, and the second location is the upper corner.
	 * 
	 * @param player
	 *            - The player that attacked or was attacked by the monster.
	 * @param monster
	 *            - The monster that attacked or was attacked by the player.
	 * @return A mapping of monsters to their bounding boxes
	 */
	private HashMap<Monster, Location[]> makeBoundingBoxes(SpoutPlayer player, Monster monster)
	{
		Location mLowerCorner = null;
		Location mUpperCorner = null;
		if (monster instanceof Spider)
		{
			mLowerCorner =
			        new Location(monster.getWorld(), (startPoint.getBlockX() + stageWidth / 2),
			                stageHeight, (startPoint.getBlockZ() + 3));
			mUpperCorner =
			        new Location(monster.getWorld(), (startPoint.getBlockX() + stageWidth / 2 + 3),
			                (stageHeight + 2), (startPoint.getBlockZ() + 6));
		}
		else if (monster instanceof Enderman)
		{
			mLowerCorner =
			        new Location(monster.getWorld(), (startPoint.getBlockX() + stageWidth / 2),
			                stageHeight, (startPoint.getBlockZ() + 3));
			mUpperCorner =
			        new Location(monster.getWorld(), (startPoint.getBlockX() + stageWidth / 2 + 2),
			                (stageHeight + 4), (startPoint.getBlockZ() + 5));
		}
		else if (!(monster instanceof Enderman))
		{
			mLowerCorner =
			        new Location(monster.getWorld(), (startPoint.getBlockX() + stageWidth / 2),
			                stageHeight, (startPoint.getBlockZ() + 3));
			mUpperCorner =
			        new Location(monster.getWorld(), (startPoint.getBlockX() + stageWidth / 2 + 2),
			                (stageHeight + 3), (startPoint.getBlockZ() + 5));
		}
		mLowerCorner.getBlock().setType(Material.GLASS);
		mUpperCorner.getBlock().setType(Material.GLASS);
		int length = Math.abs(mLowerCorner.getBlockX() - mUpperCorner.getBlockX()) + 1;
		int width = Math.abs(mLowerCorner.getBlockZ() - mUpperCorner.getBlockZ()) + 1;
		int height = Math.abs(mLowerCorner.getBlockY() - mUpperCorner.getBlockY()) + 1;
		// In a bounding box, the coordinates of the inner boxes have nothing in common with the
		// two corners that define it. In other words, the boxes on the edge of the box share
		// the x, y, or z coordinate with one of the corners.
		Location currentBlock = mLowerCorner.getBlock().getLocation();
		for (int x = 0; x < length; x++)
		{
			for (int y = 0; y < width; y++)
			{
				for (int z = 0; z < height; z++)
				{
					// 6 conditions
					if (currentBlock.getBlockX() == mLowerCorner.getBlockX()
					        || currentBlock.getBlockY() == mLowerCorner.getBlockY()
					        || currentBlock.getBlockZ() == mLowerCorner.getBlockZ()
					        || currentBlock.getBlockX() == mUpperCorner.getBlockX()
					        || currentBlock.getBlockY() == mUpperCorner.getBlockY()
					        || currentBlock.getBlockZ() == mUpperCorner.getBlockZ())
					{
						editedBlocks.add(currentBlock.getBlock());
						allEditedBlocks.add(currentBlock.getBlock());
						currentBlock.getBlock().setType(Material.GLASS);
					}
					else
						console.sendMessage("[RandomBattle] Inner box location: "
						        + currentBlock.getBlock());
					currentBlock.add(0, 1, 0);
				}
				currentBlock.setY(mLowerCorner.getBlockY());
				currentBlock.add(0, 0, 1);
			}
			currentBlock.setZ(mLowerCorner.getBlockZ());
			currentBlock.add(1, 0, 0);
		}
		HashMap<Monster, Location[]> mField = new HashMap<Monster, Location[]>();
		mField.put(monster, new Location[] {mLowerCorner, mUpperCorner});
		return mField;
	}
	
	private void teleportMonster()
	{
		Set<Map.Entry<Monster, Location[]>> toTele = field.entrySet();
		console.sendMessage("[RandomBattle] toTele size: " + toTele.toArray().length);
		Location teleportLocation = null;
		for (Map.Entry<Monster, Location[]> monster : toTele)
		{
			console.sendMessage("[RandomBattle] Teleporting monster: " + monster.getKey() + " at: "
			        + monster.getKey().getLocation());
			teleportLocation = monster.getValue()[0].getBlock().getLocation();
			teleportLocation.add(1, 1, 1);
			monster.getKey().teleport(teleportLocation);
			console.sendMessage("[RandomBattle] to: " + teleportLocation);
		}
	}
	
	private void teleportPlayer(SpoutPlayer player)
	{
		int x = stageWidth / 2;
		int z = stageLength - 2;
		player.teleport(startPoint.add(x, 1, z));
	}
	
	private void findSafeStage()
	{
		// Initially the same as startPoint, and doesn't change until a block that isn't air is
		// encountered
		// Location changePoint = currentPoint.getBlock().getLocation();
		// 5 for the max height of the boundingBox
		outer:
		for (int i = 0; i < 5; i++)
		{
			for (int j = 0; j < stageWidth; j++)
			{
				for (int k = 0; k < stageLength; k++)
				{
					if (!currentPoint.getBlock().getType().equals(Material.AIR))
					{
						removeBlocks(editedBlocks);
						goodStage = false;
						break outer;
					}
					// console.sendMessage("[RandomBattle] Current location: " + currentPoint);
					if (i == 0)
					{
						editedBlocks.add(currentPoint.getBlock());
						allEditedBlocks.add(currentPoint.getBlock());
						currentPoint.getBlock().setType(Material.GRASS);
					}
					else if (i == 1)
					{
						if (j % 4 == 0 && k % 4 == 0)
						{
							editedBlocks.add(currentPoint.getBlock());
							allEditedBlocks.add(currentPoint.getBlock());
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
	
	private void removeBlocks(ArrayList<Block> blockList)
	{
		while (blockList.size() > 0)
		{
			blockList.get(0).setType(Material.AIR);
			blockList.remove(0);
		}
	}
}
// This is the functional part of the "if location is not air" check

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