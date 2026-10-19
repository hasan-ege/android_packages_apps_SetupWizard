/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.setupwizard;

import static com.google.android.setupcompat.util.ResultCodes.RESULT_SKIP;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.RadioButton;

/**
 * Google services: microG (preinstalled, the default) or none. Choosing none disables the
 * microG apps for this user; they can be enabled again in Settings > Apps. Builds without
 * microG skip this step.
 */
public class GoogleServicesActivity extends BaseSetupWizardActivity {

    private static final String[] MICROG = {"com.google.android.gms", "com.android.vending"};

    private RadioButton mMicrog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!installed(MICROG[0])) {
            finishAction(RESULT_SKIP);
            return;
        }
        setNextText(R.string.next);
        mMicrog = findViewById(R.id.google_services_microg);
        int state = getPackageManager().getApplicationEnabledSetting(MICROG[0]);
        mMicrog.setChecked(state != PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
                && state != PackageManager.COMPONENT_ENABLED_STATE_DISABLED);
        ((RadioButton) findViewById(R.id.google_services_none)).setChecked(!mMicrog.isChecked());
    }

    private boolean installed(String pkg) {
        try {
            getPackageManager().getApplicationInfo(pkg, PackageManager.MATCH_DISABLED_COMPONENTS);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    @Override
    protected void onNextPressed() {
        int state = mMicrog.isChecked()
                ? PackageManager.COMPONENT_ENABLED_STATE_DEFAULT
                : PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER;
        for (String pkg : MICROG) {
            if (!installed(pkg)) continue;
            getPackageManager().setApplicationEnabledSetting(pkg, state, 0);
        }
        super.onNextPressed();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.setup_google_services;
    }

    @Override
    protected int getTitleResId() {
        return R.string.setup_google_services;
    }

    @Override
    protected int getIconResId() {
        return R.drawable.ic_google_services;
    }
}
