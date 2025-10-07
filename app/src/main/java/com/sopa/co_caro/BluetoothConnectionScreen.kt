package com.sopa.co_caro

import android.bluetooth.BluetoothDevice
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BluetoothConnectionScreen(
    onBack: () -> Unit,
    onGameStart: (GameState) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val bluetoothManager = remember { BluetoothManager(context) }
    
    var isHost by remember { mutableStateOf(false) }
    var isConnecting by remember { mutableStateOf(false) }
    var connectedDevice by remember { mutableStateOf<BluetoothDevice?>(null) }
    var pairedDevices by remember { mutableStateOf<List<BluetoothDevice>>(emptyList()) }
    var selectedDifficulty by remember { mutableStateOf(Difficulty.EASY) }
    
    // Animation states
    val titleScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "titleScale"
    )
    
    val contentAlpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(800, delayMillis = 200),
        label = "contentAlpha"
    )
    
    // Load paired devices
    LaunchedEffect(Unit) {
        pairedDevices = bluetoothManager.getPairedDevices()
    }
    
    // Handle connection state changes
    LaunchedEffect(bluetoothManager) {
        bluetoothManager.onConnectionStateChanged = { connected ->
            isConnecting = false
            if (connected) {
                // Start game when connected
                val gameState = GameState(
                    difficulty = selectedDifficulty,
                    gameMode = GameMode.MULTIPLAYER,
                    isConnected = true,
                    isHost = isHost
                )
                onGameStart(gameState)
            }
        }
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Text(
            text = "🔗 ${LocalizedString("bluetooth_connection")}",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.scale(titleScale)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Difficulty Selection
        Text(
            text = LocalizedString("select_difficulty"),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.alpha(contentAlpha)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.alpha(contentAlpha)
        ) {
            Difficulty.values().forEach { difficulty ->
                FilterChip(
                    onClick = { selectedDifficulty = difficulty },
                    label = { Text(getDifficultyDisplayName(difficulty)) },
                    selected = selectedDifficulty == difficulty
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Host/Client Selection
        Text(
            text = "Chọn vai trò:",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = {
                    isHost = true
                    isConnecting = true
                    bluetoothManager.startServer()
                },
                enabled = !isConnecting
            ) {
                Text(LocalizedString("create_room"))
            }
            
            Button(
                onClick = {
                    isHost = false
                },
                enabled = !isConnecting
            ) {
                Text(LocalizedString("join_room"))
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Connection Status
        if (isConnecting) {
            Text(
                text = if (isHost) LocalizedString("waiting_connection") else LocalizedString("select_device"),
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
        }
        
        // Paired Devices List (for Client)
        if (!isHost && !isConnecting) {
            Text(
                text = LocalizedString("paired_devices"),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (pairedDevices.isEmpty()) {
                Text(
                    text = LocalizedString("no_paired_devices"),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            } else {
                LazyColumn(
                    modifier = Modifier.height(200.dp)
                ) {
                    items(pairedDevices) { device ->
                        DeviceItem(
                            device = device,
                            onClick = {
                                isConnecting = true
                                bluetoothManager.connectToDevice(device)
                                connectedDevice = device
                            },
                            enabled = !isConnecting
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Back Button
        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(LocalizedString("back"))
        }
    }
}

@Composable
fun DeviceItem(
    device: BluetoothDevice,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = { if (enabled) onClick() }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = device.name ?: LocalizedString("unknown_device"),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = device.address,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun getDifficultyDisplayName(difficulty: Difficulty): String {
    return when (difficulty) {
        Difficulty.EASY -> LocalizedString("easy_3x3")
        Difficulty.MEDIUM -> LocalizedString("medium_6x6")
        Difficulty.HARD -> LocalizedString("hard_9x9")
    }
}
