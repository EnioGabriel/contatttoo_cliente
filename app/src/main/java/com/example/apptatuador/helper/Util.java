package com.example.apptatuador.helper;

import android.app.Activity;

public class Util {
    private Activity activity;
    private int[] ids;

    public Util( Activity activity, int... ids ){
        this.activity = activity;
        this.ids = ids;
    }

    public void camposDesabilitados( boolean isToLock ){
        for( int id : ids ){
            setCamposDesabilitados( id, isToLock );
        }
    }

    private void setCamposDesabilitados( int campoId, boolean isToLock ){
        activity.findViewById( campoId ).setEnabled( !isToLock );
    }
}