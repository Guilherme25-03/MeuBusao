package com.example.meubusao.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categorias")
data class Categoria(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nome: String,
    val descricao: String
)

@Entity(tableName = "despesas")
data class Despesa(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val titulo: String,
    val valor: Double,
    val categoriaId: Int,
    val data: String,
    val descricao: String
)
