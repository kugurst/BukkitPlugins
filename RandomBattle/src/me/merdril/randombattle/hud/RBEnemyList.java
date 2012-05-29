/**
 * 
 */

package me.merdril.randombattle.hud;

import java.util.ArrayList;

import me.merdril.randombattle.RBUtilities;
import me.merdril.randombattle.RandomBattle;

import org.bukkit.Material;
import org.bukkit.entity.Monster;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.getspout.spoutapi.gui.GenericListWidget;
import org.getspout.spoutapi.gui.ListWidgetItem;
import org.getspout.spoutapi.player.SpoutPlayer;

/**
 * @author mark
 */
public class RBEnemyList extends GenericListWidget
{
	RandomBattle	           plugin;
	private ArrayList<Monster>	monsters;
	private SpoutPlayer	       player;
	
	/**
	 * 
	 */
	public RBEnemyList(RandomBattle instance, SpoutPlayer player, ArrayList<Monster> monsters)
	{
		super();
		this.plugin = instance;
		this.player = player;
		PlayerInventory inventory = player.getInventory();
		boolean inventoryContains = false;
		outer: for (ItemStack item : inventory.getContents()) {
			if (item == null)
				break;
			for (int type : RBUtilities.weaponIDs) {
				if (item.getTypeId() == type) {
					inventoryContains = true;
					player.sendMessage("[RandomBattle] Item: " + Material.getMaterial(type) + " found.");
					break outer;
				}
			}
		}
		this.monsters = monsters;
		for (Monster monster : monsters) {
			String monsterName = monster.toString();
			if (monsterName.contains("Craft"))
				monsterName = monsterName.substring(5);
			this.addItem(new ListWidgetItem(monsterName, "Health: " + Integer.toString(monster.getHealth())));
		}
	}
	
	@Override
	public void onSelected(int item, boolean doubleClick)
	{
		Monster selMonster = monsters.get(item);
		selMonster.damage(2);
		this.getItem(item).setTitle(selMonster.toString().substring(5));
		this.getItem(item).setText("Health: " + Integer.toString(selMonster.getHealth()));
		this.setDirty(true);
	}
}
