package com.example.labtodo;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FirstFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FirstFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FirstFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FirstFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FirstFragment newInstance(String param1, String param2) {
        FirstFragment fragment = new FirstFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        final SecondFragment.communicator communicator = (SecondFragment.communicator)getActivity();

        String[] options = { "Sub", "ADD","Mul","Div"};
        final Spinner genderSpinner =(Spinner) getActivity().findViewById(R.id.spinner);
        ArrayAdapter<String> objGenderArr = new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_item, options);
        genderSpinner.setAdapter(objGenderArr);

        EditText FirstNumber = (EditText) getActivity().findViewById(R.id.editTextText);
        EditText SecondNumber = (EditText) getActivity().findViewById(R.id.editTextText2);
        Button click = (Button) getActivity().findViewById(R.id.button);
        TextView result = (TextView) getActivity().findViewById(R.id.textView3);
        click.setOnClickListener(v -> {
            String first = FirstNumber.getText().toString().trim();
            String second = SecondNumber.getText().toString().trim();
            int result2=0;
            String operation = genderSpinner.getSelectedItem().toString();
            if (operation.equals("ADD")){
                result2 = Integer.parseInt(first) + Integer.parseInt(second);
                result.setText("The Sum is "+result2);
            }else if (operation.equals("Sub")){
                result2 = Integer.parseInt(first) - Integer.parseInt(second);
                result.setText("The sub is "+result2);
            }else if (operation.equals("Mul")){
                 result2 = Integer.parseInt(first) * Integer.parseInt(second);
                result.setText("The Mul is "+result2);
            }else if (operation.equals("Div")){
                 result2 = Integer.parseInt(first) / Integer.parseInt(second);
                result.setText("The Sum is "+result2);
            }
            communicator.respond("The Operation is "+operation + " the Result is "+result2);
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false);
    }


}