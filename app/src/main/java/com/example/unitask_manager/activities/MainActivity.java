package com.example.unitask_manager.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.unitask_manager.R;
import com.example.unitask_manager.fragments.AddTaskFragment;
import com.example.unitask_manager.fragments.AgendaFragment;
import com.example.unitask_manager.fragments.AjustesFragment;
import com.example.unitask_manager.fragments.CursosFragment;
import com.example.unitask_manager.fragments.DashboardFragment;
import com.example.unitask_manager.fragments.StatsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setStatusBarColor(Color.TRANSPARENT);
        findViewById(R.id.main).setBackgroundColor(Color.BLACK);

        bottomNav = findViewById(R.id.bottom_navigation);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            int top = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            int bottom = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom;

            // Solo el fragmento recibe el padding top (status bar)
            findViewById(R.id.fragment_container).setPadding(0, 0, 0, 0);
            v.setPadding(0, top, 0, 0);

            // El nav bar crece hacia abajo para absorber la navigation bar del sistema
            bottomNav.setPadding(0, 0, 0, bottom);

            return WindowInsetsCompat.CONSUMED;
        });

        bottomNav = findViewById(R.id.bottom_navigation);

        if (savedInstanceState == null) {
            cargarFragmento(new DashboardFragment(), false);
        }

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment fragmento = null;
            int id = item.getItemId();

            if (id == R.id.nav_dashboard) {
                fragmento = new DashboardFragment();
            } else if (id == R.id.nav_cursos) {
                fragmento = new CursosFragment();
            } else if (id == R.id.nav_agenda) {
                fragmento = new AgendaFragment();
            } else if (id == R.id.nav_add_task) {
                fragmento = new AddTaskFragment();
            } else if (id == R.id.nav_estadisticas) {
                fragmento = new StatsFragment();
            } else if (id == R.id.nav_ajustes) {
                fragmento = new AjustesFragment();
            }

            if (fragmento != null) {
                cargarFragmento(fragmento, true);
            }
            return true;
        });
    }

    public void cargarFragmento(Fragment fragmento, boolean agregarBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragmento);
        if (agregarBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }
}
