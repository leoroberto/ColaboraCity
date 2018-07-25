package br.com.lfsolucoes.colaboracity.ui.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.widget.Button
import br.com.lfsolucoes.colaboracity.R
import br.com.lfsolucoes.colaboracity.model.Problem
import br.com.lfsolucoes.colaboracity.ui.adapter.ProblemListAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    //Firebase references
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnSair = findViewById<Button>(R.id.btn_sair)
        btnSair!!.setOnClickListener { logoutUser() }

        val recyclerView = problem_list_recyclerview
        recyclerView.adapter = ProblemListAdapter(problems(), this)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
    }

    private fun problems(): List<Problem> {
        return listOf(
                Problem("Leitura",
                        "Livro de Kotlin com Android"),
                Problem("Pesquisa",
                        "Como posso melhorar o código dos meus projetos"),
                Problem("Estudo",
                        "Como sincronizar minha App com um Web Service"))
    }

    override fun onStart() {
        super.onStart()
        var fbUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

        if(fbUser == null){
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        }
    }

    private fun logoutUser(){
        FirebaseAuth.getInstance().signOut()
        finish()
    }
}
