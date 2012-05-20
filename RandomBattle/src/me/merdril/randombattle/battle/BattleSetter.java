
package me.merdril.randombattle.battle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.merdril.randombattle.RandomBattle;
import me.merdril.randombattle.hud.RBHUD;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Spider;
import org.getspout.spoutapi.player.SpoutPlayer;

/**
 * <p>
 * Sets up the stage and teleports all actors to the stage.
 * </p>
 * @author Merdril
 */
public class BattleSetter
{
	RandomBattle	                          plugin;
	private int	                              stageHeight;
	private int	                              stageWidth;
	private int	                              stageLength;
	private Location	                      startPoint;
	private Location	                      currentPoint;
	
	// private int side = 1;
	private boolean	                          goodStage	     = true;
	private ArrayList<Block>	              editedBlocks	 = new ArrayList<Block>();
	/**
	 * <p>
	 * Contains a List<Block> of all blocks that have been modified by this class from their
	 * original state.
	 * </p>
	 * <p>
	 * It is used after a battle in removing all modified blocks and generally leaving the server as
	 * it was.
	 * </p>
	 */
	public static List<Block>	              allEditedBlocks;
	/**
	 * <p>
	 * A Map of SpoutPlayer to the Monsters they are currently facing.
	 * </p>
	 * <p>
	 * Will serve a future use when parties are added to this plugin.
	 * </p>
	 */
	public static Map<SpoutPlayer, Monster[]>	allBattleMonsters;
	/**
	 * <p>
	 * An ArrayList of Monster this instance of BattleSetter is currently servicing.
	 * </p>
	 */
	public ArrayList<Monster>	              battleMonsters	= new ArrayList<Monster>();
	private HashMap<Monster, Location[]>	  field	         = new HashMap<Monster, Location[]>();
	private CommandSender	                  console;
	
	/**
	 * <p>
	 * Initializes some tracker maps if they haven't already been, and sets some stage parameters.
	 * </p>
	 * @param instance
	 *            The JavaPlugin that this class is a part of
	 * @param player
	 *            The SpoutPlayer that is involved in this RandomBattle
	 * @param monster
	 *            The Monster that is involved in this RandomBattle
	 * @param sH
	 *            The height of the stage to create
	 * @param sW
	 *            The width of the stage to create (larger than length)
	 * @param sL
	 *            The length of the stage to create (smaller than width)
	 */
	public BattleSetter(RandomBattle instance, SpoutPlayer player, Monster monster)
	{
		// Mark the plugin for future use
		this.plugin = instance;
		// Initialize the static variables if need be
		if (allBattleMonsters == null)
			allBattleMonsters = Collections.synchronizedMap(new HashMap<SpoutPlayer, Monster[]>());
		if (allEditedBlocks == null)
			allEditedBlocks = Collections.synchronizedList(new ArrayList<Block>());
		// Mark the console command sender for future use
		console = plugin.getServer().getConsoleSender();
		// Make sure the user specified values for the stage dimensions do not break some method in
		// this class.
		if (RandomBattle.stageHeight > 122)
			stageHeight = 122;
		else
			stageHeight = RandomBattle.stageHeight;
		if (RandomBattle.stageLength < 10)
			stageLength = 10;
		else
			stageLength = RandomBattle.stageLength;
		if (RandomBattle.stageWidth < 15)
			stageWidth = 15;
		else
			stageWidth = RandomBattle.stageWidth;
		// Set the stage with the given monster grouping and player
		setStage(player, monster);
		// Add the monsters to the list of monsters in battle
		battleMonsters.add(monster);
		@SuppressWarnings ("unused")
		// Start the battle
		RBHUD overlay = new RBHUD(plugin, player, battleMonsters);
	}
	
	/**
	 * <p>
	 * Creates a stage for the monsters to stand on, and then teleports the monsters to this stage.
	 * </p>
	 * @param player
	 *            The player involved in the battle
	 * @param monster
	 *            The monster to create a grouping from
	 */
	private void setStage(SpoutPlayer player, Monster monster)
	{
		// Get the location of the two entities
		Location playerLocation = player.getLocation();
		// Try to make a stage for the monsters
		startPoint = playerLocation;
		// Move the starting point to the y above the player
		startPoint.setY(stageHeight);
		// Move the startPoint into block coordinates
		startPoint = startPoint.getBlock().getLocation();
		currentPoint = startPoint.getBlock().getLocation();
		// Debugging
		console.sendMessage(RandomBattle.prefix + "Starting point: " + currentPoint);
		// Construct the stage, apparently old me was too lazy to make this method return a boolean.
		findSafeStage();
		// Check that a stage was set
		if (!goodStage) {
			console.sendMessage(RandomBattle.prefix + "Failed to set stage for " + player.getDisplayName()
			        + " at location " + player.getLocation().toString() + ".");
			return;
		}
		player.sendMessage(RandomBattle.prefix + "Stage set. Teleporting...");
		// Update the location of the monsters and players.
		field = determineBoundingBoxes(player, monster);
		// Moves the specified player
		teleportPlayer(player);
		// Moves all monsters in field
		teleportMonster();
	}
	
