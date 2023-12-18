package com.gianzra.expert.submission.movies

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.gianzra.expert.core.data.Resource
import com.gianzra.expert.core.domain.model.Movie
import com.gianzra.expert.core.ui.MoviesAdapter
import com.gianzra.expert.core.utils.SortUtils
import com.gianzra.expert.submission.R
import com.gianzra.expert.submission.databinding.FragmentMoviesBinding
import com.gianzra.expert.submission.detail.DetailActivity
import com.gianzra.expert.submission.home.SearchViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.android.viewmodel.ext.android.viewModel

@ExperimentalCoroutinesApi
@FlowPreview
class MoviesFragment : Fragment() {

    private var _fragmentMoviesBinding: FragmentMoviesBinding? = null
    private val binding get() = _fragmentMoviesBinding!!

    private val viewModel: MoviesViewModel by viewModel()
    private lateinit var moviesAdapter: MoviesAdapter
    private var sort = SortUtils.RANDOM

    // Deklarasi searchViewModel
    private val searchViewModel: SearchViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _fragmentMoviesBinding = FragmentMoviesBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMoviesAdapter()
        setupMoviesRecyclerView()
        setMoviesList(sort)
        setupSortButtons()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)
        val item = menu.findItem(R.id.action_search)

        if (item != null) {
            val searchView = item.actionView as? SearchView

            searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    newText?.let { searchViewModel.setSearchQuery(it) }
                    return true
                }
            })
        }

        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun initMoviesAdapter() {
        moviesAdapter = MoviesAdapter()
        moviesAdapter.onItemClick = { selectedData ->
            val intent = Intent(activity, DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_MOVIE, selectedData)
            startActivity(intent)
        }
    }

    private fun setupMoviesRecyclerView() {
        with(binding.rvMovies) {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = moviesAdapter
        }
    }

    private fun setMoviesList(sort: String) {
        viewModel.getMovies(sort).observe(viewLifecycleOwner, moviesObserver)
    }

    private val moviesObserver = Observer<Resource<List<Movie>>> { movies ->
        when (movies) {
            is Resource.Loading -> showLoadingState()
            is Resource.Success -> showMoviesData(movies.data)
            is Resource.Error -> showErrorState()
        }
    }

    private fun showLoadingState() {
        binding.progressBar.visibility = View.VISIBLE
        binding.notFound.visibility = View.GONE
        binding.notFoundText.visibility = View.GONE
    }

    private fun showMoviesData(data: List<Movie>?) {
        binding.progressBar.visibility = View.GONE
        if (data.isNullOrEmpty()) {
            showEmptyState()
        } else {
            hideEmptyState()
            moviesAdapter.setData(data)
        }
    }

    private fun showEmptyState() {
        binding.notFound.visibility = View.VISIBLE
        binding.notFoundText.visibility = View.VISIBLE
    }

    private fun hideEmptyState() {
        binding.notFound.visibility = View.GONE
        binding.notFoundText.visibility = View.GONE
    }

    private fun showErrorState() {
        binding.progressBar.visibility = View.GONE
        showEmptyState()
        Toast.makeText(context, "Terjadi kesalahan", Toast.LENGTH_SHORT).show()
    }

    private fun setupSortButtons() {
        binding.random.setOnClickListener { setSortAndList(SortUtils.RANDOM) }
        binding.newest.setOnClickListener { setSortAndList(SortUtils.NEWEST) }
        binding.popularity.setOnClickListener { setSortAndList(SortUtils.POPULARITY) }
        binding.vote.setOnClickListener { setSortAndList(SortUtils.VOTE) }
    }

    private fun setSortAndList(newSort: String) {
        binding.menu.close(true)
        sort = newSort
        setMoviesList(sort)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragmentMoviesBinding = null
    }
}
