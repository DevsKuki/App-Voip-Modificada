
package org.linphone.activities.assistant

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import org.linphone.LinphoneApplication.Companion.corePreferences
import org.linphone.R
import org.linphone.activities.GenericActivity
import org.linphone.activities.SnackBarActivity
import org.linphone.activities.assistant.viewmodels.SharedAssistantViewModel

class AssistantActivity : GenericActivity(), SnackBarActivity {
    private lateinit var sharedViewModel: SharedAssistantViewModel
    private lateinit var coordinator: CoordinatorLayout

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.assistant_activity)

        sharedViewModel = ViewModelProvider(this)[SharedAssistantViewModel::class.java]

        coordinator = findViewById(R.id.coordinator)

        corePreferences.firstStart = false
    }

    override fun showSnackBar(@StringRes resourceId: Int) {
        Snackbar.make(coordinator, resourceId, Snackbar.LENGTH_LONG).show()
    }

    override fun showSnackBar(@StringRes resourceId: Int, action: Int, listener: () -> Unit) {
        Snackbar
            .make(findViewById(R.id.coordinator), resourceId, Snackbar.LENGTH_LONG)
            .setAction(action) {
                listener()
            }
            .show()
    }

    override fun showSnackBar(message: String) {
        Snackbar.make(coordinator, message, Snackbar.LENGTH_LONG).show()
    }
}
