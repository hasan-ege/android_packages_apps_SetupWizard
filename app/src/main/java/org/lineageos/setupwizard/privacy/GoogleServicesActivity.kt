/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.setupwizard.privacy

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.RadioButton
import com.google.android.setupcompat.util.ResultCodes.RESULT_SKIP
import org.lineageos.setupwizard.R
import org.lineageos.setupwizard.base.BaseSetupWizardActivity

/**
 * Google services: microG (preinstalled, the default) or none. Choosing none disables the
 * microG apps for this user; they can be enabled again in Settings > Apps. Builds without
 * microG skip this step.
 */
class GoogleServicesActivity : BaseSetupWizardActivity() {

    private val microg by lazy { findViewById<RadioButton>(R.id.google_services_microg) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!installed(MICROG[0])) {
            finishAction(RESULT_SKIP)
            return
        }
        val state = packageManager.getApplicationEnabledSetting(MICROG[0])
        val on = state != PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER &&
            state != PackageManager.COMPONENT_ENABLED_STATE_DISABLED
        microg.isChecked = on
        findViewById<RadioButton>(R.id.google_services_none).isChecked = !on
    }

    private fun installed(pkg: String) = try {
        packageManager.getApplicationInfo(pkg, PackageManager.MATCH_DISABLED_COMPONENTS)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }

    override fun onNextPressed() {
        val state = if (microg.isChecked) {
            PackageManager.COMPONENT_ENABLED_STATE_DEFAULT
        } else {
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
        }
        for (pkg in MICROG) {
            if (installed(pkg)) packageManager.setApplicationEnabledSetting(pkg, state, 0)
        }
        super.onNextPressed()
    }

    override val layoutResId = R.layout.setup_google_services

    override val titleResId = R.string.setup_google_services

    override val iconResId = R.drawable.ic_google_services

    companion object {
        private val MICROG = arrayOf("com.google.android.gms", "com.android.vending")
    }
}
