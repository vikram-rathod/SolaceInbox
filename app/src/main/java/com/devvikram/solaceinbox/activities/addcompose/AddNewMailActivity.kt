package com.devvikram.solaceinbox.activities.addcompose

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.devvikram.solaceinbox.databinding.ActivityAddNewMailBinding

class AddNewMailActivity : AppCompatActivity() {
    private lateinit var  binding : ActivityAddNewMailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewMailBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}