/**
 * 
 */

package me.merdril.randombattle.hud;

import java.util.ArrayList;

import me.merdril.randombattle.RandomBattle;

import org.bukkit.entity.Monster;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.gui.InGameHUD;
import org.getspout.spoutapi.gui.WidgetAnchor;
import org.getspout.spoutapi.player.SpoutPlayer;

/**
 * @author Merdril
 */
public class FightButton extends GenericButton
{
	RandomBattle	           plugin;
	private GenericPopup	   screen;
	private SpoutPlayer	       player;
	private InGameHUD	       mainScreen;
	private ArrayList<Monster>	monsters;
	private RBMonsterList	   monsterList;
	
	/**
	 * @param screen
	 * @param monsters
	 */
	public FightButton(RandomBattle instance, GenericPopup screen, SpoutPlayer player, ArrayList<Monster> monsters)
	{
		super();
		this.plugin = instance;
		this.screen = screen;
		this.player = player;
		this.mainScreen = player.getMainScreen();
		this.monsters = monsters;
		this.setText("Fight");
	}
	
	@Override
	public void onButtonClick(ButtonClickEvent event)
	{
		// Create the monster selection screen based on the list of field monsters
		monsterList = new RBMonsterList(plugin, player, monsters);
		// Set its dimensions
		monsterList.setHeight(mainScreen.getHeight() / 5).setWidth(4 * mainScreen.getWidth() / 7);
		screen.attachWidget(plugin, monsterList); // Attach the widget
		monsterList.setAnchor(WidgetAnchor.BOTTOM_CENTER).shiftYPos(-monsterList.getHeight() - 20)
		        .shiftXPos(-monsterList.getWidth() / 2); // Position properly
	}
}
