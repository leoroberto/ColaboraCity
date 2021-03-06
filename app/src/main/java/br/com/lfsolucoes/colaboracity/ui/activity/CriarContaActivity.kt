package br.com.lfsolucoes.colaboracity.ui.activity

import android.app.ProgressDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import br.com.lfsolucoes.colaboracity.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CriarContaActivity : AppCompatActivity() {

    private var etFirstName: EditText? = null
    private var etLastName: EditText? = null
    private var etEmail: EditText? = null
    private var etPassword: EditText? = null
    private var btnCreateAccount: Button? = null
    private var mProgressBar: ProgressDialog? = null

    //Firebase references
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null

    private val TAG = "CreateAccountActivity"

    //global variables
    private var firstName: String? = null
    private var lastName: String? = null
    private var email: String? = null
    private var password: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_criar_conta)

        initialise()
    }

    private fun initialise() {

        etFirstName = findViewById<TextView>(R.id.et_first_name) as EditText
        etLastName = findViewById<TextView>(R.id.et_last_name) as EditText
        etEmail = findViewById<TextView>(R.id.et_email) as EditText
        etPassword = findViewById<TextView>(R.id.et_password) as EditText
        btnCreateAccount = findViewById<Button>(R.id.btn_register)
        mProgressBar = ProgressDialog(this)

        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference!!.child("Users")
        mAuth = FirebaseAuth.getInstance()

        btnCreateAccount!!.setOnClickListener { createNewAccount() }
    }

    private fun createNewAccount() {
        firstName = etFirstName?.text.toString()
        lastName = etLastName?.text.toString()
        email = etEmail?.text.toString()
        password = etPassword?.text.toString()

        if (!TextUtils.isEmpty(firstName) && !TextUtils.isEmpty(lastName)
                && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {

            mProgressBar!!.setMessage("Cadastrando Usuário...")
            mProgressBar!!.show()

            mAuth!!
                    .createUserWithEmailAndPassword(email!!, password!!)
                    .addOnCompleteListener(this) { task ->
                        mProgressBar!!.hide()

                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success")

                            val userId = mAuth!!.currentUser!!.uid

                            //Verify Email
                            verifyEmail()

                            //update user profile information
                            val currentUserDb = mDatabaseReference!!.child(userId)
                            currentUserDb.child("firstName").setValue(firstName)
                            currentUserDb.child("lastName").setValue(lastName)

                            updateUserInfoAndUI()
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.exception)
                            Toast.makeText(this@CriarContaActivity, "Autenticação Falha.",
                                    Toast.LENGTH_SHORT).show()
                        }
                    }

        } else {
            Toast.makeText(this, "Todos os campos são obrigatórios", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUserInfoAndUI() {

        //start next activity
        val intent = Intent(this@CriarContaActivity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    private fun verifyEmail() {
        val mUser = mAuth!!.currentUser;
        mUser!!.sendEmailVerification()
                .addOnCompleteListener(this) { task ->

                    if (task.isSuccessful) {
                        Toast.makeText(this@CriarContaActivity,
                                "Verificação de email enviada para " + mUser.getEmail(),
                                Toast.LENGTH_SHORT).show()
                    } else {
                        Log.e(TAG, "sendEmailVerification", task.exception)
                        Toast.makeText(this@CriarContaActivity,
                                "Falha ao enviar verificação de email.",
                                Toast.LENGTH_SHORT).show()
                    }
                }
    }

}
