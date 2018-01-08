package VirtualDesktop.Ability;

import champions.ActivationInfo;
import champions.Battle;
import champions.BattleEvent;
import champions.Target;

public abstract class AbstractBattleClassAdapter{
	
	public int targetIndex=0;
	public BattleEvent battleEvent;
	
	public Target getTarget()
	{
		return battleEvent.getActivationInfo().getTarget(targetIndex);
	}
	
	public int getTargetReferenceNumber()
	{
		return battleEvent.getActivationInfo().getTargetReferenceNumber(targetIndex);
	}
	
	
	public void setTarget(Target target) {
		battleEvent.getActivationInfo().setTarget(targetIndex, target);
		
	}
	
	public ActivationInfo getActivationInfo()
	{
		return battleEvent.getActivationInfo();
	}
}
