/*
 * Copyright (C) 2020 Yet Another AOSP Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.notification;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;

import com.android.settings.R;
import com.android.settings.Utils;
import com.android.settingslib.core.AbstractPreferenceController;

/**
 * This class allows choosing a vibration pattern while ringing
 */
public class VibrationPatternPreferenceController extends AbstractPreferenceController
        implements Preference.OnPreferenceChangeListener {

    private static final String KEY_VIB_PATTERN = "vibration_pattern";
    private static final String KEY_CUSTOM_VIB_CATEGORY = "custom_vibration_pattern";

    private ListPreference mVibPattern;
    private Preference mCustomVibCategory;

    public VibrationPatternPreferenceController(Context context) {
        super(context);
    }

    @Override
    public boolean isAvailable() {
        return Utils.isVoiceCapable(mContext);
    }

    @Override
    public String getPreferenceKey() {
        return KEY_VIB_PATTERN;
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);
        mVibPattern = (ListPreference) screen.findPreference(KEY_VIB_PATTERN);
        int vibPattern = Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.RINGTONE_VIBRATION_PATTERN, 0);
        mVibPattern.setValueIndex(vibPattern);
        mVibPattern.setSummary(mVibPattern.getEntries()[vibPattern]);
        mVibPattern.setOnPreferenceChangeListener(this);

        mCustomVibCategory = screen.findPreference(KEY_CUSTOM_VIB_CATEGORY);
        mCustomVibCategory.setVisible(vibPattern == 5);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mVibPattern) {
            int vibPattern = Integer.valueOf((String) newValue);
            Settings.System.putInt(mContext.getContentResolver(),
                    Settings.System.RINGTONE_VIBRATION_PATTERN, vibPattern);
            mVibPattern.setSummary(mVibPattern.getEntries()[vibPattern]);
            boolean isCustom = vibPattern == 5;
            mCustomVibCategory.setVisible(isCustom);
            if (!isCustom) previewPattern();
            return true;
        }
        return false;
    }

    private void previewPattern() {
        Vibrator vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
        VibrationEffect effect;
        VibrationEffectProxy vibrationEffectProxy = new VibrationEffectProxy();
        int vibPattern = Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.RINGTONE_VIBRATION_PATTERN, 0);
        switch (vibPattern) {
            case 1:
                effect = vibrationEffectProxy.createWaveform(DZZZ_DA_VIBRATION_PATTERN,
                        FIVE_ELEMENTS_VIBRATION_AMPLITUDE, -1);
                break;
            case 2:
                effect = vibrationEffectProxy.createWaveform(MM_MM_MM_VIBRATION_PATTERN,
                        SEVEN_ELEMENTS_VIBRATION_AMPLITUDE, -1);
                break;
            case 3:
                effect = vibrationEffectProxy.createWaveform(DA_DA_DZZZ_VIBRATION_PATTERN,
                        SEVEN_ELEMENTS_VIBRATION_AMPLITUDE, -1);
                break;
            case 4:
                effect = vibrationEffectProxy.createWaveform(DA_DZZZ_DA_VIBRATION_PATTERN,
                        SEVEN_ELEMENTS_VIBRATION_AMPLITUDE, -1);
                break;
            default:
                effect = vibrationEffectProxy.createWaveform(SIMPLE_VIBRATION_PATTERN,
                        FIVE_ELEMENTS_VIBRATION_AMPLITUDE, -1);
                break;
        }
        vibrator.vibrate(effect, VIBRATION_ATTRIBUTES);
    }
}
