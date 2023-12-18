package com.gianzra.expert.submission.home

import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.gianzra.expert.submission.R
import com.gianzra.expert.submission.movies.MoviesFragment
import com.gianzra.expert.submission.tvshows.TvShowsFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home1)

        setupBottomNavigation()
        loadFragment(MoviesFragment())
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupBottomNavigation() {
        val btMovies = findViewById<ImageButton>(R.id.btMovies)
        val btTvShows = findViewById<ImageButton>(R.id.btTvShows)
        val btFavorite = findViewById<ImageButton>(R.id.btFavorite)

        btMovies.setOnClickListener {
            loadFragment(MoviesFragment())
        }

        btTvShows.setOnClickListener {
            loadFragment(TvShowsFragment())
        }

        btFavorite.setOnClickListener {
            moveToFavoriteFragment()
        }
    }

    private fun moveToFavoriteFragment() {
        instantiateFragment<Fragment>("com.gianzra.expert.favorite.FavoriteFragment")?.let {
            loadFragment(it)
        }
    }

    private fun <T : Fragment> loadFragment(fragment: T) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frameContainer, fragment)
            setTransition(androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            addToBackStack(null)  // Add this line if you want to navigate back
            commit()
        }
    }

    private inline fun <reified T : Fragment> instantiateFragment(className: String): T? {
        return try {
            Class.forName(className).newInstance() as T
        } catch (e: Exception) {
            showToast("Module not found")
            null
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
