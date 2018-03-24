
package com.adhithya.track.homeMap;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
        import android.location.Location;
import android.os.Handler;
import android.support.annotation.NonNull;
        import android.support.annotation.Nullable;
        import android.support.v4.app.ActivityCompat;
        import android.support.v4.app.FragmentActivity;
        import android.support.v4.content.ContextCompat;
import android.os.Bundle;
        import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

        import com.adhithya.track.R;
        import com.adhithya.track.model.LocationModel;
        import com.adhithya.track.responce.Locationresponce;
import com.adhithya.track.service.Config;
import com.adhithya.track.service.RetroClient;
        import com.google.android.gms.common.ConnectionResult;
        import com.google.android.gms.common.api.GoogleApiClient;
        import com.google.android.gms.location.LocationServices;
        import com.google.android.gms.maps.CameraUpdateFactory;
        import com.google.android.gms.maps.GoogleMap;
        import com.google.android.gms.maps.OnMapReadyCallback;
        import com.google.android.gms.maps.SupportMapFragment;
        import com.google.android.gms.maps.model.CameraPosition;
        import com.google.android.gms.maps.model.LatLng;
        import com.google.android.gms.maps.model.Marker;
        import com.google.android.gms.maps.model.MarkerOptions;

        import java.util.List;

        import retrofit2.Call;
        import retrofit2.Callback;
        import retrofit2.Response;
public class MapActivity extends FragmentActivity implements
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, OnMapReadyCallback {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private boolean mLocationPermissionGranted=false;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION= 1;
    private Location mLastKnownLocation;
    private CameraPosition mCameraPosition;
    private Marker currentMarker;
    double lati,longi;
    private ProgressDialog pDialog;
    private String key;
    private Handler handler = new Handler();
    private Runnable run;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        pDialog=new ProgressDialog(this);
        key = Config.KEY;
        intiliseGoogleApi();


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        updateLocationUI();
        getDeviceLocation();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }



    private void intiliseGoogleApi(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,
                        this /* OnConnectionFailedListener */)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

    }
    private void SendLocation(){
        Intent intent = new Intent(this,LocationService.class);
        startService(intent);


    }
    private void getDeviceLocation() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        if (mLocationPermissionGranted) {
            mLastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
        // Set the map's camera position to the current location of the device.

        lati=mLastKnownLocation.getLatitude();
        longi=mLastKnownLocation.getLongitude();
        Log.e("location",""+lati+""+longi);
        if (lati!=0.0&&longi!=0.0){
            SendLocation();
            moveCameraPostion(mCameraPosition,mLastKnownLocation,lati,longi);
        }

        if (currentMarker ==null){
            myMarker(lati,longi);
            moveCameraPostion(mCameraPosition,mLastKnownLocation,lati,longi);
        }else {
            moveCameraPostion(mCameraPosition,mLastKnownLocation,lati,longi);
        }


        //moveCameraPostion(mCameraPosition,mLastKnownLocation,clickLat,clickLong);
    }
    public void update() {
        run = new Runnable() {
            @Override
            public void run() {
               getDeviceLocation();
                handler.postDelayed(this, 5000);
            }
        };
        handler.post(run);

    }



    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        if (mLocationPermissionGranted) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            mMap.setMyLocationEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mLastKnownLocation = null;
        }
    }
    private void moveCameraPostion(CameraPosition cameraPosition,Location mLastKnownLocation,double latitude,double longitude){
        if (cameraPosition != null) {
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        } else if (mLastKnownLocation != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude), 17));
        } else {
            Log.d("Log", "Current location is null. Using defaults.");

        }
    }
    private void myMarker(double latitude,double longitude){
        currentMarker = mMap.addMarker( new MarkerOptions()
                .position( new LatLng(latitude, longitude))
                .title("My Locationresponce")
                .draggable(false));
        currentMarker.showInfoWindow();
    }

//    private void SendLocation(){
//        pDialog.show();
//        SEND(key,lati,longi).enqueue(new Callback<Locationresponce>() {
//            @Override
//            public void onResponse(Call<Locationresponce> call, Response<Locationresponce> response) {
//                if (response.body().getStatus()==1){
//                    pDialog.dismiss();
//                    Toast.makeText(MapActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
//
//                    List<LocationModel> list=response.body().getUser();
//                    LocationModel model=list.get(0);
//
//                    Log.e("MODEL",""+model.getLattitude()+" "+model.getLongtitude());
//                }else {
//                    pDialog.dismiss();
//                    Toast.makeText(MapActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Locationresponce> call, Throwable t) {
//                pDialog.dismiss();
//                Log.e("MODEL",""+t);
//            }
//        });
//    }


//    private Call<Locationresponce> SEND(String uid,double lati, double longi){
//        return RetroClient.getApiService().sendLocation( Config.KEY,uid,String.valueOf(lati),String.valueOf(longi));
//    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

}
