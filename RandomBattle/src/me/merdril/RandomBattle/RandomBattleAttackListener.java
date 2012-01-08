/**
 * 
 */

package me.merdril.RandomBattle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.ComplexEntityPart;
import org.bukkit.entity.ComplexLivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;

/**
 * @author mark
 * 
 */
public class RandomBattleAttackListener extends EntityListener
{
	RandomBattle	                                         plugin;
	protected BattleSetter	                                 begin;
	protected int	                                         randomChance	       = 70;
	private Random	                                         generator	           = new Random();
	protected static volatile HashMap<UUID, ArrayList<UUID>>	alreadyEncountered	=
	                                                                                       new HashMap<UUID, ArrayList<UUID>>();
	
	/**
	 * 
	 */
	public RandomBattleAttackListener(RandomBattle instance)
	{
		plugin = instance;
	}
	
	// Check crazy
	public void onEntityDamage(EntityDamageEvent event)
	{
		EntityDamageByEntityEvent attackEvent = null;
		if (event instanceof EntityDamageByEntityEvent)
			attackEvent = (EntityDamageByEntityEvent) event;
		if (attackEvent != null)
		{
			SpoutPlayer player = null;
			Monster monster = null;
			ComplexLivingEntity dragon = null;
			ComplexEntityPart dragonPart = null;
			UUID entityID = null;
			UUID playerID = null;
			if (attackEvent.getDamager() instanceof Player)
			{
				player = (SpoutPlayer) attackEvent.getDamager();
				playerID = player.getUniqueId();
				if (attackEvent.getEntity() instanceof Monster)
				{
					monster = (Monster) attackEvent.getEntity();
					entityID = monster.getUniqueId();
				}
				else if (attackEvent.getEntity() instanceof ComplexLivingEntity)
				{
					dragon = (ComplexLivingEntity) attackEvent.getEntity();
					entityID = dragon.getUniqueId();
				}
				else if (attackEvent.getEntity() instanceof ComplexEntityPart)
				{
					dragonPart = (ComplexEntityPart) attackEvent.getEntity();
					entityID = dragonPart.getUniqueId();
				}
			}
			else if (attackEvent.getDamager() instanceof Arrow)
			{
				Arrow arrow = (Arrow) attackEvent.getDamager();
				if (arrow.getShooter() instanceof Player)
				{
					player = (SpoutPlayer) arrow.getShooter();
					playerID = player.getUniqueId();
					if (attackEvent.getEntity() instanceof Monster)
					{
						monster = (Monster) attackEvent.getEntity();
						entityID = monster.getUniqueId();
					}
					else if (attackEvent.getEntity() instanceof ComplexLivingEntity)
					{
						dragon = (ComplexLivingEntity) attackEvent.getEntity();
						entityID = dragon.getUniqueId();
					}
					else if (attackEvent.getEntity() instanceof ComplexEntityPart)
					{
						dragonPart = (ComplexEntityPart) attackEvent.getEntity();
						entityID = dragonPart.getUniqueId();
					}
				}
			}
			else if (attackEvent.getEntity() instanceof Player)
			{
				player = (SpoutPlayer) attackEvent.getEntity();
				playerID = player.getUniqueId();
				if (attackEvent.getDamager() instanceof Monster)
				{
					monster = (Monster) attackEvent.getDamager();
					entityID = monster.getUniqueId();
				}
				else if (attackEvent.getDamager() instanceof ComplexLivingEntity)
				{
					dragon = (ComplexLivingEntity) attackEvent.getDamager();
					entityID = dragon.getUniqueId();
				}
				else if (attackEvent.getDamager() instanceof ComplexEntityPart)
				{
					dragonPart = (ComplexEntityPart) attackEvent.getDamager();
					entityID = dragonPart.getUniqueId();
				}
			}
			int randomNumber = generator.nextInt(99);
			if (player != null && (monster != null || dragon != null || dragonPart != null))
			{
				ArrayList<UUID> checkList = alreadyEncountered.get(playerID);
				if (checkList == null)
				{
					ArrayList<UUID> temp = new ArrayList<UUID>();
					temp.add(entityID);
					// Synchronized to prevent corruption from the cleaner listener.
					synchronized (alreadyEncountered)
					{
						alreadyEncountered.put(playerID, temp);
					}
					player.sendMessage("[RandomBattle] " + SpoutManager.getEntityFromId(entityID)
					        + " has been added.");
				}
				else
				{
					if (checkList.contains(entityID))
						return;
					else if (!checkList.contains(entityID))
					{
						checkList.add(entityID);
						synchronized (alreadyEncountered)
						{
							alreadyEncountered.put(playerID, checkList);
						}
						player.sendMessage("[RandomBattle] "
						        + SpoutManager.getEntityFromId(entityID) + " has been added.");
					}
				}
				if (randomChance > randomNumber)
				{
					if (monster != null)
						begin = new BattleSetter(plugin, player, monster, 124, 15, 10);
					else if (dragon != null)
						begin = new BattleSetter(plugin, player, dragon);
					else if (dragonPart != null)
						begin = new BattleSetter(plugin, player, dragonPart.getParent());
					player.sendMessage("[RandomBattle] " + attackEvent.getDamage()
					        + " random number: " + randomNumber);
				}
			}
		}
	}
}
