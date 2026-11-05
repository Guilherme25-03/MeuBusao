
package com.example.meubusao.data.repository

import com.example.meubusao.data.Categoria
import com.example.meubusao.data.CategoriaDao
import kotlinx.coroutines.flow.Flow

class CategoriaRepository(
    private val categoriaDao: CategoriaDao
) {

    fun buscarTodos(): Flow<List<Categoria>> {
        return categoriaDao.buscarTodos()
    }

    suspend fun buscarPorId(id: Int): Categoria? {
        return categoriaDao.buscarPorId(id)
    }

    suspend fun inserir(categoria: Categoria) {
        categoriaDao.inserir(categoria)
    }

    suspend fun atualizar(categoria: Categoria) {
        categoriaDao.atualizar(categoria)
    }

    suspend fun excluir(categoria: Categoria) {
        categoriaDao.excluir(categoria)
    }
}
