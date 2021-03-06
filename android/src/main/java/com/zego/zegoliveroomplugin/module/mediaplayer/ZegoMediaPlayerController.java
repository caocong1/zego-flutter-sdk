package com.zego.zegoliveroomplugin.module.mediaplayer;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;

import com.zego.zegoavkit2.IZegoMediaPlayerVideoPlayWithIndexCallback;
import com.zego.zegoavkit2.IZegoMediaPlayerWithIndexCallback;
import com.zego.zegoavkit2.ZegoMediaPlayer;
import com.zego.zegoliveroomplugin.utils.ZegoFileHelper;

import java.io.IOException;
import java.util.HashMap;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

public class ZegoMediaPlayerController implements IZegoMediaPlayerWithIndexCallback {

    private static String KEY_START = "START";
    private static String KEY_STOP = "STOP";
    private static String KEY_PAUSE = "PAUSE";
    private static String KEY_RESUME = "RESUME";
    private static String KEY_LOAD = "LOAD";
    private static String KEY_SEEK_TO = "SEEK_TO";

    private static ZegoMediaPlayerController sInstance;
    private ZegoMediaPlayer mPlayer;

    private HashMap<String, Result> mResultMap;
    //private HashMap<Integer, Result> mStartResultList;
    //private HashMap<Integer, Result> mLoadResultList;

    private IZegoMediaPlayerControllerCallback mCallback = null;

    public ZegoMediaPlayerController() {
        mResultMap = new HashMap<>();
    }

    public void init() {
        mPlayer = new ZegoMediaPlayer();
        mPlayer.init(ZegoMediaPlayer.PlayerTypeAux, 0);
        mPlayer.setEventWithIndexCallback(this);
    }

    public void uninit() {
        mPlayer.setEventWithIndexCallback(null);
        mPlayer.uninit();
        mPlayer = null;
    }

    public static ZegoMediaPlayerController getInstance() {
        if (sInstance == null) {
            Class var0 = ZegoMediaPlayerController.class;
            synchronized(ZegoMediaPlayerController.class) {
                if (sInstance == null) {
                    sInstance = new ZegoMediaPlayerController();
                }
            }
        }
        return sInstance;
    }

    public void start(String path, boolean isRepeat, boolean isAsset, Registrar registrar, Result result) {

        mResultMap.put(KEY_START, result);

        if(isAsset) {

            if(path!= null && !path.isEmpty()) {
                String loopUpKey = registrar.lookupKeyForAsset(path);
                playEffectAsync(registrar.context(), loopUpKey, isRepeat);
            } else {
                mPlayer.start("", isRepeat);
            }

        } else {
            mPlayer.start(path, isRepeat);
        }
    }

    public void stop(Result result) {

        mResultMap.put(KEY_STOP, result);
        mPlayer.stop();
    }

    public void pause(Result result) {

        mResultMap.put(KEY_PAUSE, result);
        mPlayer.pause();
    }

    public void resume(Result result) {

        mResultMap.put(KEY_RESUME, result);
        mPlayer.resume();
    }

    public void preload(String path, boolean isAsset, Registrar registrar, Result result) {

        mResultMap.put(KEY_LOAD, result);
        if(isAsset) {
            String loopUpKey = registrar.lookupKeyForAsset(path);
            preloadEffectAsync(registrar.context(), loopUpKey);
        } else {
            mPlayer.load(path);
        }

        //mLoadResultList.put(Integer.valueOf(soundID), result);
    }

    public void setVolume(int volume, Result result) {

        mPlayer.setVolume(volume);
        result.success(null);
    }

    public void setMediaPlayerEventCallback(IZegoMediaPlayerControllerCallback callback) {
        mCallback = callback;
    }

    public void seekTo(long timestamp, Result result) {

        mResultMap.put(KEY_SEEK_TO, result);
        mPlayer.seekTo(timestamp);
        //result.success(null);
    }

    public void getTotalDuration(Result result) {

        result.success(mPlayer.getDuration());
    }

