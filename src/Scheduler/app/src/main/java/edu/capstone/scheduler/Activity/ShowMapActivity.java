package edu.capstone.scheduler.Activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.MapboxMapOptions;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.localization.LocalizationPlugin;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.utils.BitmapUtils;

import java.lang.ref.WeakReference;
import java.util.List;

import edu.capstone.scheduler.R;
import edu.capstone.scheduler.util.GpsTracker;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mapbox.core.constants.Constants.PRECISION_6;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineCap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineJoin;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;

public class ShowMapActivity extends BaseActivity implements OnMapReadyCallback, PermissionsListener {
    private static final String ROUTE_LAYER_ID = "route-layer-id";
    private static final String ROUTE_SOURCE_ID = "route-source-id";
    private static final String ICON_LAYER_ID = "icon-layer-id";
    private static final String ICON_SOURCE_ID = "icon-source-id";
    private static final String RED_PIN_ICON_ID = "red-pin-icon-id";
    private long INTERVAL_IN_MILLISECONDS = 1000L;
    private long MAX_WAIT_TIME = INTERVAL_IN_MILLISECONDS * 5;

    private ShowMapActivityLocationCallback callback = new ShowMapActivityLocationCallback(this);
    private LocationEngine locationEngine;

    private MapView mapView;
    private MapboxMap mapboxMap;
    private PermissionsManager permissionsManager;

    private MapboxDirections client;
    private static DirectionsRoute currentRoute;

