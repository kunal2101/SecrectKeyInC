package com.androiddevs.mvvmnewsapp.ui.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.ui.adapters.NewsAdapter
import com.androiddevs.mvvmnewsapp.ui.ui.NewsActivity
import com.androiddevs.mvvmnewsapp.ui.ui.NewsViewModel
import com.androiddevs.mvvmnewsapp.ui.util.Resource
import kotlinx.android.synthetic.main.fragment_breaking_news.*

class BrakingNewsFragment : Fragment(R.layout.fragment_breaking_news) {

    lateinit var viewmodel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter

    val TAG = "BrakingNewsFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewmodel = (activity as NewsActivity).viewModel
        setupRecyclerView()

        newsAdapter.setOnItemClickListner {
          val bundle = Bundle().apply {
              putSerializable("article",it)
          }
            findNavController().navigate(
                R.id.action_brakingNewsFragment_to_articleFragment,
                bundle
            )
        }

        viewmodel.breakingNews.observe(viewLifecycleOwner, Observer { response ->
            when(response) {
                is Resource.Sucess -> {
                    hideProgressBar()
                    response.data?.let { newsResponce ->
                        newsAdapter.difer.submitList(newsResponce.articles)
                    }
                }

                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Log.e(TAG,"An Error occured $message")
                    }
                }

                is Resource.Loading -> {

                    showProgressBar()
                }
            }
        })

    }

    private fun hideProgressBar() {

        paginationProgressBar.visibility = View.INVISIBLE
    }

    private fun showProgressBar() {

        paginationProgressBar.visibility = View.VISIBLE
    }



    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter()
        rvBreakingNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }
}
