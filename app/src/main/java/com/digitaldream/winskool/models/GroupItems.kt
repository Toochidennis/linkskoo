package com.digitaldream.winskool.models

data class GroupItems<K, T>(val title: K?, val transaction: MutableList<T>)
