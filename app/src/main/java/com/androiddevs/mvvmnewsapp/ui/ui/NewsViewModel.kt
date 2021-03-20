package com.androiddevs.mvvmnewsapp.ui.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevs.mvvmnewsapp.ui.models.Article
import com.androiddevs.mvvmnewsapp.ui.models.NewsResponce
import com.androiddevs.mvvmnewsapp.ui.repository.NewsRepository
import com.androiddevs.mvvmnewsapp.ui.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class NewsViewModel(    val newsRepository : NewsRepository) : ViewModel() {
    val breakingNews : MutableLiveData<Resource<NewsResponce>> = MutableLiveData()
    var breakingNewsPage = 1

    val searchNews : MutableLiveData<Resource<NewsResponce>> = MutableLiveData()
    var searchNewsPage = 1
    init {
        getBreakingNews("in")
    }

    fun getBreakingNews(coutrtyCode : String) = viewModelScope.launch {
        breakingNews.postValue(Resource.Loading())

        val responce = newsRepository.getBreakingNews(coutrtyCode,breakingNewsPage)
        breakingNews.postValue(handleBreakingNewsResponce(responce))


    }
    fun searchNews(searchQuery : String) = viewModelScope.launch {
        searchNews.postValue(Resource.Loading())

        val responce = newsRepository.SearchNews(searchQuery,searchNewsPage)
        searchNews.postValue(handleSearchNewsResponce(responce))


    }


    private  fun  handleBreakingNewsResponce(responce: Response<NewsResponce>) : Resource<NewsResponce>{
        if(responce.isSuccessful){
            responce.body()?.let { resultresponce ->
                return  Resource.Sucess(resultresponce)
            }
        }

        return Resource.Error(responce.message())
    }

    private  fun  handleSearchNewsResponce(responce: Response<NewsResponce>) : Resource<NewsResponce>{
        if(responce.isSuccessful){
            responce.body()?.let { resultresponce ->
                return  Resource.Sucess(resultresponce)
            }
        }

        return Resource.Error(responce.message())
    }

    fun saveArticle(article : Article) = viewModelScope.launch {
        newsRepository.upsert(article)
    }
    fun getSavedNews() = newsRepository.getSavedList()

    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.deleteArticle(article)
    }


}