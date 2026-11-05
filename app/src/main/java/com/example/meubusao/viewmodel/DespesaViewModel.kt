
package com.example.meubusao.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.meubusao.data.Despesa
import com.example.meubusao.data.repository.DespesaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DespesaUiState(
    val despesas: List<Despesa> = emptyList(),
    val carregando: Boolean = false,
    val mensagem: String = ""
)

class DespesaViewModel(
    private val repository: DespesaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DespesaUiState())
    val uiState: StateFlow<DespesaUiState> = _uiState.asStateFlow()

    init {
        observarDespesas()
    }

    private fun observarDespesas() {
        viewModelScope.launch {
            repository.buscarTodos().collect { despesas ->
                _uiState.value = _uiState.value.copy(
                    despesas = despesas,
                    carregando = false
                )
            }
        }
    }

    fun inserir(
        titulo: String,
        valor: Double,
        categoriaId: Int,
        data: String,
        descricao: String
    ) {
        if (titulo.isBlank() || valor <= 0 || categoriaId <= 0) {
            _uiState.value = _uiState.value.copy(
                mensagem = "Preencha os dados da despesa corretamente."
            )
            return
        }

        viewModelScope.launch {
            repository.inserir(
                Despesa(
                    titulo = titulo.trim(),
                    valor = valor,
                    categoriaId = categoriaId,
                    data = data,
                    descricao = descricao.trim()
                )
            )
            limparMensagem()
        }
    }

    fun atualizar(despesa: Despesa) {
        viewModelScope.launch {
            repository.atualizar(despesa)
        }
    }

    fun excluir(despesa: Despesa) {
        viewModelScope.launch {
            repository.excluir(despesa)
        }
    }

    fun informarMensagem(mensagem: String) {
        _uiState.value = _uiState.value.copy(mensagem = mensagem)
    }

    fun limparMensagem() {
        _uiState.value = _uiState.value.copy(mensagem = "")
    }
}
