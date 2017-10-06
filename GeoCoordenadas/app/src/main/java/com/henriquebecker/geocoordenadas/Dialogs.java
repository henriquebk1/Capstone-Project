package com.henriquebecker.geocoordenadas;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public final class Dialogs{
    private Dialogs() {
    }

    public static void ShowSaveDialog(final Context context, final Activity activity,
                                      final Location currentLocation) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final View view = activity.getLayoutInflater().inflate(R.layout.dialog_save, null);
        builder.setView(view)
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String locationName = ((TextView) view.findViewById(R.id.dialog_save_name))
                                .getText().toString();
                        if (currentLocation == null) {
                            Toast.makeText(context,
                                    context.getString(R.string.cant_save),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            GeoLocation geoLocation = new GeoLocation(locationName,
                                    currentLocation.getLatitude(),
                                    currentLocation.getLongitude(), currentLocation.getAccuracy());
                            RealmHelper realmHelper =
                                    new RealmHelper(((MainActivity_) activity).realm,
                                            context);
                            realmHelper.create(geoLocation);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alertDialog = builder.create();
        final EditText nameText = (EditText)view.findViewById(R.id.dialog_save_name);
        alertDialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        alertDialog.show();
        nameText.requestFocus();
    }

    public static void ShowCalibrateDialog(final Context context, final Activity activity) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final View view = activity.getLayoutInflater().inflate(R.layout.dialog_calibrate_compass, null);
        builder.setView(view)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public static void ShowCreateNewDialog(final Context context, final Activity activity) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final View view = activity.getLayoutInflater().inflate(R.layout.dialog_create_new, null);
        final EditText nameText = (EditText)view.findViewById(R.id.dialog_create_name);
        final EditText latitudeText = (EditText)view.findViewById(R.id.dialog_create_latitude);
        final EditText longitudeText = (EditText)view.findViewById(R.id.dialog_create_longitude);
        final TextView nameFeedback = (TextView)view.findViewById(R.id.dialog_create_name_feedback);
        final TextView latitudeFeedback = (TextView)view.findViewById(R.id.dialog_create_latitude_feedback);
        final TextView longitudeFeedback = (TextView)view.findViewById(R.id.dialog_create_longitude_feedback);
        builder.setView(view)
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            String name = nameText.getText().toString();
                            double latitude =
                                    Utils.convertCoordinate(latitudeText.getText().toString(),context);
                            double longitude =
                                    Utils.convertCoordinate(longitudeText.getText().toString(),context);
                            GeoLocation geoLocation = new GeoLocation(name,
                                    latitude,
                                    longitude,
                                    1.0f);
                            RealmHelper realmHelper =
                                    new RealmHelper(((MainActivity_) activity).realm,
                                            context);
                            realmHelper.create(geoLocation);
                        }
                        catch (Exception e){
                            Log.e("TAG",e.toString());
                            Toast.makeText(context,
                                    context.getString(R.string.cant_save),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        nameText.requestFocus();
        nameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()>0){
                    nameFeedback.setText(s);
                }
                else {
                    nameFeedback.setText(context.getString(R.string.empty_text));
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        latitudeText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    latitudeFeedback.setText(Utils.formatLatitude(
                            Utils.convertCoordinate(s.toString(),context),context));
                    if(longitudeFeedback.getText().length()>1)
                        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                }
                catch (Exception e){
                    alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                    latitudeFeedback.setText(context.getString(R.string.empty_text));
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        longitudeText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if(latitudeFeedback.getText().length()>1)
                        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                    longitudeFeedback.setText(Utils.formatLongitude(Utils.convertCoordinate(s.toString(),context),context));
                }
                catch (Exception e){
                    alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                    longitudeFeedback.setText(context.getString(R.string.empty_text));
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    public static void ShowEditDialog(final Context context, final Activity activity, final GeoLocation geoLocation) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final View view = activity.getLayoutInflater().inflate(R.layout.dialog_create_new, null);
        final EditText nameText = (EditText)view.findViewById(R.id.dialog_create_name);
        final EditText latitudeText = (EditText)view.findViewById(R.id.dialog_create_latitude);
        final EditText longitudeText = (EditText)view.findViewById(R.id.dialog_create_longitude);
        final TextView nameFeedback = (TextView)view.findViewById(R.id.dialog_create_name_feedback);
        final TextView latitudeFeedback = (TextView)view.findViewById(R.id.dialog_create_latitude_feedback);
        final TextView longitudeFeedback = (TextView)view.findViewById(R.id.dialog_create_longitude_feedback);
        nameText.setText(geoLocation.getName());
        nameFeedback.setText(geoLocation.getName());
        latitudeText.setText(Utils.formatLatitude(geoLocation.getLat(),context));
        latitudeFeedback.setText(Utils.formatLatitude(geoLocation.getLat(),context));
        longitudeText.setText(Utils.formatLongitude(geoLocation.getLng(),context));
        longitudeFeedback.setText(Utils.formatLongitude(geoLocation.getLng(),context));
        builder.setView(view)
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            String name = nameText.getText().toString();
                            double latitude =
                                    Utils.convertCoordinate(latitudeText.getText().toString(),context);
                            double longitude =
                                    Utils.convertCoordinate(longitudeText.getText().toString(),context);
                            geoLocation.setName(name);
                            geoLocation.setLat(latitude);
                            geoLocation.setLng(longitude);
                            RealmHelper realmHelper =
                                    new RealmHelper(((MainActivity_) activity).realm,
                                            context);
                            realmHelper.update(geoLocation);
                        }
                        catch (Exception e){
                            Toast.makeText(context,
                                    context.getString(R.string.cant_save),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        alertDialog.show();
        nameText.requestFocus();
        nameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()>0){
                    nameFeedback.setText(s);
                }
                else {
                    nameFeedback.setText(context.getString(R.string.empty_text));
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        latitudeText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    latitudeFeedback.setText(Utils.formatLatitude(
                            Utils.convertCoordinate(s.toString(),context),context));
                    if(longitudeFeedback.getText().length()>1)
                        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                }
                catch (Exception e){
                    alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                    latitudeFeedback.setText(context.getString(R.string.empty_text));
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        longitudeText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if(latitudeFeedback.getText().length()>1)
                        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                    longitudeFeedback.setText(Utils.formatLongitude(Utils.convertCoordinate(s.toString(),context),context));
                }
                catch (Exception e){
                    alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                    longitudeFeedback.setText(context.getString(R.string.empty_text));
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    public static void ShowDeleteDialog(final Context context, final Activity activity, final GeoLocation geoLocation){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.delete);
        builder.setMessage(context.getString(R.string.delete_message,geoLocation.getName()));
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setPositiveButton(R.string.yes_delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                RealmHelper realmHelper =
                        new RealmHelper(((MainActivity_) activity).realm,
                                context);
                realmHelper.delete(geoLocation);
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public static void ShowCenterMapDialog(final Context context, ArrayList<String> locationsName,
                                           DialogInterface.OnClickListener positiveClickListener){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.geo_location);
        final ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<String>(context,android.R.layout.select_dialog_singlechoice);
        arrayAdapter.addAll(locationsName);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setAdapter(arrayAdapter, positiveClickListener);
        builder.show();
    }

    public static void ShowAddMarkerOnMap(final Context context, ArrayList<String> locationsName,
                                          DialogInterface.OnClickListener itemClickListener){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.marker);
        final ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<>(context, android.R.layout.select_dialog_singlechoice);
        arrayAdapter.addAll(locationsName);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setAdapter(arrayAdapter, itemClickListener);
        builder.show();
    }
}
