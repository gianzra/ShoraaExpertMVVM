package com.gianzra.expert.submission.tvshows

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.gianzra.expert.core.data.Resource
import com.gianzra.expert.core.domain.model.Movie
import com.gianzra.expert.core.ui.MoviesAdapter
import com.gianzra.expert.core.utils.SortUtils
import com.gianzra.expert.submission.R
import com.gianzra.expert.submission.databinding.FragmentTvShowsBinding
import com.gianzra.expert.submission.detail.DetailActivity
import com.gianzra.expert.submission.home.SearchViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.android.viewmodel.ext.android.viewModel

@ExperimentalCoroutinesApi
@FlowPreview
class TvShowsFragment : Fragment() {

    private var binding: FragmentTvShowsBinding? = null
    private val viewModel: TvShowsViewModel by viewModel()
    private lateinit var tvShowsAdapter: MoviesAdapter
    private var sort = SortUtils.RANDOM
    private val searchViewModel: SearchViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTvShowsBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initTvShowsAdapter()
        setupTvShowsRecyclerView()
        setTvShowsList(sort)
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

    private fun initTvShowsAdapter() {
        tvShowsAdapter = MoviesAdapter()
        tvShowsAdapter.onItemClick = { selectedData ->
            val intent = Intent(activity, DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_MOVIE, selectedData)
            startActivity(intent)
        }
    }

    private fun setupTvShowsRecyclerView() {
        with(binding?.rvTvShows) {
            this?.layoutManager = LinearLayoutManager(context)
            this?.setHasFixedSize(true)
            this?.adapter = tvShowsAdapter
        }
    }

    private fun setTvShowsList(sort: String) {
        viewModel.getTvShows(sort).observe(viewLifecycleOwner, tvShowsObserver)
    }

    private val tvShowsObserver = Observer<Resource<List<Movie>>> { tvShows ->
        when (tvShows) {
            is Resource.Loading -> showLoadingState()
            is Resource.Success -> showTvShowsData(tvShows.data)
            is Resource.Error -> showErrorState()
        }
    }

    private fun showLoadingState() {
        binding?.progressBar?.visibility = View.VISIBLE
        binding?.notFound?.visibility = View.GONE
        binding?.notFoundText?.visibility = View.GONE
    }

    private fun showTvShowsData(data: List<Movie>?) {
        binding?.progressBar?.visibility = View.GONE
        if (data.isNullOrEmpty()) {
            showEmptyState()
        } else {
            hideEmptyState()
            tvShowsAdapter.setData(data)
        }
    }

    private fun showEmptyState() {
        binding?.notFound?.visibility = View.VISIBLE
        binding?.notFoundText?.visibility = View.VISIBLE
    }

    private fun hideEmptyState() {
        binding?.notFound?.visibility = View.GONE
        binding?.notFoundText?.visibility = View.GONE
    }

    private fun showErrorState() {
        binding?.progressBar?.visibility = View.GONE
        showEmptyState()
        Toast.makeText(context, "Terjadi kesalahan", Toast.LENGTH_SHORT).show()
    }

    private fun setupSortButtons() {
        binding?.random?.setOnClickListener { setSortAndList(SortUtils.RANDOM) }
        binding?.newest?.setOnClickListener { setSortAndList(SortUtils.NEWEST) }
        binding?.popularity?.setOnClickListener { setSortAndList(SortUtils.POPULARITY) }
        binding?.vote?.setOnClickListener { setSortAndList(SortUtils.VOTE) }
    }

    private fun setSortAndList(newSort: String) {
        binding?.menu?.close(true)
        sort = newSort
        setTvShowsList(sort)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
