package com.sopa.co_caro

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LanguageSelector(
    onLanguageSelected: (AppLanguage) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedLanguage by rememberLanguageState(context)
    
    // Animation states
    val titleScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "titleScale"
    )
    
    val radioScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(600, delayMillis = 200),
        label = "radioScale"
    )
    
    
    Column(
        modifier = modifier
            .selectableGroup()
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = LocalizedString("select_language"),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.scale(titleScale)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        AppLanguage.values().forEach { language ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(radioScale)
                    .selectable(
                        selected = (selectedLanguage == language),
                        onClick = {
                            selectedLanguage = language
                            LanguageManager.saveLanguage(context, language)
                            LanguageManager.applyLanguage(context, language)
                            onLanguageSelected(language)
                        },
                        role = Role.RadioButton
                    )
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (selectedLanguage == language),
                    onClick = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = getLanguageFlag(language),
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = language.displayName,
                    fontSize = 16.sp,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = getLanguageDescription(selectedLanguage),
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

private fun getLanguageFlag(language: AppLanguage): String {
    return when (language) {
        AppLanguage.ENGLISH -> "🇺🇸"
        AppLanguage.VIETNAMESE -> "🇻🇳"
    }
}

private fun getLanguageDescription(language: AppLanguage): String {
    return when (language) {
        AppLanguage.ENGLISH -> "English - United States"
        AppLanguage.VIETNAMESE -> "Tiếng Việt - Việt Nam"
    }
}
