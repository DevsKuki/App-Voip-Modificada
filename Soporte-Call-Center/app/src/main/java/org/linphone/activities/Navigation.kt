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
package org.linphone.activities

import android.net.Uri
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import org.linphone.R
import org.linphone.activities.assistant.fragments.*
import org.linphone.activities.main.MainActivity
/*import org.linphone.activities.main.chat.fragments.ChatRoomCreationFragment
import org.linphone.activities.main.chat.fragments.DetailChatRoomFragment
import org.linphone.activities.main.chat.fragments.GroupInfoFragment
import org.linphone.activities.main.conference.fragments.* */
import org.linphone.activities.main.contact.fragments.ContactEditorFragment
import org.linphone.activities.main.contact.fragments.DetailContactFragment
import org.linphone.activities.main.contact.fragments.MasterContactsFragment
import org.linphone.activities.main.dialer.fragments.DialerFragment
import org.linphone.activities.main.fragments.TabsFragment
import org.linphone.activities.main.history.fragments.DetailCallLogFragment
import org.linphone.activities.main.history.fragments.MasterCallLogsFragment
import org.linphone.activities.main.settings.fragments.*
import org.linphone.activities.main.sidemenu.fragments.SideMenuFragment
import org.linphone.activities.voip.CallActivity
import org.linphone.activities.voip.fragments.*
import org.linphone.core.tools.Log

internal fun Fragment.findMasterNavController(): NavController {
    return parentFragment?.parentFragment?.findNavController() ?: findNavController()
}

fun popupTo(
    popUpTo: Int = -1,
    popUpInclusive: Boolean = false,
    singleTop: Boolean = true
): NavOptions {
    val builder = NavOptions.Builder()
    builder.setPopUpTo(popUpTo, popUpInclusive).setLaunchSingleTop(singleTop)
    return builder.build()
}

/* Main activity related */

internal fun MainActivity.navigateToDialer(args: Bundle? = null) {
    findNavController(R.id.nav_host_fragment).navigate(
        R.id.action_global_dialerFragment,
        args,
        popupTo(R.id.dialerFragment, true)
    )
}

internal fun MainActivity.navigateToChatRooms(args: Bundle? = null) {
    findNavController(R.id.nav_host_fragment).navigate(
        R.id.action_global_masterChatRoomsFragment,
        args,
        popupTo(R.id.masterChatRoomsFragment, true)
    )
}

internal fun MainActivity.navigateToChatRoom(localAddress: String?, peerAddress: String?) {
    val deepLink = "linphone-android://chat-room/$localAddress/$peerAddress"
    findNavController(R.id.nav_host_fragment).navigate(
        Uri.parse(deepLink),
        popupTo(R.id.masterChatRoomsFragment, true)
    )
}

internal fun MainActivity.navigateToContacts() {
    findNavController(R.id.nav_host_fragment).navigate(
        R.id.action_global_masterContactsFragment,
        null,
        popupTo(R.id.masterContactsFragment, true)
    )
}

internal fun MainActivity.navigateToContact(contactId: String?) {
    val deepLink = "linphone-android://contact/view/$contactId"
    findNavController(R.id.nav_host_fragment).navigate(
        Uri.parse(deepLink),
        popupTo(R.id.masterContactsFragment, true)
    )
}

/* Tabs fragment related */

internal fun TabsFragment.navigateToCallHistory() {
    val action = when (findNavController().currentDestination?.id) {
        R.id.masterContactsFragment -> R.id.action_masterContactsFragment_to_masterCallLogsFragment
        R.id.dialerFragment -> R.id.action_dialerFragment_to_masterCallLogsFragment
        R.id.genericAccountListContactFragment -> R.id.action_genericAccountListContactFragment_to_masterCallLogsFragment
        else -> R.id.action_global_masterCallLogsFragment
    }
    findNavController().navigate(
        action,
        null,
        popupTo(R.id.masterCallLogsFragment, true)
    )
}

