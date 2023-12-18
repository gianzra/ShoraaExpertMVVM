package com.gianzra.expert.favorite

import android.content.Context
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.gianzra.expert.favorite.movies.FavoriteMoviesFragment
import com.gianzra.expert.favorite.tvshows.FavoriteTvShowsFragment

class SectionsPagerAdapter(private val mContext: Context, fm: FragmentManager) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    @StringRes
    private val tabTitles = intArrayOf(
        R.string.movies,
        R.string.tvshows
    )

    private val fragment: List<Fragment> = listOf(
        FavoriteMoviesFragment(),
        FavoriteTvShowsFragment()
    )

    override fun getPageTitle(position: Int): CharSequence = mContext.getString(tabTitles[position])

    override fun getCount(): Int = tabTitles.size

    override fun getItem(position: Int): Fragment = fragment[position]
}
