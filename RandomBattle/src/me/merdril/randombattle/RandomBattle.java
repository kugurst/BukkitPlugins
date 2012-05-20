/**
 * 
 */

package me.merdril.randombattle;

import java.util.logging.Logger;

import me.merdril.randombattle.config.RBConfig;
import me.merdril.randombattle.config.RBDatabase;
import me.merdril.randombattle.listeners.RBAttackCleanerListener;
import me.merdril.randombattle.listeners.RBAttackListener;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * <p>
 * The entry point of RandomBattle and the initializer of all listeners.
 * </p>
 * <p>
 * The listeners themselves are responsible for initializing whatever classes they need to function
 * (and in turn, those classes initialize whatever it is they need to initialize).
 * </p>
 * @author Merdril
 */
public class RandomBattle extends JavaPlugin
{
	private Logger	          log	     = Logger.getLogger("Minecraft");
	/**
	 * <p>
	 * The prefix to begin all log messages with.
	 * </p>
	 */
	public static String	  prefix	 = "";
	/**
	 * <p>
	 * An int representing the z-position of the stage, the width of the stage (east-west), the
	 * length of the stage (north-south), and how often (in the long run) to have a random battle.
	 * </p>
	 */
	public static int	      stageHeight, stageWidth, stageLength, randomChance;
	private int	              trigDelNum	= 5;
	private RBCommandExecutor	cExec;
	
	// Initializes all the listeners and registers all the commands. Tells the server when it is
	// done.
	@Override
	public void onEnable()
	{
		// Load the configuration
		RBConfig config = new RBConfig(this);
		int[] dim = config.getDimensions();
		stageHeight = dim[0];
		stageWidth = dim[1];
		stageLength = dim[2];
		randomChance = config.getChance();
		// Initialize the database wrapper
		RBDatabase.initialize(this);
		
		// Get the PluginManager to minimize line length (and stack calls)
		PluginManager pm = this.getServer().getPluginManager();
		// Initialize the prefix for all communications with the outside world
		prefix = "[" + this.getName() + "] ";
		
		// Initialize the command executer
		cExec = new RBCommandExecutor(this);
		getCommand("regbattle").setExecutor(cExec);
		getCommand("stopbattles").setExecutor(cExec);
		getCommand("unregbattle").setExecutor(cExec);
		getCommand("resumebattles").setExecutor(cExec);
		getCommand("showregplayers").setExecutor(cExec);
		getCommand("showspoutplayers").setExecutor(cExec);
		getCommand("removeblocks").setExecutor(cExec);
		
		// Initialize some of the listeners: The AttackListener to initiate attacks, the
		// AttackCleaner to clear the data structures that keep track of monster-player
		// interactions, and the ScreenListener for something in the future, I guess. I don't know
		// what old me was thinking exactly.
		pm.registerEvents(new RBAttackListener(this), this);
		pm.registerEvents(new RBAttackCleanerListener(this, trigDelNum), this);
		
		// That's all folks
		log.info(prefix + "Random Battle has started!");
	}
	
	// Removes all the blocks that this program has placed. The monsters will fall to their deaths
	// (and so will the players if they were left there)
	@Override
	public void onDisable()
	{
		log.info(prefix + "Removing left over blocks...");
		cExec.removeEditedBlocks(null, null, null, null);
		log.info(prefix + "Random Battle has shut down!");
	}
}