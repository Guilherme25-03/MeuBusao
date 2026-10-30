package com.example.meubusao.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface CategoriaDao {
    @Insert
    suspend fun inserir(categoria: Categoria)

    @Query("SELECT * FROM categorias ORDER BY nome")
    suspend fun buscarTodos(): List<Categoria>

    @Update
    suspend fun atualizar(categoria: Categoria)

    @Delete
    suspend fun excluir(categoria: Categoria)

    @Query("SELECT * FROM categorias WHERE id = :id LIMIT 1")
    suspend fun buscarPorId(id: Int): Categoria?
}

@Dao
interface DespesaDao {
    @Insert
    suspend fun inserir(despesa: Despesa)

    @Query("SELECT * FROM despesas ORDER BY id DESC")
    suspend fun buscarTodos(): List<Despesa>

    @Update
    suspend fun atualizar(despesa: Despesa)

    @Delete
    suspend fun excluir(despesa: Despesa)

    @Query("SELECT * FROM despesas WHERE id = :id LIMIT 1")
    suspend fun buscarPorId(id: Int): Despesa?

    @Query("SELECT * FROM despesas WHERE categoriaId = :categoriaId ORDER BY id DESC")
    suspend fun buscarPorCategoria(categoriaId: Int): List<Despesa>
}
