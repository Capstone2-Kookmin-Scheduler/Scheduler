package edu.kookmin.scheduler.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

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
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.localization.LocalizationPlugin;
import com.mapbox.mapboxsdk.plugins.markerview.MarkerView;
import com.mapbox.mapboxsdk.plugins.markerview.MarkerViewManager;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.odsay.odsayandroidsdk.API;
import com.odsay.odsayandroidsdk.ODsayData;
import com.odsay.odsayandroidsdk.ODsayService;
import com.odsay.odsayandroidsdk.OnResultCallbackListener;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.ref.WeakReference;
import java.util.List;

import edu.kookmin.scheduler.R;
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
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;


/**
 * 지도 화면
 * @author - 구윤모
 * @start - 2020.11.13
 * @finish - 2020.11.15
 */
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

    private TextView information = null;
    private MapView mapView;
    private MapboxMap mapboxMap;
    private PermissionsManager permissionsManager;
    private MarkerView markerView;
    private MarkerViewManager markerViewManager;
    private TextView titleTextView;
    private TextView snippetTextView;

    private MapboxDirections client;
    private static DirectionsRoute currentRoute;

    private Double firstStation_lat, firstStation_lng;
    private int NOTI_ID = 1234;
    private Double lat;
    private Double lng;
    private Point origin;
    private Point destination;
    private boolean isNoti;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_showmap);
        Bundle extras = getIntent().getExtras();

        // 일정리스트나 알림에서 넘어온 bundle 값
        if (extras != null) {
            lat = extras.getDouble("lat");
            lng = extras.getDouble("lng");
            origin = Point.fromLngLat(lng, lat);
            destination = Point.fromLngLat(extras.getDouble("arrival_lng"), extras.getDouble("arrival_lat"));
            isNoti = extras.getBoolean("isNoti");
        }
        find_route_with_odsay(getApplicationContext());
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        information = (TextView) findViewById(R.id.information);

        if(isNoti==true){ // 알림을 통해서 들어왔으면 notifaction 없애기.
            NotificationManager nm = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            nm.cancel(NOTI_ID);
        }
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        ShowMapActivity.this.mapboxMap = mapboxMap;

        mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                LocalizationPlugin localizationPlugin = new LocalizationPlugin(mapView, ShowMapActivity.this.mapboxMap, style);
                enableLocationComponent(style);
                try {
                    localizationPlugin.matchMapLanguageWithDeviceDefault();
                } catch (RuntimeException exception) {
                    Log.d("ShowMapActivity", exception.toString());
                }
                initLayers(style);
                initSource(style);
            }
        });

    }

    private void initSource(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addSource(new GeoJsonSource(ROUTE_SOURCE_ID));

        GeoJsonSource iconGeoJsonSource = new GeoJsonSource(ICON_SOURCE_ID, FeatureCollection.fromFeatures(new Feature[]{
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
                iconOffset(new Float[]{0f, -9f})));
    }


    //Point.fromLngLat()로 파라미터 넣기
    private void getRoute(Point origin, Point destination) { // 지도상 경로 받아오기
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
        if (locationEngine != null) {
            locationEngine.removeLocationUpdates(callback);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mapboxMap.getLocationComponent().setLocationComponentEnabled(false);
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

                origin = Point.fromLngLat(result.getLastLocation().getLongitude(),result.getLastLocation().getLatitude());
                getRoute(origin,destination);
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
        locationEngine.getLastLocation(callback);


    }


    private void find_route_with_odsay(Context context){
        ODsayService odsayService = ODsayService.init(context,context.getString(R.string.odsay_api_key));
        odsayService.setReadTimeout(5000);
        odsayService.setConnectionTimeout(5000);

        OnResultCallbackListener onResultCallbackListener = new OnResultCallbackListener() {
            @Override
            public void onSuccess(ODsayData odsayData, API api) {
                if(api == API.SEARCH_PUB_TRANS_PATH){
                    try {
                        int count = 0;
                        JSONArray jsonArray = odsayData.getJson().getJSONObject("result").getJSONArray("path").getJSONObject(1).getJSONArray("subPath");
                        String startName = null; String endName = null; String code = null; String route = "";
                        //get firstStation Location with latitude and longitude
                        for (int i = 0; i<jsonArray.length(); i++) {
                            int type = jsonArray.getJSONObject(i).getInt("trafficType");
                            if (type == 3) continue;
                            else {
                                firstStation_lat = Double.parseDouble(jsonArray.getJSONObject(i).getString("startY"));
                                firstStation_lng = Double.parseDouble(jsonArray.getJSONObject(i).getString("startX"));
                                destination = Point.fromLngLat(firstStation_lng, firstStation_lat);
                                getRoute(origin, destination);
                                break;
                            }
                        }
                        //get path with station names
                        for (int i = 0; i<jsonArray.length(); i++){

                            int type = jsonArray.getJSONObject(i).getInt("trafficType");
                            if (type == 3) continue;
                            else if (type == 1){
                                count++;
                                code = "지하철역 " + jsonArray.getJSONObject(i).getJSONArray("lane").getJSONObject(0).getString("name");
                                startName = jsonArray.getJSONObject(i).getString("startName");
                                endName = jsonArray.getJSONObject(i).getString("endName");
                            }
                            else if (type == 2){
                                count++;
                                code = jsonArray.getJSONObject(i).getJSONArray("lane").getJSONObject(0).getString("busNo")  + "번 버스";
                                startName = jsonArray.getJSONObject(i).getString("startName");
                                endName = jsonArray.getJSONObject(i).getString("endName");
                            }

                            // 버스정류장 or 지하철역 마커뷰 표시
                            // 경로 여러개 중 첫번째 경로의 첫 정류장 표시
                            if(count==1){
                                markerViewManager = new MarkerViewManager(mapView, ShowMapActivity.this.mapboxMap);
                                View customView = LayoutInflater.from(ShowMapActivity.this).inflate(R.layout.marker_view, null);
                                customView.setLayoutParams(new FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
                                titleTextView = customView.findViewById(R.id.marker_window_title);
                                snippetTextView = customView.findViewById(R.id.marker_window_snippet);
                                titleTextView.setText(code);
                                snippetTextView.setText(startName);
                                markerView = new MarkerView(new LatLng(destination.latitude(), destination.longitude()), customView);
                                markerViewManager.addMarker(markerView);
                            }


                            route += " ( " + code + " ) " + startName + " ---> " + endName + "\n";
                        }
                        information.setText(route);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }

            @Override
            public void onError(int i, String s, API api) {
                if(api==API.BUS_STATION_INFO){}
            }
        };

        // 출경도,출위도,도경도,도위도,정렬방식(0:추천,1:타입별),검색방식(0:도시내검색,1:도시간검색),경로수단(0:모두,1:지하철,2:버스)
        odsayService.requestSearchPubTransPath(lng.toString(),lat.toString(),Double.toString(destination.longitude()),Double.toString(destination.latitude()), "0", "0", "0", onResultCallbackListener);
    }








}