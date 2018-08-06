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
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import br.com.lfsolucoes.colaboracity.model.details
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.GeoQuery
import com.firebase.geofire.GeoQueryEventListener
import com.google.firebase.database.*
import org.jetbrains.anko.*

class MainActivity : AppCompatActivity() {

    //Firebase references
    private var mAuth: FirebaseAuth? = null
    var db : DatabaseReference? = null
    var geofire : GeoFire? = null
    var recyclerView : android.support.v7.widget.RecyclerView? = null
    val problems: MutableList<Problem> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnSair = findViewById<Button>(R.id.btn_sair)
        btnSair!!.setOnClickListener { logoutUser() }

        recyclerView = problem_list_recyclerview
        recyclerView!!.adapter = ProblemListAdapter(problems, this)
        val layoutManager = LinearLayoutManager(this)
        recyclerView!!.layoutManager = layoutManager

        // Aqui é a mágica (A Toolbar será a action bar).
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Agora podemos continuar usando a action bar normalmente
        supportActionBar?.title = "ColaboraCity"

        db = FirebaseDatabase.getInstance().getReference("geo/providers")
        geofire = GeoFire(db)

        val geoQuery : GeoQuery = geofire!!.queryAtLocation(GeoLocation(-7.9352939, -34.8268051), 7.0)

        geoQuery.addGeoQueryEventListener(object : GeoQueryEventListener {
            override fun onKeyEntered(key: String, location: GeoLocation) {
                //Log.i("TAG", String.format("Provider %s is within your search range [%f,%f]", key, location.latitude, location.longitude))
                toast("A chave $key está aqui")

                db!!.child("details").child(key)!!.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onDataChange(snapshot : DataSnapshot) {

                        if (snapshot.exists()) {

                            for(s in snapshot.children) {
                                var details = snapshot.getValue(details::class.java)
                                val imagem = s.getValue()
                            }

                            //for(s in snapshot.child(key).children) {
                                //val imagem = s.getValue()
                                //val pontoReferencia = s.getValue()
                                //val tituloProblema = s.getValue()
                            //}


                            // run some code to add user
                            //var details = snapshot.getValue(details::class.java)
                            //val imagem = details?.imagem
                            //val pontoReferencia = details?.pontoReferencia
                            //val tituloProblema = details?.tituloProblema

                        }
                    }
                })

                //var p = Problem()
                //problems.add(p)
                //recyclerView!!.adapter?.notifyDataSetChanged()
            }

            override fun onKeyExited(key: String) {
                Log.i("TAG", String.format("Provider %s is no longer in the search area", key))
            }

            override fun onKeyMoved(key: String, location: GeoLocation) {
                Log.i("TAG", String.format("Provider %s moved within the search area to [%f,%f]", key, location.latitude, location.longitude))
            }

            override fun onGeoQueryReady() {
                Log.i("TAG", "onGeoQueryReady")
            }

            override fun onGeoQueryError(error: DatabaseError) {
                Log.e("TAG", "error: " + error)
            }
        })
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
