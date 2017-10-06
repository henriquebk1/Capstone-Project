package com.henriquebecker.geocoordenadas;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
@EFragment(R.layout.fragment_map)
public class MapFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnCircleClickListener,
        GoogleMap.OnPolygonClickListener,
        GoogleMap.OnPolylineClickListener,
        GoogleMap.OnCameraMoveListener,
        PopupMenu.OnMenuItemClickListener{
    @ViewById
    MapView mapView;
    GoogleMap map;

    @ViewById
    ImageView mapAdd;
    @ViewById
    ImageView mapShare;
    @ViewById
    ImageView mapLocation;
    @ViewById (R.id.mapCoordinatesText)
    TextView latLngText;
    @ViewById
    ImageView mapCancel;
    @ViewById
    ImageView mapConfirm;
    @ViewById
    ImageView mapConfig;
    @ViewById
    TextView mapHint;

    LatLng circleCenter;
    @InstanceState
    LatLng centerPosition = new LatLng(0,0);
    @InstanceState
    float zoom = 15.0f;
    ArrayList<LatLng> polyPoints;
    @InstanceState
    ArrayList<Marker> markers = new ArrayList<>();
    @InstanceState
    ArrayList<Circle> circles = new ArrayList<>();
    @InstanceState
    ArrayList<Polyline> polylines = new ArrayList<>();
    @InstanceState
    ArrayList<Polygon> polygons = new ArrayList<>();

    private Location mCurrentLocation;

    MapState currentState = MapState.IDLE;

    @AfterViews
    void initMap() {
        mapView.onCreate(getArguments());
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        switch (currentState){
            case PLACING_CIRCLE_CENTER:
                currentState = MapState.PLACING_CIRCLE_END;
                circleCenter = latLng;
                showHint(getString(R.string.place_circle_end));
                break;
            case PLACING_CIRCLE_END:
                currentState = MapState.IDLE;
                float radius[] = new float[3];
                Location.distanceBetween(latLng.latitude,latLng.longitude,
                        circleCenter.latitude,circleCenter.longitude,radius);
                CircleOptions circleOptions = new CircleOptions()
                        .center(circleCenter)
                        .radius(radius[0])
                        .strokeColor(Color.WHITE);
                circles.add(map.addCircle(circleOptions));
                hideCancelButton();
                hideHint();
                showCommonButtons();
                break;
            case PLACING_POLYGON_1:
                polyPoints.add(latLng);
                currentState = MapState.PLACING_POLYGON_2;
                showHint(getString(R.string.place_polygon_second));
                break;
            case PLACING_POLYGON_2:
                polyPoints.add(latLng);
                currentState = MapState.PLACING_POLYGON_3;
                showHint(getString(R.string.place_polygon_end));
                showConfirmButton();
                break;
            case PLACING_POLYGON_3:
            case PLACING_POLYLINE_2:
                polyPoints.add(latLng);
                break;
            case PLACING_POLYLINE_1:
                polyPoints.add(latLng);
                currentState = MapState.PLACING_POLYLINE_2;
                showHint(getString(R.string.place_polyline_end));
                showConfirmButton();
                break;
            case PLACING_MARKER:
                currentState = MapState.IDLE;
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(latLng);
                markers.add(map.addMarker(markerOptions));
                hideHint();
                hideCancelButton();
                showCommonButtons();
                break;
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if(currentState == MapState.REMOVING){
            int i=0;
            for (Marker m : markers){
                if(marker.getId().equals(m.getId())){
                    markers.remove(i);
                    marker.remove();
                }
                i++;
            }
        }
        return true;
    }

    @Override
    public void onCircleClick(Circle circle) {
        if(currentState == MapState.REMOVING){
            int i=0;
            for (Circle c : circles){
                if(c.getId().equals(circle.getId())){
                    circle.remove();
                    circles.remove(i);
                }
                i++;
            }
        }

    }

    @Override
    public void onPolygonClick(Polygon polygon) {
        if(currentState == MapState.REMOVING){
            int i=0;
            for (Polygon p : polygons){
                if(polygon.getId().equals(p.getId())){
                    polygons.remove(i);
                    polygon.remove();
                }
                i++;
            }
        }
    }

    @Override
    public void onPolylineClick(Polyline polyline) {
        if(currentState == MapState.REMOVING){
            int i=0;
            for (Polyline p : polylines){
                if(polyline.getId().equals(p.getId())){
                    polylines.remove(i);
                    polyline.remove();
                }
                i++;
            }
        }
    }

    @Override
    public void onCameraMove() {
        latLngText.setText(Utils.formatLatitude(map.getCameraPosition().target.latitude,getContext())
                + "    " +
                Utils.formatLongitude(map.getCameraPosition().target.longitude,getContext()));
        centerPosition = map.getCameraPosition().target;
        zoom = map.getCameraPosition().zoom;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        map.setOnMapClickListener(this);
        map.setOnMarkerClickListener(this);
        map.setOnCircleClickListener(this);
        map.setOnPolygonClickListener(this);
        map.setOnPolylineClickListener(this);
        map.setOnMapLongClickListener(this);
        map.setOnCameraMoveListener(this);
        map.getUiSettings().setZoomControlsEnabled(true);
        mCurrentLocation = ((MainActivity_)getActivity()).getCurrentLocation();
        latLngText.setText(Utils.formatLatitude(map.getCameraPosition().target.latitude,getContext()) + "    " +
                Utils.formatLongitude(map.getCameraPosition().target.longitude,getContext()));
        if(!markers.isEmpty()){
            ArrayList<Marker> newMarkers = new ArrayList<>();
            for(Marker m : markers){
                newMarkers.add(map.addMarker(new MarkerOptions()
                        .position(m.getPosition())
                        .title(m.getTitle())
                ));
            }
            markers = newMarkers;
        }
        if(!circles.isEmpty()){
            ArrayList<Circle> newCircles = new ArrayList<>();
            for(Circle c : circles){
                newCircles.add(map.addCircle(new CircleOptions()
                        .center(c.getCenter())
                        .radius(c.getRadius())
                        .strokeColor(c.getStrokeColor()))
                );
            }
            circles = newCircles;
        }
        if(!polygons.isEmpty()){
            ArrayList<Polygon> newPolygons = new ArrayList<>();
            for(Polygon p : polygons){
                newPolygons.add(map.addPolygon(new PolygonOptions()
                        .strokeColor(p.getStrokeColor())
                        .addAll(p.getPoints())
                ));
            }
            polygons = newPolygons;
        }
        if(!polylines.isEmpty()){
            ArrayList<Polyline> newPolylines = new ArrayList<>();
            for(Polyline p : polylines){
                newPolylines.add(map.addPolyline(new PolylineOptions()
                        .color(p.getColor())
                        .addAll(p.getPoints())
                ));
            }
            polylines = newPolylines;
        }
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(centerPosition,zoom));
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mapView != null)
            mapView.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mapView != null)
            mapView.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mapView != null)
            mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mapView != null)
            mapView.onStop();

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mapView != null)
            mapView.onSaveInstanceState(outState);
    }

    @Click(R.id.mapAdd)
    void add(){
        PopupMenu popupMenu = new PopupMenu(getContext(),mapAdd,Gravity.LEFT);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.map_add_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.show();
    }

    @Click(R.id.mapLocation)
    void location(){
        PopupMenu popupMenu = new PopupMenu(getContext(),mapLocation,Gravity.RIGHT|Gravity.TOP);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.map_location_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.show();
    }

    @Click(R.id.mapShare)
    void share(){
        PopupMenu popupMenu = new PopupMenu(getContext(),mapShare,Gravity.LEFT);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.map_share_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.show();
    }

    @Click(R.id.mapConfig)
    void config(){
        PopupMenu popupMenu = new PopupMenu(getContext(),mapConfig,Gravity.LEFT);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.map_config_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.show();
    }

    @Click(R.id.mapCancel)
    void cancel(){
        if(currentState == MapState.REMOVING){
            for (Circle circle : circles)
                circle.setClickable(false);
            for (Polygon polygon : polygons)
                polygon.setClickable(false);
            for (Polyline polyline : polylines)
                polyline.setClickable(false);
        }
        currentState = MapState.IDLE;
        hideHint();
        hideCancelButton();
        hideConfirmButton();
        showCommonButtons();
    }

    @Click(R.id.mapConfirm)
    void confirm(){
        if(currentState == MapState.PLACING_POLYGON_3){
            PolygonOptions polygonOptions = new PolygonOptions()
                    .add(polyPoints.toArray(new LatLng[polyPoints.size()]))
                    .strokeColor(Color.WHITE);
            polygons.add(map.addPolygon(polygonOptions));
        }
        else if(currentState == MapState.PLACING_POLYLINE_2){
            PolylineOptions polylineOptions = new PolylineOptions()
                    .add(polyPoints.toArray(new LatLng[polyPoints.size()]))
                    .color(Color.WHITE);
            polylines.add(map.addPolyline(polylineOptions));
        }
        currentState = MapState.IDLE;
        showCommonButtons();
        hideConfirmButton();
        hideCancelButton();
        hideHint();
    }

    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.menu_add_circle:
                drawCircle();
                break;
            case R.id.menu_add_marker:
                addMarker();
                break;
            case R.id.menu_add_polygon:
                drawPolygon();
                break;
            case R.id.menu_add_polyline:
                drawPolyline();
                break;
            case R.id.menu_location_current:
                centerCurrentLocation();
                break;
            case R.id.menu_location_geo_location:
                centerGeolocation();
                break;
            case R.id.menu_share_snapshot:
                shareSnapshot();
                break;
            case R.id.menu_config_clear:
                clearMap();
                break;
            case R.id.menu_config_remove_item:
                removeAnythingFromMap();
                break;
            default: return false;
        }
        return true;
    }

    private void removeAnythingFromMap() {
        currentState = MapState.REMOVING;
        hideCommonButtons();
        showCancelButton();
        showHint(getString(R.string.removing_from_map_hint));
        for (Circle circle : circles)
            circle.setClickable(true);
        for (Polygon polygon : polygons)
            polygon.setClickable(true);
        for (Polyline polyline : polylines)
            polyline.setClickable(true);
    }

    private void clearMap() {
        map.clear();
    }

    private void shareSnapshot() {
        map.snapshot(new GoogleMap.SnapshotReadyCallback() {
            @Override
            public void onSnapshotReady(Bitmap bitmap) {
                String path = MediaStore.Images.Media.insertImage(getContext().getContentResolver(),
                        bitmap, getString(R.string.app_name) + ": " + latLngText.getText(), null);
                Uri uri = Uri.parse(path);
                final Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                shareIntent.setType("image/png");
                startActivity(shareIntent);
            }
        });
    }


    private void centerGeolocation() {
        RealmHelper helper=new RealmHelper(((MainActivity_)getActivity()).realm,getContext());
        final ArrayList<GeoLocation> geoLocations=helper.retrieveAll();
        final ArrayList<String> locationsName=helper.retrieveNames();
        Dialogs.ShowCenterMapDialog(getContext(),locationsName,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                GeoLocation location = geoLocations.get(which);
                if(map.getCameraPosition().zoom<15.0f)
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(location.getLat(), location.getLng()),
                            15.0f));
                else
                    map.moveCamera(CameraUpdateFactory.newLatLng(
                            new LatLng(location.getLat(),location.getLng())));

            }
        });
    }

    private void centerCurrentLocation() {
        try {
            mCurrentLocation = ((MainActivity_)getActivity()).getCurrentLocation();
            if(map.getCameraPosition().zoom<15.0f)
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()),
                        15.0f));
            else
                map.moveCamera(CameraUpdateFactory.newLatLng(
                        new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude())));
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    private void drawPolyline() {
        currentState = MapState.PLACING_POLYLINE_1;
        hideCommonButtons();
        showCancelButton();
        showHint(getString(R.string.place_polyline_start));
        polyPoints = new ArrayList<>();
    }

    private void drawPolygon() {
        currentState = MapState.PLACING_POLYGON_1;
        hideCommonButtons();
        showCancelButton();
        showHint(getString(R.string.place_polygon_start));
        polyPoints = new ArrayList<>();
    }

    private void drawCircle(){
        currentState = MapState.PLACING_CIRCLE_CENTER;
        hideCommonButtons();
        showCancelButton();
        showHint(getString(R.string.place_circle_center));
    }

    private void addMarker(){
        RealmHelper helper = new RealmHelper(((MainActivity_) getActivity()).realm, getContext());
        final ArrayList<GeoLocation> geoLocations = helper.retrieveAll();
        final ArrayList<String> locationsName = helper.retrieveNames();
        locationsName.add(0, getString(R.string.current_location));
        locationsName.add(1, getString(R.string.place_on_map));
        Dialogs.ShowAddMarkerOnMap(getContext(), locationsName,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            mCurrentLocation = ((MainActivity_) getActivity()).getCurrentLocation();
                            MarkerOptions markerOptions = new MarkerOptions()
                                    .position(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()))
                                    .title(getString(R.string.current_location));
                            markers.add(map.addMarker(markerOptions));
                        } else if (which == 1) {
                            currentState = MapState.PLACING_MARKER;
                            hideCommonButtons();
                            showCancelButton();
                            showHint(getString(R.string.place_marker_hint));
                        } else {
                            GeoLocation location = geoLocations.get(which - 2);
                            MarkerOptions markerOptions = new MarkerOptions()
                                    .position(new LatLng(location.getLat(), location.getLng()))
                                    .title(location.getName());
                            markers.add(map.addMarker(markerOptions));
                        }
                    }
                });
    }

    private void hideCommonButtons(){
        mapAdd.setVisibility(View.GONE);
        mapShare.setVisibility(View.GONE);
        mapLocation.setVisibility(View.GONE);
        mapConfig.setVisibility(View.GONE);
    }

    private void showCommonButtons(){
        mapAdd.setVisibility(View.VISIBLE);
        mapShare.setVisibility(View.VISIBLE);
        mapLocation.setVisibility(View.VISIBLE);
        mapConfig.setVisibility(View.VISIBLE);
    }

    private void hideCancelButton(){
        mapCancel.setVisibility(View.GONE);
    }

    private void showCancelButton(){
        mapCancel.setVisibility(View.VISIBLE);
    }

    private void hideConfirmButton(){
        mapConfirm.setVisibility(View.GONE);
    }

    private void showConfirmButton(){
        mapConfirm.setVisibility(View.VISIBLE);
    }

    private void showHint(String hint){
        mapHint.setVisibility(View.VISIBLE);
        mapHint.setText(hint);
    }

    private void hideHint(){
        mapHint.setVisibility(View.GONE);
    }
}