internal fun TabsFragment.navigateToContacts() {
    val action = when (findNavController().currentDestination?.id) {
        R.id.masterCallLogsFragment -> R.id.action_masterCallLogsFragment_to_masterContactsFragment
        R.id.dialerFragment -> R.id.action_dialerFragment_to_masterContactsFragment
        R.id.genericAccountListContactFragment -> R.id.action_genericAccountListContactFragment_to_masterContactsFragment
        else -> R.id.action_global_masterContactsFragment
    }
    findNavController().navigate(
        action,
        null,
        popupTo(R.id.masterContactsFragment, true)
    )
}

internal fun TabsFragment.navigateToDialer() {
    val action = when (findNavController().currentDestination?.id) {
        R.id.masterCallLogsFragment -> R.id.action_masterCallLogsFragment_to_dialerFragment
        R.id.masterContactsFragment -> R.id.action_masterContactsFragment_to_dialerFragment
        R.id.genericAccountListContactFragment -> R.id.action_genericAccountListContactFragment_to_dialerFragment
        else -> R.id.action_global_dialerFragment
    }
    findNavController().navigate(
        action,
        null,
        popupTo(R.id.dialerFragment, true)
    )
}

internal fun TabsFragment.navigateToUsers() {
    val action = when (findNavController().currentDestination?.id) {
        R.id.dialerFragment -> R.id.action_dialerFragment_to_genericAccountListContactFragment
        R.id.masterCallLogsFragment -> R.id.action_masterCallLogsFragment_to_genericAccountListContactFragment
        R.id.masterContactsFragment -> R.id.action_masterContactsFragment_to_genericAccountListContactFragment

        else -> R.id.genericAccountListContactFragment
    }
    findNavController().navigate(
        action,
        null,
        popupTo(R.id.genericAccountListContactFragment, true)
    )
}
/* Dialer related */

internal fun DialerFragment.navigateToContacts(uriToAdd: String?) {
    if (uriToAdd.isNullOrEmpty()) {
        Log.e("[Navigation] SIP URI to add to contact is null or empty!")
        return
    }

    val deepLink = "linphone-android://contact/new/$uriToAdd"
    findNavController().navigate(
        Uri.parse(deepLink),
        popupTo(R.id.masterContactsFragment, true)
    )
}

internal fun DialerFragment.navigateToConfigFileViewer() {
    val bundle = bundleOf("Secure" to true)
    findMasterNavController().navigate(
        R.id.action_global_configViewerFragment,
        bundle,
        popupTo()
    )
}

internal fun DialerFragment.navigateToConferenceScheduling() {
    findMasterNavController().navigate(
        R.id.action_global_conferenceSchedulingFragment,
        null,
        popupTo()
    )
}

/* Contacts related */

internal fun MasterContactsFragment.navigateToContact() {
    if (findNavController().currentDestination?.id == R.id.masterContactsFragment) {
        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.contacts_nav_container) as NavHostFragment
        navHostFragment.navController.navigate(
            R.id.action_global_detailContactFragment,
            null,
            popupTo(R.id.emptyContactFragment, false)
        )
    }
}

internal fun MasterContactsFragment.navigateToContactEditor(
    sipUriToAdd: String? = null,
    slidingPane: SlidingPaneLayout
) {
    if (findNavController().currentDestination?.id == R.id.masterContactsFragment) {
        val bundle = if (sipUriToAdd != null) bundleOf("SipUri" to sipUriToAdd) else Bundle()
        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.contacts_nav_container) as NavHostFragment
        navHostFragment.navController.navigate(
            R.id.action_global_contactEditorFragment,
            bundle,
            popupTo(R.id.emptyContactFragment, false)
        )
        if (!slidingPane.isOpen) slidingPane.openPane()
    }
}

