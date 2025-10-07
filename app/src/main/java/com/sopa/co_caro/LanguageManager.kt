package com.sopa.co_caro

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import java.util.*

enum class AppLanguage(val code: String, val displayName: String) {
    ENGLISH("en", "English"),
    VIETNAMESE("vi", "Tiếng Việt")
}

object LanguageManager {
    private const val PREF_LANGUAGE = "pref_language"
    private var currentLanguage: AppLanguage = AppLanguage.VIETNAMESE
    private val languageChangeListeners = mutableListOf<(AppLanguage) -> Unit>()
    
    fun getSavedLanguage(context: Context): AppLanguage {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val savedCode = prefs.getString(PREF_LANGUAGE, AppLanguage.VIETNAMESE.code)
        val language = AppLanguage.values().find { it.code == savedCode } ?: AppLanguage.VIETNAMESE
        currentLanguage = language
        return language
    }
    
    fun saveLanguage(context: Context, language: AppLanguage) {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString(PREF_LANGUAGE, language.code).apply()
        currentLanguage = language
        notifyLanguageChange(language)
    }
    
    fun applyLanguage(context: Context, language: AppLanguage) {
        val locale = Locale(language.code)
        Locale.setDefault(locale)
        
        val config = Configuration(context.resources.configuration)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale)
        } else {
            @Suppress("DEPRECATION")
            config.locale = locale
        }
        
        // Apply configuration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            context.createConfigurationContext(config)
        } else {
            @Suppress("DEPRECATION")
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
        }
        
        currentLanguage = language
        notifyLanguageChange(language)
    }
    
    fun getCurrentLanguage(): AppLanguage = currentLanguage
    
    fun addLanguageChangeListener(listener: (AppLanguage) -> Unit) {
        languageChangeListeners.add(listener)
    }
    
    fun removeLanguageChangeListener(listener: (AppLanguage) -> Unit) {
        languageChangeListeners.remove(listener)
    }
    
    private fun notifyLanguageChange(language: AppLanguage) {
        languageChangeListeners.forEach { it(language) }
    }
}

@Composable
fun rememberLanguageState(context: Context): MutableState<AppLanguage> {
    val state = remember { mutableStateOf(LanguageManager.getSavedLanguage(context)) }
    
    DisposableEffect(Unit) {
        val listener: (AppLanguage) -> Unit = { language ->
            state.value = language
        }
        LanguageManager.addLanguageChangeListener(listener)
        onDispose {
            LanguageManager.removeLanguageChangeListener(listener)
        }
    }
    
    return state
}

@Composable
fun LocalizedString(
    key: String,
    vararg args: Any
): String {
    val context = LocalContext.current
    val languageState = rememberLanguageState(context)
    
    return try {
        // Create a new configuration with the current language locale
        val config = Configuration(context.resources.configuration)
        val locale = Locale(languageState.value.code)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale)
        } else {
            @Suppress("DEPRECATION")
            config.locale = locale
        }
        
        // Create a new context with the locale configuration
        val localizedContext = context.createConfigurationContext(config)
        
        val resourceId = localizedContext.resources.getIdentifier(key, "string", localizedContext.packageName)
        if (resourceId != 0) {
            if (args.isNotEmpty()) {
                localizedContext.getString(resourceId, *args)
            } else {
                localizedContext.getString(resourceId)
            }
        } else {
            key // Fallback to key if string not found
        }
    } catch (e: Exception) {
        key // Fallback to key if error
    }
}
