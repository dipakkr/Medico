package com.parivartan.medico.adapter;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;

import com.parivartan.medico.model.Medicine;

import java.util.List;

/**
 * Created by root on 3/9/18.
 */

public class MedicineAdapter extends ArrayAdapter<Medicine>{
    public MedicineAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Medicine> objects) {
        super(context, resource,objects);
    }
}