	/**
	 * <p>
	 * Returns a mapping of monsters to the corners of their bounding box. The first location is the
	 * lower corner, and the second location is the upper corner.
	 * </p>
	 * @param player
	 *            The player that attacked or was attacked by the monster.
	 * @param monster
	 *            The monster that attacked or was attacked by the player.
	 * @return A mapping of monsters to their bounding boxes
	 */
	private HashMap<Monster, Location[]> determineBoundingBoxes(SpoutPlayer player, Monster monster)
	{
		Location mLowerCorner = null;
		Location mUpperCorner = null;
		if (monster instanceof Spider) {
			mLowerCorner =
			        new Location(monster.getWorld(), (startPoint.getBlockX() + stageWidth / 2), stageHeight,
			                (startPoint.getBlockZ() + 3));
			mUpperCorner =
			        new Location(monster.getWorld(), (startPoint.getBlockX() + stageWidth / 2 + 3), (stageHeight + 2),
			                (startPoint.getBlockZ() + 6));
		}
		else if (monster instanceof Enderman) {
			mLowerCorner =
			        new Location(monster.getWorld(), (startPoint.getBlockX() + stageWidth / 2), stageHeight,
			                (startPoint.getBlockZ() + 3));
			mUpperCorner =
			        new Location(monster.getWorld(), (startPoint.getBlockX() + stageWidth / 2 + 2), (stageHeight + 4),
			                (startPoint.getBlockZ() + 5));
		}
		else if (!(monster instanceof Enderman)) {
			mLowerCorner =
			        new Location(monster.getWorld(), (startPoint.getBlockX() + stageWidth / 2), stageHeight,
			                (startPoint.getBlockZ() + 3));
			mUpperCorner =
			        new Location(monster.getWorld(), (startPoint.getBlockX() + stageWidth / 2 + 2), (stageHeight + 3),
			                (startPoint.getBlockZ() + 5));
		}
		makeBoundingBox(mLowerCorner, mUpperCorner);
		HashMap<Monster, Location[]> mField = new HashMap<Monster, Location[]>();
		mField.put(monster, new Location[] {mLowerCorner, mUpperCorner});
		synchronized (allBattleMonsters) {
			allBattleMonsters.put(player, new Monster[] {monster});
		}
		return mField;
	}
	
	/**
	 * <p>
	 * Constructs a bounding box made of glass based on the two coordinates given. Does not make
	 * blocks in the first height layer.
	 * </p>
	 * <p>
	 * In a bounding box, the coordinates of the inner boxes have nothing in common with the two
	 * corners that define it. In other words, the boxes on the edge of the box share the x, y, or z
	 * coordinate with one of the corners.
	 * </p>
	 * @param lowerCorner
	 *            The lower left Location to position the bottom left corner of the bounding box
	 * @param upperCorner
	 *            The upper right Location to position the upper right corner of the bounding box
	 */
	public void makeBoundingBox(Location lowerCorner, Location upperCorner)
	{
		int length = Math.abs(lowerCorner.getBlockX() - upperCorner.getBlockX()) + 1;
		int width = Math.abs(lowerCorner.getBlockZ() - upperCorner.getBlockZ()) + 1;
		int height = Math.abs(lowerCorner.getBlockY() - upperCorner.getBlockY()) + 1;
		Location currentBlock = lowerCorner.getBlock().getLocation();
		for (int x = 0; x < length; x++) {
			for (int y = 0; y < width; y++) {
				for (int z = 0; z < height; z++) {
					// The boolean expression of the above documentation
					if (z != 0
					        && (currentBlock.getBlockX() == lowerCorner.getBlockX()
					                || currentBlock.getBlockY() == lowerCorner.getBlockY()
					                || currentBlock.getBlockZ() == lowerCorner.getBlockZ()
					                || currentBlock.getBlockX() == upperCorner.getBlockX()
					                || currentBlock.getBlockY() == upperCorner.getBlockY() || currentBlock.getBlockZ() == upperCorner
					                .getBlockZ())) {
						editedBlocks.add(currentBlock.getBlock());
						synchronized (allEditedBlocks) {
							allEditedBlocks.add(currentBlock.getBlock());
						}
						currentBlock.getBlock().setType(Material.GLASS);
					}
					// else
					// console.sendMessage(RandomBattle.prefix+"Inner box location: "
					// + currentBlock.getBlock());
					currentBlock.add(0, 1, 0);
				}
				currentBlock.setY(lowerCorner.getBlockY());
				currentBlock.add(0, 0, 1);
			}
			currentBlock.setZ(lowerCorner.getBlockZ());
			currentBlock.add(1, 0, 0);
		}
	}
	
