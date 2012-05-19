/**
 * 
 */

package me.merdril.RandomBattle.hud;

import java.util.ArrayList;

import me.merdril.RandomBattle.RandomBattle;
import me.merdril.RandomBattle.battle.TurnListWidget;
import me.merdril.RandomBattle.listeners.RBScreenListener;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.gui.GenericWidget;
import org.getspout.spoutapi.gui.InGameScreen;
import org.getspout.spoutapi.gui.WidgetAnchor;
import org.getspout.spoutapi.player.SpoutPlayer;

/**
 * @author Merdril
 */
public class RBHUD
{
	RandomBattle	                 plugin;
	private ArrayList<GenericWidget>	invisibleWidgets;
	private SpoutPlayer	             player;
	private GenericPopup	         screen;
	private CommandButtonContainer	 buttons;
	private RBTopBar	             topBar;
	private InGameScreen	         mainScreen;
	private TurnListWidget	         turnList;
	
	/**
	 * @param player
	 * @param battleMonsters
	 */
	public RBHUD(RandomBattle instance, SpoutPlayer player, ArrayList<Monster> battleMonsters)
	{
		plugin = instance;
		this.player = player;
		
		// Setting all the default bars to be not visible.
		invisibleWidgets = new ArrayList<GenericWidget>(8);
		mainScreen = (InGameScreen) player.getMainScreen();
		if (mainScreen.getArmorBar().isVisible()) {
			mainScreen.getArmorBar().setVisible(false);
			invisibleWidgets.add(mainScreen.getArmorBar());
		}
		if (mainScreen.getHealthBar().isVisible()) {
			mainScreen.getHealthBar().setVisible(false);
			invisibleWidgets.add(mainScreen.getHealthBar());
		}
		if (mainScreen.getBubbleBar().isVisible()) {
			mainScreen.getBubbleBar().setVisible(false);
			invisibleWidgets.add(mainScreen.getBubbleBar());
		}
		if (mainScreen.getExpBar().isVisible()) {
			mainScreen.getExpBar().setVisible(false);
			invisibleWidgets.add(mainScreen.getExpBar());
		}
		if (mainScreen.getHungerBar().isVisible()) {
			mainScreen.getHungerBar().setVisible(false);
			invisibleWidgets.add(mainScreen.getHungerBar());
		}
		mainScreen.closePopup();
		
		// Making the screen objects
		screen = new RBPopupScreen(plugin, player);
		RBScreenListener screenListener = new RBScreenListener(plugin, screen, invisibleWidgets);
		plugin.getServer().getPluginManager().registerEvents(screenListener, plugin);
		buttons = new CommandButtonContainer(plugin, screen, player, battleMonsters);
		// Turn List on the right
		ArrayList<LivingEntity> tempAllEntities = new ArrayList<LivingEntity>();
		for (Monster monster : battleMonsters)
			tempAllEntities.add(monster);
		tempAllEntities.add(player);
		turnList = new TurnListWidget(plugin, tempAllEntities);
		topBar = new RBTopBar(plugin);
		
		// Setting the layout
		screen.attachWidgets(plugin, buttons, topBar, turnList);
		buttons.setAnchor(WidgetAnchor.BOTTOM_LEFT).shiftYPos(-buttons.getHeight() - 20).shiftXPos(20);
		topBar.setAnchor(WidgetAnchor.TOP_CENTER).shiftXPos(-topBar.getWidth() / 2).shiftYPos(20);
		turnList.setAnchor(WidgetAnchor.CENTER_RIGHT).shiftXPos(-turnList.getWidth() - 20)
		        .shiftYPos(-turnList.getHeight() / 2);
		mainScreen.attachPopupScreen(screen);
	}
}
