/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package champions.interfaces;

import champions.Ability;
import champions.BattleEvent;
import champions.exception.BattleEventException;

/**
 *
 * @author twalker
 */
public interface CombatLevelProvider {

    /** Returns if this is configured as a combat level provider.
     *
     * Some effect, such as effectSkillLevel, can be configured in multiple
     * way, some of which provide combat levels and some which do not.
     * isCombatLevelProvider() should be called to determine if this CombatLevelProvider
     * really is providing combat levels.
     * @return
     */
    public boolean isCombatLevelProvider();

    /** Returns the Name of the combat level provider.
     *
     * @param ability
     * @return
     */
    public String getCombatLevelName();

    public boolean applies(Ability ability);

    /** Returns the number of levels available.
     *
     * @return
     */
    public int getLevels();

    public int getConfiguredOCVModifier();
    public int getConfiguredDCVModifier();
    public int getConfiguredDCModifier();

    public boolean isDCVEditable();
    public boolean isDCEditable();

    /** Returns true if the configuration is legal.
     * 
     * @param ocv
     * @param dcv
     * @param dc
     * @return
     */
    public boolean isConfigurationLegal(int ocv, int dcv, int dc);

    /** Sets the configured modifiers.
     *
     * @param ocv
     * @param dcv
     * @param dc
     */
    public void setConfiguredModifiers(int ocv, int dcv, int dc);

    /** Activate or deactivate combat levels.
     *
     * @param activate if activate is true, the combat levels will be applied.
     * If false, they will be removed from the source.
     */
    public void activateCombatLevels(BattleEvent be,boolean activate) throws BattleEventException;


}
