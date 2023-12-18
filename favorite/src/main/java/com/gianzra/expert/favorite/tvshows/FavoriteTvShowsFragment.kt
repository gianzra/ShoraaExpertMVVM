package com.gianzra.expert.favorite.tvshows

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
import com.gianzra.expert.favorite.databinding.FragmentFavoriteTvShowsBinding
import com.gianzra.expert.core.domain.model.Movie
import com.gianzra.expert.core.ui.MoviesAdapter
import com.gianzra.expert.core.utils.SortUtils
import com.gianzra.expert.favorite.FavoriteViewModel
import com.gianzra.expert.submission.detail.DetailActivity
import com.google.android.material.snackbar.Snackbar
import org.koin.android.viewmodel.ext.android.viewModel

class FavoriteTvShowsFragment : Fragment() {

    private var _binding: FragmentFavoriteTvShowsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FavoriteViewModel by viewModel()
    private val tvShowsAdapter by lazy { MoviesAdapter() }
    private var sort = SortUtils.RANDOM

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteTvShowsBinding.inflate(inflater, container, false)
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
                if (view != null) {
                    val swipedPosition = viewHolder.adapterPosition
                    val tvShowEntity = tvShowsAdapter.getSwipedData(swipedPosition)
                    var state = tvShowEntity.favorite
                    viewModel.setFavorite(tvShowEntity, !state)
                    state = !state
                    val snackBar =
                        Snackbar.make(view, R.string.message_undo, Snackbar.LENGTH_LONG)
                    snackBar.setAction(R.string.message_ok) {
                        viewModel.setFavorite(tvShowEntity, !state)
                    }
                    snackBar.show()
                }
            }
        })

        binding.apply {
            rvFavoriteTvShows.apply {
                layoutManager = LinearLayoutManager(context)
                setHasFixedSize(true)
                adapter = tvShowsAdapter
            }

            random.setOnClickListener { setSort(SortUtils.RANDOM) }
            newest.setOnClickListener { setSort(SortUtils.NEWEST) }
            popularity.setOnClickListener { setSort(SortUtils.POPULARITY) }
            vote.setOnClickListener { setSort(SortUtils.VOTE) }
        }

        tvShowsAdapter.onItemClick = { selectedData ->
            startActivity(Intent(activity, DetailActivity::class.java).apply {
                putExtra(DetailActivity.EXTRA_MOVIE, selectedData)
            })
        }

        itemTouchHelper.attachToRecyclerView(binding.rvFavoriteTvShows)

        setSort(sort)
    }

    private fun setSort(newSort: String) {
        binding.menu.close(true)
        sort = newSort
        observeTvShows()
    }

    private fun observeTvShows() {
        viewModel.getFavoriteTvShows(sort).observe(viewLifecycleOwner, tvShowsObserver)
    }

    private val tvShowsObserver = Observer<List<Movie>> { tvShows ->
        if (tvShows.isNullOrEmpty()){
            binding.progressBar.visibility = View.GONE
            binding.notFound.visibility = View.VISIBLE
            binding.notFoundText.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
            binding.notFound.visibility = View.GONE
            binding.notFoundText.visibility = View.GONE
        }
        tvShowsAdapter.setData(tvShows)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}