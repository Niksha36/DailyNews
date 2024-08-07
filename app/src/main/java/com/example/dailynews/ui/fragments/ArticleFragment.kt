package com.example.dailynews.ui.fragments

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.dailynews.R
import com.example.dailynews.databinding.FragmentArticleBinding
import com.example.dailynews.ui.NewsActivity
import com.example.dailynews.ui.NewsViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch


class ArticleFragment: Fragment(R.layout.fragment_article) {
    lateinit var viewModel: NewsViewModel
    lateinit var binding: FragmentArticleBinding
    //ArticleFragmentArgs will be provided by navArgs
    val args:ArticleFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).viewModel
        binding = FragmentArticleBinding.bind(view)
        //connecting toolbar
        val toolbar = binding.toolbar
        (activity as? AppCompatActivity)?.setSupportActionBar(toolbar)
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayShowHomeEnabled(true)
        (activity as? AppCompatActivity)?.supportActionBar?.title = ""
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        val article = args.article
        binding.webView.apply{
            webViewClient = WebViewClient()
            article.url?.let { loadUrl(it) }
        }

        viewModel.isArticleInDb(article.url).observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.fab.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.red))
            }else{
                binding.fab.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.blue))
            }
        })

        binding.fab.setOnClickListener {
            viewModel.isArticleInDb(article.url).observe(viewLifecycleOwner, Observer {
                if(it) {
                    viewModel.deleteArticle(article)
                binding.fab.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.blue))
                val snackbar =
                    Snackbar.make(view, "Article was deleted", Snackbar.LENGTH_SHORT)
                snackbar.anchorView =
                    (activity as NewsActivity).findViewById(R.id.bottomNavigationView)
                snackbar.show()
                } else{
                    viewModel.saveArticle(article)
                    binding.fab.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.red))
                    val snackbar =
                        Snackbar.make(view, "Article was successfully saved", Snackbar.LENGTH_SHORT)
                    snackbar.anchorView =
                        (activity as NewsActivity).findViewById(R.id.bottomNavigationView)
                    snackbar.show()
                }
            })
        }
    }
}