internal fun MasterContactsFragment.clearDisplayedContact() {
    if (findNavController().currentDestination?.id == R.id.masterContactsFragment) {
        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.contacts_nav_container) as NavHostFragment
        navHostFragment.navController.navigate(
            R.id.action_global_emptyContactFragment,
            null,
            popupTo(R.id.emptyContactFragment, true)
        )
    }
}

internal fun ContactEditorFragment.navigateToContact(id: String) {
    if (findNavController().currentDestination?.id == R.id.contactEditorFragment) {
        val bundle = Bundle()
        bundle.putString("id", id)
        findNavController().navigate(
            R.id.action_contactEditorFragment_to_detailContactFragment,
            bundle,
            popupTo(R.id.contactEditorFragment, true)
        )
    }
}

internal fun DetailContactFragment.navigateToChatRoom(args: Bundle?) {
    findMasterNavController().navigate(
        R.id.action_global_masterChatRoomsFragment,
        args,
        popupTo(R.id.masterChatRoomsFragment, true)
    )
}

internal fun DetailContactFragment.navigateToDialer(args: Bundle?) {
    findMasterNavController().navigate(
        R.id.action_global_dialerFragment,
        args,
        popupTo(R.id.dialerFragment, true)
    )
}

internal fun DetailContactFragment.navigateToContactEditor() {
    if (findNavController().currentDestination?.id == R.id.detailContactFragment) {
        findNavController().navigate(
            R.id.action_detailContactFragment_to_contactEditorFragment,
            null,
            popupTo(R.id.contactEditorFragment, true)
        )
    }
}

/* History related */

internal fun MasterCallLogsFragment.navigateToCallHistory(slidingPane: SlidingPaneLayout) {
    if (findNavController().currentDestination?.id == R.id.masterCallLogsFragment) {
        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.history_nav_container) as NavHostFragment
        navHostFragment.navController.navigate(
            R.id.action_global_detailCallLogFragment,
            null,
            popupTo(R.id.emptyCallHistoryFragment, false)
        )
        if (!slidingPane.isOpen) slidingPane.openPane()
    }
}

internal fun MasterCallLogsFragment.navigateToConferenceCallHistory(slidingPane: SlidingPaneLayout) {
    if (findNavController().currentDestination?.id == R.id.masterCallLogsFragment) {
        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.history_nav_container) as NavHostFragment
        navHostFragment.navController.navigate(
            R.id.action_global_detailConferenceCallLogFragment,
            null,
            popupTo(R.id.emptyCallHistoryFragment, false)
        )
        if (!slidingPane.isOpen) slidingPane.openPane()
    }
}

internal fun MasterCallLogsFragment.clearDisplayedCallHistory() {
    if (findNavController().currentDestination?.id == R.id.masterCallLogsFragment) {
        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.history_nav_container) as NavHostFragment
        navHostFragment.navController.navigate(
            R.id.action_global_emptyFragment,
            null,
            popupTo(R.id.emptyCallHistoryFragment, true)
        )
    }
}

internal fun MasterCallLogsFragment.navigateToDialer(args: Bundle?) {
    findNavController().navigate(
        R.id.action_global_dialerFragment,
        args,
        popupTo(R.id.dialerFragment, true)
    )
}

internal fun MasterCallLogsFragment.navigateToConferenceWaitingRoom(
    address: String,
    subject: String?
) {
    val bundle = Bundle()
    bundle.putString("Address", address)
    bundle.putString("Subject", subject)
    findMasterNavController().navigate(
        R.id.action_global_conferenceWaitingRoomFragment,
        bundle,
        popupTo(R.id.conferenceWaitingRoomFragment, true)
    )
}

internal fun DetailCallLogFragment.navigateToContacts(sipUriToAdd: String) {
    if (sipUriToAdd.isEmpty()) {
        Log.e("[Navigation] SIP URI to add to contact is empty!")
        return
    }

    val deepLink = "linphone-android://contact/new/$sipUriToAdd"
    findMasterNavController().navigate(Uri.parse(deepLink))
}

