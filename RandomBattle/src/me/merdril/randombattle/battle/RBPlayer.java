/**
 * 
 */

package me.merdril.randombattle.battle;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import me.merdril.randombattle.RandomBattle;

import org.bukkit.entity.LivingEntity;
import org.getspout.spoutapi.player.SpoutPlayer;

/**
 * @author Merdril
 */
public class RBPlayer implements RBLivingEntity
{
	private RandomBattle	       plugin;
	private SpoutPlayer	           player;
	private EnumMap<Stat, Integer>	stats;
	private List<Effect>	       effects;
	private List<RBSkill>	       skills;
	private List<RBMagic>	       magicks;
	private List<RBElem>	       weakness;
	
	/**
	 * <p>
	 * Constructs an {@link RBPlayer} out of the specified {@link Stat}s and {@link SpoutPlayer}.
	 * The {@link EnumMap}&lt{@link Stat}, {@link Integer}&gt must include every {@link Stat} except
	 * for EXP, CHP, and CMP. However, if they are included, they will be used in constructing this
	 * {@link RBLivingEntity}.
	 * </p>
	 * <p>
	 * This constructor assumes that this player is not afflicted with any {@link Effect}s, knows no
	 * {@link RBSkill}s or {@link RBMagic}ks, and has no {@link RBElem} weaknesses.
	 * </p>
	 * @param instance
	 *            The {@link RandomBattle} plugin this player is tied to. Used for server calls.
	 * @param player
	 *            The {@link SpoutPlayer} this class wraps. There should be at most one instance of
	 *            {@link RBPlayer} per {@link SpoutPlayer}.
	 * @param statMap
	 *            The {@link Stat}s to assign to this {@link SpoutPlayer}.
	 */
	public RBPlayer(RandomBattle instance, SpoutPlayer player, EnumMap<Stat, Integer> statMap)
	{
		plugin = instance;
		this.player = player;
		stats = statMap;
	}
	
	/**
	 * <p>
	 * Constructs an {@link RBPlayer} out of the specified {@link Stat}s and {@link SpoutPlayer}.
	 * The {@link EnumMap}&lt{@link Stat}, {@link Integer}&gt must include every {@link Stat} except
	 * for EXP, CHP, and CMP. However, if they are included, they will be used in constructing this
	 * {@link RBLivingEntity}.
	 * </p>
	 * <p>
	 * This constructor attributes this {@link RBPlayer} with the specified status {@link Effect}s,
	 * {@link Stat}s, {@link RBElem} weaknesses, and {@link RBMagic}ks. A null or empty list can be
	 * used in place of an {@link List} to indicate this player knows/has none.
	 * </p>
	 * @param instance
	 *            The {@link RandomBattle} plugin this player is tied to. Used for server calls.
	 * @param player
	 *            The {@link SpoutPlayer} this class wraps. There should be at most one instance of
	 *            {@link RBPlayer} per {@link SpoutPlayer}.
	 * @param statMap
	 *            The {@link Stat}s to assign to this {@link SpoutPlayer}.
	 * @param effects
	 *            The {@link List}&lt{@link Effect}&gt to assign as this player's list of active
	 *            effects. That is to say, any {@link Effect} included in this {@link List} is
	 *            considered active.
	 * @param skills
	 *            The {@link List}&lt{@link RBSkill}&gt to assign as this player's known skills.
	 *            That is to say, any {@link RBSkill} included in this {@link List} is available to
	 *            the player to use (unless some effect prohibits it).
	 * @param magicks
	 *            The {@link List}&lt{@link RBMagic}&gt to assign as this player's known magicks.
	 *            That is to say, any {@link RBMagic} included in this {@link List} is available to
	 *            the player to use (unless some effect prohibits it).
	 * @param weakness
	 *            The {@link List}&lt{@link RBMagic}&gt to assign as this player's elemental
	 *            weaknesses. That is to say, any {@link RBElem} included in this {@link List} is
	 *            considered a weakness of this player.
	 */
	public RBPlayer(RandomBattle instance, SpoutPlayer player, EnumMap<Stat, Integer> statMap, List<Effect> effects,
	        List<RBSkill> skills, List<RBMagic> magicks, List<RBElem> weakness)
	{
		plugin = instance;
		this.player = player;
		stats = statMap;
		this.effects = effects;
		this.skills = skills;
		this.magicks = magicks;
		this.weakness = weakness;
	}
	
	@Override
	public Integer getStat(Stat stat)
	{
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public Integer setStat(Stat stat, int amount)
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
	
	@Override
	public int compareTo(RBLivingEntity other)
	{
		// TODO Auto-generated method stub
		return 0;
	}
}
