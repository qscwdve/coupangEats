package com.example.coupangeats.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class GpsControl(val context: Context) : LocationListener {
    data class AddressXY(val latitude: Double, val longitude: Double)
    private val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 10
    private val MIN_TIME_BW_UPDATES = (1000 * 60 * 1).toLong()
    var locationManager: LocationManager? = null

    private val GPS_ENABLE_REQUEST_CODE = 2001
    private val PERMISSIONS_REQUEST_CODE = 100

    var REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    fun getLocation(): Location? {
        var location : Location? = null
        try {
            locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
            val isGPSEnabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if (!isGPSEnabled && !isNetworkEnabled) {
            } else {
                val hasFineLocationPermission = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
                if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                    hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
                } else return null
                if (isNetworkEnabled) {
                    locationManager!!.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(),
                        this
                    )
                    locationManager!!.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(),
                        this
                    )
                    if (locationManager != null) {
                        location = locationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    }
                }
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager!!.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(),
                            this
                        )
                        if (locationManager != null) {
                            location = locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.d("@@@", "" + e.toString())
        }
        return location
    }

    fun getCurrentAddress(latitude: Double, longitude: Double): String? {

        //지오코더... GPS를 주소로 변환
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses: List<Address>? = try {
            geocoder.getFromLocation(
                latitude,
                longitude,
                7
            )
        } catch (ioException: IOException) {
            //네트워크 문제
            return null
        } catch (illegalArgumentException: IllegalArgumentException) {
            return null
        }
        if (addresses == null || addresses.isEmpty()) {
            return null
        }
        Log.d("address", "${addresses}")
        val address = addresses[0]
        return address.getAddressLine(0).toString()
        //return "${addresses[0]}"
    }
    fun getCurrentAddressArray(latitude: Double, longitude: Double) : ArrayList<String>?{
        //지오코더... GPS를 주소로 변환
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses: List<Address>? = try {
            geocoder.getFromLocation(
                latitude,
                longitude,
                7
            )
        } catch (ioException: IOException) {
            //네트워크 문제
            return null
        } catch (illegalArgumentException: IllegalArgumentException) {
            return null
        }
        if (addresses == null || addresses.isEmpty()) {
            return null
        }
        val answer = ArrayList<String>()
        answer.add(addresses[0].getAddressLine(0).toString())
        answer.add(addresses[1].getAddressLine(0).toString())
        return answer
    }

    fun getLocationToXY(address: String) : AddressXY?{
        val list : List<Address>? = Geocoder(context).getFromLocationName(address, 1)
        return if(list == null) null
        else AddressXY(list[0].latitude, list[0].longitude)
    }
    // LocationListener 관련 함수들
    override fun onLocationChanged(location: Location) {

    }

    override fun onProviderDisabled(provider: String) {
    }

    override fun onProviderEnabled(provider: String) {
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

}