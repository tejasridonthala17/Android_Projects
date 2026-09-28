package com.example.fragment

import android.Manifest
import android.app.*
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.provider.Settings
import android.text.InputType
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.work.*
import com.google.android.material.button.MaterialButton
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.*

// --- Login Fragment ---

class LoginFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val context = requireContext()
        return LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            padding(16)

            val etUsername = EditText(context).apply { hint = "Username" }
            val etPassword = EditText(context).apply {
                hint = "Password"
                inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            val btnLogin = MaterialButton(context).apply { text = "Login" }

            addView(etUsername, LinearLayout.LayoutParams(-1, -2))
            addView(etPassword, LinearLayout.LayoutParams(-1, -2))
            addView(btnLogin, LinearLayout.LayoutParams(-2, -2))

            btnLogin.setOnClickListener {
                val username = etUsername.text.toString().trim()
                val password = etPassword.text.toString().trim()

                if (username == "teju" && password == "1871") {
                    Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()

                    // Navigate to Login Success Result Fragment
                    parentFragmentManager.beginTransaction()
                        .replace((requireView().parent as ViewGroup).id, LoginSuccessFragment.newInstance(username))
                        .addToBackStack(null)
                        .commit()
                } else {
                    Toast.makeText(context, "Invalid Credentials", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

// --- Login Success / Result Screen ---

class LoginSuccessFragment : Fragment() {
    companion object {
        fun newInstance(username: String): LoginSuccessFragment {
            return LoginSuccessFragment().apply {
                arguments = Bundle().apply {
                    putString("USERNAME", username)
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val context = requireContext()
        val username = arguments?.getString("USERNAME") ?: "User"

        return LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            padding(24)

            val tvSuccessTitle = TextView(context).apply {
                text = "✓ Login Successful!"
                textSize = 24f
                setTextColor(Color.parseColor("#388E3C")) // Success Green
                gravity = Gravity.CENTER
            }

            val tvWelcomeMessage = TextView(context).apply {
                text = "Welcome back, $username!"
                textSize = 18f
                gravity = Gravity.CENTER
                setPadding(0, 16.dpToPx(), 0, 32.dpToPx())
            }

            val btnContinue = MaterialButton(context).apply {
                text = "Go to Dashboard"
                setOnClickListener {
                    (activity as? MainActivity)?.navigateToDashboard()
                }
            }

            addView(tvSuccessTitle)
            addView(tvWelcomeMessage)
            addView(btnContinue, LinearLayout.LayoutParams(-2, -2))
        }
    }
}

// --- Dashboard Fragment ---

class DashboardFragment : Fragment() {
    private lateinit var tvBatteryLevel: TextView
    private lateinit var tvChargingStatus: TextView
    private var selectedAudioUri: Uri? = null
    private var pendingCameraOption: Int? = null

    private val airplaneModeReceiver = AirplaneModeReceiver()
    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) { updateBatteryStatus(intent) }
    }

    private val pickAudioLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            selectedAudioUri = uri
            Toast.makeText(requireContext(), "Selected Music File: ${uri.lastPathSegment ?: "Audio"}", Toast.LENGTH_SHORT).show()
            startMusicService(uri)
        } else {
            Toast.makeText(requireContext(), "No audio file selected", Toast.LENGTH_SHORT).show()
        }
    }

    private val requestNotificationPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (!isGranted) {
            Toast.makeText(requireContext(), "Notifications permission required for status bar alerts", Toast.LENGTH_SHORT).show()
        }
    }

    private val requestCameraPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val cameraGranted = permissions[Manifest.permission.CAMERA] ?: false
        if (cameraGranted) {
            pendingCameraOption?.let { launchCamera(it) }
        } else {
            Toast.makeText(requireContext(), "Camera permission required to capture photos/videos", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val context = requireContext()
        val scroll = ScrollView(context)
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            padding(16)

            tvBatteryLevel = TextView(context).apply {
                text = "Battery Level: --%"
                textSize = 16f
                setTypeface(null, android.graphics.Typeface.BOLD)
            }
            tvChargingStatus = TextView(context).apply {
                text = "Charging: --"
                textSize = 14f
                setPadding(0, 2.dpToPx(), 0, 16.dpToPx())
            }
            addView(tvBatteryLevel)
            addView(tvChargingStatus)

            fun addButton(label: String, onClick: () -> Unit) {
                addView(MaterialButton(context).apply {
                    text = label
                    setBackgroundColor(Color.parseColor("#D0BCFF")) // Light violet
                    setTextColor(Color.parseColor("#381E72")) // Dark violet text
                    cornerRadius = 28.dpToPx()
                    setOnClickListener { onClick() }
                }, LinearLayout.LayoutParams(-1, -2).apply {
                    bottomMargin = 10.dpToPx()
                })
            }

            addButton("Battery Info") { showBatteryInfoDialog() }
            addButton("Start Background Worker") {
                val workRequest = OneTimeWorkRequestBuilder<MyWorker>().build()
                WorkManager.getInstance(requireContext()).enqueue(workRequest)
                Toast.makeText(context, "Background Worker Started", Toast.LENGTH_SHORT).show()
            }
            addButton("PDF Downloader (Periodic)") {
                val pdfWork = PeriodicWorkRequestBuilder<PdfDownloaderWorker>(15, TimeUnit.MINUTES).build()
                WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork("PdfDownload", ExistingPeriodicWorkPolicy.KEEP, pdfWork)
                Toast.makeText(context, "PDF Periodic Work Enqueued", Toast.LENGTH_SHORT).show()
            }
            addButton("Music") { pickAudioLauncher.launch("audio/*") }
            addButton("Start Music Player") { showMediaPlayerDialog() }
            addButton("Stop Music Player") { stopMusicService() }
            addButton("View Contacts") { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("content://contacts/people/"))) }
            addButton("Open Browser") { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"))) }
            addButton("Open Dialer") { startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:9951482141"))) }
            addButton("Camera (Photo / Video / GPS)") { showCameraDialog() }
            addButton("Start Service") { startCounterService() }
            addButton("Stop Service") { stopCounterService() }
            addButton("Go to Fragment Lab") { (activity as? MainActivity)?.navigateToLab() }

            // RED Logout Button
            val btnLogout = MaterialButton(context).apply {
                text = "Logout"
                setBackgroundColor(Color.parseColor("#D32F2F"))
                setTextColor(Color.WHITE)
                cornerRadius = 28.dpToPx()
                setOnClickListener {
                    (activity as? MainActivity)?.logout()
                }
            }
            addView(btnLogout, LinearLayout.LayoutParams(-1, -2).apply {
                topMargin = 12.dpToPx()
                bottomMargin = 16.dpToPx()
            })
        }
        scroll.addView(layout)
        return scroll
    }

    private fun startMusicService(uri: Uri?) {
        checkNotificationPermission()
        val intent = Intent(requireContext(), MusicPlayerService::class.java).apply {
            action = "START"
            uri?.let { putExtra("AUDIO_URI", it.toString()) }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireContext().startForegroundService(intent)
        } else {
            requireContext().startService(intent)
        }
    }

    private fun stopMusicService() {
        val intent = Intent(requireContext(), MusicPlayerService::class.java).apply {
            action = "STOP"
        }
        requireContext().startService(intent)
    }

    private fun startCounterService() {
        checkNotificationPermission()
        val intent = Intent(requireContext(), CounterService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireContext().startForegroundService(intent)
        } else {
            requireContext().startService(intent)
        }
    }

    private fun stopCounterService() {
        requireContext().stopService(Intent(requireContext(), CounterService::class.java))
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun showCameraDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Select Camera Option")
            .setItems(arrayOf("Photo", "Video", "GPS Camera")) { _, which ->
                handleCameraSelection(which)
            }.show()
    }

    private fun handleCameraSelection(option: Int) {
        pendingCameraOption = option
        val hasCameraPerm = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        val hasLocationPerm = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

        val needed = mutableListOf<String>()
        if (!hasCameraPerm) needed.add(Manifest.permission.CAMERA)
        if (option == 2 && !hasLocationPerm) needed.add(Manifest.permission.ACCESS_FINE_LOCATION)

        if (needed.isNotEmpty()) {
            requestCameraPermissionsLauncher.launch(needed.toTypedArray())
        } else {
            launchCamera(option)
        }
    }

    private fun launchCamera(option: Int) {
        when (option) {
            0 -> { // Photo
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                try {
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Error opening camera: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
            1 -> { // Video
                val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
                try {
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Error opening video camera: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
            2 -> { // GPS Camera
                var location: Location? = null
                try {
                    if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                            ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    }
                } catch (e: Exception) {
                    Log.e("GPSCamera", "Location error: ${e.message}")
                }

                val locationDetails = if (location != null) {
                    "📍 GPS Metadata Captured:\n\n" +
                    "• Latitude: ${location.latitude}° N\n" +
                    "• Longitude: ${location.longitude}° E\n" +
                    "• Altitude: ${location.altitude} meters\n" +
                    "• Accuracy: ${location.accuracy} meters\n" +
                    "• Provider: ${location.provider}"
                } else {
                    "📍 GPS Fix Status: Acquiring Location...\n\n" +
                    "Location coordinates will be tagged on photo capture."
                }

                AlertDialog.Builder(requireContext())
                    .setTitle("📷 GPS Camera Mode")
                    .setMessage(locationDetails)
                    .setPositiveButton("Capture Photo") { _, _ ->
                        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                            location?.let { putExtra("android.intent.extra.LOCATION", it) }
                        }
                        try {
                            startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(requireContext(), "Error opening camera: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        }
    }

    private fun updateBatteryStatus(intent: Intent?) {
        val status: Intent? = intent ?: requireContext().registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        status?.let {
            val level = it.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = it.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            val pct = if (scale > 0) (level * 100 / scale.toFloat()).toInt() else 0
            val charging = it.getIntExtra(BatteryManager.EXTRA_STATUS, -1).let { s -> s == 2 || s == 5 }
            tvBatteryLevel.text = "Battery Level: $pct%"
            tvChargingStatus.text = "Charging: $charging"
        }
    }

    private fun showMediaPlayerDialog() {
        val context = requireContext()
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            padding(24)
            val currentAudioText = if (selectedAudioUri != null) {
                "Selected File: ${selectedAudioUri?.lastPathSegment}"
            } else {
                "Playing: Default Ringtone"
            }
            addView(TextView(context).apply { text = "🎵 $currentAudioText"; textSize = 16f })
            addView(TextView(context).apply { text = "Looping: Enabled"; textSize = 14f; setPadding(0, 4.dpToPx(), 0, 16.dpToPx()) })

            val btnPick = MaterialButton(context).apply {
                text = "Go to Downloads / Drive"
                setOnClickListener { pickAudioLauncher.launch("audio/*") }
            }
            val btnStart = MaterialButton(context).apply {
                text = "Play Music"
                setOnClickListener { startMusicService(selectedAudioUri) }
            }
            val btnStop = MaterialButton(context).apply {
                text = "Stop Music"
                setOnClickListener { stopMusicService() }
            }
            addView(btnPick, LinearLayout.LayoutParams(-1, -2))
            addView(btnStart, LinearLayout.LayoutParams(-1, -2))
            addView(btnStop, LinearLayout.LayoutParams(-1, -2))
        }
        AlertDialog.Builder(context)
            .setTitle("🎵 Small Media Player")
            .setView(layout)
            .setPositiveButton("Close") { d, _ -> d.dismiss() }
            .show()
    }

    private fun showBatteryInfoDialog() {
        val context = requireContext()
        val it = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            padding(24)
            it?.let {
                val level = it.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale = it.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                val pct = if (scale > 0) (level * 100 / scale.toFloat()).toInt() else 0
                val health = when(it.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)) { 2 -> "Good"; 3 -> "Overheat"; else -> "Unknown" }
                val voltage = it.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)
                val temp = it.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) / 10.0
                val tech = it.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY) ?: "Unknown"
                val plugged = if (it.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) == 0) "Battery" else "Plugged"

                fun addInfo(label: String, value: String) {
                    addView(TextView(context).apply { text = "$label: $value"; textSize = 16f; setPadding(0, 4.dpToPx(), 0, 4.dpToPx()) })
                }
                addInfo("Level", "$pct%")
                addInfo("Health", health)
                addInfo("Voltage", "${voltage}mV")
                addInfo("Temperature", "${temp}°C")
                addInfo("Technology", tech)
                addInfo("Power Source", plugged)
            }
        }
        AlertDialog.Builder(context).setTitle("Battery Information").setView(layout)
            .setPositiveButton("Close") { d, _ -> d.dismiss() }.show()
    }

    override fun onResume() {
        super.onResume()
        requireContext().registerReceiver(airplaneModeReceiver, IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED))
        requireContext().registerReceiver(batteryReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        updateBatteryStatus(null)
    }

    override fun onPause() {
        super.onPause()
        requireContext().unregisterReceiver(airplaneModeReceiver)
        requireContext().unregisterReceiver(batteryReceiver)
    }
}

// --- Fragment Lab & Helpers ---

class FragmentLab : Fragment() {
    private var count = 0
    private val list = mutableListOf<String>()
    private lateinit var adapter: ArrayAdapter<String>
    private val containerId = View.generateViewId()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val context = requireContext()
        return LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            padding(16)
            val top = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                val btn = MaterialButton(context).apply { text = "Add Fragment" }
                val spinner = Spinner(context)
                adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, list)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter
                btn.setOnClickListener {
                    count++
                    list.add("$count")
                    adapter.notifyDataSetChanged()
                    childFragmentManager.beginTransaction().replace(containerId, NestedFragment.newInstance(count)).commit()
                    spinner.setSelection(list.size - 1)
                }
                spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                        childFragmentManager.beginTransaction().replace(containerId, NestedFragment.newInstance(pos + 1)).commit()
                    }
                    override fun onNothingSelected(p0: AdapterView<*>?) {}
                }
                addView(btn)
                addView(spinner)
            }
            val frame = FrameLayout(context).apply { id = containerId; setBackgroundColor(Color.WHITE) }
            val btnBack = MaterialButton(context).apply { text = "Back to Dashboard"; setOnClickListener { (activity as? MainActivity)?.navigateToDashboard() } }
            addView(top, LinearLayout.LayoutParams(-2, -2).apply { bottomMargin = 16.dpToPx() })
            addView(frame, LinearLayout.LayoutParams(200.dpToPx(), 200.dpToPx()).apply { bottomMargin = 16.dpToPx() })
            addView(btnBack)
        }
    }
}

