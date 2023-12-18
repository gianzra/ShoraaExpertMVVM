package com.gianzra.expert.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gianzra.expert.favorite.databinding.FragmentFavoriteBinding
import com.gianzra.expert.favorite.di.favoriteModule
import org.koin.core.context.loadKoinModules

class FavoriteFragment : Fragment() {

    private lateinit var binding: FragmentFavoriteBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeKoinModules()
        setupViewPager()
    }

    private fun initializeKoinModules() {
        loadKoinModules(favoriteModule)
    }

    private fun setupViewPager() {
        with(binding) {
            viewPager.adapter = SectionsPagerAdapter(requireContext(), childFragmentManager)
            tab.setupWithViewPager(viewPager)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.viewPager.adapter = null
    }
}
