package com.example.fasdd_android

import android.content.ContentValues.TAG
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
import com.google.android.material.badge.BadgeDrawable
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale


class NotifFragment : Fragment() {
    private lateinit var binding: FragmentNotifBinding
    private val notifList = ArrayList<Notif>()
    private lateinit var db: FirebaseFirestore



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotifBinding.inflate(inflater, container, false)
        db = FirebaseFirestore.getInstance()
        lifecycleScope.launch {
            getNotifList()
        }
        showNewsList()
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun getNotifList() {
        try {
            val documents = withContext(Dispatchers.IO) {
                db.collection("notifications").get().await()
            }
            for (document in documents) {
                val id = document.id
                val title = document.getString("title") ?: ""
                val message = document.getString("message") ?: ""
                val dateTimeStr = document.getDate("datetime") ?: ""
                val alreadyRead = document.getBoolean("already_read") ?: false
                val formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH)
                val dateTime = LocalDateTime.parse(dateTimeStr.toString(), formatter)
                val notif = Notif(id, title, dateTime, message, alreadyRead)
                notifList.add(notif)
            }
            showNewsList()
        } catch (e: Exception) {
            Log.w(TAG, "Error getting documents: ", e)
        }
    }

    private fun showNewsList() {
        val notifListAdapter = NotifListAdapter(notifList)
        binding.contentNotif.layoutManager = LinearLayoutManager(context)
        binding.contentNotif.adapter = notifListAdapter

    }

}