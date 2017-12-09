package champions;

public class DetailListHashKey extends Object {
        protected DetailList source;
        protected String property;
        
        public DetailListHashKey(DetailList source, String property) {
            this.source = source;
            this.property = property;
        }
        
        public DetailList getSource() {
            return source;
        }
        
        public String getProperty() {
            return property;
        }
        
        public int hashCode() {
            int s = 0;
            int p = 0;
            
            if ( source != null ) s = source.hashCode();
            if ( property != null ) p = source.hashCode();

            return s/2+p/2;
        }
        
        public boolean equals(Object o) {
            if ( o instanceof DetailListHashKey == false ) return false;
            DetailListHashKey hk = (DetailListHashKey)o;
            return source == hk.getSource() && property.equals(hk.getProperty());
        }
        
    }