internal fun DetailCallLogFragment.navigateToNativeContact(id: String) {
    val deepLink = "linphone-android://contact/view/$id"
    findMasterNavController().navigate(Uri.parse(deepLink))
}

internal fun DetailCallLogFragment.navigateToFriend(address: String) {
    val deepLink = "linphone-android://contact/view-friend/$address"
    findMasterNavController().navigate(Uri.parse(deepLink))
}

internal fun DetailCallLogFragment.navigateToChatRoom(args: Bundle?) {
    if (findNavController().currentDestination?.id == R.id.detailCallLogFragment) {
        findMasterNavController().navigate(
            R.id.action_global_masterChatRoomsFragment,
            args,
            popupTo(R.id.masterChatRoomsFragment, true)
        )
    }
}

internal fun DetailCallLogFragment.navigateToDialer(args: Bundle?) {
    if (findNavController().currentDestination?.id == R.id.detailCallLogFragment) {
        findMasterNavController().navigate(
            R.id.action_global_dialerFragment,
            args,
            popupTo(R.id.dialerFragment, true)
        )
    }
}

/* Settings related */

internal fun SettingsFragment.navigateToAccountSettings(identity: String) {
    if (findNavController().currentDestination?.id == R.id.settingsFragment) {
        val bundle = bundleOf("Identity" to identity)
        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.settings_nav_container) as NavHostFragment
        navHostFragment.navController.navigate(
            R.id.action_global_accountSettingsFragment,
            bundle,
            popupTo(R.id.emptySettingsFragment, false)
        )
    }
}

internal fun SettingsFragment.navigateToTunnelSettings(slidingPane: SlidingPaneLayout) {
    if (findNavController().currentDestination?.id == R.id.settingsFragment) {
        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.settings_nav_container) as NavHostFragment
        navHostFragment.navController.navigate(
            R.id.action_global_tunnelSettingsFragment,
            null,
            popupTo(R.id.emptySettingsFragment, false)
        )
        if (!slidingPane.isOpen) slidingPane.openPane()
    }
}

internal fun SettingsFragment.navigateToAudioSettings(slidingPane: SlidingPaneLayout) {
    if (findNavController().currentDestination?.id == R.id.settingsFragment) {
        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.settings_nav_container) as NavHostFragment
        navHostFragment.navController.navigate(
            R.id.action_global_audioSettingsFragment,
            null,
            popupTo(R.id.emptySettingsFragment, false)
        )
        if (!slidingPane.isOpen) slidingPane.openPane()
    }
}

internal fun SettingsFragment.navigateToVideoSettings(slidingPane: SlidingPaneLayout) {
    if (findNavController().currentDestination?.id == R.id.settingsFragment) {
        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.settings_nav_container) as NavHostFragment
        navHostFragment.navController.navigate(
            R.id.action_global_videoSettingsFragment,
            null,
            popupTo(R.id.emptySettingsFragment, false)
        )
        if (!slidingPane.isOpen) slidingPane.openPane()
    }
}

internal fun SettingsFragment.navigateToCallSettings(slidingPane: SlidingPaneLayout) {
    if (findNavController().currentDestination?.id == R.id.settingsFragment) {
        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.settings_nav_container) as NavHostFragment
        navHostFragment.navController.navigate(
            R.id.action_global_callSettingsFragment,
            null,
            popupTo(R.id.emptySettingsFragment, false)
        )
        if (!slidingPane.isOpen) slidingPane.openPane()
    }
}

internal fun SettingsFragment.navigateToChatSettings(slidingPane: SlidingPaneLayout) {
    if (findNavController().currentDestination?.id == R.id.settingsFragment) {
        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.settings_nav_container) as NavHostFragment
        navHostFragment.navController.navigate(
            R.id.action_global_chatSettingsFragment,
            null,
            popupTo(R.id.emptySettingsFragment, false)
        )
        if (!slidingPane.isOpen) slidingPane.openPane()
    }
}

