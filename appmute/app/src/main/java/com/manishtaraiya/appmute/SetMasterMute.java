package com.manishtaraiya.appmute;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SetMasterMute {
    @SuppressLint({"UseValueOf"})
    public void setMasterMute(boolean flag, Context context) {
        new MySharePreference().set_data_boolean(context, Utils.isInMuteModeKey, flag);
        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        try {
            assert mAudioManager != null;
            Method obj =mAudioManager.getClass().getMethod("setMasterMute", Boolean.TYPE, Integer.TYPE);
            Boolean obj1 = flag;
            Integer obj2 = 0;
            obj.invoke(mAudioManager, obj1, obj2);
        } catch (NoSuchMethodException nosuchmethodexception) {
            nosuchmethodexception.printStackTrace();
        } catch (InvocationTargetException invocationtargetexception) {
            invocationtargetexception.printStackTrace();
        } catch (IllegalAccessException illegalaccessexception) {
            illegalaccessexception.printStackTrace();
        }
    }
}
