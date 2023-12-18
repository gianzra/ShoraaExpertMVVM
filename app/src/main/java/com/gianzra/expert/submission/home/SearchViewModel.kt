package com.gianzra.expert.submission.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

@FlowPreview
@ExperimentalCoroutinesApi
class SearchViewModel : ViewModel() {

    private val queryChannel = Channel<String>()

    fun setSearchQuery(search: String) = viewModelScope.launch {
        queryChannel.trySend(search)
    }

}