	/**
	 * <p>
	 * Teleports monsters contained in a HashMap local to this object to the stage created by this
	 * class.
	 * </p>
	 * <p>
	 * Spiders and humanoid mobs have different teleport locations to account for their different
	 * dimensions.
	 * </p>
	 */
	private void teleportMonster()
	{
		Set<Map.Entry<Monster, Location[]>> toTele = field.entrySet();
		Location teleportLocation = null;
		for (Map.Entry<Monster, Location[]> monster : toTele) {
			teleportLocation = monster.getValue()[0].getBlock().getLocation();
			// A position adjustment for the size of spiders.
			if (monster.getKey() instanceof Spider)
				teleportLocation.add(2, 1, 2);
			else
				teleportLocation.add(1.5, 2, 1.5);
			monster.getKey().teleport(teleportLocation);
		}
	}
	
	/**
	 * <p>
	 * Teleports the specified player to: the middle-width of the stage, 3 block length from the
	 * edge of the stage, and on top of the stage.
	 * </p>
	 * <p>
	 * It also rotates the player to face the monsters on stage.
	 * </p>
	 * @param player
	 *            The SpoutPlayer to teleport to an already determined location on the stage
	 */
	private void teleportPlayer(SpoutPlayer player)
	{
		int x = stageWidth / 2 + 1;
		int z = stageLength - 3;
		Location teleportLocation =
		        new Location(player.getWorld(), startPoint.getBlockX() + x + 0.5, startPoint.getBlockY() + 1,
		                startPoint.getBlockZ() + z + 0.5, -180, 0);
		player.teleport(teleportLocation);
	}
	
	/**
	 * <p>
	 * Determines if the stage is safe to hold a battle.
	 * </p>
	 * <p>
	 * Slightly misleadingly, this method also builds the stage while its at it, but then unbuilds
	 * it should some obstruction be found.
	 * </p>
	 */
	private void findSafeStage()
	{
		// Initially the same as startPoint, and doesn't change until a block that isn't air is
		// encountered
		// Iterate over the 3 dimensions
		outer: for (int i = 0; i < stageHeight; i++) {
			for (int j = 0; j < stageWidth; j++) {
				for (int k = 0; k < stageLength; k++) {
					// If we encounter a point that is not open, remove all the blocks we have
					// changed and return
					if (!currentPoint.getBlock().getType().equals(Material.AIR)) {
						removeBlocks(editedBlocks);
						goodStage = false;
						break outer;
					}
					// console.sendMessage(RandomBattle.prefix+"Current location: " + currentPoint);
					// If we are on the first level, make the floor grass.
					if (i == 0) {
						editedBlocks.add(currentPoint.getBlock());
						synchronized (allEditedBlocks) {
							allEditedBlocks.add(currentPoint.getBlock());
						}
						currentPoint.getBlock().setType(Material.GRASS);
					}
					// If we are on the second level, place lightstone every so often
					else if (i == 1) {
						if (j % 4 == 0 && k % 4 == 0) {
							editedBlocks.add(currentPoint.getBlock());
							synchronized (allEditedBlocks) {
								allEditedBlocks.add(currentPoint.getBlock());
							}
							currentPoint.getBlock().setType(Material.GLOWSTONE);
						}
					}
					// Move right
					currentPoint.add(0, 0, 1);
				}
				// Reset the z position and move left
				currentPoint.setZ(startPoint.getBlockZ());
				currentPoint.add(1, 0, 0);
			}
			// Reset the x position and move up
			currentPoint.setX(startPoint.getBlockX());
			currentPoint.add(0, 1, 0);
		}
	}
	
	/**
	 * <p>
	 * A simply helper method to remove all the blocks that have been changed. It's generic in that
	 * it turns all the blocks in some specified ArrayList&ltBlock&gt.
	 * </p>
	 * @param blockList
	 *            The ArrayList&ltBlock&gt to remove blocks from.
	 */
	private void removeBlocks(ArrayList<Block> blockList)
	{
		if (blockList == null)
			return;
		while (blockList.size() > 0) {
			blockList.get(0).setType(Material.AIR);
			blockList.remove(0);
		}
	}
}
// This is the functional part of the "if location is not air" check

// console.sendMessage(RandomBattle.prefix+"Location: " + currentPoint
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
