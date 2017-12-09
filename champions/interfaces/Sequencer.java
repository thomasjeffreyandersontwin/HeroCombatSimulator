/*
 * Sequencer.java
 *
 * Created on September 12, 2001, 6:56 AM
 */

package champions.interfaces;

import champions.*;
/**
 *
 * @author  twalker
 * @version 
 */
public interface Sequencer {
    public BattleSequence getBattleSequence(BattleSequence battleSequence, int lookAhead, boolean advanceSegment);
    public BattleSequence getBattleSequence(BattleSequence battleSequence, int lookAhead);
    public BattleSequence getBattleSequence(int lookAhead);
    public BattleSequence getBattleSequence(int lookAhead, boolean advanceSegment);
    public BattleSequence getBattleEligible(BattleSequence battleSequence);
    public BattleSequence getBattleEligible();
}

