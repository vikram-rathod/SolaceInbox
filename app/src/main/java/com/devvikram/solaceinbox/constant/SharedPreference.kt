package com.devvikram.solaceinbox.constant

import android.content.Context
import com.devvikram.solaceinbox.model.UserModel

class SharedPreference(context: Context) {
    private val sharedPreferences =
        context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)

    fun saveData(userModel: UserModel) {
        with(sharedPreferences.edit()) {
            putString(USER_ID, userModel.userId)
            putString(USER_NAME, userModel.name)
            putString(USER_EMAIL, userModel.email)
            putString(USER_MOBILE, userModel.password)
            putBoolean(IS_LOGGED_IN, true)
            apply()
        }
    }

    fun getUser(): UserModel {
        return UserModel(
            sharedPreferences.getString(USER_ID, "").toString(),
            sharedPreferences.getString(USER_NAME, "").toString(),
            sharedPreferences.getString(USER_EMAIL, "").toString(),
            sharedPreferences.getString(USER_MOBILE, "").toString()
        )
    }

    fun clear() {
        with(sharedPreferences.edit()) {
            remove(USER_ID)
            remove(USER_NAME)
            remove(USER_EMAIL)
            remove(USER_MOBILE)
            putBoolean(IS_LOGGED_IN,false)
            apply()
        }
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(IS_LOGGED_IN, false)
    }

    fun getUserName(): String {
        return sharedPreferences.getString(USER_NAME, "").toString()
    }

    companion object {
        const val SHARED_PREF_NAME = "solace_inbox_shared_pref"
        const val USER_ID = "user_id"
        const val USER_NAME = "user_name"
        const val USER_EMAIL = "user_email"
        const val USER_MOBILE = "user_mobile"
        const val IS_LOGGED_IN = "is_logged_in"

    }
}