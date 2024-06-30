package com.example.fasdd_android

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fasdd_android.databinding.FragmentNotifBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class NotifFragment : Fragment() {

    private lateinit var binding: FragmentNotifBinding
    private val notifList = ArrayList<Notif>()
    private lateinit var db: FirebaseFirestore
    private lateinit var sharedPreferences: SharedPreferences

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotifBinding.inflate(inflater, container, false)
        db = FirebaseFirestore.getInstance()
        sharedPreferences = requireActivity().getSharedPreferences("user_id", Context.MODE_PRIVATE)
        lifecycleScope.launch {
            getNotifList()
        }
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun getNotifList() {
        val userId = sharedPreferences.getString("user_id", null)
        if (userId != null) {
            val userRef = db.collection("users").document(userId)
            db.collection("notifications")
                .whereEqualTo("user_id", userRef)
                .addSnapshotListener { snapshots, e ->
                    if (e != null) {
                        //Log.w(TAG, "listen:error", e)
                        return@addSnapshotListener
                    }

                    notifList.clear()
                    snapshots?.forEach { document ->
                        try {
                            val id = document.id
                            val type = document.getString("type") ?: ""
                            val dateTimeStr = document.getDate("datetime")
                            val alreadyRead = document.getBoolean("already_read") ?: false
                            val formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH)
                            val dateTime = LocalDateTime.parse(dateTimeStr.toString(), formatter)
                            val notif = Notif(id, type, dateTime, alreadyRead)
                            notifList.add(notif)
                        } catch (ex: Exception) {
                            //Log.e(TAG, "Error parsing notification", ex)
                        }
                    }
                    showNewsList()
                }
        }
    }

    private fun showNewsList() {
        val notifListAdapter = NotifListAdapter(notifList)
        binding.contentNotif.layoutManager = LinearLayoutManager(context)
        binding.contentNotif.adapter = notifListAdapter
    }

}
