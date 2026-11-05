
package com.example.meubusao.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.meubusao.data.repository.CategoriaRepository
import com.example.meubusao.data.repository.DespesaRepository

class CategoriaViewModelFactory(
    private val repository: CategoriaRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoriaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CategoriaViewModel(repository) as T
        }

        throw IllegalArgumentException("ViewModel desconhecido")
    }
}

class DespesaViewModelFactory(
    private val repository: DespesaRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DespesaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DespesaViewModel(repository) as T
        }

        throw IllegalArgumentException("ViewModel desconhecido")
    }
}
