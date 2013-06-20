/**
 * copyright Ed Sweeney, 2012, 2013 all rights reserved
 */
package com.onextent.augie;

import com.onextent.android.store.CodeStore;

public class AugieStore {
    
    private static CodeStore _store;
   
    public static void setCodeStore(CodeStore s) {
        
        _store = s;
    }
    
    public static CodeStore getCodeStore() {
        
        return _store;
    }
}
