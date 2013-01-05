package com.onextent.augie;

import com.onextent.util.store.CodeStore;

public class AugieStore {
    
    private static CodeStore _store;
   
    public static void setCodeStore(CodeStore s) {
        
        _store = s;
    }
    
    public static CodeStore getCodeStore() {
        
        return _store;
    }
}
