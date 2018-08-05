package br.com.lfsolucoes.colaboracity.ui.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import br.com.lfsolucoes.colaboracity.R
import br.com.lfsolucoes.colaboracity.utils.ImageUtils
import br.com.lfsolucoes.colaboracity.utils.PermissionUtils
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import java.io.File

class CadastroProblemaActivity : AppCompatActivity(), OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    // Caminho para salvar o arquivo
    var file: File? = null
    val imgView: ImageView by lazy  { findViewById<ImageView>(R.id.imagemCam)}
    var map: GoogleMap? = null
    var mapFragment: SupportMapFragment? = null
    var mGoogleApiClient: GoogleApiClient? = null
    private val TAG = "ColaboraCity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro_problema)

        // Aqui é a mágica (A Toolbar será a action bar).
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Agora podemos continuar usando a action bar normalmente
        supportActionBar?.title = "Cadastrar Problema"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val context = this
        val b = findViewById<View>(R.id.btAbrirCamera)
        b.setOnClickListener {
            // (*1*) Cria o caminho do arquivo no sdcard
            // /storage/sdcard/Android/data/br.com.livroandroid.multimidia/files/Pictures/foto.jpg
            val f = getSdCardFile("foto.jpg")
            file = f;
            Log.d("livro", "Camera file: $f")
            // Chama a intent informando o arquivo para salvar a foto
            val i = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val uri = FileProvider.getUriForFile(context, context.applicationContext.packageName + ".provider", f)
            i.putExtra(MediaStore.EXTRA_OUTPUT, uri)
            startActivityForResult(i, 0)
        }

        if (savedInstanceState != null) {
            // (*2) Se girou a tela recupera o estado
            file = savedInstanceState.getSerializable("file") as File
            showImage(file)
        }

        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment?.getMapAsync(this)

        // Configura o objeto GoogleApiClient
        mGoogleApiClient = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()

        // Solicita as permissões
        PermissionUtils.validate(this, 0,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION)




    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home){
            finish()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    // Cria um arquivo no sdcard privado do aplicativo
    fun getSdCardFile(fileName: String): File {
        val sdCardDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (!sdCardDir.exists()) {
            sdCardDir.mkdir()
        }
        val file = File(sdCardDir, fileName)
        return file
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // (*3*) Salvar o estado caso gire a tela
        outState.putSerializable("file", file)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("foto", "resultCode: " + resultCode)
        if (resultCode == Activity.RESULT_OK) {
            // (*4*) Se a câmera retornou, vamos mostrar o arquivo da foto
            showImage(file)
        }
    }

    // Atualiza a imagem na tela
    private fun showImage(file: File?) {
        if (file != null && file.exists()) {
            val w = imgView.width
            val h = imgView.height
            // (*5*) Redimensiona a imagem para o tamanho do ImageView
            val bitmap = ImageUtils.resize(file, w, h)
            toast("w/h:" + imgView.width + "/" + imgView.height + " > " + "w/h:" + bitmap.width + "/" + bitmap.height)
            toast("file:" + file)
            imgView.setImageBitmap(bitmap)
        }
    }

    fun toast(s: String) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        for (result in grantResults) {
            if (result == PackageManager.PERMISSION_DENIED) {
                // Alguma permissão foi negada, agora é com você :-)
                alertAndFinish()
                return
            }
        }
        // Se chegou aqui está OK :-)
    }

    private fun alertAndFinish() {
        run {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(R.string.app_name).setMessage("Para utilizar este aplicativo, você precisa aceitar as permissões.")
            // Add the buttons
            builder.setPositiveButton("OK") { dialog, id -> finish() }
            val dialog = builder.create()
            dialog.show()

        }
    }

    override fun onMapReady(map: GoogleMap) {
        Log.d(TAG, "onMapReady: " + map)
        this.map = map

        // Configura o tipo do mapa
        map.mapType = GoogleMap.MAP_TYPE_NORMAL


    }

    override fun onStart() {
        super.onStart()
        // Conecta no Google Play Services
        mGoogleApiClient?.connect()
    }

    override fun onStop() {
        // Desconecta
        mGoogleApiClient?.disconnect()
        super.onStop()
    }

    override fun onConnected(bundle: Bundle?) {
        toast("Conectado no Google Play Services!")
        getLastLocation()
    }

    override fun onConnectionSuspended(cause: Int) {
        toast("Conexão interrompida.")
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        toast("Erro ao conectar: " + connectionResult)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun getLastLocation() {

        val fusedClient = LocationServices.getFusedLocationProviderClient(this)

        // Verifica permissões
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Este "if" o Android Studio pede para colocar.
            // Alguma permissão foi negada, agora é com você :-)
            alertAndFinish()
            return
        }

        // Fused Location Provider API
        fusedClient.lastLocation
                .addOnSuccessListener { location ->
                    // Atualiza a localização do mapa
                    setMapLocation(location)
                }
                .addOnFailureListener {
                    Log.d(TAG, "Não foi possível ao buscar a localização do GPS")
                }
    }

    // Atualiza a coordenada do mapa
    private fun setMapLocation(l: Location) {
        if (map != null) {
            val latLng = LatLng(l.latitude, l.longitude)
            val update = CameraUpdateFactory.newLatLngZoom(latLng, 15f)
            map?.animateCamera(update)

            Log.d(TAG, "setMapLocation: " + l)
            //val text = findViewById<TextView>(R.id.text)
            //text.text = String.format("Lat/Lnt: ${l.latitude}/${l.longitude}, provider: ${l.provider}")

            // Desenha uma bolinha vermelha
            val circle = CircleOptions().center(latLng)
            circle.fillColor(Color.RED)
            circle.radius(25.0) // Em metros
            map?.clear()
            map?.addCircle(circle)
        }
    }
}
