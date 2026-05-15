package com.example.unitask_manager.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.unitask_manager.R;

public class CursosFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_placeholder, container, false);
        view.findViewById(android.R.id.content).setBackgroundColor(
                getResources().getColor(R.color.bg_light, null));
        Toast.makeText(getContext(), "Cursos — En construcción", Toast.LENGTH_SHORT).show();
        return view;
    }
}
