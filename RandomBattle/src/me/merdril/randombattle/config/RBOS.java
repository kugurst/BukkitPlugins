
package me.merdril.randombattle.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import me.merdril.randombattle.RandomBattle;
import me.merdril.randombattle.battle.BattleSetter;

import org.bukkit.block.Block;

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
	public static void initialize(RandomBattle instance)
	{
		plugin = instance;
		dataFolder = new File(plugin.getDataFolder(), "data");
		if (!dataFolder.isDirectory())
			if (!dataFolder.mkdirs())
				dataFolder = null;
		BattleSetter.allEditedBlocks = loadBlocks(BattleSetter.blocksFile);
	}
	
	/**
	 * <p>
	 * Takes in a List&ltBlock&gt of blocks and writes them to the specified filename under the data
	 * folder specified by this class.
	 * </p>
	 * @param blocks
	 *            The List&ltBlock&gt to write to file.
	 * @param fileName
	 *            The String to call the created file.
	 * @return True if the list was successfully saved. False otherwise.
	 */
	public static boolean saveBlocks(List<Block> blocks, String fileName)
	{
		// Initialize the return variable.
		boolean result = false;
		File file = null;
		if (dataFolder == null)
			file = new File(plugin.getDataFolder(), fileName);
		else
			file = new File(dataFolder, fileName);
		if (file.exists())
			if (!file.delete()) {
				plugin.getLogger().warning(RandomBattle.prefix + "Unable to delete the existing block file!");
				return result;
			}
		ObjectOutputStream out = null;
		try {
			file.createNewFile();
			out = new ObjectOutputStream(new FileOutputStream(file));
			out.writeObject(blocks);
		}
		catch (FileNotFoundException e) {
			plugin.getLogger().warning(RandomBattle.prefix + "Unable to save the list of edited blocks!");
			e.printStackTrace();
			return result;
		}
		catch (IOException e) {
			plugin.getLogger().warning(RandomBattle.prefix + "Unable to save the list of edited blocks!");
			e.printStackTrace();
			return result;
		}
		finally {
			try {
				if (out != null) {
					out.flush();
					out.close();
				}
			}
			catch (IOException e) {
				plugin.getLogger().warning(RandomBattle.prefix + "An error occured while closing the output stream!");
				e.printStackTrace();
			}
		}
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
	@SuppressWarnings ("unchecked")
	public static List<Block> loadBlocks(String fileName)
	{
		List<Block> blocks = null;
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
					        RandomBattle.prefix + "Could not find the file of the List<Block> to load from.");
					return blocks;
				}
			}
			else {
				plugin.getLogger().warning(
				        RandomBattle.prefix + "Could not find the file of the List<Block> to load from.");
				return blocks;
			}
		}
		// If we're here, then the file existed
		ObjectInputStream in = null;
		try {
			in = new ObjectInputStream(new FileInputStream(file));
			Object obj = null;
			while ((obj = in.readObject()) != null) {
				if (obj instanceof List) {
					try {
						blocks = (List<Block>) obj;
					}
					catch (ClassCastException e) {
						e.printStackTrace();
						try {
							in.close();
						}
						catch (IOException e1) {
							plugin.getLogger()
							        .warning(
							                RandomBattle.prefix
							                        + "An error occured while trying to close the input stream for the List<Block> file");
							e1.printStackTrace();
						}
						return null;
					}
				}
			}
		}
		catch (FileNotFoundException e) {
			plugin.getLogger()
			        .warning(RandomBattle.prefix + "Could not find the file of the List<Block> to load from.");
			e.printStackTrace();
		}
		catch (IOException e) {
			plugin.getLogger().warning(RandomBattle.prefix + "An error occured while trying to read the file.");
			e.printStackTrace();
		}
		catch (ClassNotFoundException e) {
			plugin.getLogger().warning(RandomBattle.prefix + "The file contains an unidentified class!");
			e.printStackTrace();
		}
		finally {
			if (in != null) {
				try {
					in.close();
				}
				catch (IOException e) {
					plugin.getLogger()
					        .warning(
					                RandomBattle.prefix
					                        + "An error occured while trying to close the input stream for the List<Block> file");
					e.printStackTrace();
				}
			}
		}
		return blocks;
	}
}
