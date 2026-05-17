package com.example.unitask_manager.settings;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import com.example.unitask_manager.R;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.materialswitch.MaterialSwitch;

/**
 * Aplica tema claro/oscuro solo a la pantalla de Ajustes (sin alterar el tema global).
 */
public final class SettingsThemeApplier {

    private SettingsThemeApplier() {
    }

    public static void apply(
            @NonNull Context context,
            @NonNull View root,
            @NonNull MaterialCardView cardCuenta,
            @NonNull MaterialCardView cardPreferencias,
            @NonNull MaterialCardView cardSoporte,
            @NonNull LinearLayout rowEditarPerfil,
            @NonNull LinearLayout rowNotificaciones,
            @NonNull LinearLayout rowPrivacidad,
            @NonNull LinearLayout rowAyuda,
            @NonNull LinearLayout rowAcercaDe,
            @NonNull MaterialSwitch switchNotificaciones,
            @NonNull MaterialSwitch switchTemaOscuro,
            boolean darkTheme
    ) {
        @ColorInt int bgColor = ContextCompat.getColor(context,
                darkTheme ? R.color.settings_bg_dark : R.color.bg_light);
        @ColorInt int cardColor = ContextCompat.getColor(context,
                darkTheme ? R.color.settings_card_dark : R.color.card_white);
        @ColorInt int textPrimary = ContextCompat.getColor(context,
                darkTheme ? R.color.settings_text_primary_dark : R.color.text_primary);
        @ColorInt int textSecondary = ContextCompat.getColor(context,
                darkTheme ? R.color.settings_text_secondary_dark : R.color.text_secondary);
        @ColorInt int sectionAccent = ContextCompat.getColor(context,
                darkTheme ? R.color.settings_accent_dark : R.color.primary_purple);
        @ColorInt int switchTrack = ContextCompat.getColor(context,
                darkTheme ? R.color.settings_accent_dark : R.color.primary_purple);

        root.setBackgroundColor(bgColor);
        View scroll = root.findViewById(R.id.scroll_ajustes);
        if (scroll != null) {
            scroll.setBackgroundColor(bgColor);
        }
        View content = root.findViewById(R.id.layout_contenido_ajustes);
        if (content != null) {
            content.setBackgroundColor(bgColor);
        }

        cardCuenta.setCardBackgroundColor(cardColor);
        cardPreferencias.setCardBackgroundColor(cardColor);
        cardSoporte.setCardBackgroundColor(cardColor);

        applyRowTheme(context, rowEditarPerfil, textPrimary, textSecondary, darkTheme);
        applyRowTheme(context, rowNotificaciones, textPrimary, textSecondary, darkTheme);
        applyRowTheme(context, rowPrivacidad, textPrimary, textSecondary, darkTheme);
        applyRowTheme(context, rowAyuda, textPrimary, textSecondary, darkTheme);
        applyRowTheme(context, rowAcercaDe, textPrimary, textSecondary, darkTheme);

        updateSectionTitles(cardCuenta, sectionAccent);
        updateSectionTitles(cardPreferencias, sectionAccent);
        updateSectionTitles(cardSoporte, sectionAccent);

        ColorStateList trackColors = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_checked},
                        new int[]{}
                },
                new int[]{switchTrack, ContextCompat.getColor(context, R.color.settings_switch_track_off)}
        );
        switchNotificaciones.setTrackTintList(trackColors);
        switchTemaOscuro.setTrackTintList(trackColors);
        switchNotificaciones.setThumbTintList(ColorStateList.valueOf(Color.WHITE));
        switchTemaOscuro.setThumbTintList(ColorStateList.valueOf(Color.WHITE));
    }

    private static void applyRowTheme(
            @NonNull Context context,
            @NonNull LinearLayout row,
            @ColorInt int textPrimary,
            @ColorInt int textSecondary,
            boolean darkTheme
    ) {
        LinearLayout textColumn = findTextColumn(row);
        if (textColumn != null) {
            TextView title = findTitleTextView(textColumn);
            TextView subtitle = findSubtitleTextView(textColumn);
            if (title != null) {
                title.setTextColor(textPrimary);
            }
            if (subtitle != null) {
                subtitle.setTextColor(textSecondary);
            }
        }

        View iconContainer = row.getChildAt(0);
        if (iconContainer != null) {
            iconContainer.setBackgroundResource(
                    darkTheme ? R.drawable.bg_settings_icon_dark : R.drawable.bg_settings_icon
            );
        }
    }

    private static void updateSectionTitles(@NonNull MaterialCardView card, @ColorInt int accent) {
        View child = card.getChildAt(0);
        if (child instanceof LinearLayout) {
            LinearLayout container = (LinearLayout) child;
            for (int i = 0; i < container.getChildCount(); i++) {
                View item = container.getChildAt(i);
                if (item instanceof TextView) {
                    TextView tv = (TextView) item;
                    CharSequence text = tv.getText();
                    if (text != null && isSectionLabel(text.toString())) {
                        tv.setTextColor(accent);
                        return;
                    }
                }
            }
        }
    }

    private static boolean isSectionLabel(@NonNull String text) {
        return "Cuenta".equals(text) || "Preferencias".equals(text) || "Soporte".equals(text);
    }

    private static LinearLayout findTextColumn(@NonNull LinearLayout row) {
        for (int i = 0; i < row.getChildCount(); i++) {
            View child = row.getChildAt(i);
            if (child instanceof LinearLayout) {
                LinearLayout column = (LinearLayout) child;
                if (column.getOrientation() == LinearLayout.VERTICAL) {
                    return column;
                }
            }
        }
        return null;
    }

    private static TextView findTitleTextView(@NonNull LinearLayout column) {
        if (column.getChildCount() > 0 && column.getChildAt(0) instanceof TextView) {
            return (TextView) column.getChildAt(0);
        }
        return null;
    }

    private static TextView findSubtitleTextView(@NonNull LinearLayout column) {
        if (column.getChildCount() > 1 && column.getChildAt(1) instanceof TextView) {
            return (TextView) column.getChildAt(1);
        }
        return null;
    }
}
