package com.digitaldream.ddl.utils

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class EndlessRecyclerViewScrollListener(private val sLayoutManager: LinearLayoutManager) :
    RecyclerView.OnScrollListener() {

    private val visibleThreshold = 5
    private var currentPage = 0
    private var previousTotalItemCount = 0
    private val startingPageIndex = 0
    private var loading = true

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        val totalItemCount: Int = sLayoutManager.itemCount
        val lastVisibleItemPosition: Int = sLayoutManager.findLastVisibleItemPosition()

        if (loading && (totalItemCount > previousTotalItemCount)) {
            loading = false
            previousTotalItemCount = totalItemCount
        }

        if (!loading && (lastVisibleItemPosition + visibleThreshold) > totalItemCount) {
            currentPage++
            onLoadMore(currentPage, totalItemCount, recyclerView)
            loading = true
        }
    }

    fun resetState() {
        currentPage = startingPageIndex
        previousTotalItemCount = 0
        loading = true
    }

    abstract fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView)

}