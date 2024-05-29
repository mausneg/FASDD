package com.example.fasdd_android

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fasdd_android.databinding.FragmentNotifBinding
import com.google.android.material.badge.BadgeDrawable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class NotifFragment : Fragment() {
    private lateinit var binding: FragmentNotifBinding
    private val notifList = ArrayList<Notif>()


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotifBinding.inflate(inflater, container, false)
        notifList.addAll(getNotifList())
        showNewsList()
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getNotifList(): ArrayList<Notif> {
        val dataTitle = resources.getStringArray(R.array.notif_title)
        val dataExcerpt = resources.getStringArray(R.array.notif_excerpt)
        val dataDateTime = resources.getStringArray(R.array.notif_datetimes)

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val listNotif = ArrayList<Notif>()
        for (position in dataTitle.indices) {
            val notif = Notif(
                dataTitle[position],
                LocalDateTime.parse(dataDateTime[position], formatter),
                dataExcerpt[position],
            )
            listNotif.add(notif)
        }
        return listNotif
    }

    private fun showNewsList() {
        val notifListAdapter = NotifListAdapter(notifList)
        binding.contentNotif.layoutManager = LinearLayoutManager(context)
        binding.contentNotif.adapter = notifListAdapter

    }

}