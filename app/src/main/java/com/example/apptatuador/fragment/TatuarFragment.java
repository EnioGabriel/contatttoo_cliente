package com.example.apptatuador.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.apptatuador.R;

public class TatuarFragment extends Fragment {


    public TatuarFragment() {
        // Required empty public constructor
    }

    public static TatuarFragment newInstance(String param1, String param2) {
        TatuarFragment fragment = new TatuarFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tatuar, container, false);
        return view;
    }
}