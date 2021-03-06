package net.gusakov.ddatestapp.framents;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import net.gusakov.ddatestapp.MainActivity;
import net.gusakov.ddatestapp.R;
import net.gusakov.ddatestapp.adapters.StudentCursorAdapter;
import net.gusakov.ddatestapp.classes.CourseUtil;
import net.gusakov.ddatestapp.classes.Filter;

/**
 * Created by gusakov on 2/23/2017.
 */

public class FilterDialogFragment extends DialogFragment {

    public static final String SHARED_PREF_INT_SPINNER_ID = "spinner_id";
    public static final String SHARED_PREF_STRING_MARK = "mark";
    private StudentCursorAdapter adapter;
    private Spinner courseSpinner;
    private EditText markEdtxt;
    private Button OKButton;
    private Button clearButton;

    public FilterDialogFragment() {

    }

    public void setAdapter(StudentCursorAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.filter_dialog, null);

        initViews(v);


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v);
        return builder.create();
    }

    private void initViews(View v) {
        courseSpinner = (Spinner) v.findViewById(R.id.courseSpinnerId);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, CourseUtil.getCourseStrings());
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        courseSpinner.setAdapter(spinnerArrayAdapter);
        markEdtxt = (EditText) v.findViewById(R.id.markEditTextId);
        OKButton = (Button) v.findViewById(R.id.OkButtonId);
        clearButton = (Button) v.findViewById(R.id.clearButtonId);
        loadFilterState(courseSpinner, markEdtxt);

        OKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!markEdtxt.getText().toString().trim().isEmpty()) {
                        Filter filter = new Filter();
                        filter.setCourseId(CourseUtil.getCourseId((String) courseSpinner.getSelectedItem()));
                        filter.setMark(Integer.parseInt(markEdtxt.getText().toString()));
                        adapter.loadData(filter,true);
                    } else {
                        Filter emptyFilter = new Filter();
                        adapter.loadData(emptyFilter,true);
                    }
                    getDialog().dismiss();
                    saveFilterState(courseSpinner, markEdtxt);
                } catch (NullPointerException e) {
                    throw new NullPointerException("Should call setAdapter(StudentCursorAdapter adapter) before show dialog");
                }
            }
        });
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                courseSpinner.setSelection(0);
                markEdtxt.setText("");
                try {
                    Filter emptyFilter = new Filter();
                    adapter.loadData(emptyFilter,true);
                } catch (NullPointerException e) {
                    throw new NullPointerException("Should call setAdapter(StudentCursorAdapter adapter) before show dialog");
                }
                getDialog().dismiss();
                saveFilterState(courseSpinner, markEdtxt);
            }
        });

    }


    private void saveFilterState(Spinner courseSpinner, EditText markEdtxt) {
        SharedPreferences sharedPref = getActivity().getSharedPreferences(MainActivity.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sharedPref.edit();
        ed.putInt(SHARED_PREF_INT_SPINNER_ID, courseSpinner.getSelectedItemPosition());
        ed.putString(SHARED_PREF_STRING_MARK, markEdtxt.getText().toString());
        ed.commit();
    }

    private void loadFilterState(Spinner courseSpinner, EditText markEdtxt) {
        SharedPreferences sharedPref = getActivity().getSharedPreferences(MainActivity.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        courseSpinner.setSelection(sharedPref.getInt(SHARED_PREF_INT_SPINNER_ID, 0));
        markEdtxt.setText(sharedPref.getString(SHARED_PREF_STRING_MARK, ""));
    }

}
