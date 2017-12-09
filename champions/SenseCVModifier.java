package champions;

import java.util.EnumSet;

public enum SenseCVModifier  {
        ZERO_OCV("0 OCV"), HALF_OCV("1/2 OCV"), FULL_OCV("FULL OCV"),
        ZERO_DCV("0 DCV"), MINUS_ONE_DCV("-1 DCV"), HALF_DCV("1/2 DCV"), FULL_DCV("FULL DCV");
        
        private final String description;
        SenseCVModifier(String description) {
            this.description = description;
        }
        
        public String getDescription() { return description; }
        
        public String toString() {
            return description;
        }
        
        public static EnumSet<SenseCVModifier> getOCVValues() {
            return EnumSet.range(ZERO_OCV,FULL_OCV);
        }
        
        public static EnumSet<SenseCVModifier> getDCVValues() {
            return EnumSet.range(ZERO_DCV,FULL_DCV);
        }
        
        /** Returns the OCV modifier against the indicated target.
         *
         *  @param ranged If true, indicates the attack is a range attack.  Otherwise, attack is HTH.
         */
        public static SenseCVModifier getOCVModifier(Sense sense, Target target, boolean ranged) {
            if ( sense != null && sense.isFunctioning() ) {
                if ( sense.isTargettingSense() && sense.isTargetableWithSense(target) ) {
                    return SenseCVModifier.FULL_OCV;
                } else if ( sense.isRangedSense() && sense.isTargetableWithSense(target) ) {
                    return SenseCVModifier.HALF_OCV;
                }
            }
            
            // There is no targeting sense or ranged sense (w/ perception roll)
            // available, so default to the worst case.
            if ( ranged ) {
                return SenseCVModifier.ZERO_OCV;
            } else {
                return SenseCVModifier.HALF_OCV;
            }
        }
        
        /** Returns the DCV modifier against the indicated target.
         *
         *  @param ranged If true, indicates the attack is a range attack.  Otherwise, attack is HTH.
         */
        public static SenseCVModifier getDCVModifier(Sense sense, Target target, boolean ranged) {
            if ( sense != null && sense.isFunctioning() ) {
                if ( sense.isTargettingSense() && sense.isTargetableWithSense(target) ) {
                    return SenseCVModifier.FULL_DCV;
                } else if ( sense.isRangedSense() && sense.isTargetableWithSense(target) ) {
                    if ( ranged ) {
                        return SenseCVModifier.FULL_DCV;
                    } else {
                        return SenseCVModifier.MINUS_ONE_DCV;
                    }
                }
            }
            
            // There is no targeting sense or ranged sense (w/ perception roll)
            // available, so default to the worst case.
            return SenseCVModifier.HALF_DCV;
        }
    }; 
