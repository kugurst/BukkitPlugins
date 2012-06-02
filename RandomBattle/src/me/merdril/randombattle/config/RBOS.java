
package me.merdril.randombattle.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantLock;

import me.merdril.randombattle.RBCommandExecutor;
import me.merdril.randombattle.RandomBattle;
import me.merdril.randombattle.battle.BattleSetter;

import org.bukkit.Location;

/**
 * <p>
 * This class contains method used for serializing objects that would be cumbersome or unnecessary
 * to wrap in a database, but are still needed to be persistent and cannot be easily described in a
 * text form.
 * </p>
 * @author Merdril
 */
public final class RBOS
{
	private static RandomBattle	 plugin;
	private static File	         dataFolder;
	private static ReentrantLock	blockLock;
	private static ReentrantLock	playerLock;
	
	// It's another utility class!
	private RBOS() throws AssertionError
	{
		throw new AssertionError();
	}
	
	// This one is more of a collection of methods, rather than a wrapper as RBDatabase.java
	/**
	 * <p>
	 * Gives this class access to the plugin facilities offered by Bukkit. And makes the object data
	 * storage folder if it doesn't already exist.
	 * </p>
	 * @param instance
	 *            The RandomBattle instance that extends JavaPlugin.
	 */
	public static void initialize(RandomBattle instance)
	{
		plugin = instance;
		dataFolder = new File(plugin.getDataFolder(), "data");
		if (!dataFolder.isDirectory())
			if (!dataFolder.mkdirs())
				dataFolder = null;
		if (blockLock == null)
			blockLock = new ReentrantLock(true);
		if (playerLock == null)
			playerLock = new ReentrantLock(true);
		Collection<Location> aebl = loadBlocks(BattleSetter.blocksFile);
		if (aebl != null)
			BattleSetter.allEditedBlockLocations = Collections.synchronizedList(new ArrayList<Location>(aebl));
		Collection<String> irp = loadRegisteredPlayers(RBCommandExecutor.registeredPlayersFile);
		if (irp != null)
			RBCommandExecutor.inactiveRegisteredPlayers = Collections.synchronizedSet(new HashSet<String>(irp));
	}
	
	/**
	 * <p>
	 * Takes in a List&ltLocation&gt of blocks and writes them to the specified filename under the
	 * data folder specified by this class.
	 * </p>
	 * @param allEditedBlockLocations
	 *            The List&ltLocation&gt to write to file.
	 * @param fileName
	 *            The String to call the created file.
	 * @return True if the list was successfully saved. False otherwise.
	 */
	public static boolean saveBlocks(List<Location> allEditedBlockLocations, String fileName)
	{
		// Initialize the return variable.
		boolean result = false;
		// Make the file to save the Locations to
		File file = null;
		if (dataFolder == null)
			file = new File(plugin.getDataFolder(), fileName);
		else
			file = new File(dataFolder, fileName);
		// Acquire the lock to proceed
		blockLock.lock();
		if (file.exists()) {
			if (!file.delete()) {
				plugin.getLogger().warning(RandomBattle.prefix + "Unable to delete the existing block file!");
				blockLock.unlock();
				return result;
			}
		}
		try {
			file.createNewFile();
		}
		catch (IOException e) {
			plugin.getLogger().warning(RandomBattle.prefix + "Unable to create a new edited block list!");
			e.printStackTrace();
			blockLock.unlock();
			return result;
		}
		// Make the PrintWriter to write the locations
		PrintWriter out = null;
		try {
			out = new PrintWriter(file);
		}
		catch (FileNotFoundException e) {
			plugin.getLogger().warning(RandomBattle.prefix + "Unable to locate the newly created location file!");
			e.printStackTrace();
			blockLock.unlock();
			return result;
		}
		// Write the locations to disk
		// Location loc = new Location(world, x, y, z);
		for (int i = 0; i < allEditedBlockLocations.size(); i++) {
			Location loc = allEditedBlockLocations.get(i);
			out.println(loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + ","
			        + loc.getBlockZ());
		}
		out.flush();
		// By this point, the lines have been written
		result = true;
		out.close();
		blockLock.unlock();
		return result;
	}
	
