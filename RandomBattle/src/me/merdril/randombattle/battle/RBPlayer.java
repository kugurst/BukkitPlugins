/**
 * 
 */

package me.merdril.randombattle.battle;

import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import me.merdril.randombattle.RandomBattle;

import org.bukkit.entity.LivingEntity;
import org.getspout.spoutapi.player.SpoutPlayer;

/**
 * @author mark
 */
public class RBPlayer implements RBLivingEntity
{
	RandomBattle	    plugin;
	private SpoutPlayer	player;
	
	/**
	 * <code>public RBPlayer(RandomBattle instance, SpoutPlayer player, Integer[] stats)</code> <br/>
	 * <br/>
	 * Defines a Random Battle player which is a SpoutPlayer with stats and corresponding methods
	 * for manipulating them.
	 * @param instance
	 *            - The RandomBattle instance
	 * @param player
	 *            - The player to represent
	 * @param stats
	 *            - The stats of the player
	 * @throws Exception
	 */
	public RBPlayer(RandomBattle instance, SpoutPlayer player, int[] stat) throws ArrayIndexOutOfBoundsException
	{
		plugin = instance;
		this.player = player;
	}
	
	@Override
	public int getStat(Stat stat)
	{
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public int setStat(Stat stat, int amount)
	{
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public List<Effect> getEffects()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<Effect> setEffect(int action, Effect... effect)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<RBSkill> getSkills()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<RBSkill> setSkills(int action, RBSkill... skills)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<RBMagic> getMagicks()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<RBMagic> setMagicks(int action, RBMagic... magicks)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<RBElem> getWeak()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<RBElem> setWeak(RBElem... elems)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public RBMove getMove(ReentrantLock lock)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public LivingEntity getEntity()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Map<Stat, Integer> getStats()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Map<Stat, Integer> getOriginalStats()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
}
