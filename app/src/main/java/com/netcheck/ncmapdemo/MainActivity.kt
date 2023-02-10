package com.netcheck.ncmapdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.netcheck.ncmapdemo.model.Operator
import com.netcheck.ncmapdemo.system.location.LocationHandler
import com.netcheck.ncmapdemo.tool.NetworkUpdater
import com.netcheck.ncmapdemo.ui.tilemap.TileMapViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.nio.charset.Charset

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getOperatorsFromServer()
        NetworkUpdater.listenToConnectivityChange(applicationContext)
        LocationHandler.obtainUserLocation(applicationContext)
    }

    private fun getOperatorsFromServer(): String? {
        var result: String? = null
        if (TileMapViewModel.GetTiles.operatorArray.isEmpty()) {
            val url = URL("https://api.dev.netcheckapp.com/api/operators")
            CoroutineScope(Dispatchers.IO).launch {
                val outputStream = ByteArrayOutputStream()
                val bb = ByteArray(10000)
                var ins: InputStream? = null
                try {
                    ins = url.openConnection().getInputStream()
                    var i = ins.read(bb)
                    while (i != -1) {
                        outputStream.write(bb, 0, i)
                        i = ins.read(bb)
                    }
                    val bytes = outputStream.toByteArray()
                    result = String(bytes, Charset.forName("UTF-8"))
                    val jsonArray: JSONArray = try {
                        JSONArray(result)
                    } catch (e: Exception) {
                        return@launch e.printStackTrace()
                    }
                    for (c in 0 until jsonArray.length()) {
                        val jProv = jsonArray.getJSONObject(c)
                        val id = jProv.getInt("id")
                        val iso = jProv.getString("iso")
                        val name = jProv.getString("name")
                        TileMapViewModel.GetTiles.operatorArray.add(Operator(id, iso, name))
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return result
    }
}