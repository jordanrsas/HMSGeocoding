package com.dtse.cjra.hmsgeocoding

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.huawei.agconnect.AGConnectOptionsBuilder
import com.huawei.hms.location.GetFromLocationRequest
import com.huawei.hms.location.HWLocation
import com.huawei.hms.location.LocationServices
import com.huawei.hms.maps.*
import com.huawei.hms.maps.HuaweiMap.OnMapLongClickListener
import com.huawei.hms.maps.model.BitmapDescriptorFactory
import com.huawei.hms.maps.model.CameraPosition
import com.huawei.hms.maps.model.LatLng
import com.huawei.hms.maps.model.MarkerOptions
import com.huawei.location.lite.common.util.ExecutorUtil
import com.huawei.location.lite.common.util.GsonUtil
import kotlinx.android.synthetic.main.activity_reverse_geocoding.*
import java.util.*

class ReverseGeocodingActivity : AppCompatActivity(), OnMapReadyCallback, OnMapLongClickListener {

    private var huaweiMap: HuaweiMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val apiKey = AGConnectOptionsBuilder().build(this).getString("client/api_key")
        MapsInitializer.setApiKey(apiKey)
        setContentView(R.layout.activity_reverse_geocoding)
        rgMap.onCreate(null)
        rgMap.getMapAsync(this)
    }

    override fun onMapReady(hMap: HuaweiMap?) {
        huaweiMap = hMap

        val cameraUpdate =
            CameraUpdateFactory.newLatLngZoom(LatLng(19.435968331401817, -99.14430559352652), 15f)

        huaweiMap?.apply {
            setOnMapLongClickListener(this@ReverseGeocodingActivity)
            animateCamera(cameraUpdate)
        }
    }

    override fun onMapLongClick(latLng: LatLng?) {
        huaweiMap?.clear()

        val coordinates: String = " " + latLng?.latitude + ", " + latLng?.longitude
        latLngEditText.setText(coordinates)

        val icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
        val marker: MarkerOptions = MarkerOptions().apply {
            this.position(latLng)
            this.title("Locación seleccionada")
            this.snippet("Coordenadas: $coordinates")
            this.icon(icon)
        }
        huaweiMap?.addMarker(marker)
        showProgressDialog()
        getReverseGeocoding(latLng)
    }

    private fun getReverseGeocoding(latLng: LatLng?) {
        val locale = Locale("es", "MX")
        val geocoderService = LocationServices.getGeocoderService(this, locale)

        val getFromLocationRequest = latLng?.let {
            GetFromLocationRequest(it.latitude, it.longitude, 5)
        }

        ExecutorUtil.getInstance().execute {
            geocoderService.getFromLocation(getFromLocationRequest)
                .addOnSuccessListener { hwLocation: MutableList<HWLocation> ->
                    printGeocoderResult(hwLocation)
                }.addOnFailureListener {
                    printError(it)
                }
        }
    }

    private fun printError(error: Exception) {
        logText.append(error.message + System.lineSeparator())
        hideProgressDialog()
    }

    private fun printGeocoderResult(geocoderResult: MutableList<HWLocation>) {
        for (hwLocation in geocoderResult) {
            val builder = StringBuilder()
            builder.append("Location:{latitude=")
                .append(hwLocation.latitude)
                .append(",longitude=")
                .append(hwLocation.longitude)
                .append(",countryName=")
                .append(hwLocation.countryName)
                .append(",countryCode=")
                .append(hwLocation.countryCode)
                .append(",state=")
                .append(hwLocation.state)
                .append(",city=")
                .append(hwLocation.city)
                .append(",county=")
                .append(hwLocation.county)
                .append(",street=")
                .append(hwLocation.street)
                .append(",featureName=")
                .append(hwLocation.featureName)
                .append(",postalCode=")
                .append(hwLocation.postalCode)
                .append(",phone=")
                .append(hwLocation.phone)
                .append(",url=")
                .append(hwLocation.url)
                .append(",extraInfo=")
                .append(GsonUtil.getInstance().toJson(hwLocation.extraInfo))
                .append("}" + System.lineSeparator() + System.lineSeparator())

            logText.append(builder.toString())
        }

        hideProgressDialog()
    }

    private fun showProgressDialog() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressDialog() {
        progressBar.visibility = View.GONE
    }

    override fun onStart() {
        super.onStart()
        rgMap.onStart()
    }

    override fun onResume() {
        super.onResume()
        rgMap.onResume()
    }

    override fun onPause() {
        super.onPause()
        rgMap.onPause()
    }

    override fun onStop() {
        super.onStop()
        rgMap.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        rgMap.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        rgMap.onDestroy()
    }
}