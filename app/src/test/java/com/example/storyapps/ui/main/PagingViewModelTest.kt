package com.example.storyapps.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.example.storyapps.DataDummy
import com.example.storyapps.MainDispatcher
import com.example.storyapps.TestPagingSource
import com.example.storyapps.getOrAwait
import com.example.storyapps.paging.StoryRepository
import com.example.storyapps.response.Story
import com.example.storyapps.ui.adapter.StoryAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class StoryPagerViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcher()

    @Mock
    private lateinit var storyRepository: StoryRepository

    @Test
    fun `when Get Story Not Null and Return Data`() = runTest {
        val storyDummy = DataDummy.dummyStoryResponse()
        val data: PagingData<Story> = TestPagingSource.snapshot(storyDummy)
        val expectedStory = MutableLiveData<PagingData<Story>>()
        expectedStory.value = data
        Mockito.`when`(storyRepository.getStory(anyString())).thenReturn(expectedStory)

        val storyViewModel = PagingViewModel(storyRepository)
        val actualStory: PagingData<Story> = storyViewModel.story("token").getOrAwait()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStory)

        assertNotNull(differ.snapshot())
        assertEquals(storyDummy.size, differ.snapshot().size)
        assertEquals(storyDummy[0], differ.snapshot()[0])
    }

    @Test
    fun `when Get Story Empty Return No Data`() = runTest {
        val data: PagingData<Story> = PagingData.from(emptyList())
        val expectedStory = MutableLiveData<PagingData<Story>>()
        expectedStory.value = data
        Mockito.`when`(storyRepository.getStory(anyString())).thenReturn(expectedStory)

        val storyViewModel = PagingViewModel(storyRepository)
        val actualStory: PagingData<Story> = storyViewModel.story("token").getOrAwait()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStory)

        assertEquals(0, differ.snapshot().size)
    }
}


val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {
    }
    override fun onRemoved(position: Int, count: Int) {
    }
    override fun onMoved(fromPosition: Int, toPosition: Int) {
    }
    override fun onChanged(position: Int, count: Int, payload: Any?) {
    }
}