internal fun SettingsFragment.navigateToNetworkSettings(slidingPane: SlidingPaneLayout) {
    if (findNavController().currentDestination?.id == R.id.settingsFragment) {
        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.settings_nav_container) as NavHostFragment
        navHostFragment.navController.navigate(
            R.id.action_global_networkSettingsFragment,
            null,
            popupTo(R.id.emptySettingsFragment, false)
        )
        if (!slidingPane.isOpen) slidingPane.openPane()
    }
}

internal fun SettingsFragment.navigateToContactsSettings(slidingPane: SlidingPaneLayout) {
    if (findNavController().currentDestination?.id == R.id.settingsFragment) {
        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.settings_nav_container) as NavHostFragment
        navHostFragment.navController.navigate(
            R.id.action_global_contactsSettingsFragment,
            null,
            popupTo(R.id.emptySettingsFragment, false)
        )
        if (!slidingPane.isOpen) slidingPane.openPane()
    }
}

internal fun SettingsFragment.navigateToAdvancedSettings(slidingPane: SlidingPaneLayout) {
    if (findNavController().currentDestination?.id == R.id.settingsFragment) {
        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.settings_nav_container) as NavHostFragment
        navHostFragment.navController.navigate(
            R.id.action_global_advancedSettingsFragment,
            null,
            popupTo(R.id.emptySettingsFragment, false)
        )
        if (!slidingPane.isOpen) slidingPane.openPane()
    }
}

internal fun SettingsFragment.navigateToConferencesSettings(slidingPane: SlidingPaneLayout) {
    if (findNavController().currentDestination?.id == R.id.settingsFragment) {
        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.settings_nav_container) as NavHostFragment
        navHostFragment.navController.navigate(
            R.id.action_global_conferencesSettingsFragment,
            null,
            popupTo(R.id.emptySettingsFragment, false)
        )
        if (!slidingPane.isOpen) slidingPane.openPane()
    }
}

internal fun AccountSettingsFragment.navigateToPhoneLinking(args: Bundle?) {
    if (findNavController().currentDestination?.id == R.id.accountSettingsFragment) {
        findNavController().navigate(
            R.id.action_accountSettingsFragment_to_phoneAccountLinkingFragment,
            args,
            popupTo()
        )
    }
}

internal fun navigateToEmptySetting(navController: NavController) {
    navController.navigate(
        R.id.action_global_emptySettingsFragment,
        null,
        popupTo(R.id.emptySettingsFragment, true)
    )
}

internal fun ContactsSettingsFragment.navigateToLdapSettings(configIndex: Int) {
    if (findNavController().currentDestination?.id == R.id.contactsSettingsFragment) {
        val bundle = bundleOf("LdapConfigIndex" to configIndex)
        findNavController().navigate(
            R.id.action_contactsSettingsFragment_to_ldapSettingsFragment,
            bundle,
            popupTo()
        )
    }
}

/* Side menu related */

internal fun SideMenuFragment.navigateToAccountSettings(identity: String) {
    val deepLink = "linphone-android://settings/$identity"
    try {
        findNavController().navigate(Uri.parse(deepLink))
    } catch (iae: IllegalArgumentException) {
        Log.e("[Navigation] Failed to navigate to deeplink [$deepLink]")
    }
}

internal fun SideMenuFragment.navigateToSettings() {
    findNavController().navigate(
        R.id.action_global_settingsFragment,
        null,
        popupTo(R.id.settingsFragment, true)
    )
}

internal fun SideMenuFragment.navigateToAbout() {
    findNavController().navigate(
        R.id.action_global_aboutFragment,
        null,
        popupTo(R.id.aboutFragment, true)
    )
}

internal fun SideMenuFragment.navigateToRecordings() {
    findNavController().navigate(
        R.id.action_global_recordingsFragment,
        null,
        popupTo(R.id.recordingsFragment, true)
    )
}

