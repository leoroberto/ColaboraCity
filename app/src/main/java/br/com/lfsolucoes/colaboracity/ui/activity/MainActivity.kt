package br.com.lfsolucoes.colaboracity.ui.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import br.com.lfsolucoes.colaboracity.R
import br.com.lfsolucoes.colaboracity.model.Problem
import br.com.lfsolucoes.colaboracity.ui.adapter.ProblemListAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_main.*
import android.support.v4.view.MenuItemCompat
import android.support.v7.widget.SearchView
import android.support.v7.widget.ShareActionProvider
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import org.jetbrains.anko.*

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

        // Aqui é a mágica (A Toolbar será a action bar).
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Agora podemos continuar usando a action bar normalmente
        supportActionBar?.title = "ColaboraCity"
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Infla o menu com os botões da action bar
        menuInflater.inflate(R.menu.menu_main, menu)

        // SearchView
        val item = menu.findItem(R.id.action_search)
        val searchView = item.actionView as SearchView
        searchView.setOnQueryTextListener(onSearch())

        // ShareActionProvider
        val shareItem = menu.findItem(R.id.action_share)
        val share = MenuItemCompat.getActionProvider(shareItem) as ShareActionProvider
        share.setShareIntent(defaultIntent)

        return true
    }

    // Intent que define o conteúdo que será compartilhado
    private val defaultIntent: Intent
        get() {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/*"
            intent.putExtra(Intent.EXTRA_TEXT, "Texto para compartilhar")
            return intent
        }

    private fun onSearch(): SearchView.OnQueryTextListener {
        return object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                toast("Buscar o texto: " + query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_search) {
            toast("Clicou no Search!")
            return true
        } else if (id == R.id.action_refresh) {
            toast("Clicou no Refresh!")
            return true
        } else if (id == R.id.action_settings) {
            toast("Clicou no Settings!")
            return true
        } else if(id == R.id.action_add){
            startActivity(Intent(this@MainActivity, CadastroProblemaActivity::class.java))
            //criarJanelaDeCadastro()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
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

    fun criarJanelaDeCadastro() {
        lateinit var tNone: EditText
        lateinit var tDescricao: EditText
        lateinit var tTipo: Spinner

        val problemsArray = arrayOf("Buraco", "Sinal Quebrado")

        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, problemsArray)



        alert {
            title = "Cadastrar Problema"
            isCancelable = false
            customView {
                verticalLayout {
                    tNone = editText {
                        hint = "Digite o nome"
                    }
                    tDescricao = editText {
                        hint = "Digite a descrição"
                    }

                    tTipo = spinner {adapter = arrayAdapter}
                }
            }
            okButton {
                //Ação de salvar no firebase
                toast("Salvou!")
            }
            cancelButton {  }
        }.show()
    }

    private fun logoutUser(){
        FirebaseAuth.getInstance().signOut()
        finish()
    }


}
