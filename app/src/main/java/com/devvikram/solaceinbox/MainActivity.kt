package com.devvikram.solaceinbox

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.devvikram.solaceinbox.activities.addcompose.AddNewMailActivity
import com.devvikram.solaceinbox.constant.MyApplication
import com.devvikram.solaceinbox.activities.login.AuthViewModel
import com.devvikram.solaceinbox.activities.login.LoginActivity
import com.devvikram.solaceinbox.databinding.ActivityMainBinding
import com.devvikram.solaceinbox.fragment.AllMalilsFragment
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

        val navHeader = binding.navigationView.getHeaderView(0)
        val userName = navHeader.findViewById<TextView>(R.id.user_name_textview)
        val userEmail = navHeader.findViewById<TextView>(R.id.email_textview)

        userEmail.text = authViewModel.getUser().email
        userName.text = authViewModel.getUser().name

        binding.addBtn.setOnClickListener {
            val intent = Intent(this, AddNewMailActivity::class.java)
            startActivity(intent)
        }

        binding.toolbarLayout.setNavigationOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
        setUpBottomNavigation()

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
//                    replaceFragment(InternalMailsFragment())
                    showLogout()
                    true
                }

                R.id.external -> {
//                    replaceFragment(ExternalMailsFragment())
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
            authViewModel.logoutState.observe(this) {
                if (it.isSuccessful) {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finishAffinity()
                } else if (it.isFailure) {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
            }

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