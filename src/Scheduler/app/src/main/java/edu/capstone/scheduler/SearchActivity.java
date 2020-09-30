package edu.capstone.scheduler;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;


import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.internal.IStatusCallback;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class SearchActivity extends AppCompatActivity {

    private TextView currentLocation;
    private Button button;

    //Use fields to define the data types to return
    List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME);

    //Use the builder to create a FindCurrentPlaceRequest
    FindCurrentPlaceRequest request = FindCurrentPlaceRequest.builder(placeFields).build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //입력값 받아오기
        button = (Button)findViewById(R.id.button);
        final EditText address = (EditText)findViewById(R.id.address);
        currentLocation = (TextView)findViewById(R.id.result);

        Places.initialize(getApplicationContext(), "AIzaSyCG6NeTZ9cdyvFoz_tNIsBHMJmfCKw1vl0");
        PlacesClient placesClient = Places.createClient(this);



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){

                if (ContextCompat.checkSelfPermission(SearchActivity.this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);
                    placeResponse.addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            FindCurrentPlaceResponse response = task.getResult();
                            for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                                Log.i("temp", String.format("Place '%s' has likelihood: %f",
                                        placeLikelihood.getPlace().getName(),
                                        placeLikelihood.getLikelihood()));
                            }
                        } else {
                            Exception exception = task.getException();
                            if (exception instanceof ApiException) {
                                ApiException apiException = (ApiException) exception;
                                Log.e(null, "Place not found: " + apiException.getStatusCode());
                            }
                        }
                    });
                } else {
                    // A local method to request required permissions;
                    // See https://developer.android.com/training/permissions/requesting
                    //getLocationPermission();
                }

            }




        });








    }





}


