package com.aldyjrz.kotlin.tools

import android.annotation.SuppressLint
import com.aldyjrz.kotlin.BuildConfig
import com.aldyjrz.kotlin.tools.BaseAllString.Companion.prefXmlFile
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.XSharedPreferences
import java.io.File

open class A2: IXposedHookZygoteInit, A3() {
    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam?) {
        getInstance()?.let {
            myPref = XSharedPreferences(BuildConfig.APPLICATION_ID, prefXmlFile)
            myPref = it.apply {
                makeWorldReadable()
                reload()
            }

        }
    }
}

abstract class A3 {
    lateinit var myPref : XSharedPreferences

    companion object {
        var xSharedPreferences: XSharedPreferences? = XSharedPreferences(BuildConfig.APPLICATION_ID, prefXmlFile)

        fun getInstance(): XSharedPreferences? {
            when (xSharedPreferences) {
                null -> xSharedPreferences = XSharedPreferences(BuildConfig.APPLICATION_ID, prefXmlFile)
            }
            return xSharedPreferences
        }
    }
}

class PrefsCenter: PrefsCenterInterface {
    @SuppressLint("SetWorldReadable", "SdCardPath")
    override fun setWorldReadable() {
        val osFile = File("/data/data/${BuildConfig.APPLICATION_ID}/shared_prefs/${prefXmlFile}.xml")
        when {
            osFile.exists() -> osFile.apply {
                setReadable(true, false)
                setExecutable(true, false)
            }
        }
    }
}

interface PrefsCenterInterface {
    fun setWorldReadable()
}

class BaseAllString {
    companion object {
        const val prefXmlFile = "BSH"
    }
}