class NestedFragment : Fragment() {
    companion object { fun newInstance(c: Int) = NestedFragment().apply { arguments = Bundle().apply { putInt("c", c) } } }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return FrameLayout(requireContext()).apply {
            setBackgroundColor(Color.LTGRAY)
            addView(TextView(context).apply { text = "Fragment ${arguments?.getInt("c") ?: 0}"; textSize = 20f; gravity = Gravity.CENTER }, FrameLayout.LayoutParams(-2, -2, Gravity.CENTER))
        }
    }
}

// --- Extension Helpers ---

fun View.padding(dp: Int) { val px = (dp * resources.displayMetrics.density).toInt(); setPadding(px, px, px, px) }
fun Int.dpToPx(): Int = (this * android.content.res.Resources.getSystem().displayMetrics.density).toInt()

// --- Services & Workers ---

class AirplaneModeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_AIRPLANE_MODE_CHANGED) {
            Toast.makeText(context, "Airplane Mode: ${intent.getBooleanExtra("state", false)}", Toast.LENGTH_SHORT).show()
        }
    }
}

class CounterService : Service() {
    private var job = Job()
    private val channelId = "counter_service_channel"

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val chan = NotificationChannel(channelId, "Counter Service Notifications", NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Shows live counter updates"
                enableVibration(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(chan)
        }
    }

