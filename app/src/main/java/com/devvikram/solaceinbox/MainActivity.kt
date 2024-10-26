package com.devvikram.solaceinbox

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devvikram.solaceinbox.activities.addcompose.AddNewMailActivity
import com.devvikram.solaceinbox.activities.login.AuthViewModel
import com.devvikram.solaceinbox.activities.login.LoginActivity
import com.devvikram.solaceinbox.adapter.NavMenuAdapter
import com.devvikram.solaceinbox.constant.MyApplication
import com.devvikram.solaceinbox.databinding.ActivityMainBinding
import com.devvikram.solaceinbox.fragment.AllMalilsFragment
import com.devvikram.solaceinbox.fragment.ExternalMailsFragment
import com.devvikram.solaceinbox.fragment.InternalMailsFragment
import com.devvikram.solaceinbox.model.NavItemModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val authViewModel: AuthViewModel by lazy {
        (application as MyApplication).authViewModel
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbarLayout.setNavigationIcon(R.drawable.baseline_menu_24)


        binding.addBtn.setOnClickListener {
            val intent = Intent(this, AddNewMailActivity::class.java)
            startActivity(intent)
        }

        binding.toolbarLayout.setNavigationOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
        handleSideNavigation()
        setUpBottomNavigation()
    }

    private fun handleSideNavigation() {
        val navHeader = binding.navigationView.getHeaderView(0)
        val userName = navHeader.findViewById<TextView>(R.id.user_name_textview)
        val userEmail = navHeader.findViewById<TextView>(R.id.email_textview)

        userEmail.text = authViewModel.getUser().email
        userName.text = authViewModel.getUser().name

        val menuRecyclerView = navHeader.findViewById<RecyclerView>(
            R.id.menu_item_recyclerview
        )
        val menuItems = mutableListOf<NavItemModel>()
        menuItems.add(NavItemModel("Inbox", R.drawable.baseline_email_24, true, 0))
        menuItems.add(NavItemModel("Sent", R.drawable.send_icon, false, 0))
        menuItems.add(NavItemModel("Drafts", R.drawable.baseline_drafts_24, false, 0))
        menuItems.add(NavItemModel("Trash", R.drawable.baseline_delete_24, false, 0))

        menuRecyclerView.layoutManager = LinearLayoutManager(this)
        //item decorator
        menuRecyclerView.addItemDecoration(
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        )

        val menuAdapter = NavMenuAdapter(this, menuItems)
        menuRecyclerView.adapter = menuAdapter
        menuAdapter.setOnNevMenuAdapterListener(
            object : NavMenuAdapter.NevMenuAdapterListener {
                override fun onMenuItemClicked(menuItem: NavItemModel) {
                    when (menuItem.title) {
                        "Inbox" -> {
                            replaceFragment(
                                AllMalilsFragment.newInstance(
                                    NavItemModel("Inbox", R.drawable.baseline_email_24, true, 0)
                                )
                            )
                        }
                        "Sent" -> {
                            // TODO: Sent fragment
                            replaceFragment(InternalMailsFragment())
                        }
                        "Drafts" -> {
                            // TODO: Drafts fragment
                            replaceFragment(ExternalMailsFragment())
                        }
                        "Trash" -> {
                            // TODO: Trash fragment
                            replaceFragment(ProfileFragment())
                        }
                    }
                }
            }

        )
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun setUpBottomNavigation() {
        replaceFragment(
            AllMalilsFragment.newInstance(
                NavItemModel("Inbox", R.drawable.baseline_email_24, true, 0)
            )
        )
        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.all -> {
                    replaceFragment(
                        AllMalilsFragment.newInstance(
                            NavItemModel("Inbox", R.drawable.baseline_email_24, true, 0)
                        )
                    )
                    true
                }
                R.id.internal -> {
                    replaceFragment(InternalMailsFragment())
                    showLogout()
                    true
                }
                R.id.external -> {
                    replaceFragment(ExternalMailsFragment())
                    true
                }
                R.id.profile -> {
                    replaceFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }


    }

    private fun showLogout() {
        val alertDialog = AlertDialog.Builder(this).create()
        alertDialog.setTitle("Logout")
        alertDialog.setMessage("Are you sure you want to logout?")

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes") { _, _ ->
            authViewModel.logoutUser()
            navigateToLogin()
        }
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No") { dialog, _ ->
            dialog.dismiss()
        }
        alertDialog.show()

    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_layout, fragment)
        fragmentTransaction.commit()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}