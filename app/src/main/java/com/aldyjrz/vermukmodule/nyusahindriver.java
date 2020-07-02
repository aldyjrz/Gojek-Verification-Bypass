package com.aldyjrz.vermukmodule;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Base64;
import android.widget.TextView;
import android.widget.Toast;

import com.crossbowffs.remotepreferences.RemotePreferences;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class nyusahindriver
        implements IXposedHookLoadPackage, IXposedHookZygoteInit
{

    private static XSharedPreferences prefs;
    String[] act, cmd1, key1, lib1, pkg1;
    private HashSet<String> commandSet;
    private Activity currentActivity;
    private Set<String> keywordSet;
    public XC_MethodHook opHook, finishOpHook;
    private HashSet appSet, activity, libnameSet;
    private String vermktoi;
    private Context systemContext;


    private void MockLocation(XC_LoadPackage.LoadPackageParam lpparam)
    {
        Class clazz1 = XposedHelpers.findClass("android.provider.Settings.Secure", lpparam.classLoader);


        findAndHookMethod("android.provider.Settings.Secure", lpparam.classLoader, "getString", ContentResolver.class, String.class, new XC_MethodHook()
        {
            protected void beforeHookedMethod(MethodHookParam paramAnonymousMethodHookParam)
                    throws Throwable
            {
                if (paramAnonymousMethodHookParam.args[1].equals("mock_location")) {
                    paramAnonymousMethodHookParam.setResult("0");
                }
            }
        });
        findAndHookMethod("android.provider.Settings.Secure", lpparam.classLoader, "getInt", ContentResolver.class, String.class, new XC_MethodHook()
        {
            protected void beforeHookedMethod(XC_MethodHook.MethodHookParam paramAnonymousMethodHookParam)
                    throws Throwable
            {
                if (paramAnonymousMethodHookParam.args[1].equals("mock_location")) {
                    paramAnonymousMethodHookParam.setResult("0");
                }
            }
        });
        findAndHookMethod("android.provider.Settings.Secure", lpparam.classLoader, "getInt", ContentResolver.class, String.class, Integer.TYPE, new XC_MethodHook()
        {
            protected void beforeHookedMethod(MethodHookParam paramAnonymousMethodHookParam)
                    throws Throwable
            {
                if (paramAnonymousMethodHookParam.args[1].equals("mock_location")) {
                    paramAnonymousMethodHookParam.setResult("0");
                }
            }
        });
        findAndHookMethod("android.provider.Settings.Secure", lpparam.classLoader, "getFloat", ContentResolver.class, String.class, new XC_MethodHook()
        {
            protected void beforeHookedMethod(MethodHookParam paramAnonymousMethodHookParam)
                    throws Throwable
            {
                if (paramAnonymousMethodHookParam.args[1].equals("mock_location")) {
                    paramAnonymousMethodHookParam.setResult("0.0f");
                }
            }
        });
        findAndHookMethod("android.provider.Settings.Secure", lpparam.classLoader, "getFloat", ContentResolver.class, String.class, Float.TYPE, new XC_MethodHook()
        {
            protected void beforeHookedMethod(MethodHookParam paramAnonymousMethodHookParam)
                    throws Throwable
            {
                if (paramAnonymousMethodHookParam.args[1].equals("mock_location")) {
                    paramAnonymousMethodHookParam.setResult("0.0f");
                }
            }
        });
        findAndHookMethod("android.provider.Settings.Secure", lpparam.classLoader, "getLong", ContentResolver.class, String.class, new XC_MethodHook()
        {
            protected void beforeHookedMethod(MethodHookParam paramAnonymousMethodHookParam)
                    throws Throwable
            {
                if (paramAnonymousMethodHookParam.args[1].equals("mock_location")) {
                    paramAnonymousMethodHookParam.setResult("0.0f");
                }
            }
        });
        findAndHookMethod("android.provider.Settings.Secure", lpparam.classLoader, "getLong", ContentResolver.class, String.class, Long.TYPE, new XC_MethodHook()
        {
            protected void beforeHookedMethod(MethodHookParam paramAnonymousMethodHookParam)
                    throws Throwable
            {
                if (paramAnonymousMethodHookParam.args[1].equals("mock_location")) {
                    paramAnonymousMethodHookParam.setResult("0L");
                }
            }
        });
        findAndHookMethod("android.location.Location", lpparam.classLoader, "getExtras", new XC_MethodHook()
        {
            protected void afterHookedMethod(MethodHookParam paramAnonymousMethodHookParam)
            {
                Bundle localBundle = (Bundle)paramAnonymousMethodHookParam.getResult();
                if ((localBundle != null) && (localBundle.getBoolean("mockLocation"))) {
                    localBundle.putBoolean("mockLocation", false);
                }
                paramAnonymousMethodHookParam.setResult(Boolean.FALSE);
            }
        });

        findAndHookMethod("android.location.Location", lpparam.classLoader, "isFromMockProvider", new XC_MethodHook()
        {
            protected void beforeHookedMethod(MethodHookParam paramAnonymousMethodHookParam)
            {
                paramAnonymousMethodHookParam.setResult(Boolean.FALSE);
            }
        });


    }

    private Boolean anyWordEndingWithKeyword(String paramString, String[] paramArrayOfString)
    {
        int j = paramArrayOfString.length;
        int i = 0;
        while (i < j)
        {
            if (paramArrayOfString[i].endsWith(paramString)) {
                return Boolean.TRUE;
            }
            i += 1;
        }
        return Boolean.FALSE;
    }


    private String[] buildGrepArraySingle(String[] original, boolean addSH) {
        StringBuilder builder = new StringBuilder();
        ArrayList<String> originalList = new ArrayList<String>();
        if (addSH) {
            originalList.add("sh");
            originalList.add("-c");
        }
        for (String temp : original) {
            builder.append(" ");
            builder.append(temp);
        }
        //originalList.addAll(Arrays.asList(original));
        // ***TODO: Switch to using -e with alternation***
        for (String temp : keywordSet) {
            builder.append(" | grep -v ");
            builder.append(temp);
        }
        //originalList.addAll(Common.DEFAULT_GREP_ENTRIES);
        originalList.add(builder.toString());
        return originalList.toArray(new String[0]);
    }

    private void bypassFa(XC_LoadPackage.LoadPackageParam lpparam)
    {
        XposedHelpers.findAndHookMethod("android.app.ApplicationPackageManager", lpparam.classLoader, "getInstalledPackages", Integer.TYPE, new XC_MethodHook() {
            @Override

            protected void afterHookedMethod(XC_MethodHook.MethodHookParam paramAnonymousMethodHookParam)
            {
                Iterator localIterator = ((List)paramAnonymousMethodHookParam.getResult()).iterator();
                while (localIterator.hasNext()) {
                    if (((PackageInfo)localIterator.next()).packageName != null) {
                        localIterator.remove();
                    }
                }
                paramAnonymousMethodHookParam.setResult(null);
            }
        });
        findAndHookMethod("android.os.SystemProperties", lpparam.classLoader, "get", String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                if (param.args[0].equals("ro.build.selinux")) {
                    param.setResult("0");

                }

            }

            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                if (param.args[0].equals("ro.build.selinux")) {
                    param.setResult("0");


                }
            }

        });

    }


    private void initFile(XC_LoadPackage.LoadPackageParam lpparam)
    {
        XposedBridge.hookMethod(XposedHelpers.findConstructorExact(File.class, String.class), new XC_MethodHook(10000)
        {
            protected void beforeHookedMethod(XC_MethodHook.MethodHookParam paramAnonymousMethodHookParam)
                    throws Throwable
            {
                if (((String)paramAnonymousMethodHookParam.args[0]).endsWith("su"))
                {
                    paramAnonymousMethodHookParam.args[0] = "";
                    return;
                }
                if (((String)paramAnonymousMethodHookParam.args[0]).endsWith("busybox"))
                {
                    paramAnonymousMethodHookParam.args[0] = "";
                    return;
                }
                if (stringContainsFromSet((String)paramAnonymousMethodHookParam.args[0], keywordSet)) {
                    paramAnonymousMethodHookParam.args[0] = "";
                }
            }
        });
    }

    private void initRuntime(final XC_LoadPackage.LoadPackageParam lpparam) {
        /**
         * Hooks exec() within java.lang.Runtime.
         * is the only version that needs to be hooked, since all of the others are "convenience" variations.
         * takes the form: exec(String[] cmdarray, String[] envp, File dir).
         * There are a lot of different ways that exec can be used to check for a rooted device. See the comments within section for more details.
         */
        findAndHookMethod("java.lang.Runtime", lpparam.classLoader, "exec", String[].class, String[].class, File.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {


                String[] execArray = (String[]) param.args[0]; // Grab the tokenized array of commands
                if ((execArray != null) && (execArray.length >= 1)) { // Do some checking so we don't break anything
                    String firstParam = execArray[0]; // firstParam is going to be the main command/program being run

                    String tempString = "Exec Command:";
                    for (String temp : execArray) {
                        tempString = tempString + " " + temp;
                    }


                    if (stringEndsWithFromSet(firstParam, commandSet)) { // Check if the firstParam is one of the keywords we want to filter


                        // A bunch of logic follows since the solution depends on which command is being called
                        // TODO: ***Clean up logic***
                        if (firstParam.equals("su") || firstParam.endsWith("/su")) { // If its su or ends with su (/bin/su, /xbin/su, etc)
                            param.setThrowable(new IOException()); // Throw an exception to imply the command was not found
                        } else if (commandSet.contains("pm") && (firstParam.equals("pm") || firstParam.endsWith("/pm"))) {
                            // Trying to run the pm (package manager) using exec. Now let's deal with the subcases
                            if (execArray.length >= 3 && execArray[1].equalsIgnoreCase("list") && execArray[2].equalsIgnoreCase("packages")) {
                                // Trying to list out all of the packages, so we will filter out anything that matches the keywords
                                //param.args[0] = new String[] {"pm", "list", "packages", "-v", "grep", "-v", "\"su\""};
                                param.args[0] = buildGrepArraySingle(execArray, true);
                            } else if (execArray.length >= 3 && (execArray[1].equalsIgnoreCase("dump") || execArray[1].equalsIgnoreCase("path"))) {
                                // Trying to either dump package info or list the path to the APK (both will tell the app that the package exists)
                                // If it matches anything in the keywordSet, stop it from working by using a fake package name
                                if (stringContainsFromSet(execArray[2], keywordSet)) {
                                    param.args[0] = new String[]{execArray[0], execArray[1], ""};
                                }
                            }
                        } else if (commandSet.contains("ps") && (firstParam.equals("ps") || firstParam.endsWith("/ps"))) { // is a process list command
                            // Trying to run the ps command to see running processes (e.g. looking for things running as su or daemonsu). Filter out.
                            param.args[0] = buildGrepArraySingle(execArray, true);
                        } else if (commandSet.contains("which") && (firstParam.equals("which") || firstParam.endsWith("/which"))) {
                            // Busybox "which" command. Thrown an excepton
                            param.setThrowable(new IOException());
                        } else if (commandSet.contains("busybox") && anyWordEndingWithKeyword("busybox", execArray)) {
                            param.setThrowable(new IOException());
                        } else if (commandSet.contains("sh") && (firstParam.equals("sh") || firstParam.endsWith("/sh"))) {
                            param.setThrowable(new IOException());
                        } else {
                            param.setThrowable(new IOException());
                        }

                        if (param.getThrowable() == null) { // Print out the new command if debugging is on
                            tempString = "New Exec BSH Team:";
                            for (String temp : (String[]) param.args[0]) {
                                tempString = tempString + " " + temp;
                            }
                        }
                    }


                }
            }
        });
        findAndHookMethod("java.lang.Runtime", lpparam.classLoader, "loadLibrary", String.class, ClassLoader.class, new XC_MethodHook() {
            protected void beforeHookedMethod(MethodHookParam paramAnonymousMethodHookParam)
                    throws Throwable {
                String str = (String) paramAnonymousMethodHookParam.args[0];
                if ((str != null) && (stringContainsFromSet(str, libnameSet))) {
                    paramAnonymousMethodHookParam.setResult(null);
                }
            }
        });
    }
    private void safetynet(XC_LoadPackage.LoadPackageParam lpparam){

        if ("android".equals(lpparam.packageName)) {
            XposedHelpers.findAndHookMethod(File.class, "exists", new XC_MethodHook() {
                /* access modifiers changed from: protected */
                public void beforeHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                    File file = (File) methodHookParam.thisObject;
                    if (new File("/sys/fs/selinux/enforce").equals(file)) {
                        methodHookParam.setResult(true);
                    } else if (new File("/system/bin/su").equals(file) || new File("/system/xbin/su").equals(file)) {
                        methodHookParam.setResult(false);
                    }
                }
            });
        }
        XposedHelpers.findAndHookMethod(JSONObject.class, "getBoolean", String.class, new XC_MethodHook() {
            /* access modifiers changed from: protected */
            public void beforeHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                String str = (String) methodHookParam.args[0];
                if ("ctsProfileMatch".equals(str) || "basicIntegrity".equals(str) || "isValidSignature".equals(str)) {
                    methodHookParam.setResult(true);
                }
            }
        });

    }
    private void moremock(XC_LoadPackage.LoadPackageParam lpparam)
    {
        if (Build.VERSION.SDK_INT >= 23)
        {
            findAndHookMethod("android.app.AppOpsManager", lpparam.classLoader, "checkOp", String.class, Integer.TYPE, String.class, opHook);
            findAndHookMethod("android.app.AppOpsManager", lpparam.classLoader, "checkOp", Integer.TYPE, Integer.TYPE, String.class, opHook);
            findAndHookMethod("android.app.AppOpsManager", lpparam.classLoader, "checkOpNoThrow", String.class, Integer.TYPE, String.class, opHook);
            findAndHookMethod("android.app.AppOpsManager", lpparam.classLoader, "checkOpNoThrow", Integer.TYPE, Integer.TYPE, String.class, opHook);
            findAndHookMethod("android.app.AppOpsManager", lpparam.classLoader, "noteOp", String.class, Integer.TYPE, String.class, opHook);
            findAndHookMethod("android.app.AppOpsManager", lpparam.classLoader, "noteOp", Integer.TYPE, Integer.TYPE, String.class, opHook);
            findAndHookMethod("android.app.AppOpsManager", lpparam.classLoader, "noteOpNoThrow", String.class, Integer.TYPE, String.class, opHook);
            findAndHookMethod("android.app.AppOpsManager", lpparam.classLoader, "noteOpNoThrow", Integer.TYPE, Integer.TYPE, String.class, opHook);
            findAndHookMethod("android.app.AppOpsManager", lpparam.classLoader, "noteProxyOp", String.class, String.class, opHook);
            findAndHookMethod("android.app.AppOpsManager", lpparam.classLoader, "noteProxyOp", Integer.TYPE, String.class, opHook);
            findAndHookMethod("android.app.AppOpsManager", lpparam.classLoader, "noteProxyOpNoThrow", String.class, String.class, opHook);
            findAndHookMethod("android.app.AppOpsManager", lpparam.classLoader, "noteProxyOpNoThrow", Integer.TYPE, String.class, opHook);
            findAndHookMethod("android.app.AppOpsManager", lpparam.classLoader, "startOp", String.class, Integer.TYPE, String.class, opHook);
            findAndHookMethod("android.app.AppOpsManager", lpparam.classLoader, "startOp", Integer.TYPE, Integer.TYPE, String.class, opHook);
            findAndHookMethod("android.app.AppOpsManager", lpparam.classLoader, "startOpNoThrow", String.class, Integer.TYPE, String.class, opHook);
            findAndHookMethod("android.app.AppOpsManager", lpparam.classLoader, "startOpNoThrow", Integer.TYPE, Integer.TYPE, String.class, opHook);

        }
    }



    private void readPrefs() {
        prefs = new XSharedPreferences("com.aldyjrz.vermukmodule", "TOI");
        prefs.makeWorldReadable();
        prefs.reload();
        List<String> packages = new LinkedList<String>();
        packages.add("com.aldyjrz.vermukmodule");
        packages.add("com.gojek.driver.bike");
    }


    public static String ready(String message) {
        byte[] data = Base64.decode(message, Base64.DEFAULT);
        try {
            return new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static final class kon extends XC_MethodHook {
        public void afterHookedMethod(XC_MethodHook.MethodHookParam param1MethodHookParam) {
            List<String> list = Arrays.asList("/sbin/su", "/system/bin/su", "/system/xbin/su", "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su", "/system/bin/failsafe/su", "/data/local/su", "/system/app/Superuser.apk");
            if (param1MethodHookParam != null) {
                String str = XposedHelpers.getObjectField(param1MethodHookParam.thisObject, "path").toString();
                if (list.contains(str)) {
                    param1MethodHookParam.setResult(Boolean.FALSE);
                }
                if (str.equals("/etc/security/otacerts.zip")) {
                    param1MethodHookParam.setResult(Boolean.TRUE);
                }
            }
        }
    }


    public void mocka(XC_LoadPackage.LoadPackageParam lpparam) {

        Class findClass = XposedHelpers.findClass("com.gojek.driver.common.MyLocation", lpparam.classLoader);
        XposedHelpers.findAndHookConstructor(findClass, Double.TYPE, Double.TYPE, Double.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Long.TYPE, String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if (param != null) {
                    param.args[7] = "false";
                }
            }
        });
        XposedHelpers.findAndHookConstructor(findClass, Location.class, Long.TYPE, String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam methodHookParam) throws Throwable {

                if (methodHookParam != null) {
                    methodHookParam.args[2] = "false";
                }
            }
        });
        XposedHelpers.findAndHookMethod(findClass, "getMockLocationStatus", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam methodHookParam) throws Throwable {

                if (methodHookParam != null) {
                    methodHookParam.args[2] = "false";
                }
            }

        });
        Class findClass2 = XposedHelpers.findClass("android.provider.Settings.Global", lpparam.classLoader);

        XposedHelpers.findAndHookMethod(findClass2, "getInt", ContentResolver.class, String.class, Integer.TYPE, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                if (methodHookParam != null) {
                    if (methodHookParam.args[1].equals("install_non_market_apps")) {
                        methodHookParam.setResult(0);
                    }
                }

            }
        });




        if (Build.VERSION.SDK_INT >= 23)
        {
            findAndHookMethod("android.app.AppOpsManager", lpparam.classLoader, "checkOp", String.class, Integer.TYPE, String.class, opHook);
            findAndHookMethod("android.app.AppOpsManager", lpparam.classLoader, "checkOp", Integer.TYPE, Integer.TYPE, String.class, opHook);
            findAndHookMethod("android.app.AppOpsManager", lpparam.classLoader, "checkOpNoThrow", String.class, Integer.TYPE, String.class, opHook);
            findAndHookMethod("android.app.AppOpsManager", lpparam.classLoader, "checkOpNoThrow", Integer.TYPE, Integer.TYPE, String.class, opHook);
            findAndHookMethod("android.app.AppOpsManager", lpparam.classLoader, "noteOp", String.class, Integer.TYPE, String.class, opHook);
            findAndHookMethod("android.app.AppOpsManager", lpparam.classLoader, "noteOp", Integer.TYPE, Integer.TYPE, String.class, opHook);
            findAndHookMethod("android.app.AppOpsManager", lpparam.classLoader, "noteOpNoThrow", String.class, Integer.TYPE, String.class, opHook);
            findAndHookMethod("android.app.AppOpsManager", lpparam.classLoader, "noteOpNoThrow", Integer.TYPE, Integer.TYPE, String.class, opHook);
            findAndHookMethod("android.app.AppOpsManager", lpparam.classLoader, "noteProxyOp", String.class, String.class, opHook);
            findAndHookMethod("android.app.AppOpsManager", lpparam.classLoader, "noteProxyOp", Integer.TYPE, String.class, opHook);
            findAndHookMethod("android.app.AppOpsManager", lpparam.classLoader, "noteProxyOpNoThrow", String.class, String.class, opHook);
            findAndHookMethod("android.app.AppOpsManager", lpparam.classLoader, "noteProxyOpNoThrow", Integer.TYPE, String.class, opHook);
            findAndHookMethod("android.app.AppOpsManager", lpparam.classLoader, "startOp", String.class, Integer.TYPE, String.class, opHook);
            findAndHookMethod("android.app.AppOpsManager", lpparam.classLoader, "startOp", Integer.TYPE, Integer.TYPE, String.class, opHook);
            findAndHookMethod("android.app.AppOpsManager", lpparam.classLoader, "startOpNoThrow", String.class, Integer.TYPE, String.class, opHook);
            findAndHookMethod("android.app.AppOpsManager", lpparam.classLoader, "startOpNoThrow", Integer.TYPE, Integer.TYPE, String.class, opHook);

        }
    }

    void macet(XC_LoadPackage.LoadPackageParam lpparam){
        Class<?> eek = XposedHelpers.findClass("com.gojek.driver.home.HomeFragment", lpparam.classLoader);
        Class<?> map = XposedHelpers.findClass("com.gojek.driver.views.map.AsphaltMap$ɩ", lpparam.classLoader);

        XposedHelpers.findAndHookMethod(eek, "ı", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                prefs = new XSharedPreferences(BuildConfig.APPLICATION_ID, "TOI");

                new File("/data/data/com.aldyjrz.vermukmodule/shared_prefs/TOI.xml").setReadable(true, false);
                new File("/data/data/com.aldyjrz.vermukmodule/shared_prefs/TOI.xml").setExecutable(true, false);

                prefs.makeWorldReadable();
                prefs.reload();
                boolean bool = prefs.getBoolean("sw_trf", true);
                if (param != null) {
                    Object obj =   XposedHelpers.getObjectField(param.thisObject, "ǃ");

                    XposedHelpers.callMethod(obj, "setTrafficEnabled", bool);


                }

            }
        });
    /*    XposedHelpers.findAndHookMethod("com.gojek.driver.views.map.AsphaltMap", lpparam.classLoader , "ˎ", "com.google.android.gms.maps.GoogleMap", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                prefs = new XSharedPreferences("com.aldyjrz.vermukmodule", "TOI");

                new File("/data/data/com.aldyjrz.vermukmodule/shared_prefs/TOI.xml").setReadable(true, false);
                new File("/data/data/com.aldyjrz.vermukmodule/shared_prefs/TOI.xml").setExecutable(true, false);

                prefs.makeWorldReadable();
                prefs.reload();
                boolean bool = prefs.getBoolean("sw_trf", true);

                if (param != null) {

                        XposedHelpers.callMethod(param.args[0], "setTrafficEnabled", bool );

                }

            }
        });
*/



    }
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam loadPackageParam)
            throws Throwable {
        this.systemContext = (Context) XposedHelpers.callMethod(XposedHelpers.callStaticMethod(XposedHelpers.findClass("android.app.ActivityThread", loadPackageParam.classLoader), "currentActivityThread"), "getSystemContext", new Object[0]);
        prefs = new XSharedPreferences(BuildConfig.APPLICATION_ID, "TOI");
        moremock(loadPackageParam);
        safetynet(loadPackageParam);
        if (loadPackageParam.packageName.equals("com.gojek.driver.bike")) {
            act = new String[]{"com.aldyjrz.vermukmodule", "com.gojek.driver.car", "com.gojek.goboxdriver", "com.grabtaxi.driver2"};
            pkg1 = new String[]{"com.aldyjrz.vermukmodule", "id.co.cimbniaga.mobile.android", "com.deuxvelva.satpolapp", "com.telkom.mwallet"};
            key1 = new String[]{"magisksu", "supersu", "magisk", "superuser", "Superuser", "noshufou", "xposed", "rootcloak", "chainfire", "titanium", "Titanium", "substrate", "greenify", "daemonsu", "root", "busybox", "titanium", ".tmpsu", "su", "rootcloak2"};
            cmd1 = new String[]{"su", "which", "busybox", "pm", "am", "sh", "ps", "magisk"};
            lib1 = new String[]{"tool-checker"};
            appSet = new HashSet(Arrays.asList(this.pkg1));
            keywordSet = new HashSet(Arrays.asList(this.key1));
            commandSet = new HashSet<>(Arrays.asList(this.cmd1));
            libnameSet = new HashSet<>(Arrays.asList(this.lib1));
            activity = new HashSet<>(Arrays.asList(this.act));

            prefs = new XSharedPreferences(BuildConfig.APPLICATION_ID, "TOI");
            prefs.makeWorldReadable();
            prefs.reload();
            initFile(loadPackageParam);
            initRuntime(loadPackageParam);
            hideXposed(loadPackageParam);
            bypassFa(loadPackageParam);
            mocka(loadPackageParam);
            MockLocation(loadPackageParam);

            Class<?> asek = XposedHelpers.findClass("com.gojek.driver.home.MainActivity", loadPackageParam.classLoader);

            XposedHelpers.findAndHookMethod(asek,"ɂ", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    if (param != null) {
                        param.setResult((Object) null);
                    }
                }
            });

            Class<?> findClass8 = XposedHelpers.findClass("dark.EX$If", loadPackageParam.classLoader);
            XposedHelpers.findAndHookMethod(findClass8, "ı", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    if (param != null) {
                        param.setResult(Boolean.FALSE);
                    }
                }
            });
            Class<?> findClass9 = XposedHelpers.findClass("dark.EX$ı", loadPackageParam.classLoader);

            XposedHelpers.findAndHookMethod(findClass9, "Ι", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                    super.beforeHookedMethod(methodHookParam);
                    if (methodHookParam != null) {
                        methodHookParam.setResult(Boolean.FALSE);
                    }
                }
            });
            Class<?> findClass = XposedHelpers.findClass("dark.ayd", loadPackageParam.classLoader);

            XposedHelpers.findAndHookMethod(findClass,"Ι", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    if (param != null) {
                        param.setResult(0L);

                    } else {
                        throw null;
                    }

                }
            });





            prefs.makeWorldReadable();
            prefs.reload();
            SharedPreferences pref = new RemotePreferences(systemContext, "com.aldyjrz.vermukmodule", "TOI");
            vermktoi = pref.getString("srcImage", "");
            XposedBridge.log("LADANG GANDUM DIPENUHI COKLAT - TOI");

            final Class<?> x = XposedHelpers.findClass("id.idi.ekyc.dto.CheckUserBiometricRequestDTO", loadPackageParam.classLoader);


            XposedHelpers.findAndHookMethod(x, "validate",new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    if (param != null) {
                        param.setResult(true);
                    }
                }
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    if (param != null) {
                        param.setResult(true);
                    }
                }
            });
            Class<?> ZXC = XposedHelpers.findClass("id.idi.ekyc.services.VerifyUserBiometricService$if", loadPackageParam.classLoader);

            XposedHelpers.findAndHookMethod(ZXC, "run",new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    if (param != null) {
                        RemotePreferences pref = new RemotePreferences(systemContext, "com.aldyjrz.vermukmodule", "TOI");
                        vermktoi = pref.getString("srcImage", "");
                        XposedHelpers.setObjectField(param.thisObject, "ι", vermktoi );
                        XposedBridge.log("Face Data : "+vermktoi);

                    }
                }
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    if (param != null) {
                        RemotePreferences pref = new RemotePreferences(systemContext, "com.aldyjrz.vermukmodule", "TOI");
                        vermktoi = pref.getString("srcImage", "");
                        XposedHelpers.setObjectField(param.thisObject, "ι", vermktoi);
                        XposedBridge.log(String.valueOf(XposedHelpers.getObjectField(param.thisObject, "ι")));
                    }
                }
            });


            Class<?> cc = XposedHelpers.findClass("id.idi.ekyc.dto.VerifyUserBiometricRequestDTO", loadPackageParam.classLoader);
            XposedHelpers.findAndHookMethod(cc, "validate",new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    if (param != null) {
                        param.setResult(true);
                        XposedBridge.log("Validating face data request...");
                    }
                }
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    if (param != null) {
                        param.setResult(true);
                    }
                }
            });
