package com.hwangjr.xposed.mods.phonedebug;

import android.content.pm.ApplicationInfo;
import android.os.Process;

import java.lang.reflect.Method;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class PhoneDebug implements IXposedHookLoadPackage {
    public boolean debugApps = true;
    public static final int DEBUG_ENABLE_DEBUGGER = 0x1;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if (BuildConfig.DEBUG) {
            XposedBridge.log("-- handle package: " + loadPackageParam.packageName + " process: " + loadPackageParam.processName);
        }

        if (loadPackageParam.appInfo == null ||
                (loadPackageParam.appInfo.flags & (ApplicationInfo.FLAG_SYSTEM | ApplicationInfo.FLAG_UPDATED_SYSTEM_APP)) != 0) {
            XposedBridge.log("-- appInfo: " + loadPackageParam.appInfo);
            return;
        }

        try {
            Method start = Process.class.getMethod(
                    "start", String.class, String.class, Integer.TYPE, Integer.TYPE, int[].class,
                    Integer.TYPE, Integer.TYPE, Integer.TYPE, String.class, String[].class);
            XposedBridge.log("-- start hook, appInfo: " + loadPackageParam.appInfo);
            XposedBridge.hookMethod(start, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                    if (debugApps) {
                        int id = 5;
                        int flags = (Integer) methodHookParam.args[id];
                        if ((flags & DEBUG_ENABLE_DEBUGGER) == 0) {
                            flags |= DEBUG_ENABLE_DEBUGGER;
                        }
                        methodHookParam.args[id] = flags;
                        if (BuildConfig.DEBUG) {
                            XposedBridge.log("set debuggable flags to: " + flags);
                        }
                    }
                }
            });
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
/*
        XposedBridge.hookAllMethods(Process.class, "start", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if (debugApps) {
                    int id = 5;
                    int flags = (Integer) param.args[id];
                    if ((flags & DEBUG_ENABLE_DEBUGGER) == 0) {
                        flags |= DEBUG_ENABLE_DEBUGGER;
                    }
                    param.args[id] = flags;
                    if (BuildConfig.DEBUG) {
                        XposedBridge.log("set debuggable flags to: " + flags);
                    }
                }
            }
        });*/
    }
}
