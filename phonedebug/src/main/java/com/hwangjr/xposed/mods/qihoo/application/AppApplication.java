package com.hwangjr.xposed.mods.qihoo.application;

import android.content.Context;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class AppApplication implements IXposedHookLoadPackage {

    private String TAG = AppApplication.class.getName();
    private String PACKAGE_NAME = "com.hwangjr.app";
    private String QIHOO_STUBAPP = "com.qihoo.util.StubApp579459766";
    private String QIHOO_STUBAPP_FUNC_INSTANCE = "getNewAppInstance";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if (loadPackageParam.packageName.equals(PACKAGE_NAME)) {
            XposedBridge.log("start hook...");
            XposedHelpers.findAndHookMethod(QIHOO_STUBAPP, loadPackageParam.classLoader,
                    QIHOO_STUBAPP_FUNC_INSTANCE, Context.class, new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            Context context = (Context) param.args[0];
                            ClassLoader classLoader = context.getClassLoader();
                            hookApp(classLoader);
                        }
                    });
        }
    }

    private void hookApp(ClassLoader classLoader) {
        XposedHelpers.findAndHookMethod("com.hwangjr.app.App", classLoader, "getParams", String.class, String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
            }
        });
    }
}
