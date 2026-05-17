package com.example.unitask_manager.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.unitask_manager.R;
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

        bottomNav = findViewById(R.id.bottom_navigation);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            int top = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            findViewById(R.id.fragment_container).setPadding(
                    findViewById(R.id.fragment_container).getPaddingLeft(),
                    top,
                    findViewById(R.id.fragment_container).getPaddingRight(),
                    0);

            int bottom = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom;
            if (bottom > 0) {
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) bottomNav.getLayoutParams();
                params.bottomMargin = bottom;
                bottomNav.setLayoutParams(params);
            }
            return insets;
        });

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

    private void cargarFragmento(Fragment fragmento, boolean agregarBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragmento);
        if (agregarBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }
}
