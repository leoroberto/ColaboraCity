package br.com.lfsolucoes.colaboracity

import android.app.ProgressDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity() {

    //Firebase references
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnSair = findViewById<Button>(R.id.btn_sair)
        btnSair!!.setOnClickListener { logoutUser() }

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