/*
            XposedHelpers.findAndHookMethod(cc, "setFaceData", String.class ,new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    if (param != null) {
                        param.setResult("/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAUDBAQEAwUEBAQFBQUGBwwIBwcHBw8LCwkMEQ8SEhEPERETFhwXExQaFRERGCEYGh0dHx8fExciJCIeJBweHx7/2wBDAQUFBQcGBw4ICA4eFBEUHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh7/wAARCAHgAoADASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwD3gU4U1acK5WzVC0tJTuKhyL5RRSikpRRzC5RaUUlLT5hWFFOFNpaOYLDxS5pBRTuFh2acDTRSii4WHCnU0UoouFh4pwpgNPBpcwWHClFIOeKdRzBYKQ0tI1HMFhGZVBZuAKwfEGsxWUZeSTBP3V7mn+KNYh0u0aWRhkAlQa8U1DW9R17UpShb5jtBz0FHMOxseJfEGq6pcPbWxba/Cqn9am0fw7HpVv8AaryUSXkgzj+5VrToItHtlfaHmI6n1ouJ3m2ljl3OaakFiOW6tbQgyyMXYcKD/Oue8Sa1Le25s7UNHEerDksfQVtTWplZVmChDx05NUraGzS+NxHCHitidvoWqkxFC10PS9ItY9V11Jpjj9xZ5wZG/wBo+lamw3yC/wBTEMFuozHbRj5UXsPc1gazq6z3klzqDBgv3FPRRXJeIPF0lyfIgYhP9k9apCZ2mt+IJtQVIYp2ttPXjYnylq5+81loozp+j/6Ijn9/OPvkfWuOl1W6dRtbpxjPSlGoSfdY5z3rRAa0j2yu2wyzyd3dsk06KUK3I5rJW8YL29c006iQSCaYjqob5Y0x0JFQtdyGRnLcH1Nc2dSAxtOTTvt7Hkk0mM6WK828yElaspqMG3DDntiuSNyzDhqkhuWzgnmkB1cd6m/Plbua07S5klT5IcH/AHq4v7RIB8khzjvSQzX0kg8u5dfXjNAHodvdXKuFRJTt68irqXcjkSLIyMOorjNPg1NF3pcsxb8K07a21FG8x1cgcmmB6JperusY82Q9MELWwuq5gIkmwuOCRzXn+n3Nzjb5G1h1JNa8N08sLQyyLycc9aYHc6Nr2IQkiZ9wa6zS9USYBTIntmvEbia4gYLZSMTn7pPFTaX4gksrsLdSPFIOcE5BpAe/REPyGFPDV594f8YwTRfu0MwAycHmum03xHp16oA3o/8AdbjFSwN3dS7uKrxSq/KsCD0NS5NSFh+aWmA0bqm47Ds0Cm5FGaLgPozTc+9GaLgOJzRTc0ZouApNJRnFITmlzDEJppNKaaaXMFgzQTQTSZNHMFhDTTSk0wmncLCNTTzTiaaaXMFhpo6UpxnpTTRcLCUhpTTScUuYOUQmmmlNIaOYfKNNNNOPWkNHMLlG96aTSsab1ppisITUbU80wntVpiaM4U6mg0orORSHA0opopwrMseMUZxTaWgBwNFIKUDNMQ4U5cd6aBinCgLDgKUCkFOpgApQKSnCgBR0pRSUooCwop4plOBpBYeDS5plKDQFh+ajuJFSFmY9BzThknFY/jO7Fnos0gIU7cDmgZ5h8UtZa6zbxE5fIUZ7DvXPeH2WyjR5AN23IHvWPqeptqHi+VfM/dxoFB7Dpn+tXNJniutVuJ2yYLRdqL6mmB1GnPc6lqYDsSAu7YOwrWhiRbgyPgLHwCf1qv4Pj22MmozALJcArGPQVU8U3qWlr5Bfazd/50ITKfirUpZFkhtpMF/lXHGBXKeMPFVrpNjFpdi6yTKg3BT1PcmsLxX4iZMw28mO27vXCyBpLppCS2eWLdSa0RJpX2r3k25mdjuPTPSs9ncAySfePpULTAHBOc0sbecWUttIHy5PWtEQ2SpOEUAtk1Ibg+tZrTAZ3VG0zAcVaC5rNdYXBNVmklmJKDpVaHc7fOMVahk8rle1MB4RtoBbB9anR2xjPNJM/nxqY1CN0PpSQxOQMnOKGBZiZcgmQ5HarcDqpwWx7mqMULb+QMGrUVuB8ozzUjNBXjK5EuD0zV3TRGJQv2nIzzWTDYMxADHjtmr1vYsh+VtpPHWgD0jw28CIrSSh1HbGa73TZNHvbZrZjGGYd+CK8h0iGSKJEjkfcB69a6rSrbUAvmRqsjEZINMDrtT8MNGN9rKhDDOD3rEubSeBQ8ttgL1NJ9u8SWsRc2Ec0fYb+RWZfeML0qYbjTXBXg8k0AXZtKNwqT28w3dcF8Gqd3pNzNzO5BUcciq0Gt+H7vaLuwuraTtJE560zUL9IQHtLieVP+mnPFACWA1fSb5HtZN6/wAQ9q7RNZicJI0nly7QT9a88k1UkiWKTDjtmrsGrLOgLhQ+OcVIHo9h42m0+4VLwGSH+8DXoGla/p95EjxS53jIOa8IFyJIMAbkxjPWtHQNZks5FtogNh6VLQz3tJlcjacZp+73rzDRfFs3n+Wz7gOx7V2ul61BdIMtg+hqWBuAg96UH3qGN1bBXnNPPBxUMY/NGfemZ96M0rjsPBFLkVFmlzRcdiTNJnPemZozSuFhxpBjvSZ4pM+9ADjgmmnFBI7U1jQAhNNNKTTTQAGmmlppoAKaaU0lAwNNNKaQ0ANNNIpxppNACGmNSk0xjQAH1phNBNNJoEDGo2NDGoy1aRIZUWnDimA0uamQIfSgcUwGnA1maDs0opO2aUUAOpRSZzRQA8GnLTBTgaYDwacDTM0oNADqcD3pmaUGgB+aUUzNOBpgOpRTQaUGkwH5opM4GaMgdaQDl5auF+M1yU8KTyRn7jqD+JrrtSvEt4yS2MDJ+leUfF/xBZHw1JYG4BnunUhf7oDUwPKJIvsoExBV51J3V0vg7Tylg25iTIc9ax48XFzEzHMaLtFdjoJSK0bavTAFOwrnS20kNtY2iSOEROrGvJ/HviJr7XZ44GIgjG1ST1rqPG2pvFpEUaH5g+ce1eT67OJbuRgSBjgCqSE2ZF7O092xLZVffrTJCV6NmmbCrEnJye/akZG3EAcGtEQyNuhqJQ+7PpUjRMF4JLVYhgblmIxVokptGcgkE5NSLASQSpxWlH5CrnGWFK10qdFBPfirQFWO2kznnH0qdLc5wV+tRnUZmfaIwoz1oF2+7JPNMC2sIGVNTRW4XLlv1rJlvZOVB5Pek+0SMuWkbHoKTQG3JLFGBlwM9KcbzYAFQn6ViwsjEFsnvk1oWeox2u4PCsm4YGR0qbAX0uWxuDEVYt9R2kcbz05rKe/eVdogVF9cVFA9yc4XJ7UDudxp2siFlLW5Oe4rorPxC8GGiEgYdSDwK860zUrqI4k0+SUdCAa0zfOyEx2N1CD6niqGemw+L7aWAfakY7Tg4bBNadt4j8M3FuYzaqJmHSQA5rwt5rx5CrBsZyBU0M2pxSZQjOOjUAemalZwX8jmzdIccjFcxqlnq9spYSq49Aay7LxJc2oMc0akjk4apJfFaSgCG2ZSOz9KAIme5Wf96m0svPtS2d7LBcgqQ2Oqmkk8RwXEZjlsAjEfeBrNa5UtuBFJoDvdN1e2K7ZMxk+nQVfF3CpMsdwpA6diK89tbgNgsauvJKyb0JHToakdzr7W/nin86OYMD1BNdnoeuqwXLFJOnWvJILlsEKxJx0rU03UDBMrs5wBzzUtAe/aH4nNu6Q3xIjc4V+1drFLHLGrI6kHkEdDXifh66i1jSZbR5BGyjdGWPOa2PB/ia5026Ok3reYqng5zj8ahoZ6tnFGaqWV7FdJlGBz3FWM88VAx+aM03NBNIY7NGabn3oz70DHZozTc0hagB2aQmm5ooAUmmmg0lABSGlpDQA2jNFIaAAnPNNPrSk0wmgANMJpWNMJoARjTGNOJzUbGkAhNMY0rGo2NACMaiJ5pzGmE1pEiRWzinZNGKUAGlIQU7NAWnADNZlgKXOaXFKB60DAUo4oxSgUAApwoApwFAgFLQBSgUAFKM0YpQKBhmnUmKUCmAopc0gpfrSAMiorqYIoUfeY4FPkIVSSegzXL3Oro9/K3mfJbgg/71AHJ/EjxNdS62vhvSm/ekATSDt7V5j8VNJ/sXQY7ieR5LmWQYJNdlpcCf27cahMf39w7MGPbJ6VyHxy1Vb+Ky0xBunjYsW9B0FUhMo6BcltGinmh+dgCMVuWeqqjKFcc/w1zPh8SR20MDk7QBmp7+JwWljzkNnIq0ibkuu3z3NxJHIcbBgZrkLhBubJwxNal3PI0rGRvmIxWPesQ3zDBqkhFNo5N7BlGOxps0YjQEnJPal84h+GNRTvvYtn8KpIlkW7njtTDIxbg0oJdgqjJPepprZoIgWP3hmtESysWY8ZpDIAOTUMrcEZqANIzlSMDtVAWDLzwajaUgjmqkrvu+U9KaXYnrTAtCQux46VIsmBVVGz3pxOe9AFtZvepI5uc7qz8+pNPViOhxSsBqxzqpzuYj0rWsNS8pN6FRt7MM5rlhLjHzEHNWoZyO5pAd9Y+JpLVELQwup64Xmuu8PeLNFusi7it0I6bxXkdrehSN3Oexq6n2CcB5N0Z9qB3Pbb+9trmENZ6Rp1xFjkqcEVx+tnRopwLqykh3ddrYFczol7PYyLJZ3X7sckMa7JPGWjtAo1KG1mOMEFOTQO5yGqT6IFItY3ZycDLZrMk2yNhHCMOxNd0938NdS2yMHs5c4+TK1ImieDZWY2d7K7kcF2oC55/wCVcKMcNUTiRTgKVJ68dK6+58PTW85NtexvFnIyecU17SD7tw6qT7daAOXjuDEgJIP41r6TfwHHnZxnpS6jp0EGQIlKnkHPUVkKsEMpKyMCDyKTQzrRJa43eW4B7ioXuIYydobHrWUupGYBMbVXgU4zZXBBHpUtAdTouqkRhN5GMj8K3bO9IYSISX69eteeW135MmQCVPpXTaNeRyOiu2M96loo9o8B6nLPaPLHIW8oAPGeoNd/YXqXEIZWBOOa8C8Ga1Jo+ssGJeKU7Xx0Ir2PTmiIW5tXHlv1ANZtDOkDA0uapwThsAnBqypqWND80uabmjNIY7IpOTSUvPagQoBooyaTmgAxRiikJoGJSGlNNOaAA000E00mgAY1GTTmOajJoACaYTQTSUABNMalJxTSaQDWqN+lPY1G5zQBCWOSMU0nPSlfrTferiRIYCO9KDUQYZp2R2pSBIlBpQaiBpwNQWTBqUHmogacG4oAkzS5pgPFGaAJQacDUQNLn1pAS55pcmog1O3UwJM5pQcCow1G7FAEmfelzjvUQajePWgCcGgGow3vQGoAJ+Y3HX5DXkur3Elre3kMZyGYl8+9erTPhT9DXm/j/R5rvTpbyxBFzGckjuKAORm1BI4nncbTH2964TU5Ibq9N5d7yzt8qnsK2b7VjdacbK6tR9pj6sBjNY8Nql1ErB2LL1BrSKJZesoVSVdjdRxVnUYSsTsSBx0q/omjNJ8yOMKMnJrP8UTKhZVC/KMcVpYlnF6tIxkYKef5VQndnHzHJFF5K5lclxjPPvVeWUscKMZ6CnYm42SB9u/HvUCp8zdTmrt48kEMauuA44PrVNJAGwSCfarSAsxQbEjJ4B4/Go78syKN3TipI5RIoQtjHIzUU6s0LE9RVIVjHlJLjnoajZWDkgnFSy7vMAGPenzbWAIGOKAsUmG7rTMEfWpQo3U5lw2ByKdxWIlyBUqYZcH86cEJGRSiMjkj6UXCwwrg4BzQAetKUYHJPWndOKLhYYBk4IqeMkYAFM6MMDI71OgH4UBYkikYdOtTiZ+P1qAAcgdaCcDFIC4txtYHdgj3p8hS4A3KNx71nbhnOOfWlMrqcg5HSmBdGmyyFQsnuKfLHq1mxkDucdOe1Jp2oNs2SrgHjNaN0LhoN1vI0igZx1xQA7S/E0yIsVxkketbE+q2N5Cm658tj0FcXdQMHEhBDEUzb0x96iwzp5HkiYqJTJHnjLZqJ5I5FOcZrCW7mhJOdw9DWjY3sMo+fgnr7UxlkKY2yvfnFT29yXYq3Vf5UxSGHUFe3ND2ygFgWB68d6loZdiAJJU/KasWE7wSYYnHWqEEuSByAOuR3q9CyEjcMioaGjpNO1D5lYHd6Yr1v4Za2Hgkt5j8oAIye9eC/aPskqlWwvpXW+Ete8gPG7YDDKn3qGhn0tHtaNXAHPQirUMgYdeRXJ/D3X4tU05IHceeo5GetdMPkkJXj1FZsZc+lFRq2acDUlDgaXdTM4pC1IB+6jdURb3pN3vRcCUvTd1RFqazUXAmL+9JvqAt700t70gJy4phcVEWppai4Epaoy2TTC1NLU7gSFs00tgcVGWpC1FwHlqQsKjLUm6gB5OajYigtTGNCAjlOKZuB70shGKgByTVxIkReYPenCQHrXGp450DJzqMPp98VYTxloDnI1O3/GQVMioo6wODTg1clN468MQDDaxbZ9nzVdfiH4YZiBqsWPxqSrHbbx+VKr59a41PHPhxzldVhPtuqdPGnh9hkanEfX5xQFjrRJx0NOD5HQ1zSeKNIcBkv4SD/tCpE8S6YTj+0IfzH+NArHRBxil8wVgr4g05v+YjB/30P8akXW9Oc8X0P/fQoCxtCQdc04SKeprHXVrI/dvIz/wIU4alAfuXCH8aB2NbzAO9DSDGc1jnVYwcCaMY9TUb6uvQSw/nQBtiYDvSrKN1c6uqZYjzYfwNSjU9rcyxDPHWgR0Hmjpml80VgDUFwT58ftzQdQVVyZ0z6A07AbF9KDAcHoDWP9qWN3RwHjdcEVl3niCALJiUEr1xXFeKPFsVvaZikKs3Q+lUkS2QeNvD9vb3JmtGVS53DPpXLW80dkXM8YwCa5vxV4qu7hkEd67FevNYtl4iZ4PLunDEHrmtUiWz0n+3rOGF3jwpZT0Neaa/4hnubiSMHAbjnrUF/eySHzIpQMfyrFupBLKXYDNVYgTzpPPCvITWjbPieMsw296y4nUzoCPetCIgMyAZXGQfemBevb1W07Eg3FDhM9QKyUYC6RwCdw5qaTJ4IzUsUGTkLRcpIQgICOSM5BFSXUqmBcE1MOYyrgA9qp3IKnHWlzDsZ8mN+aCwIpLlGL5B4PWo1yOvaquKwpHORSsQME0u0EccE0mzPBNO4C+4qSOU4AZQcUigA8UAfNzSuFi7E9pIcSpxS3VpBtxA+QRxnrVQZ9KlSQgYxRzBYrMm08gnFSLlcZ71K4DrweRTlQkDI6UcwWDb3FJ05Ip5Qjp0oI7U7hYi2rknPXtSFMEAdKcyK3LEjHpQx4wKYrCCMtwM1NZXtzYzbhISDwQaiSUpwDjNDEMMnHNUTY2ZJYb+LzYuGxyKzXg2vv3HIqG2keCUOh47irvnpI+X+Ud6AKp2McbgD70wISxIOCO4qWaNGJIXGaYEKggA0DLlnI6RbmYAA4wT1rUttTgQASc+tc5944INIhZXBBPpQB1cmrWlxiOK3WIdc92pY5ih2lxg9K5yGZ8gP36VoW0hbhjx2NS0UbTESJzzjpTrO4McoQucdPpVK1uBkFjnFLcS5Ysvy55FZtDPQPBniWTR9TjkeVmUsO/SvonQNYg1K2jnjZWDqOlfIdjcFkCt98d69K+FPixrHUE028l2qxwuTxWckNH0UpAPBp28Cs+1uA0KuzcEZB7VL5y5x5i/nWbKLTOOtNLcZqv5y/8APRPzpomU9JE/Okx2LBakLe9QiQY+8v501pMdxSCxOWppYetQGXHcUwyc9R+dAWJy3qaTd71XZz1yOPek3E85GPrTsMnLU0tURfA6j86Tfn+IfnRYCQtTd1NJPqv500nB7UWAeTSFuKYzgDJZaYZF/vr+dFgJS1NLe9Q+and1FN81OvmL+dAFjdTS3aoDPGB/rFxTTKmM+YOKEIkkORiq+QpOTStKh5Dj8qileMqR5i1pElnx6kTMMF8MOcY60LFnq5JFfSEnwd0h85kYMe4AqJvgppjD5b2VM9toocQTPndYlSTBcc9Oaf8AZyzFfMAzxgV9Bv8ABHTnXab6QnHBKDiqTfAeN33LrhXH/TI/41Firnhi20iuCZeOgqSC0vp5jHbQzSY67ELc17afgS7HjXkwP+mJ/wAa3vDnw58Q+HrdoNK1XTXRjkmWI5/Oiwcx86T2WuxnYba5Udsxmka31VBmRZQfdTX1C2geORyLnRH9ipFRnQfG7nElv4ddffNFgufMSvfhSGdge3Bo8++24MjD8a+nD4Z8TMTv03w4/wCFRHwhrsmTLoXhth9BRYLnzWLrUFUf6U2fZ6WO/wBQDYN1L+Ehr6PfwHfyE58O+Hx9GFQS/Dq7eMg+HdBJ9pf/AK1FgueAR6lfqRm4nOfSQ1ftdR1KQgLdS4/3jXrV98LtUcDy9F0lB3CTYrMm+EHiBJDKgtYwf4RLkCnYLnALqmoxth7yU+nzGtC1ub6c5+3Tf99Hiuni+EmuzKwM1uTn+/zVqy8Bazprr9pt1dO+DVJCuZdhpt7PEHN/Pk9txq49rfWcbNLetgdt3NdINMlVtiwbGA4JrO1Xw7eygSG6WIEZbPaq5SWzjdU1GOCN2G8se9cP4i1N5ABvyg7VteMJIra4kt47vzcfLkHvXA6jJuwGLEA4Oe9NITZXlZp5hwc+vpReww26IyHezjJ9qjNx5attA5/SqU1y7KFLZArRIkV70gmMg+xqFpWMnTj1qrNKxfccVJHICAPWiwi2jAMGNXoHbPHOaz0AYYFamlw7yuOgqGykrk0S+tXtgEPy8nFMmgdcNGARmlDOi8de9ZtmqiNVGZMsOfaq1ynOKt+eI+Dzmq87hySO9LmKsUZF4wapyoVckEkVfkI6HrUEi1SZDRVV81KCCOtQzDYcimq2TkdKq4rFpB3qVSp7VDG4Ix3qVR8vvQ2Ow7B64pVXnHelCvtGCcDrQoYtkA5FTzD5R6rtbkdashwF2hMHFVgko52sfpU0ZkABKnP0p8w+UCwIxim7R1NJLvD42E+tJvYcYGfSmpEuIjpjkHiozgcAYp/7xuCvFNCnG4g1SkTyiEZGetR4APAqYLgZxkmmsMc96rmJaGhscGl3Z47Uw9eaZvxxTuKxaWVd2HP0qzuUjGQRWZuDcHmpopCD1qhFl4wRkVBtwcGrEUisQGqWWBWUMg5pgVFAYe9W7V9oCscn1qAIUbDDaf508kbc9KTGjRTrlRUhbcnI5HSqVpKc4DZ9RV0qHj3A845FQ0MjjvNsgIJBB5FbUcjShJYSfOUgoQec1y8nzEspxg1r6ReiOVQDntUNDTPYPB/xRube0FhqURaSMYVjXQJ8ULQfftTuPsK8NndyfMRiHBzx3rZ0KYasjRhP9ITgp61m4lo9cHxMsWOBbH64FOPxM01CAbcn8K8pNvLu4tn4PIApphAH7y3Zee9TylnrKfFHTRy1u2B0xipv+Fq6L0NvNk8fdFeRLBA33Y/w5py2SlgxhYkdMA0uUND1wfE/RyOLeUn0K0p+Jukn/l2YfhXlUOmO3zGFxn/ZNSppF1JISlrMxIwAIyafKF0emn4maUOtsT+AqIfFCw37f7PIX14rz6Pw1rDYZdLuiPUQtU8HhLxDK+YtIujn1hNPlFc7xviZp3A+yc/QUn/CzNPzj7Hj8BXID4f+KicnR58n2pR8PfFrYC6Lcj320+ULnUyfE+yX7toOPYVE/wAU4QpK2XGM1zf/AArnxWf+YNcfkKQ/DrxaT/yBbk+nAo5Qub8nxRJXKWgHtTB8UucfZOfrXPv8P/FcZydEu8j/AGagm8C+KEG59Fuxj0jJo5RXOmHxQJ62n60w/FCQFv8ARR7c1zKeB/E0gwulXWQcn92RU4+Hnil1DjR7g8elLlC5sv8AFO5Xpap+JqN/ihdMOIEXPvWWPhx4lZQ0mmSoAe4qR/h5rEY/eWrnP+zmnyibLbfErUN2dqHFNPxL1HtBC3pyarL8PtRcbWhmGT/zzqWL4b6m3CRyDHcrVKNiGz6FCjrxShRnoKmCgUoSqYkRhR/dFO2j0qQL7U7aKmwyILShB0OcVLilC0WAjEYHALY+tOWMAcFqkC0u2iwEYQf3n/Sl2DH3n/OpQvfFG3NFhkQiXqS//fVOCJ6N+dSBacEpWAiMansfxNI0SMu1hU+2gr6U7CMO8s/Ifz4GOR1X1pi3cMseJE2n3rbmQupGM1z+q2rwK0hTj170wKupzaVa20t1cFF2Dn3rxH4g+LU1ETQae2yMHaT04ro/HmrSMzWpIBOcLnnFeK+KrsRyskRw/OVqkiTF1u+hEh2uWI4xXPX07Pg549KS6kLSuT0BqnJJu6dKpIBs0nHFVJHz1qSQnNQyCqERjgmnwEj73rTQB1p8Y3HHSpYzQtFIGB3rd047RjGTWTZRngmti2Tacg1hJm0IlxThsDNDAA4K5Bp0Ib7wX86m2zbeNuD1rK5soGZPgnIGRVCRtpOzt2rdltQeTnNVZbBcttHXqaXMh8jM5CJFzxTTAwb1BrQjsfL5AJzT1QHjb06U+YXIZMkBwSVqq9uAx4xnmugCDoRnNMktQegqlMlwOeI2Dcvap7e5UMBIuCelX5dOBBAyPaq0lgeoBp8wuUuxBZEG0ipY4QB0rGSO8g+6xwDxVuG9nDDepxSbKUTXjhwak8hc5FVbW7EnQEnoa04wCAeualyLUSubbIzkcVWuLUOuCoVh0IrUK/hTWUN1HakpD5DG8sxjDfnQV3cAVoSRcYIqFogOQOatTIcCkUKjpUTR4ByKvMhHGKiZfWqUyHEz5U4quwPpWhIgJqB04rRSMpRKD7jwrYNPSU5wc8Uj4D4pDjHFaozsWUmwRk9atx3ToAVPTtWapqQOccGqQjehkguI/wB6Mk1FJbBDtzle1Z9rP8oJPPpV+NzIMbuaGNAkZR92ODVyIkLkd6rZbODT4HAbDdDSsMgu0dZN6DIJ+YZqaxikEbSbG8vP3j0zVm5VZLbhRuFZxuJV+R5Dsz0HQUrAbcMwAyTWr4eupNO1eDUrYjfGwyv94Vz0Ll0z1xUlvdESqrNtOeuaVh3PuH4ezaXr3h6C9S0iWVkG8GMda3pND0iYYn0y1kB7NCp/pXzz+z78RXs72LSLxt0TnapPY19MwyJcxiVCCCMjFLlDmZmReHdBhbMWi2KH1WBR/Spl0qwTASytlHtEv+FaGBSEc0uUXMymLOENgQQgD/pmP8KlW2C9EQfRRU+B6UEA9qVguyBoSKYIgByKt4pjDtinYLsqmCMnJUUGFAOg/KpyMU3BNFguyAQR9himmJQMAYqzt7ikK0WC7KxiXH3QaaI0U8KKs7aaVzRygQFVP8K01oUPYVPs96QgUWC5VMKdQB+VMMa+35CrZXNMKUWAqmJDjOPypjRKfb8KtlAKYy07CKSr608KfSnbaUCpEIFpcU4ClxSKGYpwFOApwAIoAaBSgU4CnAUDG49qAKfilAoC40CnBacBTgKBDMUbakApcUARYGcEVR11FW2aZyCiDpmtMJkgY615v+0J4lj8NeD3sYJcajf/ALuJFPzAHqaaEzwz4n+ILS48UmKzflMh5F6V5Rrdw0l3KWkO4nH4V013A1mrpcfNPgFmPrXL62yGUbQOASSKtAYl0xPyHoaqsNoAFTynMnJphVSrEnpV2JKjkluBUZPGDT2zuJ71G4J4zQwQgB3AdqtQRg49jUES5YZrVsIASBispuxpFXZd06IkDity1gAwWFRada4XJFa8UQC4xXJOR2QiQpGPTj0qZUUDpUyxDGQKXyzWLkbJEBQelIUCjgVZ8v16U0oKhyHYpGMZxio2t8jOPxq4YySc0gTHXpS5gsZxh2npTlQHHFX/AC1PUUzyQMkDrT5hcpB5KE8Dio3thg4HBq4qEHGKcqkHpVKYnEzDaAjpTPsSNlWXANaxjGTgYpfK6cVXOLlMZNO2NuiHNXYoWQYzgVeEVDRkDpScirFYpxmkK4GKsFfbio3HoKSkBXdaj2A8mpyCTyKQpxiqUhNFR4wearyR1oMpx0qF0PcVakQ4mZKhB6VXce1aEqZOAKqvGQeRWsZGUomdIgIyBzUDDFX5U+Y4qpIoB6da6Iu5hKNhikHgU1ic5HWhvl5FNbnkVqjNjlcpkgZq9bXBDKwbGO1ZwNOQlTkZpiN1rgSgsTzSQuc/NWZHMVI4wauwSK2MmiwzUjcmM+4xWfdRDnacVatyx+UHrUV5le3PeiwrjbaVQdu7kcEVPIjAhkxnOQSKzVYLJuB+takMivFtPJ7UWC5qeG9VnstZt7kEBkcH0FfaXws17+1NDgnD7wV5APSvhbY0bNIpyMce1e9/sx+OktdQj0G/bCSH92xPQ0rBc+p+vI6dRRg08DKBgeO3uKCtTYVyM0nSpCtJt9qLBcbmkwaft5pdtKwXIip9KTYasADHNNIHaiwXIdh9KQqR2qYj0ppFOwXIce1NI9qnI9qYRSHchK01h7VKRTGFA7kZpuKewNJRcLkbD0prL7VKQKTFCEZ4pRQKcBmpAKAKUCnAVJQBTTttAFPAoAQLTttKtOAFADNtKFp2KUCgBAKcBSgU4CgBAKMU/FDDAzQAy4ubewtJL26YLHEpavmHUJbrx/4/1PXdQYrZacAY0J4UZ+UfoTXv/jK3l1DT5bbJRXXaMV5x4e8OJDput2UI5e6BY4+8oXp+dNCZ87fESeEa/IqOwyc8Vx+ovmLb6d66r4lAReL9SQ4C2zbBjua4q7kWSEMrHJq4iM6QjPBziojnOc1LLhelQ5ycVoIYw6nNR09zz0pg5OKTY0T2iFzkjkdK6DS4TleBWTYISR6V0+kW+SoPSuWrI6aUbmtYpgAVoqg44FMtoVTg9KtADoK45M60rEewZo2VKFz+FOAGMEVmyiFlwMUwpxVlgD25phU9SKTGVWQEe9NKEkegq1syelAjOaRRAsVHl4PSrKoT2pfL9qA3Knlk9qBHirnl5FI0ftTFYqqnHSn+VnkCrAiJ7U4R4ouFisI6Yy+1XDHio3QjtQFimUPYVE8ZPbFXihzjFJ5Y6YpoRn+Wck0bMrnFXzF7UqwHqBxVbCZmmI9j1qF4ieSK1mh9qheDI6VaEYskJHNVZEA5I4rbmhPTFUJYCc5HFWmQ0ZM0XPSqVwnBOOlbMkfGMVUniyORXRCRjKJiMSQfSmKcHnpVy7tztODiqTKynnoK6EzmkrCch8g8HtUgIPFJgnpTSCCMVZDJCD0FWIJCoG41BuyBkdKXPOfSmI27FsnAPParep2rwxozuhZ13AA5IHvWNZSsHB3cVenkJQMWzxyaBFJiFOSKtWb84z3qrLgnIHWn2zBXGc0CNpMCPPY9a0PDgurPW4LyzVtqEEsOxzWTGzGIgHg12nw4jju5PsUsoCykAN70gPsr4aX9zqPhWzkuz++EYDZ69K6XFcv8OLG707RYbe7cSYQBHHcV1PrSYDcUYp9FIBmKMU7ApCKAuNoxSkUYoAbikxTqMUAMIppWpMUEUhkBWmlc1Pj2ppWkO5AUppSp2WmEUWHcgK80hGKmI55FNKiqQjKUU4CkFPHTpWYwApwFApwFSUAxTxikApQKAHAe9KBQo9aeq5oEJilApwGeMUqrmgBAtOApwHHSlxQMTFIRnrT8UYxQBUvLcSqBjpWFpemxQtqEhUAZZia6cgN1Fcp8Rr06J4L1i/iwriByDnvimhHw58RLsXnjrWwjHyzcuQf+BVyjZRdhHStJG+13t3cHLSSSkCqWoKY5jEww3XFaIkz5CSSKjIxUrAdRUTk5+lUBGxz1pq5zwOaUkZoQgOPXtUSYI2dJjDYIFdbpsRCDiuf8PxEkZHvXX2UZVAcYrjqO53UloW4gSoHenDOetOQcZFNkdU5chRWB0Einn3p+VA5bFZ0upRqpCleD1qjd6ysSggZPpS5RXNzeo6sM00TKTy3FcsdZZ3PBHpTG1ORX9Vo5AudX5yA/e4pyyIehzXOQaijwl2z9Ks213kKVb5TS5Bpm+u0rnNKBzzVBJ8DGatQS925pcpSkWlQUoj9RQj5ABqdWDdBjFLlHciWP2pRGMEkVYUA8UED7tKw7lZo/QVG0eT0q6VpCooBlLys84oMYHarTDAzimDk5NUiWVxHjrT1QdhUjYoQjJFUlckjMQI6VG8S44FWCecVG7oOc55q0hXKUsIPaqM8GOgrUkdWGRVeQJgk1aRLZiTW5z0qnPCRW7Ko61QuIwTmtEZsxJogwPGRWXd252nAOK6OaNQOKqPbhsitYysZSVzBgXIAI56U6WEjkdanvYmgk3jgZ5ojxIM5rVSMGioVOAaXB6Ac1akjIHAqMoQQDVpk2Fg+U5NXGmzDgCqygHjNICdxXJ4p3FYc33Qc0qEg5HWhAWH0qWKIs4qhWNG0DOmAea6DS5pbJ7eWI7WVwTis3SrREUSTSbR/CO5ret7SWWLzFTCKwB/GkxH2Z8KNSfVPCFhcq4cbAG5+6e9df3NeV/AGK903SBY3K/uZUEkTDocivVO5qWAtFFFIQUnNLRQIbRS4oxQMSkxTsUYoC43FGKdijFAxhFBFOoIosBEwzTCtTEU0ikO5Dtx1ppWpiOKYRTAxB6VIPpTQtSAVmUApyikFOFSUOApwFIDxSigByingCmg0oJoAcKcuKRRmnAH0piHYBHPejHHFAFLQAgNFFANIBQucV5T+05eSW/wANru3hJ3SsFbHYZ5/lXq6nkHPSvOfjVp7al4euowoY7ScH2FUI+JvBUDXOvQWqDczbiQe1VviBDFbeLHhhbcAi7v8AexzXd/Cfw9IPFWoyzIVNtA7DjocgVwnjfbJ4qmKjODnPrVokxJOBiq0hqzMRVRzliDTAYeO1EQ3TICO/Wg+mals1bzlAOc9KiRUdzsPDkBwpHJxXUxRExAc1jeHYsRqx4ranlESEs20CuGb1PQpqyI728htkyzAbR3rnrzVjM+IyXB6EU7US92SG5H86rQWB4YfLjsKg0K8xdkYru3dTVd1ncbWDYHTmt+G2IBG3rThacEgAiqRJz0cMgXoQfepzE20GttbXcMFae9gG44FOwjCgVw2znaK0rYHAHpU72AUfjSpCY+1MaJPMKsp3HirsVyqr15qhsJyaFJ71Nikaq3oDbQ3NW4Lo7Qdw59650MWkZQCO2afbTRo4TecipaKR19tKGxk1MAC+axtPuAcHNa8bbucis2UTYoKnpilU5FPUA9aQ2VpBgVXkfbyauTgYrOuW2gntTRLEeYHoaaJgG61QecFztPFMmnCgc81oiWWby9CHaCcmsu51NYlwWqtfzkqWByfasoRtJy4OTWiRDNJ9anUExxEj1Jpqa7I7bHTbVRLTJ+YkDHABqKaARfNtx+FUkQzSfVlbAbj8Khe8VieaymTdkgMT24oj81MrtJ/CqJZfeYHmmQzq7le4qu3mZAKHBpCu1vQnpVEsvTW0c8RVgCDWJdW0tlMMBih9K0re6aJwsox71seRFeQgjBJHWmmS1c5pH3pkH8aY6g8mtK/0OeA74cgHkisyTdHlJAd3860jIylGwBM9DUqpnjFRI4XpUqTh2ORitkyGPSMrgnGKuWsfzjIGM1Xjw5yOlXbfIKgiqJN7QreJrncUMhHTd0FepfCjw9Fr9jqVrcEJOtwhXj+H/IrzTRbwQkDA6Y5Fewfs2XrT+N7m2GHDQFvxGP8AGkJnv3hfS/sNhbWyjCwKFHHYCuhoACjgYJ5NApMQtFFFIkKKKKACiiigAooooAKKKKAEoIpaKBjCM0hFPIpCKLDIytIVqQimkUAYePWngUypAc1mWNwaeooGKUEZxUlDgM04LxTQQOlO3GgYoFPGO9R5NJzQKxOrCnhhVYbhT1JHemhMn3D0pCwFMBzS4zTELuBpQR6U0L7U7GO1ABn2rnfGsPmaZIgABcYrosgCqOpW4uU2EZ9KAPG/DPhJbLUNfuPLG2WE4bHWvkjxFiTW52xgqWB/Ov0Qm02OHSrxwAMxEGvzy1sga7ertBAdsH8TVoRhTn5cmqhJ71duxxxVBj82KbEB5NXNMUGfOO+KqMMHGa1vC8LXGoohXIX5j71lUdkaU1dnfaLGEtY8rzjNW7uNZV2kAip7OBUgwT9OKWRea8+ctT0orQyPsgBxtqT7MowSKuOME1GzY4NTcZAIwO1LtXIFLK6gZLACsy+1SKEYLBf61ohM1FCYPelGMAgc1zEmuzsQtvEee56UwapfswVsc+9aIlnUSMoH1qGVlLdO1YC305JywOPenrfTgEtg07CubGV6Ac0nlgnArNh1JAwEgOenNX4Z1Zs561DLRMLcnkVXmgKksBzV9JQQAaeyq/NQ2UkR6cWXg5IHtXQWzFlB24BrKtECnIrXt2JGDWTZaRaj9KlHFRJgVKDxxSuOxXusjJ6VkXbnkDoetbFwpYc1l3UQ7CmmJoyNrFmwOc1DKjPIQQcLWiUGeB9aaygDpWqZDRnPZbxtztpFtFXjHAq7I4HOagacdK0RDQRWiv16VZFhbswJjLGoEugMcgVYW7jAzvH51ZLQ42UZyEiA/Co/7OQ87AfrVmO6ibH7wZIqxHJHwAwJoJsZM9inQIBiqM+mqTllwOua6XAc880S24cEAA+1K4cpyM+nFUZhzjmn6bK0AAcEgV0/2IMPuVB/Zy787Bn6UcwuUu6YYLyHayjniua8aaQlqv2hEwGro7O1kt5QyDAHJFQ+Ko3vdNC++Pc1cZEyieaAgcHgipEYCotYhltrooDt781FC5KgE811QZyyVjShlAIFX4ZSwGODWRE4HXrV23fketWQzXEzLtxnjrivc/2RLeWTxlc3pRvLNs65PY8f4V4Lv2Qs7HkCvrH9knTkj8KS6gFG6R2Un6YoJPcT1oo7mikJhRRRSEFFFFABRRRQAUUUUAFFFFABRRRQAUhpaKBjDSU4ijFAzBFPGBTF44qQDNZliDmlxk0BOetPC+9SWN6UoNO2Zo2UAAanA57UgFOAA4NMBR1p2BSAdhT8UEsQY6U4CgAfjTgBTEHFGKXilAFADCB1oUDvinke1AXJFAGR44vl03wfqN0eqW7sPwBr8+fFNmbPUyJAQ8yebz6NyK+5vjCs934duNOtgzNOgjwvoTzXx18bvJj8fNZxABLa2SHj2H/16pAec3QxwKoOvzZrRuhzj0qhNz2ouKxEQW4A68V2Hw8tCZ3lI5B7+lcihKjJHOeK9I8DweRpu9hhn/WsKzsjeirs6TO1QM8AVDI1K7Niq8hIyK89vU9FLQbLIAc1RuZ8ZJNPuHPPPNZl6zY46d6EOxV1O/dV2ocsf0rJZTPIWlBduwqS4G9iTkY/WtzwhpZvnaRh8q8VtEhlGw0i+nIEUefQVt2ngy+uBueXaB1ru9JsIraMBVGRxVvXWNto1xJCCGEZOfwrdIwkzxHX/s2l6kbJLkPIOuPWqH9pkOAeg4qC0tJru+nurhWYFjyaLW2jkllgcNkE4x2q7EczNO3uoLhDznP6Vaime3kBLFo+n0rmYw1vdrGGKgkDHrXRSp5LJHMuFccZqJRNISOhtJVlQMpyDzVpJQOprE00FPl3EitUcc1zSOiJqWkikjFa1uQVzXM2suyQDNblpNwATWTNDSQmphnHNV4pAatKQRyam4Ec33azbkjmtKYjaay7tgAcVSBlGVwOMVUuLjbnAp10xHOax9QuCFKqcseK2iQwvL0q2FOSeTUAkllGclQabbwqG3OfmNbWm6Pd3+1Yl8sE4ya2ijJsxzGx6u3B9aiLwKMtIT7A13LeDUtrGW7u5ThFJ615ZfyTL5kkakRCTC59K0sZORsmePqsjqx96lS+nhG8SF89q512uBEkhjOCeT7VcspHfjZgnpSaBSOw0vV1lULIcEHpXRW8glAYHg15rLJIkoZVYMD1FdV4f1TzFCM3NYyNUdQAAARTtisc1DDKjLgHmp0YHAFZORViZIgcECnzWMTxZ25I5pY2HFWoTzjHWqjMlxPJ/iDZJbaoHYfIw4FcqOMAV6Z8T7HzIBcBclfSvMFI6jNd1GV0cVVWZZtizNljx2FakGMgkYrLtzzzWjG3QV0HOXZCWt2A9K+wP2RbgS+AJ4ieY7g/kQK+OYwTKwyfmX1r6w/Y6lP9hahBuHySKSPwoEe/etLQepoqRBRRRQIKKKKACiiigAooooAKKKKACiiigAoopKAA0lLSUDRhBe9SLSYpQKi1zUUDJpwoAzTselSMBS0UuOKQxuKUClxzT1WmJiKuKfigCnAGmSIFpwAHelGKMZ60AGKXJpfpSgd6dgG80hLAgj1qTHFIQKVgOV8Wwt5Ml0c/Jk49a+FviXLJJ441CWYHe8rdu2eK+/8AxHAH0ucEclOlfA3xbjkg8YXe7BPnMePrTA4y54GKz3Aya0bkErn1rPfgnimMaiB5UU92Ar1jRIDHYxIMYC5rzHSY/N1G2VuMtyK9Zs0xGAvQKBXJiGdVBDpBxVWXPORV5xnrVeWMmuE7kZVwO1Z10rtwtb72+eopjWajg00DOTNsynLAtz0rf8H30dh5kMq7C7ZB7YqO6gVckCqbQsw4O01qpENHqGmyC4VTGwOfStmXTFvNNaCdvlde9eP2F/qtiwWCVmHvXRWvjjV4FWOW234HUitoyMZwucV44nXRdQk06O12J2bH61zVrqMUchlC73OcflXoPinUoNfjIutMPmdmxjH41yB0WLcGSNlx2rZMy5WZmkW1xq+uxqqt5aHcxx0Fdr4ktUaBdy8ouVxWfaQXFrHttnER9cdamkW9mQK8i4HOMVEpGkI2KmkXG+Xy5Bt4yCa22ceUfUVirZSI+/dwDmr4lOzDDPvXPJnTFEtvMd3zDBrZtJegzWBF9/Na9oeAM1gzVI3YZCB1qykuR1rMhc8DNW0ORU3HYfczgDrWZcS7s81PeE7CKxp5ispUnGelVEloZdSHpmsqbaZhlSx6/SrVzIVjeTqR0qrbzBAc8k8k4raLM2dD4Z0ZbiRHnPU5wa9O0nSba3jVlTcRXk+keIIrSZVYYUd69B0HxRZXICNdICR0JxXRFmMzoNdt4p9HnjdMDacivAPEH2JpJNPtztIOQfevoBryxuIirzqVcYxmvHPHng2d9SlvNKnQo5zgNzV3MbHOR6cIdOImuFJU84btVjwjBFqeoeTCCwjU546GqMmjakYzEWKvnG4niuk8Nxw6Bp7pb/vbyT77UmxxWpQuLdllkQAHYcCp9MjCOHC7SeoqezsrySRpZSMuckCrsNqUOCOlcs5HQi/ZyEAZNakDE4rMtoyCK07ZcdaxbKsXYs8GrkJ4qnHVuE0XEY3ja3E+iTgdQCf0rxKL7uDkYJFe/wCtxebp00ZHVTXgt0hh1CeBuNrnFd+HkcldElvuzkGtS3ORn1rMthg47Vp23AGBXccdi5aDMyZGc8V75+yx4iTRvFU+mXDBYr0BQT2YdP5mvBrFA0gbJ4OeK7zwiWgvYr2JirrgqRQKx91E9x0NLXKfC7xJH4k8MRSPIDd24Ec4757H8a6oVIhaKKKCQooooAKKKKACiiigAooooAKKKKACkpaKAExSU6igZhjNOUU0GlzipNUS8mgDmkHPenAVIIMU4CgD2pwFIYKKeBigZ9KcBTJEwaVaXFFAAKWkFLTAKcKTFOApiF5pD9Kdg+lBHHSmBn624W3kDdNhwK+G/jfp0sHimSeRMLK5x+dfcmqwmW0lyeg4r5q/ab8PiKx065jiBYB3kYD6Uho+aL+Py2K9s8VlS4Tjqa3dVjDMSvJDY+lZRt/MbHQ9zQ2UXfCVuZNZBI5AGPxr0+AARkA98Vw/hGHZqDSHnAAruLQfuwBXBXep2UUSAE+9BXOcipAtSBM965TsKRU44qKU5GO9aDQ96geEZzjmlcDKli3dRVPyW3kFfxraaI5PFM+zDrTuFihDCOoqZlHYVbWIAYAproBxirUhcpnSIQ24Cq7w9SQK1ZUBAGOlQtDx0rRTFyozPLOelL5WOtXHiI6CmMnbFDmCiUXjqF0I4AzWgY8c1Ao3TbVXI9aybuWkRW8LFwSePpWrbJgimQwgdBVuCM5HFQ2WWoF4zVpOBTII+KmKGpGV51LLWPfW/wAxcLkiuhZOMGqdzBkHihMTObeLzFKkcUJbIvJXpWhLAySMccGhYsjkcGtYyM2il9mgYYaMH8KaLCAfdUrj0OK0PJweBTxCCeVrVSIcSitvOp/d3Uy+26po7a7zzdSkfWryQjPSrcMQx0quYnlMz+zWkXLFsD1NSQ6cImDhs+xrZRAOMUNGPSpcg5StBGFTH50pjVjkCnsMcUqnHFYyZVhYogOlW4kNV0OWx6VdhBrNsZJEmOtW4h6VGi1ZiXFAmFxHvgZW7ivCfFNuLfxHcIF68179sLJj1rxj4j2bReITIo6g9q7MO7HPWV0c7Dw30rRt88VnRgsBnjP6Vqwr+6UjPFeknocLRqaZGvmDsWrt9DT7OkeelchosQaaMdTuHFenS2C/2MkkSjIGTTuSzq/hL4nfw14tjMzEWN1+7lGeAD3/AA6/nX04rK6LIjBlYZBHQivjG2YzQgBv3sfWvov4G+Kl1nw8ul3MubyzXaATyydvy6UiT0WikHoaWgQUUUUCCiiigAooooAKKKKACiiigAooooAKKKKBmIBTwPSmgHNPA9ag1HAU8A0oFLikIAOKcooA4pwoGKMU7FNHWl3HPFAgxilxSUoNMAxSgUopwFNAwApwFAFKBVEhgUYpaKLCIpIw67COCa8y+POkJe+Ebx/LBeO3YL7V6j0rmPiTbed4ZvWAzmIjH4VJSPztvmK30kYBwr4NOggDs5xjFTeKoHtPE81tsODL/Wr9tbbFxjk9azkzeEblrw3EBIxC7SetdbbLhRgVg6FGQN2OSa6aBPkxiuKqztgrAiAHJNSDigLx0pcVzM3AjI4FMaPjNTAUhGallIqNGPSo2GOAKuMmajkj9qVx2KhGKawFSyIc5B6VC+4dKpMVhjAdKicjByMU8qzNnnik8tmOCOKq4rFcjNN2dzVox45prLRcdio6kjAFRwQBXyByetXCgIpVjC8ikUkIiqOtWoUGBioY1LZzz6VbjU8YpMZPCMdqsoBjpUMSjrVhFBGQahjGsgIqKSMEdKtbOM1Gy0XFYzp7fI6VX8jnpWsVB4qF4hnIFNMTRQ8kDtTljGeatCP2oaPjI61opE2IljXINTqAOgqIAjqKeDiq5hWJwAKRsEYqEygHGaQS7hxSbFYcyntTQMnpyKcC2OBViCDv61DY7Bbw45x1q/DFjrRBFxzVyOMZ6VIhI48irCJ04pUTFTovANCJY3GE+grzb4l2qf2nHIw+9mvTmX5Tj0riPiBbGVEmAztropOzMaiujynyvKnKISec81pw8Rg5zjqKi1BAkm4DGeRVjT181tuOnWvSjLQ8+ejN/QowZoz0JOa9R0KZJLNreQ9V4rz3w9Zt8hK5I713GnDywpx0p8xg5FWeE2F2SVJQn8xXQ+Cddfw54qs9SiB8gt+8A6Mp4P6VFqduLvTy0aguoyKxrEme2a2cgOhyvtTTHe59jWdzDeWkV3buHilQOjDuDU1eU/ALxK1zp8nh69k/f2+Wgyeq9x+HX8a9V9qsBaKKKBBRRRQAUUUUAFFFFABRRRQAUUUUAFFFFAGQB6U8DvTRThUGpIOKcKYOTmnCiwxwFHtilXmlx3peoCde1B6UtJVAKKcKaKeBSEKop4FItOFUSxaUUlKKYhaKKKBXEqpq1qL2xeAj7w21cpOlJlI+FP2h/CraB4+adVIhlcEHGB71ziwc5HIwK+s/2lvBC+JfAs13aQg3lqwm4HJABz/n2r5O0qWV0aCZcSRnaxxWE9DqpM0NJtykYJ6k5NbkIAWqemxN5fzYyK0EUDiuKo7nbEGGBTRyc05jTQe9c7NkPyKBTcZpy+lS1ctBjJoZBT1GaUilYZVeLPQVEYsdRV8DHGBUTqOtMCn5eOopkiY6CrTgdKhcGmBVkGBzUJFTzehqu7AdaAG9OaaWB61G7ljtQ5I6ipooSxBIpgS265781diUE9KjhiIxkVbiTBoYWHolTxxnGBSRLVuNBUjIfLIFRSjAzV5kBFVpozg8VLQFAuAevWnfeHFRzQEPuANNVmQ4I4NIB+MHpTguaZu5p6HmrQhpjJqN4j2q0GFOCg9qq4rFHyCTk85p0cGRgir4jX0pQgzRcViGGEelXIoh2pEQCrEQ9qlisPiT1FWEGOlMRRUoHHSkDJFPNSoeahUcVMo71SIZJ1B5xXNeKIgbfDDIFdMo3cVh+I48wuuK1p7mctjybxHGEdGXu3FXfBtp9qncsCe9VPFjGOaOM11XgWzNrbqzgBpOld0XoeZWOq0SwCAErwK02iMeRRYzoqAMBx1qS8uIHPykg4q7nKTaPOvnm3dsg81m63atpuq+ah/dsc1JbAGRZIgTKCO1bet2RvtMy0ZWZRkU0yolbRNVn0jV7TWLNsNE4J9x6GvqHRdRt9X0m31K1bMcyBh7HuPwr5E0t/Oja0lBUjt717B8AvEr293N4bvXIWQ77Ysejdx+I/lWyLPZ6Wk6GlpiYUUUUCCiiigAooooAKKKKACiiigAooooAyhThQBTgKg1FHFOB4pKUCgYq08cmkA96UDFABjBpQKKUUwALTgKBTgKCRQKWiiqJClFH40AUALRSUtAgpDS0UmMiniSaB4ZAGV1KkH0NfEPxT0MeHfiFqNtEmyITHC/7J5H86+4jXzB+1LpixeN4LtUwLiBGJ9SCR/QVhUN6Tszz20iwikngjNWGGDjFOt0AgjGQeKWTGc1xzR6EGQOMcU0gkYqR/emAc9a52jcVOFxTsgGkA7U9RmpLHr0p4XIoVfSnjIFAyMqFqJhnkVYbGKhbFICq+S2MVDLx1q01VZQaAKspxVGdsnAPJq3c52mqPJkwR06Gmhlu1gX7xHPrV+KNQM4zVS2zir8IAHPWqAcBjkVIhJPSopJUQZOefaiCZSRk0AXYslqtxntVSJ16g1OsijvQBOelRsM0pkGMg9KaJFz94fnSaAY0SnqKimtVI61Z3Kf4h+dLjIqGrAYMwaKfYw+U9DT1bNW9WiVo9y9VqhF2oQFuIjp2qdRVeLmrKjIqhDlFSopI5pqYqdBQAIvtUqLz0oUdxUiClYQ9FNSYOKRQaeOePSgkQZqZORTQoqSMAU0SyRcjis3WU3Rk4561pLkniqmoqHDE8YU1rDcylseL+KoGuPEtvbKT8zjP517DpVno9pbwrcO25UH51wGm6Z9t8fRMRvRWJJ/u4FdlLOnnsvmIQmRXbFXPOqrU30n0TGUR2980832kLyLYnPTJrmPtUAO0SKB9KPtltuwbjBHpWqic7SOoXV9MU5jswD3qV9cVyQsB/GuU+2WeM/aOfrihtRtABl3b2FWojL15GTdm5t1CEnOM1oRXklrNb6hbMUngYOCPY1hDUrc8iNzSjVIx/q4JM9KtID648H63B4i8PWupwMpMigSKP4XHUVr14T+zZq96dYvdLkVltZIjKinswI6fgTXu2MUwYUUUUEhRRRQAUUUUAFFFFABRRRQAUUUUAZoFKBigUuKg1HAUq0BaUCkAopaQCndqAFFLSA0tNAOFOHWminDrVEi0UUUxBSikpRQIKKWkoEFLSAAdKWhlCGvB/2rbPzBo1yo5w6E/Qg/1r3g15N+03CH8JWcm3LLcEf+O1jNaGlN2keCQgCJMegolHpS22DaxkDtRJ0rjkj0KbK55JoUcc0o4NLjNc7OlbDcc8VKuBUY64pwPNSaImVqXPeox1pwPapGDVG2OlPY00gGgCFxUEy8VbIFQSjOcUWAy7gHBqjkLJg9DWpcJnNZlyGRsgdKody9bsoGTWVr+vyWKEQRl5PQdqfHdqMqWrPvpIfML/Kc9SaqwXKC+KNULb5bV9oGelbWlawlwAxBUnqDVBJAwAVEIqWC0UtvVcHrxRYVzp4LsEAq3FWUuh3Nc/CSi49KGuGVSfSiwrm7NqEUcbPJJtArl9T8f2Ng/lpE8zDuBVh4DdgLKfkPUVBL4Y06d95RSfpQMh034jw3M6pJatGCetd7pd+l5Arr0auFj8J28b+aqoADXSaOBaw+VuPHTNS0M1NQOEcDnisy2DNxjpVuaUOODmmRJg5HepsBKi4IqygxUaAdamjAoEPRamTimpjpUqgUASJ9KlUDFRp0qQUEtki08Y6Ypq4FPxkdaCRR71IgGM1GpzUoGBQSxy5AqvcLu3qe6mpx0pjDLE+oxWkdzOWxi/DfQJNa8bfYYWKtJvG704r1y2+AseN9xq67ycnalYX7Ptokfj+SVlGfLkI9jxX0bzXo0ldHnVtzx2L4DaMRibVJz/uoKuQfAnwqg/eXN2/5CvVvxordIwPNIvgj4KQfPFdOfUyYq9bfCHwRByNPkf8A3pDXe4FGBVWA5KH4a+C4hgaNE3+8Sauw+CfCUIwmhWQ+qZroPwooAoafomkadP59jp1vbyEEbo0wcVf5paKBBRRRQIKKKKACiiigAooooAKKKKACiiigDPAp2KQCnCpNh1KKSlqRBQcYxQKXtzQACnCm4pwpoB4pcUgpRVEsWiiigQUtIKdTEFFFFAgooooGgrivjNoz6z4JuFiQtJbnzlA7gDn9K7WmsqupRwGVhgg9xUSVy07Hxbao8cTRvwyE0OCB1r1P44eDbPRZl1fTU2RzsfMj7A15hIMjNcc1Y7qUrlRuGNIDzillGGpgHNc0jsiP74pygdaaB3p61mzVC0ZAFOAyKY470mMCc0nQgUmSDSE80gFJwKgkOTmpWNQSMOlNAV5vrVK5QNkVclzVWQA1SFcxbuzV2yMr3yKzpLPy5GYksPQ10coFU5Yg2cVQrmXbRgODyPata1zjB/Cq4iwcgVctVIXOODQBIQAKiVdz4xUknC8VCj4akBciAxwOlDyFQdppYDnjHWldOtK40U2uroAEtlc9PWpbSe6lckrtHrU0cIY844q7BEqgYGKQya1QlfmOSatBcDpTYcLUoIxSAFBFSrTAPSpFAxmkBNH71Og4qup4qZDQImjHfNTL1qFTgVKpoJJKePrUeaUdcCgQ/PSpR0qBTzip14xQSx3alC5pKcowpJ6CtILUzk9Df+GV4dN8T284ONz7D9CcV9HDkZr5k8BW9xf+J7OC3QtmZST2GDzX02OBivSpLQ86s9RaKKK3RgFFFFMkKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigdigBTqQGlBFSaodilpueMCjOKmwDxQetJmimkA4U4daaB3pwpoTFFOFIKKZLHUUneloEFOptOpiYUUUUCCiiigApCKWiky0cR8Z9OF94JuXA+aHD5x2r5qYDyFz24NfXuv2i3+iXlmwyJYmXH4V8l6javBdXNseDHIRz25rmqI6KLMq4Azioas3C1WB5xXHJHpQY8HpTgaYDjinKeayZqiTPamOTmlz3pCc0ihhOOtNZuOvSkkNQOeCRSEPeTA61XklAGajkk7VUuZgq4B5qkiWyWWcc81XeX5Sc1nyXDBsc80oZmHXiqSETmYNyOaY5B4ohRmO0DFPaNd3zdRTsA2GMscjpWhFDtjBAqks8cAPTFW4LyN+h60WKRVum25XPGapvIGJVTz7VsSWcc6l8jJHai102KPl2yaloBunwu8WSalkQg7TVtXgiTagofy5FwvJpMexWgUA81aQ4NRJEQcYOKl24GKRLZOjCpVIIway5JnQ9sd6RL1Ublup6ZpWFc2QR2p6dKpwTq2M1ZRh60WHcsKalQnPWoUapU5pBcsKakWok9KlBHSgQ4Ng04NnmmYB60ueetAEq4NTLyRUEdTp1zTRDJPw4rZ8IaQmvaxDp7uyRuRvK9cVit0A559K9J+BWnibUp71k4iXAJHeuijG7OetKyPSvDHhXRvDyY0+2AkIwZG5Y1uUdTS16MVZHnN33CiiitEQwooooEFFFFAwoooouFgoooouFgooopXCwUUlGRRcdhaKTNGfagLC0UnPpRz60rhYWikyPWgEU7hYoZozTN1AJoNCQcc04Y9KYCeKUGkBIKUdaYDTgaAH+1OFMBp4NCExwpRTRThVEsWiiigQUuaSigB1FIKWgkKKKKACiiikyhO+K+Zvinpp0zxveR7cRzEuo9c819M147+0NpJP2PVo15+4xH6VlUWhrTdmeJXa4zgVQI5rSvgRJz3FZzgg1xTR6dN6BShqYTim5rFmxMGozio91G7ioLGytk9aqzNipJWOaqytTEyKZ+M5rOunwc1ZnJHuKyb6bGcdqpEsaXG7LHirdtJDtxkDArlrzUZY59iA4quL66KEqCCTirihHXTajFFwoHHeqUuoGRshvrXMyXNyXxtJNLHNcHgRH06VdgOkS5DHk81ZgmCjOa5U3lzC2WhJWrcWpAgFgy/WixSOvtL0quCc5qeS67hq5e31KLcAXFTtqUAOTIBj1NLlHc3GuiejUsd6VbOelc+2q2qqTvyfrUX9qROPk3EewqHEGztbHU7dyY3bDHtV/ajndGQwPSvOVv137lPzA1q6b4gWN9sj4xx1qWiTpruMD8ay7iIM+7JyKWXWoJY+ZRmo1u43zgg5qRFm2uDGQrZzWvazbgK55gXG5OorQ02Rgg3Aj1BpDR0EbetWUPSqFu+7vVyOpGWlIoJO/2pi8Cn5zQIfmlyM0wHnFKOKYMsRGp4+TVdB2q1EKpEMmBCpnHPavcvgzYfZfC/2hlw07k/hXhtlC1xeRxKMl2CgV9NeHbQWGh2lqBjZEoP1rsoqxw4h9DRFLSCjIrrRxi0UmaM1QhaKTmj8aLjsLRScetHFFwsFGRSZAo3AUuYLC/hRzTS49aaZQO9S5ofKSc0HFQGdR3FRtdxjgHJ+lS6iHystcUuQKpm5yOKYbliOlS6yK5GXiwFJvHqKzXuJMcdaZ50h6sKh1ylSNPzFA601pkFZxlamtISOtT7cpUi810g4FRtejOADVIscU0+9VGrcTp2LQOaUGmKaeMZrrMxwNLnFNNNJJ6UhkoanA1CvFSqQO9MB6+9SA1ECDT1pWEyQGnCmLTgaokeKKQUtBIUUUUAANLSUo6UCFooooAKKKKGNCGuY+JuljVvCN1CF3Og3r9RXUUyVFkieNxlWBBrOSuUnY+O7yL5mQr80Z71mzL8xFdx8S9HbRvFVzCVxG7Fl+hrjLkfMQO1cc0ejSldFN6j3YqWQDGagbiuZnUhSTnIPFLuwMVHuxTd/FQy0xs7fMT2qq5JqaU5qFqLiZUvAwGFGc9aybq3ZkIGcmt1wWGDUMluDVJiOUbTSzFiOaVbEjtXSm3HUgULbKT90ValYLHPCxHXHNOS1UcYHNdGtqh4IFIbGM87RkVakgsYQtImj2snNQS6ao5VR+VdGLKNRxmg2qEZPWncZyTaeehQE/SpINOJIJj+6K6f7LGTyMU4WydQKTY7GAmmpwSlO+wKOiV0It0zgipo7SMgDFS5CscrJYnBxHVU6XLJuLJ3yK7s6fEFB6006fGOMVDkI4ZNPuY8bcHnNX7RZFwpOG7V1DWCk4CiofsKDnaOKm4WKlqGHPWtG2b5hnHNNW3A7dKljjw3SkBp2h6CtGLGKzLY4xmtCFgKkCyMClzTM5pVFAEi8mpRUSA5qZRVITJYucGrcS5PFV4QDVuIY61SM5M6b4a6aNQ8W28e3KRHc34V9CdBgV5d8DtN8tLrUWX73yqa9NLZPWuqm7I8+s7yJKM1FvGaNw7/AM619oZcpLmkL471EWHSmE4HWj2ocpK0qr940z7ShOAelRMAe1JtA6VLqspRJ2l9qaZjUWQKaxJ5qHUZSiSNOcZqIzuTzSEcUm2odRj5Qdmbqxx7UhGTk5NPAoIxUubKSGY4oxSk8YpualyZVhCcdaQkUEio2NS2UkOLCkyKbmkyKVyrDyRTSaTIpCRQmFgJprGhj6U0885rWkyZotlsfU09aTAzTu2BXpo5EO6jFHFJQTSuMCTnilApoPNOBPSi4D1NSKajFPWncCQU4GminDii5I8GlFNFLmi4rDqKaGHalyadwsLRmk5peaLisLmikoouFh1FJ+NHHrSuAtN6GlOKbmokxnl/x40D7XpqarEhLw8Pgdq8AnXDEnAB4r7E1iyi1HTZ7OYArKhXmvlHxppMuka1dWMq4ZHO0+tc1Q66EjnJeM1VkYip5GJXJH1qrIcjBrlkdsWRiVS2AeaQtxVcJtkJ9e9OJzxmszUViTzR/CPWmkmlzgUihMEEHqDR1o3c4pR60wsMlU4zQnApTzg9qUAdaaYWDcN1P3Y6UwrjoaUAleKLiAkZyTUMjelOcNtx3qIKckU7gCnNP3beKRI2708JyM0XAcDmrMBwM1XC1PGCBUtjLSsDTs57VEmcVIvNTcYu3PNJ5YNSADHNKOlTcmxXMeOKbtAPFTyEZqE9eKq4WJYjirkTnAqhGeasxNQIvo1TKaqRtmrCHJxmmhFhBkZNSoCfpUSHOKsxD2q0iWyxAB0q3GjMyRpyzsAAKqIdikmuo+G2mtrHiOANGTDE25vwqkjGbsj2jwTYf2X4atoNuGZQzfWtUtzjOKkZVRVRRgKMCmEAmtNjj31EJz1ozig0n0p3ExcZoPFKAaCDSuJIacU0sBTiMdaYwz0FS2UkDMOtGc9KQrSrxSuMXHrQMUvWmk0h2AnBoLZ7Uwk5pC2KVykgY0wmlZqZmk2UkH86ac06msR0zUNlJDT1pM+9BpKCrC5ppNBoxTQMCRTc8GjnnNGcdq1pPUiZep2T3pv0GaN4DYyOPevUcrHGh3NNOaRpF6l1/Om+dEvVx+dZuaK5R8eSORipFBzzVc3MOOJBR9rhUZ38UvaIOUuAAU4YFUft0fQHNBv4xz6deaPaIfIzQBA4p4cCsr+04801tUXqo/Sl7RB7M2N4pdw9Kw/7UYkkJSHVJc4xS9qg9mbu4dqXdXPf2jOcnOKZ/aFyerUvaj9kzpN470GVR1OK5o3k7H7+Kf58ndyaPbB7I6AzJjO4Un2iP+8Kw1nbHJpfMJ71LrB7I2TdIOhpDdJ2NY+89qASO5qfasfskajXqDvTGvlAyM4rO+lGD2pe0uUqaL8d+D0FeT/HLQvtYGqxIAyjDECvSlFVtasotR02W3lXduUgVLlc0jFJ6HyTOCrFegqlJya6Pxppkul6tNbyLgbyVrm5CAxNZyN4kEhINR5xUrkEc1EeTWTRsgDZ4oJ45pjfWkbJGKk0Hg+lCt1qNT2p2ce9CGSAUvU0L60vU5xTAdgYpAxwQaD0wKQqaAEwSeaChHQdafGpPap1jHegRVAw2PWnBST0q0IgaURYoAroPSpVxSmI8Y4p6pziobGKgp469aAuOtFTcdh/brSZ4pAeKaT7UAIxNNNLyaQgkcU1uSwHNTxk1Eq4qZBVEFqE1YjNVI81YiODnNWhMuxHjFWoSSc44qjESelXrbOBuq0jNssMm9duOte2fBvRPsGim8kTDzcj6V5d4L0mTWtZht0B2Kcsfavomyt47Szjt4wAqqAAK1UTlqy6EhyTmkINPAoI70WMLjMetGBS0lIYvSgGjBxSD2pAI3SozUjdKjapZQhNIKQnnmkBpFIfk0hPGKCabmkMRqjYkVISMVC5pMpITJpaQDignFSy0hCaaT60tJ17UhiUc+lIxxzTS2KVxj6Qmm7jSbqaYmKTnimnpQWpoataW5ExusXTQxBUbBNYguJ2JJmb2q3rUgknK9QpqiADxiumpOxjCNyvfTXQWOOOZyzuAee1XoXYYJdm4xyaqtDunD54A4FWUOBWLmzRRJhIQcAmneax71CCKXIqeZlcpMJD1FL5jHqaiFOBGKXMx8qH7iacCexqIGnA0czCxLx65oNRg9qdmi47EgpRiowfSng0BYeMZqRcd6jBpwIHemFiQY9aetRZzzT1NBLJBzTgKjBp4JppCHYxTgDTQcU9cnkD8apIVwCmpEQluBmopp4oELyOAK5zXfFX2SI/ZsZ6CrsO5xP7Rfh1UtoNZtlwRxIAP1rwafcJSSeCMivZ/GHiG/1jTpLS6lyrdFrx/UoBFOcA8cVLRcGUGbPBPNN3jGe9JJgNn1qF2PY1lJHTEnyDTSSKZHIDzjinE7jWZogByetPWmAYNSKBnNIokX061KqgjNRxkZqdcGgBETvin7MkU6NanAAHSmBCkZFPC1Lt/WnKo70ARhcHkUjkHgcVI4OOO1R7c9aQDVBHFSqmaEXpzUyrgdahjIynqKQrg+1TYyOKR19qkCBhUbdcVMQD2xTCoPamDIyCKcvWnYBoOAKaIYcA5NSr7VCDnqKlUcVaJJY8irEZ55qBKmiPOKtCZdg9atxsQCFGXxwDVKBgOtWZLy2srZrq5kWNRwCTWsTGWx7z8FvD/wDZ+h/2ldKDNc8r7LXek7jXhvww+LEUUcWm3rLLbr8qyDsK9ntb+2voVuLWVHhYZUg1skcUr3uy3mlHOaanzDil+tHIyLiEdqjbg1IxqN+lS4tAODA8UcZqEkjkCpFbIzSsUhTTGpxPpTDxzUtDGMKYCQakNRyDvUGiHZoJpivnjNKeKTKQGonOKeaikNSykKGA+tBYEVWIIYkE05HycGoKJee1AOKbmjNK4xWNRmncdqY1IaGk00k+tITimZz3oQMeW4601SKSmFsdK1pbkT2MmeXzHZvU0xTUYOBSq1a1HqZwRMCKdmoQ1ODe9ZM0JRTxjtUQY04NxmgCVeBTsjFRbs8UuTSAkzSimLTwaAHU4DPFMzS5NUA8AAYzT1PPJqPJ7U9Bnp0qlFsV0PGB1NPUj0oWInkAmpktmxnFaKkyXIjWniniLHWnM1rEu6aQDFWqRPMIiluAKbcyw2yZnlC+2a5/xD4zs7BGjtiCw4rzvXfFdzfOx3EIewNVyEuR6hJ4k0eJiHvMY44PeszVfGunwRkW85c44FeTG4jfLOu8n1NNNwhbO0U+UVzqdS8V6hdMdjBUzWBcX88xPmOzH3NZ890c4UZJqldXojQhmwaLAmTXtwwcsxzjrzXLa0RIxfHBq3JdNcysiglOmc1geKtRS1aOJCOOCKlo1iytKuBtHaqzggcnilFwJFDKeDzQ59DmsZI6osjR9rYzUytxVYnBzjpTw/vWTRqiypyM5p6k1XV+1PV6kotoV9asRkHiqMT5qxHJSGXF+tSqwx1qmZDjg05ZMJyaALZkHSjzBVXzDjg01pccZ5oAtmQdM0AgDrVAzepp8UuO9JjL6kVMDxxVWJ81MJKlgTLgClbBFQ+YM4zSl+2eKQDG603pSs3eo2bA60hCsQOaTdmoWfNOjbcKtEslHpUqDsaiQVID61SJJkIH41KDjmq27vmlaTHOapCZfiYE4PTivMfiprFzeawtlGzR20QwAvc16Ckm4gg8A5rhPFen+ddzTt1LZHHStYMxkij8P9ZmsdQWCWQsrHAzX0f8L/FM0V3FbfaG8nj5Sa+VEDWtwjkgMr5zXr3gvUwiRyBsZA5FdcDjmj7B0zUxMvyggeprRS5VvSvKPA/iQBoknfMbAA5NekhEliWSM5VhkEV0RimYmkHRh15oIB71mKzA7WJqwkkgIwCQar2aYrlzyAR97FNMZXoarm8CcEc1Ityp6EVDoodxxBprL9acsyE7SaczcdOKzdApSIGFNI461MxB7UwrnpWUqJakVmUhsigvUjqecioJRjJrCUGjRMXdmmnGaZk0E+9ZtGiFwDxSFeaMkUbuKhjDtimMD1zQTTSTUMoXJ6UjZ6Uh3DoKTJpDuNaoiTUjDnrTWXmgBM8U3POaXGelJg1pS3Insc+GBHWlBpiqMYAxTgK2qIzgyRWp4xUa04A1DRdyQHtmnAgd6jAzxSqp64o5WwuiYMMe9KD6UixseMc1Zis5XGdpFUqbYnJIhU09eelXI9LlZclsfSrUWlIoBkfPsK0jQbJdRGWoJ7VYjhZuAM1oi2ijPK5qSLylP3QBW8aFjN1CglsOrCrMUUYGCtWgIyCT+FVb+eKOM4YDFaqmkTz3JN8S85VcUgkiZuJ4z/wKuE8T64kKsiSEA8cGuFl128NxuS9ZB1xmnZCue06leRwRkhwCPevPfFOvy/PGjnmsePXrpodss++snUJRcHLOeuetSwuZ17PJNI0jOx7Yqt7t/wDqq1MoVsAg5qpMSoznvS1C4yQkHG7Apock4HT1qOZ0U7i2eKzru8fb5cOSzfkKAuTX9+kQKA9PTvWY0rzKXfcATwDUkNs5YyzEMewpl4eqKMFRRa40M8/yYWYYAHFefeJb1p7vavzZeuw1+Rbe0JB5xXAWbG4v3cno3FZSNYnRQhkt4mPXbyKkEmRUKOWjAJ6Coy+2sZI64k+7PPNIWPYVGsozg0846g1k0aoesmWqQSZ79arYOSc8mk3Mq8nOKlody9G4Xipo5Ae9ZolPWpUmAGamxVzTD8daUyYTByaoCcEYzTllB4JosF0XvM4phkzxmq6y471G0nzH0osFybzBuIyaRGmEgJ6VFvGeKkVxgAmlYZowzEDrU/mk96y0lx0NP8/HU1LQXNHzcGl87nFZ/wBoUc5pBPkbs0rCuaBlHrUTy5HBqqJSeR0qWLJxnrRYLkqAnrUwG0YPWmAqq570jSjqaZJOr44NKZB1qoZQScHpTGmA70xFsy4zTfML4AJAzVHzWk+6Mc96u2yEmqE2X4MKnJrK1O3WXdkVqH7vTFVpVDcGqTIlqcHrunlclVGQeeK2vA1wxgxnOxulXdQtVl3ADNY/hM/Zdclt2OATmuqlI5akT2jQrrZarJ1KV7V8PNZ+2WAglbLAfLzXz/ocxZQgPB4ru/Aurmy1JI2JHO3rXdA5Gj2idCykgfMKrwyzRS4JJHpVi0lE0AfIO4VFKVRjnHWtUSy6rxT8Mo3jio57fYcqBn1FRwsM5Xg1cjlPCuMg96dhGa0aAfK7q3qDSKbsP+7uAf8AZcVduYFBLRkYNVCjEkN+BosFycXFwrqrw8d2U1IL2EcEMCenFVklKfI781OCFGWUEe9JpMdyx5sIGTIB9TTHWORcqQc1A6LKQpiVlqZbdARtTaAPWs5UkylJoja3xnHSonhdRV2KBEBIdjn1NP8AIOd3mE+2KwlQNFUMvBzgg0FfUVotbSdQVI96geKVeGiz6kGsZYctVSrgYpGFTbeeQRzjkYpjKSxCkED0rGVBmiqIjPtTSB1pXUg4waYzEVk6TRakhWUYqNxxQznGcZqJ5Dmp5Wh3FLYNG4EGoWY9aj8wirpR1Im9DHXNPAH51agsJWb7pAPrV6HTlBG7tXe6NznU7GZFG7HABq3FYzPwqmteC0jTGFq2qqowOKFQB1DHi0p8As2PWrEWnQoQGbdV6Ugjg81VlcocYya1VFIlzZajt7dOiipAqgcNgCsmfUUt0LNjjuaw7/xhaW4+cjJPrVqCRPMdmrovVh+dP3ofunNeYz+OYN2Vwf6VJYeNbcyZeQD15qrIVz0mUbugxWbeOYFYlgcVh/8ACVWrxbkmDcZ61k6n4k8xGKgnilYDQ1LXHiztfFczrPiObaVEhAPXmsfVtQeWTcCQKxp5PMBJPWk0MZqGoS3crK5yuarI4C5ZcEUjKE+aq806R5DsBU2C5Yebauc4qtNqMakrvyw96pPK8/Yqnp3NRyRqoLbRSsO4+51MrkhWJ+lVzqLPgEEgjvVC8uWVgg71WaRjkKeaVguXp53mfbjC45xTIgFCoOo9aS1GQGf0qeNOckZNOwXJywCqAMkdazrhy9xuUgndzV2Qnk+1Z6jbKCOh60rDTMPxnNttmGMkrwa5HRl/ds5BOTXUePEK2AkVuimud0YA6ajYGTzWU0bU2aKNgUyXJGQKaMB857dKe3HUda52jsi0V2Yg9xUqTY4zTZBu5qF1IGcUuUvmRfjlUnB5qQbWFZiuyngVKk5QcmocWPmRfMeVzj9KYEx0HWmR3G4feqYSA8VPKw5kNwxOAOBRhgRycVMpyakCAjOaXKx3RXUvyc8UDcepqz5eDjANJ5J7rS5WF0QNuxwaUbs9aseXxggUCIZzScWPmRAruCfTNO3MccZ5qYRLnmnrEB1qeVj5kRhSeDU6xAkEnpQABS71HejlYuZE8agdcYp25RkiqrTAcA1E84HGaOVi5kXHl96ieb3qq0y9c1EZS5+U0crDmRaebnKnFEZ3ncDwagiiZgNwyKv28PHSnZi5kPt0O72rTt1C9RVaGPHXHFWVfAAA6UrMV0PkPGBUJ5pWYmmYYc4qkmS2hrqoIYiuWu0+x+I0mThXIzXVTY2niub8Ujy1guAvIcZNdNKLRhUaO/8ADsuArHr6VuzTSW11Hcx5wcE4rmdDkURQt03AV0l4SdPVyM13wOKW57L4H1tLzTYt8vQDjNdVNtZQ68g14P4G1SS1k8ouQOoGa9h8O6gL228skZArZEM04pgrcnpV6KZWHBNYVzMYZCMDitHT5FkiDhvwqhGmrHGCMiopk4yF4p0TcYpxOKAM+SMBwSORUkE29SrKQR0zUso5yKh2Enk0AWASpz0qRJx0JqLAcAE1BMdj4XnNILmgjq38VSCQVkJI4YEngVYS4z0pMdy3c3PlJkjIxVFdVjEgUtyeMGoNRnJTBPFZYhDvuXqelTYLnSpMkvPrQtvCMnb19Ky7GC6Qr82QOta6Egc0nFMdxnkqR34qN7UNzwfwqVpQvBpiz4ORUummUpNEEljnoKgksH2naCfrV43GOaPtIJHFZOih+0ZhzWtwgJKZqnJlXKsCDjPIrqgVfqAfrSS2kEoy6ChUEgdRs//Z");
                    }
                }
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    if (param != null) {
                        param.setResult("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");
                    }
                }
            });

            XposedHelpers.findAndHookMethod(c, "getImageFile", Context.class,new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    if (param != null) {
                        param.setResult("/sdcard/DCIM/Camera/IMG_20200625_214341.jpg");

                    }
                }
            });

*/
        }


    }



    public void hideXposed(XC_LoadPackage.LoadPackageParam lpparam) {
        findAndHookMethod("java.lang.Class", lpparam.classLoader, "forName", String.class, Boolean.TYPE, ClassLoader.class, new XC_MethodHook() {
            protected void beforeHookedMethod(MethodHookParam paramAnonymousMethodHookParam)
                    throws Throwable {
                String str = (String) paramAnonymousMethodHookParam.args[0];
                if ((str != null) && ((str.equals("de.robv.android.xposed.XposedBridge")) || (str.equals("de.robv.android.xposed.XC_MethodReplacement")))) {
                    paramAnonymousMethodHookParam.setThrowable(new ClassNotFoundException());

                }
            }
        });
        Class clazz2 = XposedHelpers.findClass("java.io.File", lpparam.classLoader);
        XposedHelpers.findAndHookMethod(clazz2, "exists", new kon());
        String str = Build.TAGS;
        if (!str.equals("release-keys")) {
            XposedHelpers.setStaticObjectField(android.os.Build.class, "TAGS", "release-keys");

        }


        prefs = new XSharedPreferences("com.aldyjrz.vermukmodule", "TOI");
        new File("/data/data/com.aldyjrz.vermukmodule/shared_prefs/TOI.xml").setReadable(true, false);
        new File("/data/data/com.aldyjrz.vermukmodule/shared_prefs/TOI.xml").setExecutable(true, false);
        prefs.makeWorldReadable();
        prefs.reload();

    }

    private final boolean isContext() {
        Activity activity = currentActivity;
        if (activity != null) {
            Activity activity1 = null;
            if (activity != null) {
                Context context = activity.getApplicationContext();
            } else {
                activity = null;
            }
            if (activity != null) {
                Looper looper = null;
                Activity activity2 = currentActivity;
                activity = activity1;
                if (activity2 != null) {
                    Context context = activity2.getApplicationContext();
                    activity = activity1;
                    if (context != null)
                        looper = context.getMainLooper();
                }
                return looper != null;
            }
        }
        return false;
    }
    public void initZygote(IXposedHookZygoteInit.StartupParam paramStartupParam)
            throws Throwable {


        findAndHookMethod(Instrumentation.class, "newActivity", ClassLoader.class, String.class, Intent.class, new XC_MethodHook() {
            protected void afterHookedMethod(MethodHookParam paramAnonymousMethodHookParam)
                    throws Throwable {
                currentActivity = (Activity) paramAnonymousMethodHookParam.getResult();
                readPrefs();
            }
        });
        findAndHookMethod(Activity.class, "onResume", new XC_MethodHook() {
            protected void beforeHookedMethod(MethodHookParam paramAnonymousMethodHookParam)
                    throws Throwable {
                if ((currentActivity != null) &&  (currentActivity.getPackageName().equals("com.gojek.driver.bike"))) {
                    readPrefs();
                    currentActivity.getWindow().addFlags(FLAG_KEEP_SCREEN_ON);

                }
            }
        });

        if (Build.VERSION.SDK_INT >= 23) {
            opHook = new XC_MethodHook() {
                @SuppressLint({"InlinedApi"})
                protected void beforeHookedMethod(XC_MethodHook.MethodHookParam paramAnonymousMethodHookParam)
                        throws Throwable {
                    Object localObject = paramAnonymousMethodHookParam.args[0];
                    if ((localObject.equals(58)) || (localObject.equals("android:mock_location"))) {
                        paramAnonymousMethodHookParam.setResult(0);
                    }
                }
            };
            finishOpHook = new XC_MethodHook() {
                protected void beforeHookedMethod(XC_MethodHook.MethodHookParam paramAnonymousMethodHookParam)
                        throws Throwable {
                    Object localObject = paramAnonymousMethodHookParam.args[0];
                    if ((localObject.equals(58)) || (localObject.equals("android:mock_location"))) {
                        paramAnonymousMethodHookParam.setResult(null);
                    }
                }
            };

        }

    }

    public boolean stringContainsFromSet(String base, Set<String> values) {
        if (base != null && values != null) {
            for (String tempString : values) {
                if (base.matches(".*(\\W|^)" + tempString + "(\\W|$).*")) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean stringEndsWithFromSet(String base, Set<String> values) {
        if (base != null && values != null) {
            for (String tempString : values) {
                if (base.endsWith(tempString)) {
                    return true;
                }
            }
        }

        return false;
    }

}