    override fun onStartCommand(i: Intent?, f: Int, s: Int): Int {
        val notif = NotificationCompat.Builder(this, channelId)
            .setContentTitle("⏱️ Counter Service Started")
            .setContentText("Counter is active in background")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(true)
            .build()
        startForeground(201, notif)
        Toast.makeText(this, "Counter Service Started", Toast.LENGTH_SHORT).show()

        job.cancel()
        job = Job()
        val currentScope = CoroutineScope(Dispatchers.Default + job)
        currentScope.launch {
            var c = 0
            while (isActive) {
                Log.d("CounterService", "Counter: $c")
                val updatedNotif = NotificationCompat.Builder(this@CounterService, channelId)
                    .setContentTitle("⏱️ Counter Service Running")
                    .setContentText("Current Count: $c seconds")
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setOngoing(true)
                    .build()
                val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                nm.notify(201, updatedNotif)
                c++
                delay(1000)
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
        val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val stopNotif = NotificationCompat.Builder(this, channelId)
            .setContentTitle("🛑 Counter Service Stopped")
            .setContentText("Counter Service has stopped")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            .build()
        nm.notify(202, stopNotif)
        Toast.makeText(this, "Counter Service Stopped", Toast.LENGTH_SHORT).show()
    }

    override fun onBind(i: Intent?): IBinder? = null
}

class MusicPlayerService : Service() {
    private var player: MediaPlayer? = null
    private val channelId = "music_service_channel"

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val chan = NotificationChannel(channelId, "Music Player Notifications", NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Shows music player controls and status"
                enableVibration(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(chan)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action

        when (action) {
            "STOP" -> {
                stopSelf()
                return START_NOT_STICKY
            }
            "PAUSE" -> {
                if (player?.isPlaying == true) {
                    player?.pause()
                }
                updateNotification(false)
                Toast.makeText(this, "Music Paused", Toast.LENGTH_SHORT).show()
                return START_STICKY
            }
            "PLAY" -> {
                if (player != null) {
                    if (player?.isPlaying == false) {
                        player?.start()
                    }
                } else {
                    initPlayer(intent)
                }
                updateNotification(true)
                Toast.makeText(this, "Music Playing", Toast.LENGTH_SHORT).show()
                return START_STICKY
            }
            else -> { // "START" or null
                initPlayer(intent)
                updateNotification(true)
                Toast.makeText(this, "Music Player Started", Toast.LENGTH_SHORT).show()
                return START_STICKY
            }
        }
    }

    private fun initPlayer(intent: Intent?) {
        player?.stop()
        player?.release()

        val audioUriStr = intent?.getStringExtra("AUDIO_URI")
        val audioUri = if (audioUriStr != null) Uri.parse(audioUriStr) else Settings.System.DEFAULT_RINGTONE_URI

        try {
            player = MediaPlayer.create(this, audioUri)?.apply {
                isLooping = true
                start()
            }
        } catch (_: Exception) {
            player = MediaPlayer.create(this, Settings.System.DEFAULT_RINGTONE_URI)?.apply {
                isLooping = true
                start()
            }
        }
    }

    private fun updateNotification(isPlaying: Boolean) {
        val playIntent = Intent(this, MusicPlayerService::class.java).apply { action = "PLAY" }
        val playPendingIntent = PendingIntent.getService(
            this, 10, playIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val pauseIntent = Intent(this, MusicPlayerService::class.java).apply { action = "PAUSE" }
        val pausePendingIntent = PendingIntent.getService(
            this, 11, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = Intent(this, MusicPlayerService::class.java).apply { action = "STOP" }
        val stopPendingIntent = PendingIntent.getService(
            this, 12, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val toggleAction = if (isPlaying) {
            NotificationCompat.Action.Builder(
                android.R.drawable.ic_media_pause, "Pause", pausePendingIntent
            ).build()
        } else {
            NotificationCompat.Action.Builder(
                android.R.drawable.ic_media_play, "Play", playPendingIntent
            ).build()
        }

        val stopAction = NotificationCompat.Action.Builder(
            android.R.drawable.ic_menu_close_clear_cancel, "Stop", stopPendingIntent
        ).build()

        val statusText = if (isPlaying) "Playing music track" else "Music Paused"

        val notif = NotificationCompat.Builder(this, channelId)
            .setContentTitle("🎵 Music Player")
            .setContentText(statusText)
            .setSmallIcon(if (isPlaying) android.R.drawable.ic_media_play else android.R.drawable.ic_media_pause)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(isPlaying)
            .addAction(toggleAction)
            .addAction(stopAction)
            .build()

        startForeground(101, notif)
    }

    override fun onDestroy() {
        player?.stop()
        player?.release()
        player = null

        val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val stopNotif = NotificationCompat.Builder(this, channelId)
            .setContentTitle("🛑 Music Player Stopped")
            .setContentText("Music Playback Stopped")
            .setSmallIcon(android.R.drawable.ic_media_pause)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            .build()
        nm.notify(102, stopNotif)

        Toast.makeText(this, "Music Player Stopped", Toast.LENGTH_SHORT).show()

        super.onDestroy()
    }

    override fun onBind(i: Intent?): IBinder? = null
}

class MyWorker(c: Context, p: WorkerParameters) : Worker(c, p) {
    override fun doWork(): Result { Log.d("MyWorker", "Work Start"); Thread.sleep(3000); Log.d("MyWorker", "Work End"); return Result.success() }
}

class PdfDownloaderWorker(val context: Context, p: WorkerParameters) : Worker(context, p) {
    override fun doWork(): Result {
        val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val request = DownloadManager.Request(Uri.parse("https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf"))
            .setTitle("Sample PDF").setDescription("Downloading...").setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "sample.pdf")
        dm.enqueue(request)
        return Result.success()
    }
}
