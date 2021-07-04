package com.telkomsel.hometestmusicplayer

import android.util.Log
import org.junit.Rule
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.telkomsel.hometestmusicplayer.ui.main.viewmodel.MusicViewModel
import org.mockito.Mock
import androidx.lifecycle.Observer
import com.telkomsel.hometestmusicplayer.data.model.MusicModel
import com.telkomsel.hometestmusicplayer.data.network.RetrofitClient
import com.telkomsel.hometestmusicplayer.data.repository.MusicRepository
import junit.framework.Assert.assertNotNull
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.MockitoAnnotations
import java.net.HttpURLConnection


@RunWith(MockitoJUnitRunner::class)
class RequestViewModelTest {

    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()
    private lateinit var viewModel: MusicViewModel
    private val apiService = RetrofitClient.create()


    @Mock
    private lateinit var apiMusicObserver: Observer<MusicModel>

    private lateinit var mockWebServer: MockWebServer

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        viewModel = MusicViewModel()
        viewModel.getMusic().observeForever(apiMusicObserver)

        mockWebServer= MockWebServer()
        mockWebServer.start()
    }


    @Test
    fun `read sample success json file`(){
        val reader = MockResponseFileReader("success_response.json")
        assertNotNull(reader.content)
    }

    @Test
    fun `get search musics and check response Code 200 returned`(){
        // Assign
        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(MockResponseFileReader("success_response.json").content)
        mockWebServer.enqueue(response)

        // Act
        val actualResponse = apiService.getMusicFromTest("Iwan Fals", "music").execute()
        // Assert
        assertEquals(response.toString().contains("200"),actualResponse.code().toString().contains("200"))
    }

    @Test
    fun `get search musics and check response success returned`(){
        // Assign
        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(MockResponseFileReader("success_response.json").content)
        mockWebServer.enqueue(response)

        val mockResponse = response.getBody()?.readUtf8()
        // Act
        val  actualResponse = apiService.getMusicFromTest("Iwan Fals", "music").execute()
        // Assert
        assertEquals(mockResponse?.let { `parse mocked JSON response`(it) }, actualResponse.body()?.resultCount)
    }

    private fun `parse mocked JSON response`(mockResponse: String): Int {
        val reader = JSONObject(mockResponse)
        return reader.getInt("resultCount")
    }

    @Test
    fun `get musics for failed response 400 returned`(){
        // Assign
        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST)
            .setBody(MockResponseFileReader("failed_response.json").content)
        mockWebServer.enqueue(response)
        // Act
        val  actualResponse = apiService.getMusicFromTest("Iwan Fals", "musicss").execute()
        // Assert
        assertEquals(response.toString().contains("400"),actualResponse.toString().contains("400"))
    }

    @After
    fun tearDown() {
        viewModel.getMusic().removeObserver(apiMusicObserver)
        mockWebServer.shutdown()
    }

}