    public void getCurrentDuration(Result result) {

        result.success(mPlayer.getCurrentDuration());
    }

    public void muteLocal(boolean mute, Result result) {
        mPlayer.muteLocal(mute);
        result.success(null);
    }

    public void setPlayerType(int type, Result result) {
        mPlayer.setPlayerType(type);
        result.success(null);
    }

    public void enableRepeatMode(boolean enable, Result result) {
        mPlayer.enableRepeatMode(enable);
        result.success(null);
    }

    public void setProcessInterval(long timestamp, Result result) {
        mPlayer.setProcessInterval(timestamp);
        result.success(null);
    }

    private void playEffectAsync(final Context context, final String fileName, final boolean isRepeat) {
        new Thread() {
            @Override
            public void run() {
                final String path = ZegoFileHelper.copyAssetsFile2Phone(context, fileName);
                // copy完成
                mPlayer.start(path, isRepeat);

            }
        }.start();

    }

    private void preloadEffectAsync(final Context context, final String fileName) {
        new Thread(){
            @Override
            public void run() {
                final String path = ZegoFileHelper.copyAssetsFile2Phone(context, fileName);
                // copy完成
                mPlayer.load(path);
            }
        }.start();
    }

    @Override
    public void onPlayEnd(int index) {
        if(mCallback != null) {
            mCallback.onPlayEnd();
        }
    }

    @Override
    public void onPlayStart(int index) {
        Result result = mResultMap.get(KEY_START);
        if(result != null) {
            result.success(null);
            mResultMap.remove(KEY_START);
        }
    }

    @Override
    public void onPlayPause(int index) {
        Result result = mResultMap.get(KEY_PAUSE);
        if(result != null) {
            result.success(null);
            mResultMap.remove(KEY_PAUSE);
        }
    }

    @Override
    public void onPlayStop(int index) {
        Result result = mResultMap.get(KEY_STOP);
        if(result != null) {
            result.success(null);
            mResultMap.remove(KEY_STOP);
        }
    }

    @Override
    public void onPlayResume(int var1) {
        Result result = mResultMap.get(KEY_RESUME);
        if(result != null) {
            result.success(null);
            mResultMap.remove(KEY_RESUME);
        }
    }

    @Override
    public void onPlayError(int error, int index) {
        if(mCallback != null) {
            mCallback.onPlayError(error);
        }
    }

    @Override
    public void onVideoBegin(int index) {

    }

    @Override
    public void onAudioBegin(int index) {

    }

    @Override
    public void onBufferBegin(int var1) {
        if(mCallback != null) {
            mCallback.onBufferBegin();
        }
    }

    @Override
    public void onBufferEnd(int var1) {
        if(mCallback != null) {
            mCallback.onBufferEnd();
        }
    }

    @Override
    public void onSeekComplete(int error, long timestamp, int index) {
        Result result = mResultMap.get(KEY_SEEK_TO);
        if(result != null) {
            HashMap<String, Object> retMap = new HashMap<>();
            retMap.put("errorCode", error);
            retMap.put("timestamp", timestamp);
            result.success(retMap);

            mResultMap.remove(KEY_SEEK_TO);
        }
    }

    @Override
    public void onSnapshot(Bitmap var1, int var2) {

    }

    @Override
    public void onLoadComplete(int var1) {
        Result result = mResultMap.get(KEY_LOAD);
        if(result != null) {
            result.success(null);
            mResultMap.remove(KEY_LOAD);
        }
    }

    @Override
    public void onProcessInterval(long timestamp, int index) {
        if(mCallback != null) {
            mCallback.onProcessInterval(timestamp);
        }
    }

    private boolean numberToBoolValue(Boolean number) {

        return number != null ? number.booleanValue() : false;
    }

    private int numberToIntValue(Number number) {

        return number != null ? number.intValue() : 0;
    }

    private long numberToLongValue(Number number) {

        return number != null ? number.longValue() : 0;
    }
}
