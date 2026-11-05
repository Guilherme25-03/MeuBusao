
package com.example.meubusao.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.meubusao.data.Categoria
import com.example.meubusao.data.repository.CategoriaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CategoriaUiState(
    val categorias: List<Categoria> = emptyList(),
    val carregando: Boolean = false,
    val mensagem: String = ""
)

class CategoriaViewModel(
    private val repository: CategoriaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoriaUiState())
    val uiState: StateFlow<CategoriaUiState> = _uiState.asStateFlow()

    init {
        observarCategorias()
    }

    private fun observarCategorias() {
        viewModelScope.launch {
            repository.buscarTodos().collect { categorias ->
                _uiState.value = _uiState.value.copy(
                    categorias = categorias,
                    carregando = false
                )
            }
        }
    }

    fun inserir(nome: String, descricao: String) {
        if (nome.isBlank()) {
            informarMensagem("Informe o nome da categoria.")
            return
        }

        viewModelScope.launch {
            repository.inserir(
                Categoria(
                    nome = nome.trim(),
                    descricao = descricao.trim()
                )
            )
            limparMensagem()
        }
    }

    fun atualizar(categoria: Categoria) {
        if (categoria.nome.isBlank()) {
            informarMensagem("Informe o nome da categoria.")
            return
        }

        viewModelScope.launch {
            repository.atualizar(categoria)
            informarMensagem("Categoria atualizada.")
        }
    }

    fun excluir(categoria: Categoria) {
        viewModelScope.launch {
            repository.excluir(categoria)
        }
    }

    fun informarMensagem(mensagem: String) {
        _uiState.value = _uiState.value.copy(mensagem = mensagem)
    }

    fun limparMensagem() {
        _uiState.value = _uiState.value.copy(mensagem = "")
    }
}
