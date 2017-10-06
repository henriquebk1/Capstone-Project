package com.henriquebecker.geocoordenadas;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.CheckedChange;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;


@EFragment(R.layout.fragment_settings)
public class SettingsFragment extends Fragment {
    @ViewById(R.id.radio_DMS)
    RadioButton radioDMS;
    @ViewById(R.id.radio_decimal)
    RadioButton radioDecimal;
    @ViewById(R.id.radioMetric)
    RadioButton radioMetric;
    @ViewById(R.id.radioImperial)
    RadioButton radioImperial;

    @AfterViews
    void init(){
        if(MyPrefs.isDMS(getContext())){
            radioDMS.setChecked(true);
        }
        else {
            radioDecimal.setChecked(true);
        }
        if(MyPrefs.isMetric(getContext())){
            radioMetric.setChecked(true);
        }
        else {
            radioImperial.setChecked(true);
        }
    }

    @CheckedChange({R.id.radio_DMS,R.id.radio_decimal,R.id.radioMetric,R.id.radioImperial})
    void changed(CompoundButton buttonView, boolean isChecked){
        if(isChecked){
            switch (buttonView.getId()){
                case R.id.radio_DMS:
                    MyPrefs.setDMS_format(getContext());
                    break;
                case R.id.radio_decimal:
                    MyPrefs.setDecimal_format(getContext());
                    break;
                case R.id.radioMetric:
                    MyPrefs.setMetric(getContext());
                    break;
                case R.id.radioImperial:
                    MyPrefs.setImperial(getContext());
                    break;
            }
        }
    }

}
