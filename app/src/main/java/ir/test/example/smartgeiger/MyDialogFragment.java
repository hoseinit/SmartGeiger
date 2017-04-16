package ir.test.example.smartgeiger;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import ir.test.example.smartgeiger.utility.MyPreferences;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyDialogFragment extends DialogFragment implements View.OnClickListener {

    MyPreferences myPreferences;
    OnPrefChangeListener Callback;
    private EditText mETCpm, mETSv;
    private Button bSave, bRestore;
    private View.OnClickListener myClickListener;

    // Empty constructor required for DialogFragment
    public MyDialogFragment() {
    }

    public interface OnPrefChangeListener {
        public void onPrefChange();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_view, container);
        myPreferences = new MyPreferences(getContext());
        mETCpm = (EditText) view.findViewById(R.id.etMaxCpm);
        mETSv = (EditText) view.findViewById(R.id.etMaxSV);
        bRestore = (Button) view.findViewById(R.id.bRestore);
        bSave = (Button) view.findViewById(R.id.bSave);
        bRestore.setOnClickListener(this);
        bSave.setOnClickListener(this);

        initEditTexts();

        return view;
    }

    private void initEditTexts() {
        mETSv.setText(String.valueOf(myPreferences.getSv()));
        mETCpm.setText(String.valueOf(myPreferences.getCPM()));
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.bSave) {
            myPreferences.setCPM(Integer.valueOf(mETCpm.getText().toString()));
            myPreferences.setSv(Integer.valueOf(mETSv.getText().toString()));
            Callback.onPrefChange();
            getDialog().dismiss();

        } else if (view.getId() == R.id.bRestore) {
            mETSv.setText(String.valueOf(100));
            mETCpm.setText(String.valueOf(100));

        }

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            Callback = (OnPrefChangeListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }


}

