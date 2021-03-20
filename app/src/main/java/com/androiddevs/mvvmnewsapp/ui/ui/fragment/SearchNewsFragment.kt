package com.androiddevs.mvvmnewsapp.ui.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.ui.adapters.NewsAdapter
import com.androiddevs.mvvmnewsapp.ui.ui.NewsActivity
import com.androiddevs.mvvmnewsapp.ui.ui.NewsViewModel
import com.androiddevs.mvvmnewsapp.ui.util.Resource
import kotlinx.android.synthetic.main.fragment_search_news.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchNewsFragment :Fragment (R.layout.fragment_search_news){

     lateinit var newsAdapter: NewsAdapter
    lateinit var  viewmodel : NewsViewModel
    val TAG = "SearchNewsFragment"
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewmodel = (activity as NewsActivity).viewModel
        setupRecylerView()
        newsAdapter.setOnItemClickListner {
            val bundle = Bundle().apply {
                putSerializable("article",it)
            }
            findNavController().navigate(
                R.id.action_searchNewsFragment_to_articleFragment,
                bundle
            )
        }
        var job : Job? = null
        etSearch.addTextChangedListener {editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(500L)
                editable?.let {
                    if(editable.toString().isNotEmpty()){
                        viewmodel.searchNews(editable.toString())
                    }
                }
            }
        }

        viewmodel.searchNews.observe(viewLifecycleOwner, Observer { responce ->

            when(responce) {
                is Resource.Sucess -> {
                    hideProgressView()
                    responce.data?.let { searchResponce ->
                        newsAdapter.difer.submitList(searchResponce.articles)
                    }
                }

                is Resource.Error -> {
                    hideProgressView()
                    responce.message?.let { message ->
                        Log.e(TAG,"An Error occured $message")
                    }
                }

                is Resource.Loading -> {
                    showProgressView()
                }

            }
        })


    }
    private fun showProgressView(){
        paginationProgressBar.visibility = View.VISIBLE
    }

    private fun hideProgressView(){
        paginationProgressBar.visibility = View.INVISIBLE
    }

    private fun setupRecylerView(){
     newsAdapter = NewsAdapter()
        rvSearchNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }

    }
}
