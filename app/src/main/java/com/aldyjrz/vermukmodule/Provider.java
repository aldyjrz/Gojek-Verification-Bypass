package com.aldyjrz.vermukmodule;

import com.crossbowffs.remotepreferences.RemotePreferenceProvider;

public class Provider extends RemotePreferenceProvider {
    public Provider() {
        super("com.aldyjrz.vermukmodule", new String[] {"TOI"});
    }
}