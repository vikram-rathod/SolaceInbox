package com.devvikram.solaceinbox.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.devvikram.solaceinbox.adapter.CategoryEmailAdapter
import com.devvikram.solaceinbox.common.SharedViewModel
import com.devvikram.solaceinbox.constant.MyApplication
import com.devvikram.solaceinbox.databinding.FragmentAllMalilsBinding
import com.devvikram.solaceinbox.model.CategorizedEmail
import com.devvikram.solaceinbox.model.Mail
import com.devvikram.solaceinbox.model.NavItemModel
import com.devvikram.solaceinbox.utility.AppUtil
import com.google.android.material.snackbar.Snackbar
import java.util.Calendar

class AllMalilsFragment : Fragment() {
    var emailAdapter: CategoryEmailAdapter? = null


    private lateinit var binding : FragmentAllMalilsBinding
    private val categorizedEmails: HashMap<String, ArrayList<Mail>> = HashMap()

    private val allMailsViewModel: AllMailViewModel by lazy {
        (requireActivity().application as MyApplication).allMailsViewModel
    }
    private val sharedViewModel: SharedViewModel by lazy {
        (requireActivity().application as MyApplication).sharedViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        allMailsViewModel.fetchAllMails()
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
            Log.d(TAG, "onViewCreated: Email List Size: ${emailList.size}")
            val categorized = categorizeEmails(emailList).map { CategorizedEmail(it.key, it.value) }

            Log.d(TAG, "onViewCreated: Categories Email list size ${categorized.size}")
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
                        emailAdapter = CategoryEmailAdapter(requireActivity(), categorized as ArrayList)
                        binding.recyclerViewEmails.adapter = emailAdapter
                        binding.recyclerViewEmails.layoutManager = LinearLayoutManager(requireContext())

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
        handleSearch()

    }
    private fun categorizeEmails(emailList: ArrayList<Mail>): HashMap<String, ArrayList<Mail>> {
        val categorizedEmails = HashMap<String, ArrayList<Mail>>()

        emailList.forEach { email ->
            val emailDate = AppUtil.getEmailDate(email.cDate)

            Log.d("CategorizeEmails", "Email Date: $emailDate")

            // Calendar to set up date ranges
            val calendar = Calendar.getInstance()

            // Set up "Today"
            val today = calendar.apply {
                set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
            }.time
            Log.d("CategorizeEmails", "Today: $today")

            // Set up "Yesterday"
            val yesterday = calendar.apply {
                add(Calendar.DAY_OF_YEAR, -1); set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0)
            }.time
            Log.d("CategorizeEmails", "Yesterday: $yesterday")

            // Set up "Last Week"
            val lastWeek = calendar.apply {
                add(Calendar.DAY_OF_YEAR, -6); set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0)
            }.time
            Log.d("CategorizeEmails", "Last Week: $lastWeek")

            // Set up "Last Month"
            val lastMonth = calendar.apply {
                add(Calendar.MONTH, -1); set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0)
            }.time
            Log.d("CategorizeEmails", "Last Month: $lastMonth")

            // Categorize the email based on its date
            when {
                emailDate.after(today) || emailDate == today -> {
                    addUniqueEmailToCategory("Today", email, categorizedEmails)
                    Log.d("CategorizeEmails", "Email categorized as Today")
                }
                emailDate.after(yesterday) -> {
                    addUniqueEmailToCategory("Yesterday", email, categorizedEmails)
                    Log.d("CategorizeEmails", "Email categorized as Yesterday")
                }
                emailDate.after(lastWeek) -> {
                    addUniqueEmailToCategory("Last Week", email, categorizedEmails)
                    Log.d("CategorizeEmails", "Email categorized as Last Week")
                }
                emailDate.after(lastMonth) -> {
                    addUniqueEmailToCategory("Last Month", email, categorizedEmails)
                    Log.d("CategorizeEmails", "Email categorized as Last Month")
                }
                else -> {
                    addUniqueEmailToCategory("Older", email, categorizedEmails)
                    Log.d("CategorizeEmails", "Email categorized as Older")
                }
            }
        }

        return categorizedEmails
    }




    private fun addUniqueEmailToCategory(
        category: String,
        email: Mail,
        categorizedEmails: HashMap<String, ArrayList<Mail>>
    ) {
        val emailList = categorizedEmails.getOrPut(category) { ArrayList() }

        if (!emailList.contains(email)) {
            emailList.add(email)
        }
    }

    private fun handleSearch(){
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { filterEmails(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { filterEmails(it) }
                return true
            }
        })
    }

    private fun filterEmails(query: String) {
        val emailList = allMailsViewModel.allMails.value?.mails ?: arrayListOf()

        val filteredEmails = emailList.filter { email ->
            email.subject.contains(query, ignoreCase = true) ||
                    email.body.contains(query, ignoreCase = true) ||
                    email.senderName.contains(query, ignoreCase = true)
        }

        val filteredCategorizedEmails = categorizeEmails(ArrayList(filteredEmails)).map {
            CategorizedEmail(it.key, it.value)
        }

        emailAdapter?.updateData(filteredCategorizedEmails as ArrayList<CategorizedEmail>)
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