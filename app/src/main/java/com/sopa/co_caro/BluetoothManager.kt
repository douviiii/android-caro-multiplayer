package com.sopa.co_caro

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.*
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*

class BluetoothManager(private val context: Context) {
    
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var bluetoothSocket: BluetoothSocket? = null
    private var bluetoothServerSocket: BluetoothServerSocket? = null
    private var inputStream: InputStream? = null
    private var outputStream: OutputStream? = null
    
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    var onMessageReceived: ((String) -> Unit)? = null
    var onConnectionStateChanged: ((Boolean) -> Unit)? = null
    
    companion object {
        private const val APP_NAME = "CoCaro"
        private val APP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    }
    
    fun isBluetoothAvailable(): Boolean {
        return bluetoothAdapter != null
    }
    
    fun isBluetoothEnabled(): Boolean {
        return bluetoothAdapter?.isEnabled == true
    }
    
    fun getPairedDevices(): List<BluetoothDevice> {
        if (!hasBluetoothPermissions()) return emptyList()
        return bluetoothAdapter?.bondedDevices?.toList() ?: emptyList()
    }
    
    private fun hasBluetoothPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            android.Manifest.permission.BLUETOOTH_CONNECT
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    fun startServer(): Boolean {
        return try {
            if (!hasBluetoothPermissions()) return false
            
            bluetoothServerSocket = bluetoothAdapter?.listenUsingRfcommWithServiceRecord(
                APP_NAME, APP_UUID
            )
            
            scope.launch {
                try {
                    bluetoothSocket = bluetoothServerSocket?.accept()
                    bluetoothServerSocket?.close()
                    setupStreams()
                    onConnectionStateChanged?.invoke(true)
                    startListening()
                } catch (e: IOException) {
                    onConnectionStateChanged?.invoke(false)
                }
            }
            true
        } catch (e: IOException) {
            false
        }
    }
    
    fun connectToDevice(device: BluetoothDevice): Boolean {
        return try {
            if (!hasBluetoothPermissions()) return false
            
            scope.launch {
                try {
                    bluetoothSocket = device.createRfcommSocketToServiceRecord(APP_UUID)
                    bluetoothAdapter?.cancelDiscovery()
                    bluetoothSocket?.connect()
                    setupStreams()
                    onConnectionStateChanged?.invoke(true)
                    startListening()
                } catch (e: IOException) {
                    onConnectionStateChanged?.invoke(false)
                }
            }
            true
        } catch (e: IOException) {
            false
        }
    }
    
    private fun setupStreams() {
        bluetoothSocket?.let { socket ->
            inputStream = socket.inputStream
            outputStream = socket.outputStream
        }
    }
    
    private fun startListening() {
        scope.launch {
            val buffer = ByteArray(1024)
            while (true) {
                try {
                    val bytes = inputStream?.read(buffer) ?: break
                    if (bytes > 0) {
                        val message = String(buffer, 0, bytes)
                        onMessageReceived?.invoke(message)
                    }
                } catch (e: IOException) {
                    break
                }
            }
        }
    }
    
    fun sendMessage(message: String): Boolean {
        return try {
            outputStream?.write(message.toByteArray())
            true
        } catch (e: IOException) {
            false
        }
    }
    
    fun disconnect() {
        try {
            bluetoothSocket?.close()
            bluetoothServerSocket?.close()
            inputStream?.close()
            outputStream?.close()
            onConnectionStateChanged?.invoke(false)
        } catch (e: IOException) {
            // Ignore
        }
    }
    
    fun cleanup() {
        disconnect()
        scope.cancel()
    }
}
