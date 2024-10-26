package com.devvikram.solaceinbox

import android.content.Context
import android.os.Bundle
import com.devvikram.solaceinbox.databinding.ReplyOptionLayoutBinding
import com.google.android.material.bottomsheet.BottomSheetDialog


class ReplyEmailBottomSheetDialog(context: Context) : BottomSheetDialog(context) {

    private lateinit var binding: ReplyOptionLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ReplyOptionLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
