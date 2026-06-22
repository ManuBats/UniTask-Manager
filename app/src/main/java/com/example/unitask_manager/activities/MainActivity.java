package com.example.unitask_manager.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.unitask_manager.R;
import com.example.unitask_manager.data.local.TokenManager;
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

        // 2. Control de Acceso (Auth Gate) - Redirección al Login si no hay token
        TokenManager tokenManager = new TokenManager(this);
        if (!tokenManager.hasToken()) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        // 3. Configuración del tema (Oscuro / Claro)
        SharedPreferences prefs = getSharedPreferences("unitask_settings_prefs", MODE_PRIVATE);
        boolean darkTheme = prefs.getBoolean("dark_theme_enabled", false);
        AppCompatDelegate.setDefaultNightMode(darkTheme ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

        // 4. Asignar la vista de la actividad
        setContentView(R.layout.activity_main);

        // 5. Vincular el BottomNavigationView
        bottomNav = findViewById(R.id.bottom_navigation);

        // 6. Controlar márgenes y áreas seguras de la pantalla (Edge-to-Edge)
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

        // 7. Cargar el fragmento inicial si es la primera vez que se abre la actividad
        if (savedInstanceState == null) {
            cargarFragmento(new DashboardFragment(), false);
        }

        // 8. Listener de navegación inferior
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

    public void cargarFragmento(Fragment fragmento, boolean agregarBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragmento);
        if (agregarBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }
}