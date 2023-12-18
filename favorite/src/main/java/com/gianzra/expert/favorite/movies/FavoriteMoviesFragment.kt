package com.gianzra.expert.favorite.movies

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gianzra.expert.favorite.R
import com.gianzra.expert.favorite.databinding.FragmentFavoriteMoviesBinding
import com.gianzra.expert.core.domain.model.Movie
import com.gianzra.expert.core.ui.MoviesAdapter
import com.gianzra.expert.core.utils.SortUtils
import com.gianzra.expert.favorite.FavoriteViewModel
import com.gianzra.expert.submission.detail.DetailActivity
import com.google.android.material.snackbar.Snackbar
import org.koin.android.viewmodel.ext.android.viewModel

class FavoriteMoviesFragment : Fragment() {

    private var _binding: FragmentFavoriteMoviesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FavoriteViewModel by viewModel()
    private val moviesAdapter by lazy { MoviesAdapter() }
    private var sort = SortUtils.RANDOM

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteMoviesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                return makeMovementFlags(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val swipedPosition = viewHolder.adapterPosition
                val movie = moviesAdapter.getSwipedData(swipedPosition)
                var state = movie.favorite
                viewModel.setFavorite(movie, !state)
                state = !state

                val snackBar =
                    Snackbar.make(requireView(), R.string.message_undo, Snackbar.LENGTH_LONG)
                snackBar.setAction(R.string.message_ok) {
                    viewModel.setFavorite(movie, !state)
                }
                snackBar.show()
            }
        })

        binding.apply {
            rvFavoriteMovies.apply {
                layoutManager = LinearLayoutManager(context)
                setHasFixedSize(true)
                adapter = moviesAdapter
            }

            random.setOnClickListener { setSort(SortUtils.RANDOM) }
            newest.setOnClickListener { setSort(SortUtils.NEWEST) }
            popularity.setOnClickListener { setSort(SortUtils.POPULARITY) }
            vote.setOnClickListener { setSort(SortUtils.VOTE) }
        }

        moviesAdapter.onItemClick = { selectedData ->
            startActivity(Intent(activity, DetailActivity::class.java).apply {
                putExtra(DetailActivity.EXTRA_MOVIE, selectedData)
            })
        }

        itemTouchHelper.attachToRecyclerView(binding.rvFavoriteMovies)

        setSort(sort)
    }

    private fun setSort(newSort: String) {
        binding.menu.close(true)
        sort = newSort
        observeMovies()
    }

    private fun observeMovies() {
        viewModel.getFavoriteMovies(sort).observe(viewLifecycleOwner, moviesObserver)
    }

    private val moviesObserver = Observer<List<Movie>> { movies ->
        if (movies.isNullOrEmpty()) {
            binding.progressBar.visibility = View.GONE
            binding.notFound.visibility = View.VISIBLE
            binding.notFoundText.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
            binding.notFound.visibility = View.GONE
            binding.notFoundText.visibility = View.GONE
        }
        moviesAdapter.setData(movies)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}