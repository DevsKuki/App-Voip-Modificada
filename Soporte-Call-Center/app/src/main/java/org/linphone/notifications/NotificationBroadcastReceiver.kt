/*
 * Copyright (c) 2010-2020 Belledonne Communications SARL.
 *
 * This file is part of linphone-android
 * (see https://www.linphone.org).
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.linphone.notifications

import android.app.RemoteInput
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.linphone.LinphoneApplication.Companion.coreContext
import org.linphone.LinphoneApplication.Companion.ensureCoreExists
import org.linphone.core.Call
import org.linphone.core.Core
import org.linphone.core.tools.Log

class NotificationBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.i("[Notification Broadcast Receiver] Ensuring Core exists")
        ensureCoreExists(context.applicationContext, false)

        val notificationId = intent.getIntExtra(NotificationsManager.INTENT_NOTIF_ID, 0)
        Log.i(
            "[Notification Broadcast Receiver] Got notification broadcast for ID [$notificationId]"
        )

        if (intent.action == NotificationsManager.INTENT_ANSWER_CALL_NOTIF_ACTION || intent.action == NotificationsManager.INTENT_HANGUP_CALL_NOTIF_ACTION) {
            handleCallIntent(intent)
        }
    }

    private fun handleCallIntent(intent: Intent) {
        val remoteSipAddress = intent.getStringExtra(NotificationsManager.INTENT_REMOTE_ADDRESS)
        if (remoteSipAddress == null) {
            Log.e("[Notification Broadcast Receiver] Remote SIP address is null for notification")
            return
        }

        val core: Core = coreContext.core

        val remoteAddress = core.interpretUrl(remoteSipAddress, false)
        val call = if (remoteAddress != null) core.getCallByRemoteAddress2(remoteAddress) else null
        if (call == null) {
            Log.e(
                "[Notification Broadcast Receiver] Couldn't find call from remote address $remoteSipAddress"
            )
            return
        }

        if (intent.action == NotificationsManager.INTENT_ANSWER_CALL_NOTIF_ACTION) {
            coreContext.answerCall(call)
        } else {
            if (call.state == Call.State.IncomingReceived ||
                call.state == Call.State.IncomingEarlyMedia
            ) {
                coreContext.declineCall(call)
            } else {
                coreContext.terminateCall(call)
            }
        }
    }

    private fun getMessageText(intent: Intent): CharSequence? {
        val remoteInput = RemoteInput.getResultsFromIntent(intent)
        return remoteInput?.getCharSequence(NotificationsManager.KEY_TEXT_REPLY)
    }
}
