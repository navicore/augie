/**
 * copyright Ed Sweeney, 2012, 2013 all rights reserved
 */
package com.onextent.android.ui;

import com.onextent.android.codeable.Code;
import com.onextent.android.codeable.CodeableName;

public interface CallbackActivity {
    
    void registerCallback(CodeableName dest, Callback cb);
    Code sendCode(CodeableName dest, Code code);
    
    interface Callback {
        
        Code handleCode(CodeableName dest, Code code);
    }
}
