/**
 * 
 */

package me.merdril.RandomBattle.HUD;

import java.util.ArrayList;

import me.merdril.RandomBattle.RandomBattle;

import org.bukkit.entity.Monster;
import org.getspout.spoutapi.gui.InGameScreen;
import org.getspout.spoutapi.gui.WidgetAnchor;
import org.getspout.spoutapi.player.SpoutPlayer;

/**
 * @author Merdril
 * 
 */
public class RandomBattleHUD
{
	RandomBattle	                plugin;
	private SpoutPlayer	            player;
	private RandomBattlePopupScreen	screen;
	private CommandButtonContainer	buttons;
	private RandomBattleTopBar	    topBar;
	private InGameScreen	        mainScreen;
	
	/**
	 * @param player
	 * @param battleMonsters
	 * 
	 */
	public RandomBattleHUD(RandomBattle instance, SpoutPlayer player,
	        ArrayList<Monster> battleMonsters)
	{
		plugin = instance;
		this.player = player;
		player.closeActiveWindow();
		
		// Setting all the default bars to be not visible.
		mainScreen = (InGameScreen) player.getMainScreen();
		mainScreen.getArmorBar().setVisible(false);
		mainScreen.getHealthBar().setVisible(false);
		mainScreen.getBubbleBar().setVisible(false);
		mainScreen.getExpBar().setVisible(false);
		mainScreen.getHungerBar().setVisible(false);
		mainScreen.getChatBar().setVisible(false);
		mainScreen.getChatTextBox().setVisible(false);
		mainScreen.closePopup();
		
		// Making the screen objects
		screen = new RandomBattlePopupScreen(plugin, player);
		buttons = new CommandButtonContainer(plugin, screen, player, battleMonsters);
		
		// Setting the layout
		screen.attachWidgets(plugin, buttons, topBar);
		buttons.setAnchor(WidgetAnchor.BOTTOM_LEFT).shiftYPos(-buttons.getHeight() - 20)
		        .shiftXPos(20);
		mainScreen.attachPopupScreen(screen);
	}
}