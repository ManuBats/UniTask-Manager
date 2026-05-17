package com.example.unitask_manager.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.unitask_manager.R;
import com.example.unitask_manager.settings.SettingsDialogHelper;
import com.example.unitask_manager.settings.SettingsPreferenceManager;
import com.example.unitask_manager.settings.SettingsThemeApplier;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.materialswitch.MaterialSwitch;

/**
 * Pantalla de Ajustes con preferencias locales y diálogos informativos.
 */
public class AjustesFragment extends Fragment {

    private SettingsPreferenceManager preferenceManager;

    private View rootAjustes;
    private TextView tvAvatarIniciales;
    private TextView tvNombreUsuario;
    private TextView tvCorreoUsuario;

    private MaterialCardView cardCuenta;
    private MaterialCardView cardPreferencias;
    private MaterialCardView cardSoporte;

    private LinearLayout rowEditarPerfil;
    private LinearLayout rowNotificaciones;
    private LinearLayout rowPrivacidad;
    private LinearLayout rowAyuda;
    private LinearLayout rowAcercaDe;

    private MaterialSwitch switchNotificaciones;
    private MaterialSwitch switchTemaOscuro;
    private MaterialButton btnCerrarSesion;

    private boolean suppressSwitchCallbacks;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ajustes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        preferenceManager = new SettingsPreferenceManager(requireContext());
        bindViews(view);
        loadSavedState();
        setupListeners();
        applyLocalTheme(preferenceManager.isDarkThemeEnabled(), false);
    }

    private void bindViews(@NonNull View view) {
        rootAjustes = view.findViewById(R.id.root_ajustes);
        tvAvatarIniciales = view.findViewById(R.id.tv_avatar_iniciales);
        tvNombreUsuario = view.findViewById(R.id.tv_nombre_usuario);
        tvCorreoUsuario = view.findViewById(R.id.tv_correo_usuario);

        cardCuenta = view.findViewById(R.id.card_cuenta);
        cardPreferencias = view.findViewById(R.id.card_preferencias);
        cardSoporte = view.findViewById(R.id.card_soporte);

        rowEditarPerfil = view.findViewById(R.id.row_editar_perfil);
        rowNotificaciones = view.findViewById(R.id.row_notificaciones);
        rowPrivacidad = view.findViewById(R.id.row_privacidad);
        rowAyuda = view.findViewById(R.id.row_ayuda);
        rowAcercaDe = view.findViewById(R.id.row_acerca_de);

        switchNotificaciones = view.findViewById(R.id.switch_notificaciones);
        switchTemaOscuro = view.findViewById(R.id.switch_tema_oscuro);
        btnCerrarSesion = view.findViewById(R.id.btn_cerrar_sesion);
    }

    private void loadSavedState() {
        String userName = preferenceManager.getUserName();
        updateProfileUi(userName);
        tvCorreoUsuario.setText(preferenceManager.getDefaultEmail());

        setSwitchCheckedSilently(switchNotificaciones, preferenceManager.isNotificationsEnabled());
        setSwitchCheckedSilently(switchTemaOscuro, preferenceManager.isDarkThemeEnabled());
    }

    private void setupListeners() {
        rowEditarPerfil.setOnClickListener(v -> showEditProfileDialog());

        switchNotificaciones.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (suppressSwitchCallbacks) {
                return;
            }
            preferenceManager.setNotificationsEnabled(isChecked);
            int message = isChecked
                    ? R.string.settings_notifications_on
                    : R.string.settings_notifications_off;
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        });

        switchTemaOscuro.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (suppressSwitchCallbacks) {
                return;
            }
            preferenceManager.setDarkThemeEnabled(isChecked);
            applyLocalTheme(isChecked, true);
        });

        rowPrivacidad.setOnClickListener(v ->
                SettingsDialogHelper.showMessageDialog(
                        requireContext(),
                        R.string.settings_privacy_title,
                        R.string.settings_privacy_message
                )
        );

        rowAyuda.setOnClickListener(v ->
                SettingsDialogHelper.showMessageDialog(
                        requireContext(),
                        R.string.settings_help_title,
                        R.string.settings_help_message
                )
        );

        rowAcercaDe.setOnClickListener(v ->
                SettingsDialogHelper.showMessageDialog(
                        requireContext(),
                        R.string.settings_about_title,
                        R.string.settings_about_message
                )
        );

        btnCerrarSesion.setOnClickListener(v -> showLogoutConfirmation());
    }

    private void showEditProfileDialog() {
        SettingsDialogHelper.showEditProfileDialog(
                requireContext(),
                preferenceManager.getUserName(),
                newName -> {
                    preferenceManager.setUserName(newName);
                    updateProfileUi(newName);
                    Toast.makeText(
                            requireContext(),
                            R.string.settings_profile_updated,
                            Toast.LENGTH_SHORT
                    ).show();
                }
        );
    }

    private void updateProfileUi(@NonNull String userName) {
        tvNombreUsuario.setText(userName);
        tvAvatarIniciales.setText(SettingsPreferenceManager.computeInitials(userName));
    }

    private void showLogoutConfirmation() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.settings_logout_title)
                .setMessage(R.string.settings_logout_message)
                .setNegativeButton(R.string.settings_logout_cancel, null)
                .setPositiveButton(R.string.settings_logout_confirm, (dialog, which) ->
                        Toast.makeText(
                                requireContext(),
                                R.string.settings_logout_success,
                                Toast.LENGTH_LONG
                        ).show()
                )
                .show();
    }

    private void applyLocalTheme(boolean darkTheme, boolean animate) {
        Runnable applyTheme = () -> SettingsThemeApplier.apply(
                requireContext(),
                rootAjustes,
                cardCuenta,
                cardPreferencias,
                cardSoporte,
                rowEditarPerfil,
                rowNotificaciones,
                rowPrivacidad,
                rowAyuda,
                rowAcercaDe,
                switchNotificaciones,
                switchTemaOscuro,
                darkTheme
        );

        if (!animate) {
            applyTheme.run();
            return;
        }

        rootAjustes.animate()
                .alpha(0.88f)
                .setDuration(120)
                .withEndAction(() -> {
                    applyTheme.run();
                    rootAjustes.animate().alpha(1f).setDuration(180).start();
                })
                .start();
    }

    private void setSwitchCheckedSilently(@NonNull MaterialSwitch switchView, boolean checked) {
        suppressSwitchCallbacks = true;
        switchView.setChecked(checked);
        suppressSwitchCallbacks = false;
    }
}