	/**
	 * <p>
	 * Loads a List&ltBlock&gt from the specified fileName in the object data folder.
	 * </p>
	 * <p>
	 * If the file is not found in the object data folder, it is looked for in the plugin data
	 * folder.
	 * </p>
	 * @param fileName
	 *            The name of the file to read the List&ltBlock&gt from.
	 * @return A List&ltBlock&gt if the file was located and successfully read and casted from. null
	 *         if some error occurs.
	 */
	public static Collection<Location> loadBlocks(String fileName)
	{
		HashSet<Location> blocks = null;
		boolean inData = true;
		File file = null;
		if (dataFolder != null)
			file = new File(dataFolder, fileName);
		else {
			file = new File(plugin.getDataFolder(), fileName);
			inData = false;
		}
		// Check for the file's existence
		if (!file.exists()) {
			if (inData) {
				// It did not exist in the data folder, so let's check the plugin folder
				file = new File(plugin.getDataFolder(), fileName);
				if (!file.exists()) {
					plugin.getLogger().warning(
					        RandomBattle.prefix + "Could not find the file of the List<Location> to load from.");
					return blocks;
				}
			}
			else {
				plugin.getLogger().warning(
				        RandomBattle.prefix + "Could not find the file of the List<Location> to load from.");
				return blocks;
			}
		}
		// If we're here, then the file existed
		// Make a Scanner to read the file contents
		Scanner in = null;
		try {
			in = new Scanner(file);
		}
		catch (FileNotFoundException e) {
			plugin.getLogger().warning(RandomBattle.prefix + "Could not open the Location file!");
			e.printStackTrace();
			return blocks;
		}
		// Initialize the location array
		blocks = new HashSet<Location>();
		// Read the file contents from the scanner
		// Location loc = new Location(world, x, y, z);
		while (in.hasNextLine()) {
			String line = in.nextLine();
			// Do not parse empty lines
			if (line.isEmpty())
				continue;
			try {
				String[] locArray = line.split("\\,");
				blocks.add(new Location(plugin.getServer().getWorld(locArray[0]), Integer.parseInt(locArray[1]),
				        Integer.parseInt(locArray[2]), Integer.parseInt(locArray[3])));
			}
			catch (NumberFormatException e) {
				plugin.getLogger().warning(RandomBattle.prefix + "Line: \"" + line + "\" is not parsable!");
			}
		}
		in.close();
		return blocks;
	}
	
	/**
	 * <p>
	 * Saves the currently registered players that this plugin is currently servicing.
	 * </p>
	 * @param players
	 *            A Collection&ltSpoutPlayer&gt containing the players to save
	 * @return True if the writing stream was successfully flushed after printing the players. False
	 *         otherwise.
	 */
	public static boolean saveRegisteredPlayers()
	{
		boolean saved = false;
		// Check for the status of the players file
		File file = null;
		if (dataFolder == null)
			file = new File(plugin.getDataFolder(), RBCommandExecutor.registeredPlayersFile);
		else
			file = new File(dataFolder, RBCommandExecutor.registeredPlayersFile);
		// Acquire the lock before going on to delete the file
		playerLock.lock();
		if (file.exists()) {
			if (!file.delete()) {
				plugin.getLogger().warning(RandomBattle.prefix + "Unable to delete the existing saved players file!");
				playerLock.unlock();
				return saved;
			}
		}
		try {
			file.createNewFile();
		}
		catch (IOException e) {
			plugin.getLogger().warning(RandomBattle.prefix + "Unable to create the new player file!");
			e.printStackTrace();
			playerLock.unlock();
			return saved;
		}
		// At this point, we have a file, so we can start writing to it.
		PrintWriter out = null;
		try {
			out = new PrintWriter(file);
		}
		catch (FileNotFoundException e) {
			plugin.getLogger().warning(RandomBattle.prefix + "Unable to open the player file!");
			e.printStackTrace();
			playerLock.unlock();
			return saved;
		}
		// Our PrintWriter is now set up, so let's write those values
		synchronized (RBCommandExecutor.registeredPlayers) {
			for (String player : RBCommandExecutor.registeredPlayers)
				out.println(player);
		}
		synchronized (RBCommandExecutor.inactiveRegisteredPlayers) {
			for (String player : RBCommandExecutor.inactiveRegisteredPlayers)
				out.println(player);
		}
		out.flush();
		saved = true;
		out.close();
		playerLock.unlock();
		return saved;
	}
	
	/**
	 * <p>
	 * Loads the registered players from disc from the file specified by RBCommandExecutor. This
	 * class does not return a collection of players, but a string of their display names.
	 * </p>
	 * <p>
	 * This method does not check to see if the players are online or if the players are currently
	 * SpoutCraft enabled.
	 * </p>
	 * @param registeredPlayersFile
	 * @return A Collection&ltString&gt (HashSet&ltString&gt) if the player file was successfully
	 *         read from. null otherwise.</p>
	 */
	public static Collection<String> loadRegisteredPlayers(String registeredPlayersFile)
	{
		// Construct the return Collection
		HashSet<String> players = new HashSet<String>();
		// Assume the file is in the data folder
		File file = new File(dataFolder, registeredPlayersFile);
		// If not, check the plugin data folder
		if (!file.exists()) {
			file = new File(plugin.getDataFolder(), registeredPlayersFile);
			// If it's also not in the data folder, then return
			if (!file.exists()) {
				plugin.getLogger().warning(RandomBattle.prefix + "Could not find the player file to load from!");
				return null;
			}
		}
		// At this point, we have an existent file
		Scanner in = null;
		try {
			in = new Scanner(file);
		}
		catch (FileNotFoundException e) {
			plugin.getLogger().warning(RandomBattle.prefix + "Unable to open the player file for reading!");
			e.printStackTrace();
			return null;
		}
		while (in.hasNextLine()) {
			String line = in.nextLine();
			if (!line.isEmpty())
				players.add(line);
		}
		return players;
	}
}
