package com.devvikram.solaceinbox.utility

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.TimeZone
//date string in this format Sat Oct 12 13:03:04 GMT+05:30 2024

class AppUtil {
    companion object {
        @SuppressLint("SimpleDateFormat")
        fun getEmailDate(cDate: String): Date {

            if(cDate.isEmpty()){
                return Date()
            }
            val sdf = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy")
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            return sdf.parse(cDate)!!

        }

        @SuppressLint("SimpleDateFormat")
        fun getTimeFromDate(cDate: String): String {
            //return HH:mm
            val sdf = SimpleDateFormat("HH:mm")
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            return sdf.format(getEmailDate(cDate))
        }

        fun isToday(cDate: String): Boolean {
            if (cDate.isEmpty()) {
                return false
            }
            val today = Date()
            val emailDate = getEmailDate(cDate)
            val todayCalendar = Calendar.getInstance()
            todayCalendar.time = today
            val emailCalendar = Calendar.getInstance()
            emailCalendar.time = emailDate
            return todayCalendar.get(Calendar.YEAR) == emailCalendar.get(Calendar.YEAR) &&
                    todayCalendar.get(Calendar.DAY_OF_YEAR) == emailCalendar.get(Calendar.DAY_OF_YEAR)

        }

        fun isYesterday(cDate: String): Boolean {
            if (cDate.isEmpty()) {
                return false
            }
            try {
                val today = Date()
                val emailDate = getEmailDate(cDate)
                val todayCalendar = Calendar.getInstance()
                todayCalendar.time = today
                val emailCalendar = Calendar.getInstance()
                emailCalendar.time = emailDate

                return todayCalendar.get(Calendar.YEAR) == emailCalendar.get(Calendar.YEAR) &&
                        todayCalendar.get(Calendar.DAY_OF_YEAR) - 1 == emailCalendar.get(Calendar.DAY_OF_YEAR)
            } catch (e: Exception) {
                return false
            }


        }

        @SuppressLint("SimpleDateFormat")
        fun getDateInDmy(cDate: String): String {
            // Check if the input date string is empty
            if (cDate.isEmpty()) {
                return "Invalid date"
            }

            val sdf = SimpleDateFormat("dd-MM-yyyy")
            sdf.timeZone = TimeZone.getTimeZone("UTC")

            return try {
                val emailDate = getEmailDate(cDate)
                sdf.format(emailDate)
            } catch (e: Exception) {
                "Invalid date format"
            }
        }


        @SuppressLint("SimpleDateFormat")
        fun getTimeAgoString(cDate: String): String {
            //seconds ago,min,hours,days,months,years
            if (cDate.isEmpty()) {
                return "Invalid date"
            }
            try {
                val sdf = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy")
                sdf.timeZone = TimeZone.getTimeZone("UTC")
                val emailDate = sdf.parse(cDate)!!

                val now = Date()
                val diff = now.time - emailDate.time

                val seconds = diff / 1000
                val minutes = seconds / 60
                val hours = minutes / 60
                val days = hours / 24
                val months = days / 30
                val years = months / 12
                return when {
                    years > 0 -> "$years years ago"
                    months > 0 -> "$months months ago"
                    days > 0 -> "$days days ago"
                    hours > 0 -> "$hours hours ago"
                    minutes > 0 -> "$minutes minutes ago"
                    seconds > 0 -> "$seconds seconds ago"
                    else -> "just now"
                }
            } catch (e: Exception) {
                return "Invalid date"
            }

        }
    }
}

























