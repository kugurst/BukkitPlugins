
package me.merdril.randombattle.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import me.merdril.randombattle.RandomBattle;

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
	private static RandomBattle	plugin;
	private static File	        dataFolder;
	
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
	public static void initialize(RandomBattle instance) throws AssertionError
	{
		plugin = instance;
		dataFolder = new File(plugin.getDataFolder(), "data");
		if (!dataFolder.isDirectory())
			// Failing to make the data folder results in over-engineering, and it doesn't really
			// make sense to continue. Just terminate the plugin
			if (!dataFolder.mkdirs()) {
				plugin.getLogger().severe(
				        "Unable to create data folder. Check your plugin folder permissions. Shutting Down...");
				throw new AssertionError("Cannot create plugin data folder.");
			}
	}
	
	/**
	 * <p>
	 * Takes in a List&ltLocation&gt of blocks and writes them to the specified filename under the
	 * data folder specified by this class. It is expected that this method is only called by the
	 * blockSaver object initialized by BattleSetter.
	 * </p>
	 * @param blocks
	 *            The List&ltLocation&gt to write to file.
	 * @param fileName
	 *            The String to call the created file.
	 * @return True if the list was successfully saved. False otherwise.
	 */
	public static boolean saveBlocks(List<Location> blocks, String fileName)
	{
		System.out.println(blocks);
		// Initialize the return variable.
		boolean result = false;
		// Make the file to save the Locations to
		File file = new File(dataFolder, fileName);
		if (file.exists()) {
			if (!file.delete()) {
				plugin.getLogger().warning(
				        RandomBattle.prefix
				                + "Unable to delete the existing block file! Check your folder/file permissions.");
				return result;
			}
		}
		try {
			file.createNewFile();
		}
		catch (IOException e) {
			plugin.getLogger().warning(
			        RandomBattle.prefix + "Unable to create a new edited block list! Check your folder permissions.");
			e.printStackTrace();
			return result;
		}
		// Make the PrintWriter to write the locations
		PrintWriter out = null;
		try {
			out = new PrintWriter(file);
		}
		catch (FileNotFoundException e) {
			plugin.getLogger().warning(
			        RandomBattle.prefix + "Unable to open file for writing! What happened in those few microseconds?");
			e.printStackTrace();
			return result;
		}
		// Write the locations to disk
		// Location loc = new Location(world, x, y, z);
		for (Location loc : blocks)
			out.println(loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + ","
			        + loc.getBlockZ());
		out.flush();
		// By this point, the lines have been written
		result = true;
		out.close();
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
	public static ArrayList<Location> loadBlocks(String fileName)
	{
		ArrayList<Location> blocks = new ArrayList<Location>();
		File file = new File(dataFolder, fileName);
		if (!file.exists())
			return blocks;
		else if (!file.canRead()) {
			plugin.getLogger().warning(
			        RandomBattle.prefix + "Cannot read from the Location file. Check your file permissions.");
			return blocks;
		}
		
		// Make a Scanner to read the file contents
		Scanner in = null;
		try {
			in = new Scanner(file);
		}
		catch (FileNotFoundException e) {
			plugin.getLogger().warning(
			        RandomBattle.prefix
			                + "Could not open the Location file for reading! What happened in those few microseconds?");
			e.printStackTrace();
			return blocks;
		}
		// Read the file contents from the scanner
		// Location loc = new Location(world, x, y, z);
		while (in.hasNextLine()) {
			String line = in.nextLine();
			// Do not parse empty lines
			if (line.isEmpty())
				continue;
			try {
				// World,X,Y,Z
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
	public static boolean saveRegisteredPlayers(String registeredPlayersFile, Set<String>... playerLists)
	{
		boolean saved = false;
		// Check for the status of the players file
		File file = new File(dataFolder, registeredPlayersFile);
		if (file.exists()) {
			if (!file.delete()) {
				plugin.getLogger().warning(RandomBattle.prefix + "Unable to delete the existing saved players file!");
				return saved;
			}
		}
		try {
			file.createNewFile();
		}
		catch (IOException e) {
			plugin.getLogger().warning(RandomBattle.prefix + "Unable to create the new player file!");
			e.printStackTrace();
			return saved;
		}
		// At this point, we have a file, so we can start writing to it.
		PrintWriter out = null;
		try {
			out = new PrintWriter(file);
		}
		catch (FileNotFoundException e) {
			plugin.getLogger().warning(
			        RandomBattle.prefix
			                + "Unable to open the player file for writing! What happened in those few microseconds?");
			e.printStackTrace();
			return saved;
		}
		// Our PrintWriter is now set up, so let's write those players
		for (Set<String> players : playerLists)
			for (String player : players)
				out.println(player);
		out.flush();
		saved = true;
		out.close();
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
	public static HashSet<String> loadRegisteredPlayers(String registeredPlayersFile)
	{
		// Construct the return
		HashSet<String> players = new HashSet<String>();
		
		File file = new File(dataFolder, registeredPlayersFile);
		// If the file doesn't exist, return the empty HashSet
		if (!file.exists())
			return players;
		// If we cannot read from the file, inform the server manager
		if (!file.canRead()) {
			plugin.getLogger().warning(
			        RandomBattle.prefix + "Cannot read from the players file. Check your file permissions.");
			return players;
		}
		
		Scanner in = null;
		try {
			in = new Scanner(file);
		}
		catch (FileNotFoundException e) {
			plugin.getLogger().warning(
			        RandomBattle.prefix
			                + "Unable to open the players file for reading. What happened in those few microseconds?");
			e.printStackTrace();
			return players;
		}
		while (in.hasNextLine()) {
			String line = in.nextLine();
			if (!line.isEmpty())
				players.add(line);
		}
		in.close();
		return players;
	}
}
