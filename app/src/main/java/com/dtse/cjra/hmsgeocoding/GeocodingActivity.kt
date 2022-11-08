package com.dtse.cjra.hmsgeocoding

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.huawei.agconnect.AGConnectOptionsBuilder
import com.huawei.hms.location.GetFromLocationNameRequest
import com.huawei.hms.location.HWLocation
import com.huawei.hms.location.LocationServices
import com.huawei.hms.maps.CameraUpdateFactory
import com.huawei.hms.maps.HuaweiMap
import com.huawei.hms.maps.MapsInitializer
import com.huawei.hms.maps.OnMapReadyCallback
import com.huawei.hms.maps.model.BitmapDescriptorFactory
import com.huawei.hms.maps.model.LatLng
import com.huawei.hms.maps.model.MarkerOptions
import com.huawei.hms.maps.model.PolygonOptions
import com.huawei.location.lite.common.util.ExecutorUtil
import com.huawei.location.lite.common.util.GsonUtil
import kotlinx.android.synthetic.main.activity_geocoding.*
import java.util.*

class GeocodingActivity : AppCompatActivity(), OnMapReadyCallback {

    private var huaweiMap: HuaweiMap? = null
    private val cdmx = LatLng(19.435968331401817, -99.14430559352652)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val apiKey = AGConnectOptionsBuilder().build(this).getString("client/api_key")
        MapsInitializer.setApiKey(apiKey)
        setContentView(R.layout.activity_geocoding)
        gMap.onCreate(null)
        gMap.getMapAsync(this)

        searchGeocodingButton.setOnClickListener {
            showProgressDialog()
            //huaweiMap?.clear()
            searchGeocodingByName()
        }
    }

    private fun searchGeocodingByName() {
        val address = addressEditText.text.toString()
        if (address.isNotBlank()) {
            val rectangle: List<LatLng> = createRectangle(cdmx, 0.12, 0.12)
            ExecutorUtil.getInstance().execute {
                val getFromLocationNameRequest =
                    GetFromLocationNameRequest(address, 5)
                getFromLocationNameRequest.apply {
                    lowerLeftLatitude = rectangle.get(0).latitude
                    lowerLeftLongitude = rectangle.get(0).longitude
                    upperRightLatitude = rectangle.get(2).latitude
                    upperRightLongitude = rectangle.get(2).longitude
                }

                val locale = Locale("es", "MX")
                val geocoderService =
                    LocationServices.getGeocoderService(this@GeocodingActivity, locale)

                geocoderService.getFromLocationName(getFromLocationNameRequest)
                    .addOnSuccessListener {
                        printGeocodingResults(it)
                    }
                    .addOnFailureListener {
                        printError(it)
                    }
            }
        }
    }

    private fun printGeocodingResults(geocoderResult: List<HWLocation>) {
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
            addMarker(LatLng(hwLocation.latitude, hwLocation.longitude))
        }
        hideProgressDialog()
    }

    private fun addMarker(latLng: LatLng) {
        val icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
        val marker: MarkerOptions = MarkerOptions().apply {
            this.position(latLng)
            this.icon(icon)
        }
        huaweiMap?.addMarker(marker)
    }

    private fun printError(error: Exception) {
        logText.append(error.message + System.lineSeparator())
        hideProgressDialog()
    }

    override fun onMapReady(hMap: HuaweiMap?) {
        huaweiMap = hMap

        val cameraUpdate =
            CameraUpdateFactory.newLatLngZoom(cdmx, 10f)

        huaweiMap?.apply {
            animateCamera(cameraUpdate)
            uiSettings.isZoomGesturesEnabled = false
            uiSettings.isRotateGesturesEnabled = false
            //uiSettings.isScrollGesturesEnabled = false
            uiSettings.isTiltGesturesEnabled = false
            addPolygon(
                PolygonOptions().addAll(
                    createRectangle(
                        cdmx, 0.12, 0.12
                    )
                )
                    .fillColor(ResourcesCompat.getColor(resources, R.color.poligon, null))
                    .strokeColor(Color.BLACK)
            )
        }
    }

    private fun createRectangle(
        center: LatLng,
        halfWidth: Double,
        halfHeight: Double
    ): List<LatLng> {
        return listOf(
            LatLng(center.latitude - halfHeight, center.longitude - halfWidth),
            LatLng(center.latitude - halfHeight, center.longitude + halfWidth),
            LatLng(center.latitude + halfHeight, center.longitude + halfWidth),
            LatLng(center.latitude + halfHeight, center.longitude - halfWidth)
        )
    }

    private fun showProgressDialog() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressDialog() {
        progressBar.visibility = View.GONE
    }

    override fun onStart() {
        super.onStart()
        gMap.onStart()
    }

    override fun onResume() {
        super.onResume()
        gMap.onResume()
    }

    override fun onPause() {
        super.onPause()
        gMap.onPause()
    }

    override fun onStop() {
        super.onStop()
        gMap.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        gMap.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        gMap.onDestroy()
    }
}