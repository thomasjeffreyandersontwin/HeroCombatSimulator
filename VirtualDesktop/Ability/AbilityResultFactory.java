package VirtualDesktop.Ability;

import VirtualDesktop.Attack.AreaEffect.AreaEffectAttackResults;
import VirtualDesktop.Attack.Autofire.AutofireAttackResults;
import VirtualDesktop.Attack.Sweep.SweepAttackResults;
import VirtualDesktop.Roster.SingleAttackResults;
import champions.battleMessage.AFShotMessageGroup;
import champions.battleMessage.AreaEffectAttackMessageGroup;
import champions.battleMessage.SingleAttackMessageGroup;
import champions.battleMessage.SweepMessageGroup;

public class AbilityResultFactory {
	 public static SingleAttackResults GetAbilityForType(String type) {
		 if(type.equals( "AttackAutofireTargetsResult")){
			 return new AutofireAttackResults();
		 }
		 if(type.equals( "AttackSingleTargetResult")){
			 return new SingleAttackResults();
		 }
		 if(type.equals( "AttackSweepTargetsResult")){
			 return new SweepAttackResults();
		 }
		 if(type.equals( "AttackAreaEffectTargetResults")){
			 return new AreaEffectAttackResults();
		 }
		
		 return new SingleAttackResults();
			 
		 }
	 }