internal fun SideMenuFragment.navigateToScheduledConferences() {
    findNavController().navigate(
        R.id.action_global_scheduledConferencesFragment,
        null,
        popupTo(R.id.scheduledConferencesFragment, true)
    )
}

/* Calls related */

internal fun CallActivity.navigateToActiveCall() {
    if (findNavController(R.id.nav_host_fragment).currentDestination?.id != R.id.singleCallFragment) {
        findNavController(R.id.nav_host_fragment).navigate(
            R.id.action_global_singleCallFragment,
            null,
            popupTo(R.id.conferenceCallFragment, true)
        )
    }
}

internal fun CallActivity.navigateToConferenceCall() {
    if (findNavController(R.id.nav_host_fragment).currentDestination?.id != R.id.conferenceCallFragment) {
        findNavController(R.id.nav_host_fragment).navigate(
            R.id.action_global_conferenceCallFragment,
            null,
            popupTo(R.id.singleCallFragment, true)
        )
    }
}

internal fun CallActivity.navigateToOutgoingCall() {
    findNavController(R.id.nav_host_fragment).navigate(
        R.id.action_global_outgoingCallFragment,
        null,
        popupTo(R.id.singleCallFragment, true)
    )
}

internal fun CallActivity.navigateToIncomingCall(earlyMediaVideoEnabled: Boolean) {
    val args = Bundle()
    args.putBoolean("earlyMediaVideo", earlyMediaVideoEnabled)
    findNavController(R.id.nav_host_fragment).navigate(
        R.id.action_global_incomingCallFragment,
        args,
        popupTo(R.id.singleCallFragment, true)
    )
}

internal fun OutgoingCallFragment.navigateToActiveCall() {
    findNavController().navigate(
        R.id.action_global_singleCallFragment,
        null,
        popupTo(R.id.outgoingCallFragment, true)
    )
}

internal fun IncomingCallFragment.navigateToActiveCall() {
    findNavController().navigate(
        R.id.action_global_singleCallFragment,
        null,
        popupTo(R.id.incomingCallFragment, true)
    )
}

internal fun SingleCallFragment.navigateToCallsList() {
    if (findNavController().currentDestination?.id == R.id.singleCallFragment) {
        findNavController().navigate(
            R.id.action_singleCallFragment_to_callsListFragment,
            null,
            popupTo()
        )
    }
}

internal fun SingleCallFragment.navigateToConferenceParticipants() {
    if (findNavController().currentDestination?.id == R.id.singleCallFragment) {
        findNavController().navigate(
            R.id.action_singleCallFragment_to_conferenceParticipantsFragment,
            null,
            popupTo()
        )
    }
}

internal fun SingleCallFragment.navigateToConferenceLayout() {
    if (findNavController().currentDestination?.id == R.id.singleCallFragment) {
        findNavController().navigate(
            R.id.action_singleCallFragment_to_conferenceLayoutFragment,
            null,
            popupTo()
        )
    }
}

internal fun SingleCallFragment.navigateToIncomingCall() {
    if (findNavController().currentDestination?.id == R.id.singleCallFragment) {
        findNavController().navigate(
            R.id.action_global_incomingCallFragment,
            null,
            popupTo(R.id.singleCallFragment, true)
        )
    }
}

internal fun SingleCallFragment.navigateToOutgoingCall() {
    if (findNavController().currentDestination?.id == R.id.singleCallFragment) {
        findNavController().navigate(
            R.id.action_global_outgoingCallFragment,
            null,
            popupTo(R.id.singleCallFragment, true)
        )
    }
}

internal fun ConferenceCallFragment.navigateToCallsList() {
    if (findNavController().currentDestination?.id == R.id.conferenceCallFragment) {
        findNavController().navigate(
            R.id.action_conferenceCallFragment_to_callsListFragment,
            null,
            popupTo()
        )
    }
}

