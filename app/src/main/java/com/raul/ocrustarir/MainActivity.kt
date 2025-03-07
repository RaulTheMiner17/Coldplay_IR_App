package com.raul.ocrustarir

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.hardware.usb.UsbConstants
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbEndpoint
import android.hardware.usb.UsbManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.Executors
import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.core.app.ActivityCompat
import kotlin.math.sqrt
import org.apache.commons.math3.transform.FastFourierTransformer
import org.apache.commons.math3.transform.DftNormalization
import org.apache.commons.math3.transform.TransformType
import org.apache.commons.math3.complex.Complex

class MainActivity : AppCompatActivity() {

    data class CommandData(val name: String, val hexCode: String, val color: Int)

    // Your command data (using Int for colors)
    private val commands = arrayOf(
        CommandData("Random Flash", "", Color.DKGRAY),
        CommandData("Random Fade 1", "", Color.DKGRAY),
        CommandData("Random Fade 2", "", Color.DKGRAY),
        CommandData("Random Fade 3", "", Color.DKGRAY),
        CommandData("Random Fade 4", "", Color.DKGRAY),
        CommandData("Random Fade 5", "", Color.DKGRAY),
        CommandData("Random Fade 6", "", Color.DKGRAY),

        CommandData("Red Flash", "57572b2b2b572baf2b83572b2b2b2b5757af57af2b", Color.RED),
        CommandData("Red Fade 1", "57572b2b2b572baf2b83572b2b2b2b5757af57af2b832b2b572b2b832baf57af2b", Color.RED),
        CommandData("Red Fade 2", "57572b2b2b572baf2b83572b2b2b2b5757af57af2b572b57572b2b2b2b2b2baf57af2b", Color.RED),
        CommandData("Red Fade 3", "57572b2b2b572baf2b83572b2b2b2b5757af57af2b832b2b572b2b2b2b2b2baf57af2b", Color.RED),
        CommandData("Red Fade 4", "57572b2b2b572baf2b83572b2b2b2b5757af57af2baf57572b832b2b2b5757af2b", Color.RED),
        CommandData("Red Fade 5", "57572b2b2b572baf2b83572b2b2b2b5757af57af2b832b2b572b2b5757af57af2b", Color.RED),
        CommandData("Red Fade 6", "57572b2b2b572baf2b83572b2b2b2b5757af57af2b572b57572b2b5757af57af2b", Color.RED),
        CommandData("Green Flash", "57572b2b2b2b57af2b572b572b572b5757af57af2b", Color.GREEN),
        CommandData("Green Fade 1", "57572b2b2b2b57af2b572b572b572b5757af57af2b832b2b572b2b832baf57af2b", Color.GREEN),
        CommandData("Green Fade 2", "57572b2b2b2b57af2b572b572b572b5757af57af2b572b57572b2b2b2b2b2baf57af2b", Color.GREEN),
        CommandData("Green Fade 3", "57572b2b2b2b57af2b572b572b572b5757af57af2b832b2b572b2b2b2b2b2baf57af2b", Color.GREEN),
        CommandData("Green Fade 4", "57572b2b2b2b57af2b572b572b572b5757af57af2baf57572b832b2b2b5757af2b", Color.GREEN),
        CommandData("Green Fade 5", "57572b2b2b2b57af2b572b572b572b5757af57af2b832b2b572b2b5757af57af2b", Color.GREEN),
        CommandData("Green Fade 6", "57572b2b2b2b57af2b572b572b572b5757af57af2b572b57572b2b5757af57af2b", Color.GREEN),
        CommandData("Light Green Flash", "2b2b2b2b575757af2b572b572b572b2b2b572b832b572b572b", 0xFF90EE90.toInt()), // Light Green
        CommandData("Light Green Fade 1", "2b2b2b2b575757af2b572b572b572b2b2b572b832b572b572b832b2b572b2b832baf57af2b", 0xFF90EE90.toInt()),
        CommandData("Light Green Fade 2", "2b2b2b2b575757af2b572b572b572b2b2b572b832b572b572b572b57572b2b2b2b2b2baf57af2b", 0xFF90EE90.toInt()),
        CommandData("Light Green Fade 3", "2b2b2b2b575757af2b572b572b572b2b2b572b832b572b572b832b2b572b2b2b2b2b2baf57af2b", 0xFF90EE90.toInt()),
        CommandData("Light Green Fade 4", "2b2b2b2b575757af2b572b572b572b2b2b572b832b572b572baf57572b832b2b2b5757af2b", 0xFF90EE90.toInt()),
        CommandData("Light Green Fade 5", "2b2b2b2b575757af2b572b572b572b2b2b572b832b572b572b832b2b572b2b5757af57af2b", 0xFF90EE90.toInt()),
        CommandData("Light Green Fade 6", "2b2b2b2b575757af2b572b572b572b2b2b572b832b572b572b572b57572b2b5757af57af2b", 0xFF90EE90.toInt()),
        CommandData("Yellow Green Flash", "2b2b2b2b575757af2b832b2b2b572b572b2b2b8357af2b", 0xFFADFF2F.toInt()), // Yellow-Green
        CommandData("Yellow Green Fade 1", "2b2b2b2b575757af2b832b2b2b572b572b2b2b8357af2b832b2b572b2b832baf57af2b", 0xFFADFF2F.toInt()),
        CommandData("Yellow Green Fade 2", "2b2b2b2b575757af2b832b2b2b572b572b2b2b8357af2b572b57572b2b2b2b2b2baf57af2b", 0xFFADFF2F.toInt()),
        CommandData("Yellow Green Fade 3", "2b2b2b2b575757af2b832b2b2b572b572b2b2b8357af2b832b2b572b2b2b2b2b2baf57af2b", 0xFFADFF2F.toInt()),
        CommandData("Yellow Green Fade 4", "2b2b2b2b575757af2b832b2b2b572b572b2b2b8357af2baf57572b832b2b2b5757af2b", 0xFFADFF2F.toInt()),
        CommandData("Yellow Green Fade 5", "2b2b2b2b575757af2b832b2b2b572b572b2b2b8357af2b832b2b572b2b5757af57af2b", 0xFFADFF2F.toInt()),
        CommandData("Yellow Green Fade 6", "2b2b2b2b575757af2b832b2b2b572b572b2b2b8357af2b572b57572b2b5757af57af2b", 0xFFADFF2F.toInt()),
        CommandData("Blue Flash", "2b2b2b83572b2baf2b572b2b2b57572b2b572b2b2b2b2b2b2b832b", Color.BLUE),
        CommandData("Blue Fade 1", "2b2b2b83572b2baf2b572b2b2b57572b2b572b2b2b2b2b2b2b832b832b2b572b2b832baf57af2b", Color.BLUE),
        CommandData("Blue Fade 2", "2b2b2b83572b2baf2b572b2b2b57572b2b572b2b2b2b2b2b2b832b572b57572b2b2b2b2b2baf57af2b", Color.BLUE),
        CommandData("Blue Fade 3", "2b2b2b83572b2baf2b572b2b2b57572b2b572b2b2b2b2b2b2b832b832b2b572b2b2b2b2b2baf57af2b", Color.BLUE),
        CommandData("Blue Fade 4", "2b2b2b83572b2baf2b572b2b2b57572b2b572b2b2b2b2b2b2b832baf57572b832b2b2b5757af2b", Color.BLUE),
        CommandData("Blue Fade 5", "2b2b2b83572b2baf2b572b2b2b57572b2b572b2b2b2b2b2b2b832b832b2b572b2b5757af57af2b", Color.BLUE),
        CommandData("Blue Fade 6", "2b2b2b83572b2baf2b572b2b2b57572b2b572b2b2b2b2b2b2b832b572b57572b2b5757af57af2b", Color.BLUE),
        CommandData("Light Blue Flash", "2b2b2b83572b2baf2b572b2b2b572b2b2b2b2b2b2b2b57572b832b", Color.CYAN),
        CommandData("Light Blue Fade 1", "2b2b2b83572b2baf2b572b2b2b572b2b2b2b2b2b2b2b57572b832b832b2b572b2b832baf57af2b", Color.CYAN),
        CommandData("Light Blue Fade 2", "2b2b2b83572b2baf2b572b2b2b572b2b2b2b2b2b2b2b57572b832b572b57572b2b2b2b2b2baf57af2b", Color.CYAN),
        CommandData("Light Blue Fade 3", "2b2b2b83572b2baf2b572b2b2b572b2b2b2b2b2b2b2b57572b832b832b2b572b2b2b2b2b2baf57af2b", Color.CYAN),
        CommandData("Light Blue Fade 4", "2b2b2b83572b2baf2b572b2b2b572b2b2b2b2b2b2b2b57572b832baf57572b832b2b2b5757af2b", Color.CYAN),
        CommandData("Light Blue Fade 5", "2b2b2b83572b2baf2b572b2b2b572b2b2b2b2b2b2b2b57572b832b832b2b572b2b5757af57af2b", Color.CYAN),
        CommandData("Light Blue Fade 6", "2b2b2b83572b2baf2b572b2b2b572b2b2b2b2b2b2b2b57572b832b572b57572b2b5757af57af2b", Color.CYAN),
        CommandData("Magenta Flash", "2b2b2b2b575757af2b8357832b2b2b2b57832b2b2b832b", Color.MAGENTA),
        CommandData("Magenta Fade 1", "2b2b2b2b575757af2b8357832b2b2b2b57832b2b2b832b832b2b572b2b832baf57af2b", Color.MAGENTA),
        CommandData("Magenta Fade 2", "2b2b2b2b575757af2b8357832b2b2b2b57832b2b2b832b572b57572b2b2b2b2b2b2baf57af2b", Color.MAGENTA),
        CommandData("Magenta Fade 3", "2b2b2b2b575757af2b8357832b2b2b2b57832b2b2b832b832b2b572b2b2b2b2b2baf57af2b", Color.MAGENTA),
        CommandData("Magenta Fade 4", "2b2b2b2b575757af2b8357832b2b2b2b57832b2b2b832baf57572b832b2b2b5757af2b", Color.MAGENTA),
        CommandData("Magenta Fade 5", "2b2b2b2b575757af2b8357832b2b2b2b57832b2b2b832b832b2b572b2b5757af57af2b", Color.MAGENTA),
        CommandData("Magenta Fade 6", "2b2b2b2b575757af2b8357832b2b2b2b57832b2b2b832b572b57572b2b5757af57af2b", Color.MAGENTA),
        CommandData("Yellow Flash", "57572b2b2b2b57af2b832b2b2b572b5757af57af2b", Color.YELLOW),
        CommandData("Yellow Fade 1", "57572b2b2b2b57af2b832b2b2b572b5757af57af2b832b2b572b2b832baf57af2b", Color.YELLOW),
        CommandData("Yellow Fade 2", "57572b2b2b2b57af2b832b2b2b572b5757af57af2b572b57572b2b2b2b2b2baf57af2b", Color.YELLOW),
        CommandData("Yellow Fade 3", "57572b2b2b2b57af2b832b2b2b572b5757af57af2b832b2b572b2b2b2b2b2baf57af2b", Color.YELLOW),
        CommandData("Yellow Fade 4", "57572b2b2b2b57af2b832b2b2b572b5757af57af2baf57572b832b2b2b5757af2b", Color.YELLOW),
        CommandData("Yellow Fade 5", "57572b2b2b2b57af2b832b2b2b572b5757af57af2b832b2b572b2b5757af57af2b", Color.YELLOW),
        CommandData("Yellow Fade 6", "57572b2b2b2b57af2b832b2b2b572b5757af57af2b572b57572b2b5757af57af2b", Color.YELLOW),
        CommandData("Pink Flash", "2b2b2b83572b2baf2b572b2b2baf57572b832b2b2b832b", 0xFFFFC0CB.toInt()), // Pink
        CommandData("Pink Fade 1", "2b2b2b83572b2baf2b572b2b2baf57572b832b2b2b832b832b2b572b2b832baf57af2b", 0xFFFFC0CB.toInt()),
        CommandData("Pink Fade 2", "2b2b2b83572b2baf2b572b2b2baf57572b832b2b2b832b572b57572b2b2b2b2b2baf57af2b", 0xFFFFC0CB.toInt()),
        CommandData("Pink Fade 3", "2b2b2b83572b2baf2b572b2b2baf57572b832b2b2b832b832b2b572b2b2b2b2b2baf57af2b", 0xFFFFC0CB.toInt()),
        CommandData("Pink Fade 4", "2b2b2b83572b2baf2b572b2b2baf57572b832b2b2b832baf57572b832b2b2b5757af2b", 0xFFFFC0CB.toInt()),
        CommandData("Pink Fade 5", "2b2b2b83572b2baf2b572b2b2baf57572b832b2b2b832b832b2b572b2b5757af57af2b", 0xFFFFC0CB.toInt()),
        CommandData("Pink Fade 6", "2b2b2b83572b2baf2b572b2b2baf57572b832b2b2b832b572b57572b2b5757af57af2b", 0xFFFFC0CB.toInt()),
        CommandData("Orange Flash", "2b2b2b2b575757af2b832b2b2b572b2b2b572b8357af2b", 0xFFFFA500.toInt()), // Orange
        CommandData("Orange Fade 1", "2b2b2b2b575757af2b832b2b2b572b2b2b572b8357af2b832b2b572b2b832baf57af2b", 0xFFFFA500.toInt()),
        CommandData("Orange Fade 2", "2b2b2b2b575757af2b832b2b2b572b2b2b572b8357af2b572b57572b2b2b2b2b2baf57af2b", 0xFFFFA500.toInt()),
        CommandData("Orange Fade 3", "2b2b2b2b575757af2b832b2b2b572b2b2b572b8357af2b832b2b572b2b2b2b2b2baf57af2b", 0xFFFFA500.toInt()),
        CommandData("Orange Fade 4", "2b2b2b2b575757af2b832b2b2b572b2b2b572b8357af2baf57572b832b2b2b5757af2b", 0xFFFFA500.toInt()),
        CommandData("Orange Fade 5", "2b2b2b2b575757af2b832b2b2b572b2b2b572b8357af2b832b2b572b2b5757af57af2b", 0xFFFFA500.toInt()),
        CommandData("Orange Fade 6", "2b2b2b2b575757af2b832b2b2b572b2b2b572b8357af2b572b57572b2b5757af57af2b", 0xFFFFA500.toInt()),
        CommandData("Turquoise Flash", "2b2b2b2b575757af2b572b2b57572b5757af2b2b2b832b", 0xFF40E0D0.toInt()), // Turquoise
        CommandData("Turquoise Fade 1", "2b2b2b2b575757af2b572b2b57572b5757af2b2b2b832b832b2b572b2b832baf57af2b", 0xFF40E0D0.toInt()),
        CommandData("Turquoise Fade 2", "2b2b2b2b575757af2b572b2b57572b5757af2b2b2b832b572b57572b2b2b2b2b2baf57af2b", 0xFF40E0D0.toInt()),
        CommandData("Turquoise Fade 3", "2b2b2b2b575757af2b572b2b57572b5757af2b2b2b832b832b2b572b2b2b2b2b2baf57af2b", 0xFF40E0D0.toInt()),
        CommandData("Turquoise Fade 4", "2b2b2b2b575757af2b572b2b57572b5757af2b2b2b832baf57572b832b2b2b5757af2b", 0xFF40E0D0.toInt()),
        CommandData("Turquoise Fade 5", "2b2b2b2b575757af2b572b2b57572b5757af2b2b2b832b832b2b572b2b5757af57af2b", 0xFF40E0D0.toInt()),
        CommandData("Turquoise Fade 6", "2b2b2b2b575757af2b572b2b57572b5757af2b2b2b832b572b57572b2b5757af57af2b", 0xFF40E0D0.toInt()),

        )