    private Double lat;
    private Double lng;
    private Point origin;
    private Point destination;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_showmap);

        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        Log.e("latlng","는 "+lat+"      "+lng);
        mapView.getMapAsync(this);

    }
    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        ShowMapActivity.this.mapboxMap = mapboxMap;

        mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                LocalizationPlugin localizationPlugin = new LocalizationPlugin(mapView, mapboxMap, style);
                enableLocationComponent(style);
                try {
                    localizationPlugin.matchMapLanguageWithDeviceDefault();
                } catch (RuntimeException exception) {
                    Log.d("ShowMapActivity", exception.toString());
                }

                /**
                 * Todo origin - 내 현재 위치, destination - 경로의 첫 정류장/지하철역 위도경도
                 */

                destination = Point.fromLngLat(126.995417,37.561623);
                initLayers(style);
                initSource(style);
                getRoute(origin,destination);


                // Map is set up and the style has loaded. Now you can add data or make other map adjustments
            }
        });

    }
    private void initSource(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addSource(new GeoJsonSource(ROUTE_SOURCE_ID));

        GeoJsonSource iconGeoJsonSource = new GeoJsonSource(ICON_SOURCE_ID, FeatureCollection.fromFeatures(new Feature[] {
                Feature.fromGeometry(Point.fromLngLat(origin.longitude(), origin.latitude())),
                Feature.fromGeometry(Point.fromLngLat(destination.longitude(), destination.latitude()))}));
        loadedMapStyle.addSource(iconGeoJsonSource);
    }

    /**
     * Add the route and marker icon layers to the map
     */
    private void initLayers(@NonNull Style loadedMapStyle) {
        LineLayer routeLayer = new LineLayer(ROUTE_LAYER_ID, ROUTE_SOURCE_ID);

// Add the LineLayer to the map. This layer will display the directions route.
        routeLayer.setProperties(
                lineCap(Property.LINE_CAP_ROUND),
                lineJoin(Property.LINE_JOIN_ROUND),
                lineWidth(5f),
                lineColor(Color.parseColor("#009688"))
        );
        loadedMapStyle.addLayer(routeLayer);

// Add the red marker icon image to the map

// Add the red marker icon SymbolLayer to the map
        loadedMapStyle.addLayer(new SymbolLayer(ICON_LAYER_ID, ICON_SOURCE_ID).withProperties(
                iconImage(RED_PIN_ICON_ID),
                iconIgnorePlacement(true),
                iconAllowOverlap(true),
                iconOffset(new Float[] {0f, -9f})));
    }


    //Point.fromLngLat()로 파라미터 넣기
    private void getRoute(Point origin, Point destination) { // 지도상 경로 받아오기
        Log.e("경로의 첫",origin.toString());
        Log.e("경로의 끝", destination.toString());
        client = MapboxDirections.builder()
                .origin(origin)//출발지 위도 경도
                .destination(destination)//도착지 위도 경도
                .overview(DirectionsCriteria.OVERVIEW_FULL)//정보 받는정도 최대
                .profile(DirectionsCriteria.PROFILE_WALKING)//길찾기 방법(도보,자전거,자동차)
                .accessToken(getString(R.string.mapbox_access_token))
                .build();

        client.enqueueCall(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {


                if (response.body() == null) {
                    Log.e("경로없음", "No routes found, make sure you set the right user and access token.");
                    return;
                } else if (response.body().routes().size() < 1) {
                    Log.e("경로없음", "No routes found");
                    return;
                }
                // Print some info about the route
                currentRoute = response.body().routes().get(0);


                // Draw the route on the map
                if (mapboxMap != null) {
                    mapboxMap.getStyle(new Style.OnStyleLoaded() {
                        @Override
                        public void onStyleLoaded(@NonNull Style style) {

                            // Retrieve and update the source designated for showing the directions route
                            GeoJsonSource source = style.getSourceAs(ROUTE_SOURCE_ID);

                            // Create a LineString with the directions route's geometry and
                            // reset the GeoJSON source for the route LineLayer source
                            if (source != null) {
                                source.setGeoJson(LineString.fromPolyline(currentRoute.geometry(), PRECISION_6));
                            }

                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                Log.e("ShowMapActivity.", "Error: " + throwable.getMessage());
                Toast.makeText(ShowMapActivity.this, "Error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(locationEngine!=null){
            locationEngine.removeLocationUpdates(callback);
        }
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent(@NonNull Style style) {

        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            // Get an instance of the component
            LocationComponent locationComponent = mapboxMap.getLocationComponent();

            // Activate with a built LocationComponentActivationOptions object
            locationComponent.activateLocationComponent(LocationComponentActivationOptions.builder(this, style).build());

            // Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);

            // Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);

            // Set the component's render mode
            locationComponent.setRenderMode(RenderMode.COMPASS);
            locationComponent.zoomWhileTracking(17);
            initLocationEngine();

        } else {

            permissionsManager = new PermissionsManager(this);

            permissionsManager.requestLocationPermissions(this);

        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    enableLocationComponent(style);
                }
            });
        } else {
            Toast.makeText(this, "위치 권한 필요", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    class ShowMapActivityLocationCallback implements LocationEngineCallback<LocationEngineResult> {
        private final WeakReference<ShowMapActivity> activityWeakReference;

        ShowMapActivityLocationCallback(ShowMapActivity activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void onSuccess(LocationEngineResult result) {
            ShowMapActivity activity = activityWeakReference.get();

            if (activity != null) {
                Location location = result.getLastLocation();
                if (location == null) {
                    return;
                }
                lat = result.getLastLocation().getLatitude();
                lng = result.getLastLocation().getLongitude();
                origin = Point.fromLngLat(lng,lat);

                // Pass the new location to the Maps SDK's LocationComponent
                if (activity.mapboxMap != null && result.getLastLocation() != null) {
                    activity.mapboxMap.getLocationComponent().forceLocationUpdate(result.getLastLocation());
                }

            }
        }

        @Override
        public void onFailure(@NonNull Exception exception) {
            Log.d("LocationChangeActivity", exception.getLocalizedMessage());
            ShowMapActivity activity = activityWeakReference.get();
            if (activity != null) {
                Toast.makeText(activity, exception.getLocalizedMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
    @SuppressLint("MissingPermission")
    private void initLocationEngine() { // 실시간 위치 정보 업데이트
        locationEngine = LocationEngineProvider.getBestLocationEngine(this);
        LocationEngineRequest request = new LocationEngineRequest.Builder(INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(MAX_WAIT_TIME).build();
        locationEngine.requestLocationUpdates(request, callback, getMainLooper());
        locationEngine.getLastLocation();
        Location location = locationEngine.getLastLocation(callback);

    }
}
