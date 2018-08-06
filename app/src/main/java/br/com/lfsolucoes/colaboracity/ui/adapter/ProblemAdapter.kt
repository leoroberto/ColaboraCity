package br.com.lfsolucoes.colaboracity.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.Adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.lfsolucoes.colaboracity.R
import br.com.lfsolucoes.colaboracity.model.Problem
import kotlinx.android.synthetic.main.problema_item.view.*

class ProblemListAdapter(private val problems: List<Problem>,
                          private val context: Context) : Adapter<ProblemListAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val problem = problems[position]
        holder?.let {
            it.bindView(problem)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.problema_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return problems.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindView(problem: Problem) {
            val problema = itemView.problema_item
            val pontoReferencia = itemView.problema_item_referencia
            val imagem = itemView.problema_item_imagem

            problema.text = problem.problema
            pontoReferencia.text = problem.pontoReferencia
        }

    }

}