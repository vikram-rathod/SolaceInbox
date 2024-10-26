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
import com.devvikram.solaceinbox.model.EmailType
import com.devvikram.solaceinbox.model.NavItemModel
import com.devvikram.solaceinbox.utility.AppUtil

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
        handeNavigationRail()
        setUpBottomNavigation()
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

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun handeNavigationRail() {
        binding.navigationRailView.setOnItemSelectedListener {

            when (it.itemId) {
                R.id.profile_icon -> {
                    replaceFragment(ProfileFragment())
//                    close drawer
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                }

                R.id.add -> {
                    showUnavailableFeatureSnackBar("The Add feature is currently unavailable but will be available soon.")
                }

                R.id.log_out -> {
                    showLogout()
                }
            }
            true
        }

    }

    private fun updateSideNavigationSelection(title: String) {
        val navMenuAdapter = (binding.navigationView.getHeaderView(0)
            .findViewById<RecyclerView>(R.id.menu_item_recyclerview).adapter as NavMenuAdapter)
        navMenuAdapter.updateSelection(title)
    }

    private fun handleSideNavigation() {
        val navHeader = binding.navigationView.getHeaderView(0).apply {
            findViewById<TextView>(R.id.user_name_textview).text = authViewModel.getUser().name
            findViewById<TextView>(R.id.email_textview).text = authViewModel.getUser().email
        }

        val menuItems = listOf(
            NavItemModel("Inbox", R.drawable.baseline_email_24, true, 0),
            NavItemModel("Sent", R.drawable.send_icon, false, 0),
//            NavItemModel("Drafts", R.drawable.baseline_drafts_24, false, 0),
//            NavItemModel("Trash", R.drawable.baseline_delete_24, false, 0)
        )

        navHeader.findViewById<RecyclerView>(R.id.menu_item_recyclerview).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            addItemDecoration(
                DividerItemDecoration(
                    this@MainActivity,
                    DividerItemDecoration.VERTICAL
                )
            )
            adapter = NavMenuAdapter(this@MainActivity, menuItems).apply {
                setOnNevMenuAdapterListener(object : NavMenuAdapter.NevMenuAdapterListener {
                    override fun onMenuItemClicked(menuItem: NavItemModel) {
                        when (menuItem.title) {
                            "Inbox" -> {
                                replaceFragment(
                                    AllMalilsFragment.newInstance(
                                        EmailType.INBOX,
                                        NavItemModel("Inbox", R.drawable.baseline_email_24, true, 0)
                                    )
                                )
                            }

                            "Sent" -> {
                                replaceFragment(
                                    AllMalilsFragment.newInstance(
                                        EmailType.SENT,
                                        NavItemModel("Sent", R.drawable.baseline_email_24, true, 0)
                                    )
                                )
                            }

//                            "Drafts" -> showUnavailableFeatureSnackBar("The Draft feature is currently unavailable but will be available soon.")
//                            "Trash" -> showUnavailableFeatureSnackBar("The Trash feature is currently unavailable but will be available soon.")
                        }
                        binding.drawerLayout.closeDrawer(GravityCompat.START)
                    }
                })
            }
            (adapter as NavMenuAdapter).notifyDataSetChanged()
        }
    }

    private fun showUnavailableFeatureSnackBar(message: String) {
        AppUtil.showSnackBar(this@MainActivity, message)
    }



    private fun setUpBottomNavigation() {
        replaceFragment(
            AllMalilsFragment.newInstance(
                EmailType.INBOX, NavItemModel("Inbox", R.drawable.baseline_email_24, true, 0)
            )
        )
        updateSideNavigationSelection("Inbox")
        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.all -> {
                    replaceFragment(
                        AllMalilsFragment.newInstance(
                            EmailType.INBOX,
                            NavItemModel("Inbox", R.drawable.baseline_email_24, true, 0)
                        )
                    )
                    updateSideNavigationSelection("Inbox")
                    true
                }
//                R.id.internal -> {
//                    replaceFragment(InternalMailsFragment())
//                    true
//                }
//                R.id.external -> {
//                    replaceFragment(ExternalMailsFragment())
//                    true
//                }
                R.id.profile -> {
                    replaceFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }


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