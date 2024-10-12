package com.devvikram.solaceinbox.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.devvikram.solaceinbox.adapter.CategoryEmailAdapter
import com.devvikram.solaceinbox.constant.MyApplication
import com.devvikram.solaceinbox.databinding.FragmentAllMalilsBinding
import com.devvikram.solaceinbox.model.CategorizedEmail
import com.devvikram.solaceinbox.model.Mail
import com.devvikram.solaceinbox.model.NavItemModel
import com.devvikram.solaceinbox.utility.AppUtil
import com.google.android.material.snackbar.Snackbar
import java.util.Calendar

class AllMalilsFragment : Fragment() {

    private lateinit var binding : FragmentAllMalilsBinding
    private val categorizedEmails: MutableMap<String, ArrayList<Mail>> = mutableMapOf()

    private val allMailsViewModel: AllMailViewModel by lazy {
        (requireActivity().application as MyApplication).allMailsViewModel
    }

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
        allMailsViewModel.allMails.observe(viewLifecycleOwner) { state ->
            val emailList = state.mails
            val categorized = categorizeEmails(emailList).map { CategorizedEmail(it.key, it.value) }

            when {
                state.isLoading -> {
                    binding.recyclerViewEmails.visibility = View.GONE
                    binding.progressBar.visibility = View.VISIBLE
                    binding.emptyView.visibility = View.GONE
                    binding.searchView.visibility = View.GONE
                }

                state.isSuccessful -> {
                    binding.recyclerViewEmails.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE
                    binding.searchView.visibility = View.VISIBLE
                    binding.emptyView.visibility =
                        if (emailList.isEmpty()) View.VISIBLE else View.GONE

                    val emailAdapter = CategoryEmailAdapter(requireActivity(), categorized)

                    if (binding.recyclerViewEmails.adapter == null) {
                        binding.recyclerViewEmails.adapter = emailAdapter
                        binding.recyclerViewEmails.layoutManager = LinearLayoutManager(requireContext())
                    } else {
                        (binding.recyclerViewEmails.adapter as CategoryEmailAdapter).apply {
                            notifyDataSetChanged()
                        }
                    }
                    Snackbar.make(binding.root, "Data loaded successfully", Snackbar.LENGTH_SHORT).show()
                }


                state.isFailure -> {
                    binding.recyclerViewEmails.visibility = View.GONE
                    binding.progressBar.visibility = View.GONE
                    binding.emptyView.visibility = View.VISIBLE
                    binding.searchView.visibility = View.GONE
                    binding.emptyView.text = "Your inbox is currently empty."
                    Toast.makeText(
                        requireContext(),
                        "Failed to load data: ${state.message}",
                        Toast.LENGTH_SHORT
                    )
                }
            }
        }

    }
    private fun categorizeEmails(emailList: List<Mail>): MutableMap<String, ArrayList<Mail>> {
        emailList.forEach { email ->
            val emailDate = AppUtil.getEmailDate(email.cDate)
            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(
                Calendar.SECOND,
                0
            ); set(Calendar.MILLISECOND, 0)
            }
            val yesterday = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, -1); set(
                Calendar.HOUR_OF_DAY,
                0
            ); set(Calendar.MINUTE, 0);
            }
            val lastWeek = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, -7); set(
                Calendar.HOUR_OF_DAY,
                0
            ); set(Calendar.MINUTE, 0);
            }
            val lastMonth = Calendar.getInstance().apply {
                add(Calendar.MONTH, -1); set(Calendar.HOUR_OF_DAY, 0); set(
                Calendar.MINUTE,
                0
            );
            }

            when {
                emailDate.after(today.time) -> {
                    categorizedEmails.getOrPut("Today") { ArrayList() }.add(email)
                }

                emailDate.after(yesterday.time) -> {
                    categorizedEmails.getOrPut("Yesterday") { ArrayList() }.add(email)
                }

                emailDate.after(lastWeek.time) -> {
                    categorizedEmails.getOrPut("Last Week") { ArrayList() }.add(email)
                }

                emailDate.after(lastMonth.time) -> {
                    categorizedEmails.getOrPut("Last Month") { ArrayList() }.add(email)
                }

                else -> {
                    categorizedEmails.getOrPut("Older") { ArrayList() }.add(email)
                }
            }
        }
        return categorizedEmails
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