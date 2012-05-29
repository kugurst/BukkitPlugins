
package me.merdril.randombattle.battle;

import java.util.HashMap;

public enum RBSkill {
	HEAD_SMASHER, CALL_FRIENDS, SWORD_SLASH, PILLAR, EXPLODE, AIM, TRIPLE_SHOT, STICKY_WEB, POISON_ARROW, GROAN, MAUL,
	SMASH, FORTIFY;
	public static final HashMap<String, RBSkill>	skillMap	= new HashMap<String, RBSkill>();
	static {
		for (RBSkill skill : RBSkill.values())
			skillMap.put(skill.toString().toLowerCase(), skill);
	}
}
