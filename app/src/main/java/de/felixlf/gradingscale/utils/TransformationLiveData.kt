package de.felixlf.gradingscale.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class PairLiveData<A, B>(first: LiveData<A>, second: LiveData<B>) : MediatorLiveData<Pair<A?, B?>>() {
  init {
    addSource(first) { value = it to second.value }
    addSource(second) { value = first.value to it }
  }
}

class TripleLiveData<A, B, C>(first: LiveData<A>, second: LiveData<B>, third: LiveData<C>) : MediatorLiveData<Triple<A?, B?, C?>>() {
  init {
    addSource(first) { value = Triple(it, second.value, third.value) }
    addSource(second) { value = Triple(first.value, it, third.value) }
    addSource(third) { value = Triple(first.value, second.value, it) }
  }
}

fun <A, B> LiveData<A>.combine(other: LiveData<B>): PairLiveData<A, B> {
  return PairLiveData(this, other)
}

fun <A, B, C> LiveData<A>.combine(second: LiveData<B>, third: LiveData<C>): TripleLiveData<A, B, C> {
  return TripleLiveData(this, second, third)
}

fun <T> Flow<T>.updateMutableLD(liveData: MutableLiveData<T>, scope: CoroutineScope) = onEach { liveData.value = it }.launchIn(scope)
