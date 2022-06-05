package com.example.myapplication.adapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.myapplication.R
import com.example.myapplication.fragments.TeamDetailsFragment
import com.example.myapplication.fragments.TeamMatchesFragment

private val TAB_TITLES = arrayOf(
    R.string.details,
    R.string.matches
)

class TeamDetailsPagerAdapter(private val context: Context, fm: FragmentManager) :
    FragmentPagerAdapter(fm) {
    override fun getCount(): Int {
        return TAB_TITLES.size
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> TeamDetailsFragment()
            else -> TeamMatchesFragment()
        }
    }

    override fun getPageTitle(position: Int): CharSequence {
        return context.resources.getString(TAB_TITLES[position]).uppercase()
    }
}