package VirtualDesktop;

import champions.powers.*;

import champions.Ability;





public class MobEffectCombat extends effectCombatLevel
{
	static final long serialVersionUID = 5295848683348707403L;
	public boolean applies(Ability ability) {
		return true;
	}
	
	 public MobEffectCombat(String name, Ability ability) {
	 	super(name, ability);
	 }

	public boolean isConfigurationLegal(int ocv, int dcv, int dc) {
        return true;
      }
}