    private lateinit var usbManager: UsbManager
    private var connection: UsbDeviceConnection? = null
    private var device: UsbDevice? = null
    private var endpointOut: UsbEndpoint? = null
    private var endpointIn: UsbEndpoint? = null
    private lateinit var permissionIntent: PendingIntent

    private lateinit var statusTextView: TextView
    private lateinit var commandContainer: LinearLayout // LinearLayout to hold the buttons
    private lateinit var scrollView: ScrollView
    private lateinit var countEditText: EditText
    private lateinit var delayEditText: EditText
    private lateinit var stopButton: Button


    private val executorService = Executors.newSingleThreadExecutor()
    private val handler = Handler(Looper.getMainLooper()) // For UI updates
    private var sendCount = 0 //counter how many times a button is pressed
    private var currentCommandType: String? = null; // Store the *type* of random command
    private var isSending = false // Flag to track sending state
    private var lastSentCommand: CommandData? = null // Keep track of the last sent command

    // Add class variables
    private var isSyncing = false
    private var audioRecord: AudioRecord? = null
    private var audioThread: Thread? = null
    private lateinit var syncButton: Button


    companion object {
        private const val TAG = "ElkSmartComm"
        private const val ACTION_USB_PERMISSION = "com.raul.ocrustarir.USB_PERMISSION"

        private const val USB_VENDOR_SMTCTL = 0x045c
        private const val USB_PRODUCT_SMTCTL_SMART_EKX4S = 0x0195
        private const val USB_PRODUCT_SMTCTL_SMART_EKX5S_T = 0x0184
        private const val USB_INTERFACE = 0

        private val C_TRANSMIT = byteArrayOf(-1, -1, -1, -1)
        private val C_LEARN = byteArrayOf(-2, -2, -2, -2)
        private val C_STOP = byteArrayOf(-3, -3, -3, -3)
        private val C_IDENTIFY = byteArrayOf(-4, -4, -4, -4)
        private const val REQUEST_RECORD_AUDIO = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Raul's Coldplay Led Controller" // App Title

        // --- UI Setup ---
        scrollView = ScrollView(this)
        commandContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER // Center buttons horizontally
            setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16))
        }

        // Input fields with box style
        countEditText = createBoxedEditText("Count", InputType.TYPE_CLASS_NUMBER, "1")
        delayEditText = createBoxedEditText("Delay (seconds)", InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL, "0.5")

        // Stop Button
        stopButton = Button(this).apply {
            text = "Stop"
            background = createRoundedRectDrawable(Color.RED, dpToPx(20)) // 20dp corner radius
            setTextColor(Color.WHITE)
            setOnClickListener { stopSending() }
            val size = dpToPx(40) // Example: 40dp x 40dp button
            layoutParams = LinearLayout.LayoutParams(size, size).apply {
                marginEnd = dpToPx(8)
            }
        }

        // Input Layout (Row)
        val inputLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            addView(countEditText, LinearLayout.LayoutParams(dpToPx(100), LinearLayout.LayoutParams.WRAP_CONTENT).apply { marginEnd = dpToPx(8) })
            addView(delayEditText, LinearLayout.LayoutParams(dpToPx(150), LinearLayout.LayoutParams.WRAP_CONTENT).apply { marginEnd = dpToPx(8) })
            addView(stopButton, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT))
        }

        // Sync Music Button (Fixed: Placed Below and Centered)
        syncButton = Button(this).apply {
            text = "Sync Music"
            background = createRoundedRectDrawable(Color.BLUE, dpToPx(20))
            setTextColor(Color.WHITE)
            setOnClickListener { toggleAudioSync() }
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dpToPx(16) // Space below input layout
                gravity = Gravity.CENTER_HORIZONTAL // Center it horizontally
            }
        }

        statusTextView = TextView(this).apply {
            gravity = Gravity.CENTER
            textSize = 16f
            setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16))
        }

        // Main Layout
        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            addView(statusTextView)
            addView(inputLayout) // Add input layout
            addView(syncButton) // FIXED: Added below input layout and centered
            addView(scrollView) // Add the ScrollView containing buttons
        }

        createCommandButtons()

        scrollView.addView(commandContainer)
        setContentView(mainLayout)


        // --- End UI Setup ---

        usbManager = getSystemService(Context.USB_SERVICE) as UsbManager
        permissionIntent = PendingIntent.getBroadcast(this, 0, Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_IMMUTABLE)

        val filter = IntentFilter().apply {
            addAction(ACTION_USB_PERMISSION)
            addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
            addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(usbReceiver, filter, RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(usbReceiver, filter)
        }


        findAndConnectDevice()
    }
    private fun checkAudioPermission() {
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_RECORD_AUDIO)
        } else {
            startAudioSync()
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_RECORD_AUDIO -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startAudioSync()
                } else {
                    statusTextView.text = "Microphone access required for music sync"
                }
            }
        }
    }
    private fun nextPowerOf2(n: Int): Int {
        var power = 1
        while (power < n) {
            power = power shl 1
        }
        return power
    }
    // Audio sync toggle function
    private fun startAudioSync() {
        val sampleRate = 44100
        val bufferSize = AudioRecord.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        if (bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE) {
            statusTextView.text = "Invalid audio buffer size"
            return
        }

        // Calculate the nearest power of 2
        val fftSize = nextPowerOf2(bufferSize)

        try {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize
            )

            if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
                statusTextView.text = "Audio recorder not initialized"
                return
            }

            audioRecord?.startRecording()
            isSyncing = true
            statusTextView.text = "Listening for beats..."

        } catch (e: Exception) {
            Log.e(TAG, "Audio init failed", e)
            statusTextView.text = "Audio initialization failed: ${e.message}"
            return
        }

        audioThread = Thread {
            val buffer = ShortArray(bufferSize)
            val transformer = FastFourierTransformer(DftNormalization.STANDARD)
            val fftBuffer = DoubleArray(fftSize) // Use padded size
            var lastBeat = 0L
            val cooldown = 100

            // Adjustable parameters
            val threshold = 600000.0 // Increased threshold
            val minEnergy = 500000.0 // Minimum energy required for a beat
            val bassRangeStart = 0 // Start of bass range (inclusive)
            val bassRangeEnd = 10 // End of bass range (exclusive)

            while (isSyncing) {
                try {
                    val read = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                    if (read > 0) {
                        // Convert audio samples to doubles for FFT
                        for (i in 0 until read) {
                            fftBuffer[i] = buffer[i].toDouble()
                        }

                        // Pad the remaining buffer with zeros
                        for (i in read until fftSize) {
                            fftBuffer[i] = 0.0
                        }

                        // Perform FFT
                        val fftResult = transformer.transform(fftBuffer, TransformType.FORWARD)

                        // Calculate magnitude for each frequency bin
                        val magnitudes = DoubleArray(fftSize / 2)
                        for (i in 0 until fftSize / 2) {
                            val real = fftResult[i].real
                            val imaginary = fftResult[i].imaginary
                            magnitudes[i] = sqrt(real * real + imaginary * imaginary)
                        }

                        // Detect beats in specific frequency ranges (e.g., bass)
                        val bassRange = magnitudes.slice(bassRangeStart until bassRangeEnd)
                        val bassEnergy = bassRange.sum()

                        Log.d(TAG, "Bass Energy: $bassEnergy") // Log bass energy for debugging

                        if (bassEnergy > threshold && bassEnergy > minEnergy && System.currentTimeMillis() - lastBeat > cooldown) {
                            lastBeat = System.currentTimeMillis()
                            Log.d(TAG, "Beat detected! Bass energy: $bassEnergy")

                            handler.post {
                                val validCommands = commands.filter {
                                    it.hexCode.isNotEmpty() && it.name.contains("Flash")
                                }
                                if (validCommands.isNotEmpty()) {
                                    val command = validCommands.random()
                                    Log.d(TAG, "Sending command: ${command.name}")
                                    sendCode(command.hexCode)
                                } else {
                                    Log.e(TAG, "No valid commands found")
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Audio processing error", e)
                }
            }
        }.apply { start() }
    }

    private fun toggleAudioSync() {
        if (isSyncing) {
            stopAudioSync()
            syncButton.text = "Sync Music"
        } else {
            checkAudioPermission()
            syncButton.text = "Stop Syncing"
        }
    }

    private fun stopAudioSync() {
        isSyncing = false
        audioThread?.join()
        audioRecord?.apply {
            stop()
            release()
        }
        audioRecord = null
    }


    // Helper function to create EditText with box style
    private fun createBoxedEditText(hint: String, inputType: Int, defaultValue: String): EditText {
        return EditText(this).apply {
            this.hint = hint
            this.inputType = inputType
            gravity = Gravity.CENTER
            setText(defaultValue)
            background = createRoundedRectDrawable(Color.DKGRAY, dpToPx(8)) // Light gray box
            setPadding(dpToPx(12), dpToPx(12), dpToPx(12), dpToPx(12)) // Add padding inside the box
        }
    }
    private fun createCommandButtons() {
        commandContainer.removeAllViews() // Clear existing buttons

        val buttonWidth = dpToPx(97)
        val buttonHeight = dpToPx(80)
        val buttonMargin = dpToPx(8)
        val cornerRadius = dpToPx(20)

        val buttonLayoutParams = LinearLayout.LayoutParams(buttonWidth, buttonHeight).apply {
            setMargins(buttonMargin, buttonMargin, buttonMargin, buttonMargin)
        }

        // --- Button Creation ---
        var currentRowLayout: LinearLayout? = null

        for (i in commands.indices) {
            val command = commands[i]

            // Create a new row layout for every 3 buttons
            if (i % 3 == 0 ) {
                currentRowLayout = LinearLayout(this).apply {
                    orientation = LinearLayout.HORIZONTAL
                    gravity = Gravity.CENTER
                }
                commandContainer.addView(currentRowLayout)
            }


            val button = Button(this).apply {
                text = command.name
                background = createRoundedRectDrawable(command.color, cornerRadius)
                setTextColor(if (isColorDark(command.color)) Color.WHITE else Color.BLACK)

                // Set onClickListener based on button type
                if (command.name.startsWith("Random")) {
                    // Random button: Store the *type*, not the hexCode.
                    val type = command.name.substringAfter("Random ")
                    setOnClickListener {
                        if (!isSending) {
                            isSending = true
                            sendCount = 0
                            currentCommandType = type  // Store the TYPE
                            sendCodeWithCountAndDelay("") // Empty string - we'll pick randomly
                        }
                    }
                } else {
                    // Regular button:  NO hexCode needed here.
                    setOnClickListener {
                        if (!isSending) {
                            isSending = true
                            sendCount = 0
                            currentCommandType = null // Clear any previous random type
                            sendCodeWithCountAndDelay(command.hexCode) //pass hexcode
                        }
                    }
                }

                layoutParams = buttonLayoutParams
                gravity = Gravity.CENTER
            }
            currentRowLayout?.addView(button)
        }
    }




    private fun sendRandomCommand() {
        if (!isSending) {
            return
        }

        if (currentCommandType == null) {
            isSending = false
            return
        }

        val filteredCommands = commands.filter {
            it.name.contains(currentCommandType!!, ignoreCase = true) && it.hexCode.isNotEmpty()
        }

        if (filteredCommands.isNotEmpty()) {
            // Filter out the last sent command, if any, and if there are other options
            val availableCommands = filteredCommands.filter { it != lastSentCommand }
            val commandToSend = if (availableCommands.isNotEmpty()) {
                availableCommands.random() // Select from the *filtered* list
            } else {
                // If there are no other options, allow repeating the last command
                filteredCommands.random()
            }

            lastSentCommand = commandToSend // Update lastSentCommand
            sendCode(commandToSend.hexCode)
        } else {
            statusTextView.text = "No commands found for type: $currentCommandType"
            isSending = false
        }
    }

    private fun sendCodeWithCountAndDelay(hexCode: String) {
        //Keep this, but now hexcode is not always used
        val count = countEditText.text.toString().toIntOrNull() ?: 1
        val delaySeconds = delayEditText.text.toString().toDoubleOrNull() ?: 0.5
        val delayMillis = (delaySeconds * 1000).toLong()

        if (sendCount < count && isSending) {
            //Here, is where we should use random command or normal.
            if(currentCommandType != null)
            {
                //Call sendRandomCommand with no arguments
                sendRandomCommand();
            }
            else {
                sendCode(hexCode) // Send the provided hexCode
            }
            sendCount++
            if (sendCount < count && isSending) {
                handler.postDelayed({
                    sendCodeWithCountAndDelay(hexCode)
                }, delayMillis)
            } else {
                isSending = false
                currentCommandType = null // Reset the stored type
            }
        } else {
            isSending = false
            currentCommandType = null; //ensure to reset
        }
    }
    private fun stopSending() {
        isSending = false // Set the flag to stop sending
        handler.removeCallbacksAndMessages(null) // Remove any pending callbacks
        statusTextView.text = "Sending stopped."
        sendCount = 0;
        currentCommandType = null; //reset type
        lastSentCommand = null;
    }

    private fun createRoundedRectDrawable(color: Int, radius: Int): GradientDrawable {
        val drawable = GradientDrawable()
        drawable.shape = GradientDrawable.RECTANGLE
        drawable.setColor(color)
        drawable.cornerRadius = radius.toFloat()
        drawable.setStroke(dpToPx(1), if (isColorDark(color)) Color.WHITE else Color.BLACK)
        return drawable
    }

    private fun isColorDark(color: Int): Boolean {
        val darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255
        return darkness >= 0.5
    }

    private fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), resources.displayMetrics).toInt()
    }
    private fun findAndConnectDevice() {
        val deviceList = usbManager.deviceList
        for (device in deviceList.values) {
            if (device.vendorId == USB_VENDOR_SMTCTL && (device.productId == USB_PRODUCT_SMTCTL_SMART_EKX4S || device.productId == USB_PRODUCT_SMTCTL_SMART_EKX5S_T)) {
                this.device = device
                requestDevicePermission()
                return
            }
        }
        statusTextView.text = "Device not found."
    }

    private fun requestDevicePermission() {
        device?.let {
            if (!usbManager.hasPermission(it)) {
                usbManager.requestPermission(it, permissionIntent)
            } else {
                connectDevice()
            }
        }
    }
    private fun hexStringToByteArray(s: String): ByteArray? {
        val cleanString = s.replace("\\s".toRegex(), "").lowercase() //Remove spaces, lowercase
        val len = cleanString.length
        if (len % 2 != 0) {
            return null
        }
        val data = ByteArray(len / 2)
        try {
            for (i in 0 until len step 2) {
                data[i / 2] = ((Character.digit(cleanString[i], 16) shl 4) + Character.digit(cleanString[i + 1],16)).toByte()
            }
        } catch (e: NumberFormatException) {
            Log.e(TAG, "Invalid hex string: $s", e)
            return null
        }

        return data
    }

    private fun byteArrayToHexString(bytes: ByteArray): String {
        val sb = StringBuilder()
        for (b in bytes) {
            sb.append(String.format("%02x", b))
        }
        return sb.toString()
    }

    private val usbReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                ACTION_USB_PERMISSION -> {
                    val device: UsbDevice? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableExtra(UsbManager.EXTRA_DEVICE, UsbDevice::class.java)
                    } else {
                        @Suppress("DEPRECATION")
                        intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)
                    }
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        device?.let {
                            connectDevice()
                        }
                    } else {
                        statusTextView.text = "Permission denied for device $device"
                    }
                }
                UsbManager.ACTION_USB_DEVICE_ATTACHED -> {
                    findAndConnectDevice()
                }
                UsbManager.ACTION_USB_DEVICE_DETACHED -> {
                    val detachedDevice: UsbDevice? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableExtra(UsbManager.EXTRA_DEVICE, UsbDevice::class.java)
                    } else {
                        @Suppress("DEPRECATION")
                        intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)
                    }

                    if (detachedDevice != null && detachedDevice == this@MainActivity.device) {
                        disconnectDevice()
                    }
                }
            }
        }
    }

    private fun connectDevice() {
        device?.let { dev ->
            val usbInterface = dev.getInterface(USB_INTERFACE)

            if (usbManager.openDevice(dev)?.claimInterface(usbInterface, true) == false) {
                statusTextView.text = "Could not claim interface. Kernel driver attached?"
                Log.e(TAG, "Could not claim interface. Kernel driver attached?")
                return
            }

            connection = usbManager.openDevice(dev)
            if (connection == null) {
                statusTextView.text = "Could not open device connection"
                Log.e(TAG, "Could not open device connection")
                return
            }

            connection?.claimInterface(usbInterface, true)

            for (i in 0 until usbInterface.endpointCount) {
                val endpoint = usbInterface.getEndpoint(i)
                if (endpoint.type == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                    if (endpoint.direction == UsbConstants.USB_DIR_OUT) {
                        endpointOut = endpoint
                    } else if (endpoint.direction == UsbConstants.USB_DIR_IN) {
                        endpointIn = endpoint
                    }
                }
            }

            if (endpointOut != null && endpointIn != null) {
                statusTextView.text = "Device connected."
                flushInputBuffer()
                sendIdentify()
            } else {
                statusTextView.text = "Endpoints not found."
                Log.e(TAG, "Endpoints not found.")
                connection!!.releaseInterface(usbInterface)
                connection!!.close()
                connection = null
            }
        }
    }
    private fun flushInputBuffer() {
        if (connection == null || endpointIn == null) {
            Log.e(TAG, "flushInputBuffer - connection or endpointIn is null")
            return
        }
        val dummyBuffer = ByteArray(64)
        var readResult: Int
        do {
            readResult = connection!!.bulkTransfer(endpointIn, dummyBuffer, dummyBuffer.size, 100)
            if (readResult >= 0) {
                Log.d(TAG, "flushInputBuffer: Cleared $readResult bytes from input buffer")
            }
        } while (readResult >= 0)
    }

    private fun disconnectDevice() {
        connection?.apply {
            releaseInterface(device?.getInterface(USB_INTERFACE))
            close()
        }
        connection = null
        device = null
        endpointIn = null
        endpointOut = null
        statusTextView.text = "Device disconnected."
    }

    private fun sendIdentify() {
        executorService.execute {
            if (device == null || connection == null || endpointOut == null || endpointIn == null) {
                Log.e(TAG, "sendIdentify: device/connection/endpoints are null")
                return@execute
            }

            try {
                // Clear any pending IN transfers (important for correct identification)
                flushInputBuffer()

                // Send identification command
                var sent = connection!!.bulkTransfer(endpointOut, C_IDENTIFY, C_IDENTIFY.size, 500)
                if (sent < 0) {
                    Log.e(TAG, "sendIdentify: Error sending identify command: $sent")
                    handler.post { statusTextView.text = "Error sending identify command $sent" }
                    return@execute
                }

                // Receive identification response
                val identifyBuffer = ByteArray(64)
                val received = connection!!.bulkTransfer(endpointIn, identifyBuffer, identifyBuffer.size, 1000)
                if (received < 0) {
                    Log.e(TAG, "sendIdentify: Error receiving identify response: $received")
                    handler.post { statusTextView.text = "Error receiving identify command $sent" }
                    return@execute
                }

                if (received >= 6 && identifyBuffer.copyOfRange(0, 4).contentEquals(C_IDENTIFY) &&
                    identifyBuffer[4] == 0x70.toByte() && identifyBuffer[5] == 0x01.toByte()
                ) {
                    Log.d(TAG, "sendIdentify: Device identified successfully.")
                } else {
                    Log.e(TAG, "sendIdentify: Device identification failed. Response: ${byteArrayToHexString(identifyBuffer.copyOfRange(0, received))}")
                    handler.post { statusTextView.text = "Device identification failed." }
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error during sendIdentify", e)
                handler.post { statusTextView.text = "Error during identify: ${e.message}" }
            }
        }
    }


    private fun sendCode(hexCode: String) {
        val codeBytes = hexStringToByteArray(hexCode)

        if (codeBytes == null) {
            statusTextView.text = "Invalid hex code."
            return
        }

        executorService.execute {
            Log.d(TAG, "sendCode() called. hexCode: $hexCode")

            // Add connection validity check
            if (!validateUsbConnection()) {
                Log.e(TAG, "Connection invalid, attempting reconnect")
                runOnUiThread { statusTextView.text = "Reconnecting device..." }
                disconnectDevice()
                findAndConnectDevice()
                if (!validateUsbConnection()) {
                    runOnUiThread { statusTextView.text = "Device reconnect failed" }
                    return@execute
                }
            }

            try {
                // Force flush both buffers before each transmission
                flushInputBuffer()
                flushOutputBuffer()

                val pulses = decodeLearned(codeBytes)
                val compressed = compressPulses(pulses)
                val message = prepareTransmitMessage(compressed)
                sendTransmitMessage(message)

                // Explicitly flush after transmission
                flushInputBuffer()

            } catch (e: Exception) {
                Log.e(TAG, "Error sending code", e)
                runOnUiThread { statusTextView.text = "Error: ${e.message}" }
            }
        }
    }

    // New helper functions
    private fun validateUsbConnection(): Boolean {
        return connection != null &&
                endpointOut != null &&
                endpointIn != null &&
                connection!!.claimInterface(device!!.getInterface(USB_INTERFACE), true)
    }

    private fun flushOutputBuffer() {
        if (connection == null || endpointOut == null) return
        val dummy = ByteArray(64)
        connection!!.bulkTransfer(endpointOut, dummy, dummy.size, 100)
    }

    private fun prepareTransmitMessage(compressedData: ByteArray): ByteArray {
        val buffer = ByteBuffer.allocate(compressedData.size + 9)  // 4 + 3 + 2 + data
        buffer.order(ByteOrder.BIG_ENDIAN)

        buffer.put(C_TRANSMIT)

        val frequency = 38000L + 0x7ffff
        buffer.put(mangle((frequency.toInt() shr 8).toByte()))
        buffer.put(mangle((frequency.toInt() shr 16).toByte()))
        buffer.put(mangle(frequency.toInt().toByte()))

        // Corrected size handling:  Handle both bytes of the size.
        buffer.put(mangle((compressedData.size shr 8).toByte())) // High byte
        buffer.put(mangle((compressedData.size).toByte()))        // Low byte

        buffer.put(compressedData)

        return buffer.array()
    }

    private fun sendTransmitMessage(message: ByteArray) {
        var offset = 0
        Log.d(TAG, "Full message to send: ${byteArrayToHexString(message)}")

        connection?.let { conn ->
            endpointOut?.let { endOut ->
                var attempts = 0
                val maxAttempts = 3 // Retry up to 3 times

                while (offset < message.size && attempts < maxAttempts) {
                    val chunkSize = minOf(62, message.size - offset)
                    val chunk = ByteArray(if (chunkSize == 62) 63 else chunkSize)
                    System.arraycopy(message, offset, chunk, 0, chunkSize)

                    if (chunkSize == 62) {
                        chunk[62] = checksum(chunk, 62)
                    }

                    val sent = connection!!.bulkTransfer(endpointOut, chunk, chunk.size, 500)

                    if (sent >= 0) {
                        Log.d(TAG, "Chunk sent. Bytes sent: $sent")
                        offset += chunkSize
                        attempts = 0 // Reset attempts on success
                        Thread.sleep(2)
                    } else {
                        Log.e(TAG, "Error sending chunk. bulkTransfer returned: $sent, Attempt: ${attempts + 1}")
                        attempts++
                        Thread.sleep(50) // Wait a bit before retrying

                        if (attempts >= maxAttempts) {
                            Log.e(TAG, "Max attempts reached. Stopping transmission.")
                            handler.post { statusTextView.text = "Failed to send after multiple attempts." }
                            return@let // Exit the sending loop
                        }
                    }
                }
                if (offset == message.size) {
                    handler.post { statusTextView.text = "Code sent successfully." }
                } else {
                    handler.post { statusTextView.text = "Incomplete code sent." }
                }
            } ?: run {
                Log.e(TAG, "Endpoint OUT is null during sendTransmitMessage")
                handler.post { statusTextView.text = "Error: Endpoint OUT is null" }
                return
            }
        } ?: run {
            Log.e(TAG, "Connection is null during sendTransmitMessage")
            handler.post { statusTextView.text = "Error: Connection is null" }
            return
        }
    }

    private fun learnCode() {
        executorService.execute {
            if (device == null || connection == null || endpointOut == null || endpointIn == null) {
                handler.post { statusTextView.text = "Device not connected." }
                Log.e(TAG, "learnCode: device/connection/endpoints are null")
                return@execute
            }

            try {
                flushInputBuffer()
                var sent = connection!!.bulkTransfer(endpointOut, C_LEARN, C_LEARN.size, 1000)
                if (sent < 0) {
                    handler.post { statusTextView.text = "Error sending learn command." }
                    Log.e(TAG, "learnCode: Error sending learn command: $sent")
                    return@execute
                }

                val receivedData = receiveLearnData()

                if (receivedData != null) {
                    val learnedCode = byteArrayToHexString(receivedData)
                    handler.post {
                        // Instead of setting to an EditText, we'll log it and update the status.
                        // codeEditText.setText(learnedCode)  // Remove this line
                        statusTextView.text = "Code learned: $learnedCode" // Show the learned code
                    }
                    Log.d(TAG, "Learned Code: $learnedCode")

                    try {
                        val decodedPulses = decodeLearned(receivedData)
                        Log.d(TAG, "Decoded Pulses $decodedPulses")
                    } catch (e: Exception) {
                        Log.e(TAG, "Error During decoding", e)
                    }

                } else {
                    handler.post { statusTextView.text = "Error receiving learned code." }
                    Log.e(TAG, "learnCode: Error receiving code")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error learning code", e)
                handler.post { statusTextView.text = "Error learning code: ${e.message}" }
            }
        }
    }

    private fun receiveLearnData(): ByteArray? {
        val buffer = ByteArray(64)
        var received: Int
        val result = mutableListOf<Byte>()

        received = connection!!.bulkTransfer(endpointIn, buffer, buffer.size, 2000)
        if (received < 6 || !buffer.copyOfRange(0, 4).contentEquals(C_LEARN)) {
            Log.e(TAG, "receiveLearnData: Initial response invalid. Received: $received, Data: ${byteArrayToHexString(buffer.copyOfRange(0, received))}")
            return null
        }

        val expectedSize = (buffer[4].toInt() and 0xFF shl 8) or (buffer[5].toInt() and 0xFF)
        Log.d(TAG, "receiveLearnData: Expected size: $expectedSize")
        result.addAll(buffer.copyOfRange(6, received).toList())

        while (result.size < expectedSize) {
            received = connection!!.bulkTransfer(endpointIn, buffer, buffer.size, 200)

            if (received > 0) {
                result.addAll(buffer.copyOfRange(0, received).toList())
                Log.d(TAG, "receiveLearnData: Received chunk: ${byteArrayToHexString(buffer.copyOfRange(0, received))}, Total: ${result.size}")
            } else if (received != -1) {
                Log.e(TAG, "receiveLearnData: Error receiving chunk: $received")
                return null
            }
            if (received == -1) {
                // The device seems to queue up its output with pauses.
                Log.d(TAG, "learn/recv: LIBUSB_ERROR_TIMEOUT")
            }
        }
        // Send stop command after receiving all data
        val stopSent = connection!!.bulkTransfer(endpointOut, C_STOP, C_STOP.size, 1000)
        if (stopSent < 0) {
            Log.e(TAG, "Error sending stop command after learning: $stopSent")
        }
        return result.toByteArray()
    }

    data class Pulse(val on: Int, val off: Int)

    private fun decodeLearned(data: ByteArray): List<Pulse> {
        val pulses = mutableListOf<Pulse>()
        var i = 0
        while (i < data.size) {
            var on = 0
            while (i < data.size && data[i] == 0xFF.toByte()) {
                on += 4080
                i++
            }
            if (i < data.size) {
                on += (data[i].toInt() and 0xFF) * 16
                i++
            }

            var off = 0
            while (i < data.size && data[i] == 0xFF.toByte()) {
                off += 4080
                i++
            }
            if (i < data.size) {
                off += (data[i].toInt() and 0xFF) * 16
                i++
            }

            pulses.add(Pulse(on, off))
        }
        return pulses
    }

    private fun compressPulses(pulses: List<Pulse>): ByteArray {
        val encoded = mutableListOf<Byte>()

        val counts = mutableMapOf<Pulse, Int>()
        for (pulse in pulses) {
            counts[pulse] = counts.getOrDefault(pulse, 0) + 1
        }

        val sortedCounts = counts.entries.sortedByDescending { it.value }
        val p1 = sortedCounts.getOrNull(0)?.key ?: Pulse(0, 0)
        // Correctly handle the case where there's only one unique pulse.
        val p2 = if (sortedCounts.size >= 2) sortedCounts[1].key else p1

        compressValue(p2.on, encoded)
        compressValue(p2.off, encoded)
        compressValue(p1.on, encoded)
        compressValue(p1.off, encoded)
        encoded.add(0xFF.toByte())
        encoded.add(0xFF.toByte())
        encoded.add(0xFF.toByte())

        for (pulse in pulses) {
            when (pulse) {
                p1 -> encoded.add(0)
                p2 -> encoded.add(1)
                else -> {
                    compressValue(pulse.on, encoded)
                    compressValue(pulse.off, encoded)
                }
            }
        }

        return encoded.toByteArray()
    }

    private fun compressValue(value: Int, output: MutableList<Byte>) {
        var v = value
        if (v <= 2032) {
            // Round to nearest, and clamp to minimum of 2.
            val rounded = (v.toDouble() / 16.0 + 0.5).toInt()
            val clamped = maxOf(2, rounded)
            output.add(clamped.toByte())
        } else {
            do {
                var b = (v and 0x7F).toByte()
                v = v shr 7
                if (v != 0) {
                    b = (b.toInt() or 0x80).toByte()
                }
                output.add(b)
            } while (v != 0)
        }
    }

    private fun mangle(byte: Byte): Byte {
        var reversed = 0
        var value = byte.toInt() and 0xFF

        for (i in 0 until 8) {
            reversed = (reversed shl 1) or (value and 1)
            value = value shr 1
        }
        return (reversed.inv() and 0xFF).toByte()
    }

    private fun checksum(data: ByteArray, length: Int): Byte {
        var sum = 0
        for (i in 0 until length) {
            sum += data[i].toInt() and 0xFF
        }
        return mangle(((sum and 0xF0) or ((sum shr 8) and 0x0F)).toByte())
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(usbReceiver)
        executorService.shutdown()
        disconnectDevice()
    }
}