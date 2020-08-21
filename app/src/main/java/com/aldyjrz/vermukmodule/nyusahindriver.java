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

import com.aldyjrz.kotlin.tools.A2;
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

public class nyusahindriver extends A2
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

    private void BSHToast(String paramString) {
        if (currentActivity != null) {
            Toast toast = Toast.makeText(currentActivity, paramString, Toast.LENGTH_SHORT);
            TextView localTextView = toast.getView().findViewById(android.R.id.message);
            if (localTextView != null) {
                localTextView.setGravity(17);
            }
            toast.show();
        }
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
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam loadPackageParam) {
        this.systemContext = (Context) XposedHelpers.callMethod(XposedHelpers.callStaticMethod(XposedHelpers.findClass("android.app.ActivityThread", loadPackageParam.classLoader), "currentActivityThread"), "getSystemContext", new Object[0]);
        final File a = new File("/data/user_de/0/com.bca.mobiles/shared_prefs/BSH.xml");
        final File b = new File("/data/user/0/com.bca.mobiles/shared_prefs/BSH.xml");
        final File c = new File("/data/data/com.bca.mobiles/shared_prefs/BSH.xml");
        File file;
        int sdk = Build.VERSION.SDK_INT;
        if (sdk == 23) {
            file = a;
        } else if (sdk < 23) {
            file = b;
        } else {
            file = c;
        }
        myPref = new XSharedPreferences(file);
        myPref.makeWorldReadable();
        myPref.reload();
        if (myPref == null) {
            myPref = new XSharedPreferences(BuildConfig.APPLICATION_ID, "BSH");
            myPref = new XSharedPreferences(file);
        }
        myPref.makeWorldReadable();
        myPref.reload();
        if (myPref == null) {
            myPref = new XSharedPreferences(BuildConfig.APPLICATION_ID, "BSH");
        }
        myPref.makeWorldReadable();
        myPref.reload();


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

            initFile(loadPackageParam);
            initRuntime(loadPackageParam);
            hideXposed(loadPackageParam);
            bypassFa(loadPackageParam);
            mocka(loadPackageParam);
            MockLocation(loadPackageParam);

            vermktoi = myPref.getString("srcImage", "");
            XposedBridge.log("====VERMUK GRATIS====");
            XposedBridge.log("Made with Luv by BSH Team");
            Class findClass2 = XposedHelpers.findClass("id.idi.ekyc.services.VerifyUserBiometricService$5", loadPackageParam.classLoader);
             if (!vermktoi.equals("")) {
                XposedHelpers.findAndHookMethod(findClass2, "run", new XC_MethodHook() {
                    /* access modifiers changed from: protected */
                    public void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                        super.afterHookedMethod(methodHookParam);
                        if (methodHookParam != null) {
                            XposedHelpers.setObjectField(methodHookParam.thisObject, "ɩ", vermktoi);
                            XposedBridge.log("Face Data : Crott");
                        }
                    }

                    /* access modifiers changed from: protected */
                    public void beforeHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                        super.beforeHookedMethod(methodHookParam);
                        if (methodHookParam != null) {
                            XposedHelpers.setObjectField(methodHookParam.thisObject, "ɩ", vermktoi);
                            XposedBridge.log("Face Data : Inject");
                        }
                    }
                });
            }
            XposedHelpers.findAndHookMethod(XposedHelpers.findClass("id.idi.ekyc.dto.VerifyUserBiometricRequestDTO", loadPackageParam.classLoader), "validate", new XC_MethodHook() {
                /* access modifiers changed from: protected */
                public void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                    super.afterHookedMethod(methodHookParam);
                    if (methodHookParam != null) {
                        methodHookParam.setResult(true);
                        XposedBridge.log("Validating face data request...");
                    }
                }

                /* access modifiers changed from: protected */
                public void beforeHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                    super.beforeHookedMethod(methodHookParam);
                    if (methodHookParam != null) {
                        methodHookParam.setResult(true);
                    }
                }
            });
        }
        gopartner(loadPackageParam);

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
    private void gopartner(XC_LoadPackage.LoadPackageParam loadPackageParam){
        if(loadPackageParam.packageName.equals("com.gojek.partner")) {
            act = new String[]{"com.bca.mobiles", "com.gojek.driver.car", "com.gojek.goboxdriver", "com.gojek.partner"};
            pkg1 = new String[]{"com.bca.mobiles", "id.co.cimbniaga.mobile.android", "com.deuxvelva.satpolapp", "com.telkom.mwallet"};
            key1 = new String[]{"magisksu", "bsh", "bsh-vip", "edconfig", "xposedbridge", "edxp", "supersu", "magisk", "superuser", "Superuser", "noshufou", "xposed", "rootcloak", "manager", "edxposed", "xposed", "substrate", "greenify", "daemonsu", "root", "busybox", "titanium", ".tmpsu", "su", "rootcloak2"};
            cmd1 = new String[]{"su", "which", "busybox", "pm", "am", "sh", "ps","edxposed", "magisk"};
            lib1 = new String[]{"tool-checker"};
            appSet = new HashSet<>(Arrays.asList(this.pkg1));
            keywordSet = new HashSet<>(Arrays.asList(this.key1));
            commandSet = new HashSet<>(Arrays.asList(this.cmd1));
            libnameSet = new HashSet<>(Arrays.asList(this.lib1));
            activity = new HashSet<>(Arrays.asList(this.act));
            moremock(loadPackageParam);
            initFile(loadPackageParam);
            initRuntime(loadPackageParam);
            hideXposed(loadPackageParam);
            bypassFa(loadPackageParam);
            this.systemContext = (Context) XposedHelpers.callMethod(XposedHelpers.callStaticMethod(XposedHelpers.findClass("android.app.ActivityThread", loadPackageParam.classLoader), "currentActivityThread"), "getSystemContext", new Object[0]);
            moremock(loadPackageParam);
            mocka(loadPackageParam);
            MockLocation(loadPackageParam);
            govermuk(loadPackageParam);

         }

    }
    private void govermuk(XC_LoadPackage.LoadPackageParam lpparam){
        final String vermuk = myPref.getString("srcImage", "");
        final Class<?> x = XposedHelpers.findClass("id.idi.ekyc.dto.VerifyUserBiometricRequestDTO", lpparam.classLoader);
        if(!vermuk.equals("")) {
            XposedHelpers.findAndHookMethod(x, "validate", new XC_MethodHook() {
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
            Class<?> getFaceData = XposedHelpers.findClass("id.idi.ekyc.dto.VerifyUserBiometricRequestDTO", lpparam.classLoader);
            XposedHelpers.findAndHookMethod(getFaceData, "getFaceData", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    if (param != null) {
                        XposedHelpers.setObjectField(param.thisObject, "d", vermuk);
                        XposedBridge.log("VFD-BSH: " + vermuk);
                    }
                }

                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    if (param != null) {
                        String ver = myPref.getString("srcImage", "");
                        XposedHelpers.setObjectField(param.thisObject, "d", ver);
                        XposedBridge.log(String.valueOf(XposedHelpers.getObjectField(param.thisObject, "d")));
                    }
                }
            });
            Class<?> VerifyUserBiometricRequestDTO = XposedHelpers.findClass("id.idi.ekyc.dto.VerifyUserBiometricRequestDTO", lpparam.classLoader);
            XposedHelpers.findAndHookMethod(VerifyUserBiometricRequestDTO, "validate", new XC_MethodHook() {
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
        }
    }

    public void initZygote(IXposedHookZygoteInit.StartupParam paramStartupParam) {
        final File a = new File("/data/user_de/0/com.bca.mobiles/shared_prefs/BSH.xml");
        final File b = new File("/data/user/0/com.bca.mobiles/shared_prefs/BSH.xml");
        final File c = new File("/data/data/com.bca.mobiles/shared_prefs/BSH.xml");
        File file;
        int sdk = Build.VERSION.SDK_INT;
        if (sdk == 23) {
            file = a;
        } else if (sdk < 23) {
            file = b;
        } else {
            file = c;
        }
        myPref = new XSharedPreferences(file);
        myPref.makeWorldReadable();
        myPref.reload();
        if (myPref == null) {
            myPref = new XSharedPreferences(BuildConfig.APPLICATION_ID, "BSH");
            myPref = new XSharedPreferences(file);
        }
        myPref.makeWorldReadable();
        myPref.reload();
        if (myPref == null) {
            myPref = new XSharedPreferences(BuildConfig.APPLICATION_ID, "BSH");
        }
        myPref.makeWorldReadable();
        myPref.reload();


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
                    BSHToast("Welcome, BSH - 2020");
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
