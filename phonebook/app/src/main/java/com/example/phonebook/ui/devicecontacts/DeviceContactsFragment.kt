package com.example.phonebook.ui.devicecontacts

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.phonebook.R
import com.example.phonebook.util.DeviceContactsSync
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DeviceContactsFragment : Fragment() {

    private lateinit var adapter: DeviceAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_device_contacts, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recycler = view.findViewById<RecyclerView>(R.id.recyclerDevice)
        recycler.layoutManager = LinearLayoutManager(requireContext())
        adapter = DeviceAdapter(emptyList())
        recycler.adapter = adapter

        if (hasPermission()) loadDeviceContacts()

        view.findViewById<View>(R.id.btnSyncDevice).setOnClickListener {
            if (!hasPermission()) {
                Toast.makeText(requireContext(), "Grant Contacts permission first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            lifecycleScope.launch {
                val added = withContext(Dispatchers.IO) { DeviceContactsSync.syncToApp(requireContext()) }
                Toast.makeText(requireContext(), "Synced: $added new contacts added", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun hasPermission() = ContextCompat.checkSelfPermission(
        requireContext(), Manifest.permission.READ_CONTACTS
    ) == PackageManager.PERMISSION_GRANTED

    private fun loadDeviceContacts() {
        lifecycleScope.launch {
            val list = withContext(Dispatchers.IO) { DeviceContactsSync.readDeviceContacts(requireContext()) }
            adapter.updateData(list)
        }
    }
}
