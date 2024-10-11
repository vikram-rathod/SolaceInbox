package com.devvikram.solaceinbox

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.devvikram.solaceinbox.databinding.FragmentAllMalilsBinding
import com.devvikram.solaceinbox.model.NavItemModel

class AllMalilsFragment : Fragment() {

    private lateinit var binding : FragmentAllMalilsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAllMalilsBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    companion object {
        const val TAG = "AllMailsFragment"
        @JvmStatic
        fun newInstance(navItemModel: NavItemModel) =
            AllMalilsFragment().apply {
                arguments = Bundle().apply {
//                    putParcelable("nav_item_model", navItemModel)
                }
            }
    }
}