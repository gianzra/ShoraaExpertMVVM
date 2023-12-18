package com.gianzra.expert.submission.detail

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.gianzra.expert.submission.databinding.ActivityDetailBinding
import com.gianzra.expert.submission.R
import com.gianzra.expert.core.domain.model.Movie
import org.koin.android.viewmodel.ext.android.viewModel

class DetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_MOVIE = "extraMovie"
    }

    private lateinit var binding: ActivityDetailBinding
    private val viewModel: DetailViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val detailMovie = intent.getParcelableExtra<Movie>(EXTRA_MOVIE)
        detailMovie?.let { populateDetail(it) }

        binding.backButton.setOnClickListener { onBackPressed() }
        binding.share.setOnClickListener { share() }
    }

    private fun populateDetail(movie: Movie) {
        with(binding) {
            titleDetail.text = movie.title
            date.text = movie.releaseDate
            overview.text = movie.overview
            popularity.text = getString(
                R.string.popularity_detail,
                movie.popularity.toString(),
                movie.voteCount.toString(),
                movie.voteAverage.toString()
            )
            userScore.text = movie.voteAverage.toString()

            loadImage(posterTopBar, movie.posterPath)
            loadImage(subPoster, movie.posterPath)

            var favoriteState = movie.favorite
            setFavoriteState(favoriteState)
            binding.favoriteButton.setOnClickListener {
                favoriteState = !favoriteState
                viewModel.setFavoriteMovie(movie, favoriteState)
                setFavoriteState(favoriteState)
            }
        }
    }

    private fun loadImage(imageView: ImageView, imagePath: String) {
        Glide.with(this@DetailActivity)
            .load(getString(R.string.baseUrlImage, imagePath))
            .into(imageView)
        imageView.tag = imagePath
    }

    private fun setFavoriteState(state: Boolean) {
        val drawableRes = if (state) R.drawable.ic_favorite else R.drawable.ic_favorite_border
        binding.favoriteButton.setImageDrawable(ContextCompat.getDrawable(this, drawableRes))
    }

    private fun share() {
        val mimeType = "text/plain"
        ShareCompat.IntentBuilder(this).apply {
            setType(mimeType)
            setChooserTitle(getString(R.string.shareTitle))
            setText(getString(R.string.shareBody))
            startChooser()
        }
    }
}
