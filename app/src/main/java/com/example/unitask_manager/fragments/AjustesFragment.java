package com.example.unitask_manager.fragments;

import android.content.Intent;
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
import com.example.unitask_manager.activities.LoginActivity;
import com.example.unitask_manager.data.local.TokenManager;
import com.example.unitask_manager.data.repository.AuthRepository;
import com.example.unitask_manager.dto.response.UsuarioResponse;
import com.example.unitask_manager.models.Usuario;
import com.example.unitask_manager.settings.SettingsDialogHelper;
import com.example.unitask_manager.settings.SettingsPreferenceManager;
import com.example.unitask_manager.settings.SettingsThemeApplier;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.materialswitch.MaterialSwitch;

import androidx.appcompat.app.AppCompatDelegate;

/**
 * Pantalla de Ajustes con preferencias locales y conexión a la API de backend.
 */
public class AjustesFragment extends Fragment {

    private SettingsPreferenceManager preferenceManager;
    private AuthRepository authRepository;

    private View rootAjustes;
    private TextView tvAvatarIniciales;
    private TextView tvNombreUsuario;
    private TextView tvCorreoUsuario;

    private MaterialCardView cardCuenta;
    private MaterialCardView cardPreferencias;
    private MaterialCardView cardSoporte;

    private LinearLayout rowEditarPerfil;
    private LinearLayout rowCambiarContrasena;
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
    }

    /**
     * Obtiene de forma segura el repositorio solo cuando se necesita interactuar con la red.
     */
    private AuthRepository getAuthRepository() {
        if (authRepository == null && getContext() != null) {
            TokenManager tokenManager = new TokenManager(requireContext());
            authRepository = new AuthRepository(requireContext(), tokenManager);
        }
        return authRepository;
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
        rowCambiarContrasena = view.findViewById(R.id.row_cambiar_contrasena);
        rowNotificaciones = view.findViewById(R.id.row_notificaciones);
        rowPrivacidad = view.findViewById(R.id.row_privacidad);
        rowAyuda = view.findViewById(R.id.row_ayuda);
        rowAcercaDe = view.findViewById(R.id.row_acerca_de);

        switchNotificaciones = view.findViewById(R.id.switch_notificaciones);
        switchTemaOscuro = view.findViewById(R.id.switch_tema_oscuro);
        btnCerrarSesion = view.findViewById(R.id.btn_cerrar_sesion);
    }

    private void loadSavedState() {
        if (preferenceManager != null) {
            String userName = preferenceManager.getUserName();
            updateProfileUi(userName != null ? userName : "Usuario");
            tvCorreoUsuario.setText(preferenceManager.getUserEmail() != null ? preferenceManager.getUserEmail() : "");

            setSwitchCheckedSilently(switchNotificaciones, preferenceManager.isNotificationsEnabled());
            setSwitchCheckedSilently(switchTemaOscuro, preferenceManager.isDarkThemeEnabled());
        }
    }

    private void setupListeners() {
        rowEditarPerfil.setOnClickListener(v -> showEditProfileDialog());
        rowCambiarContrasena.setOnClickListener(v -> showChangePasswordDialog());

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
            AppCompatDelegate.setDefaultNightMode(isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
            requireActivity().recreate();
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
        String currentName = preferenceManager.getUserName() != null ? preferenceManager.getUserName() : "";
        String currentEmail = preferenceManager.getUserEmail() != null ? preferenceManager.getUserEmail() : "";

        SettingsDialogHelper.showEditProfileDialog(
                requireContext(),
                currentName,
                currentEmail,
                (newName, newEmail) -> {
                    AuthRepository repo = getAuthRepository();
                    if (repo == null) {
                        Toast.makeText(requireContext(), "Error de inicialización interna", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    UsuarioResponse requestDto = new UsuarioResponse();
                    requestDto.setNombre(newName);
                    requestDto.setEmail(newEmail);

                    repo.updateUserProfile(requestDto, new AuthRepository.AuthCallback() {
                        @Override
                        public void onSuccess(Usuario usuario) {
                            // REQUISITO CUMPLIDO: Guardar en persistencia local SharedPreferences
                            if (preferenceManager != null) {
                                preferenceManager.setUserName(newName);
                                preferenceManager.setUserEmail(newEmail);
                            }

                            // Refrescar el diseño visual
                            updateProfileUi(newName);
                            tvCorreoUsuario.setText(newEmail);
                            Toast.makeText(
                                    requireContext(),
                                    R.string.settings_profile_updated,
                                    Toast.LENGTH_SHORT
                            ).show();
                        }

                        @Override
                        public void onError(String error) {
                            Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
                        }
                    });
                }
        );
    }

    private void showChangePasswordDialog() {
        SettingsDialogHelper.showChangePasswordDialog(
                requireContext(),
                (currentPassword, newPassword) -> {
                    AuthRepository repo = getAuthRepository();
                    if (repo == null) {
                        Toast.makeText(requireContext(), "Error de inicialización interna", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    repo.updatePassword(currentPassword, newPassword, new AuthRepository.ObjectCallback() {
                        @Override
                        public void onSuccess() {
                            if (preferenceManager != null) {
                                preferenceManager.setUserPassword(newPassword);
                            }
                            Toast.makeText(
                                    requireContext(),
                                    R.string.settings_password_updated,
                                    Toast.LENGTH_SHORT
                            ).show();
                        }

                        @Override
                        public void onError(String error) {
                            Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
                        }
                    });
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
                .setPositiveButton(R.string.settings_logout_confirm, (dialog, which) -> {
                    AuthRepository repo = getAuthRepository();
                    if (repo != null) {
                        repo.logout();
                    }
                    Intent intent = new Intent(requireContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    requireContext().startActivity(intent);
                })
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
                rowCambiarContrasena,
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