internal fun ConferenceCallFragment.navigateToConferenceParticipants() {
    if (findNavController().currentDestination?.id == R.id.conferenceCallFragment) {
        findNavController().navigate(
            R.id.action_conferenceCallFragment_to_conferenceParticipantsFragment,
            null,
            popupTo()
        )
    }
}

internal fun ConferenceCallFragment.navigateToConferenceLayout() {
    if (findNavController().currentDestination?.id == R.id.conferenceCallFragment) {
        findNavController().navigate(
            R.id.action_conferenceCallFragment_to_conferenceLayoutFragment,
            null,
            popupTo()
        )
    }
}

internal fun ConferenceCallFragment.refreshConferenceFragment() {
    if (findNavController().currentDestination?.id == R.id.conferenceCallFragment) {
        findNavController().navigate(
            R.id.action_global_conferenceCallFragment,
            null,
            popupTo(R.id.conferenceCallFragment, true)
        )
    }
}

internal fun ConferenceParticipantsFragment.navigateToAddParticipants() {
    if (findNavController().currentDestination?.id == R.id.conferenceParticipantsFragment) {
        findNavController().navigate(
            R.id.action_conferenceParticipantsFragment_to_conferenceAddParticipantsFragment,
            null,
            popupTo(R.id.conferenceAddParticipantsFragment, true)
        )
    }
}

/* Assistant related */

internal fun WelcomeFragment.navigateToEmailAccountCreation() {
    if (findNavController().currentDestination?.id == R.id.welcomeFragment) {
        findNavController().navigate(
            R.id.action_welcomeFragment_to_emailAccountCreationFragment,
            null,
            popupTo()
        )
    }
}

internal fun WelcomeFragment.navigateToPhoneAccountCreation() {
    if (findNavController().currentDestination?.id == R.id.welcomeFragment) {
        findNavController().navigate(
            R.id.action_welcomeFragment_to_phoneAccountCreationFragment,
            null,
            popupTo()
        )
    }
}

internal fun WelcomeFragment.navigateToNoPushWarning() {
    if (findNavController().currentDestination?.id == R.id.welcomeFragment) {
        findNavController().navigate(
            R.id.action_welcomeFragment_to_noPushWarningFragment,
            null,
            popupTo()
        )
    }
}

internal fun WelcomeFragment.navigateToAccountLogin() {
    if (findNavController().currentDestination?.id == R.id.welcomeFragment) {
        findNavController().navigate(
            R.id.action_welcomeFragment_to_accountLoginFragment,
            null,
            popupTo()
        )
    }
}

internal fun WelcomeFragment.navigateToGenericLoginWarning() {
    if (findNavController().currentDestination?.id == R.id.welcomeFragment) {
        findNavController().navigate(
            R.id.action_welcomeFragment_to_genericAccountWarningFragment,
            null,
            popupTo()
        )
    }
}

internal fun WelcomeFragment.navigateToRemoteProvisioning() {
    if (findNavController().currentDestination?.id == R.id.welcomeFragment) {
        findNavController().navigate(
            R.id.action_welcomeFragment_to_remoteProvisioningFragment,
            null,
            popupTo()
        )
    }
}

internal fun GenericAccountWarningFragment.navigateToGenericLogin() {
    if (findNavController().currentDestination?.id == R.id.genericAccountWarningFragment) {
        findNavController().navigate(
            R.id.action_genericAccountWarningFragment_to_genericAccountLoginFragment,
            null,
            popupTo(R.id.welcomeFragment, popUpInclusive = false)
        )
    }
}

internal fun GenericAccountLoginFragment.navigateToEchoCancellerCalibration() {
    if (findNavController().currentDestination?.id == R.id.genericAccountLoginFragment) {
        findNavController().navigate(
            R.id.action_genericAccountLoginFragment_to_echoCancellerCalibrationFragment,
            null,
            popupTo()
        )
    }
}
