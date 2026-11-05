
package com.example.meubusao.data.repository

import com.example.meubusao.data.Despesa
import com.example.meubusao.data.DespesaDao
import kotlinx.coroutines.flow.Flow

class DespesaRepository(
    private val despesaDao: DespesaDao
) {

    fun buscarTodos(): Flow<List<Despesa>> {
        return despesaDao.buscarTodos()
    }

    fun buscarPorCategoria(categoriaId: Int): Flow<List<Despesa>> {
        return despesaDao.buscarPorCategoria(categoriaId)
    }

    suspend fun buscarPorId(id: Int): Despesa? {
        return despesaDao.buscarPorId(id)
    }

    suspend fun inserir(despesa: Despesa) {
        despesaDao.inserir(despesa)
    }

    suspend fun atualizar(despesa: Despesa) {
        despesaDao.atualizar(despesa)
    }

    suspend fun excluir(despesa: Despesa) {
        despesaDao.excluir(despesa)
    }
}
