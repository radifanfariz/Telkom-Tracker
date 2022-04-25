package com.project.trackernity.other

import android.graphics.Color

object Constants {
    const val BASE_URL = "https://server.rno-one.com/"

    const val TRACKING_DATABASE_NAME = "tracking_db"

    const val AES_SECRET_KEY = "A88BF378612E335FB3B35DD6D57EC000"

    const val REQUEST_CODE_LOCATION_PERMISSION = 0
    const val REQUEST_CODE_ACTION_FINISH_RUN = 3

    const val LOCATION_UPDATE_INTERVAL = 5000L
    const val FASTEST_LOCATION_INTERVAL = 2000L

    const val UPDATE_INTERVAL = 50L
    const val UPDATE_INTERVAL_MARKER = 3000L
    const val UPDATE_INTERVAL_MARKER_ANIMATION = 5000L

    const val POLYLINE_COLOR = Color.RED
    const val POLYLINE_WIDTH = 8f
    const val MAP_ZOOM = 15f

    const val DEFAULT_ZOOM = 15

    // Keys for storing activity state.
    // [START maps_current_place_state_keys]
    const val KEY_CAMERA_POSITION = "camera_position"
    const val KEY_LOCATION = "location"
    // [END maps_current_place_state_keys]

    const val ACTION_START_OR_RESUME_SERVICE = "ACTION_START_OR_RESUME_SERVICE"
    const val ACTION_START_SERVICE = "ACTION_START_SERVICE"
    const val ACTION_PAUSE_SERVICE = "ACTION_PAUSE_SERVICE"
    const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"
    const val ACTION_SHOW_TRACKING_FRAGMENT = "ACTION_SHOW_TRACKING_FRAGMENT"
    const val ACTION_FINISH_RUN = "ACTION_FINISH_RUN"

    const val NOTIFICATION_CHANNEL_ID = "tracking_channel"
    const val NOTIFICATION_CHANNEL_NAME = "Tracking"
    const val NOTIFICATION_ID = 1

    const val NOTIFICATION_CHANNEL_TARGET_ID = "target_reached_channel"
    const val NOTIFICATION_CHANNEL_TARGET_NAME = "Target reached"
    const val NOTIFICATION_TARGET_ID = 2

    const val STOP_TRACKING_DIALOG_TAG = "STOP_TRACKING_DIALOG_TAG"
    const val START_TRACKING_DIALOG_TAG = "START_TRACKING_DIALOG_TAG"
    const val START_INTERACTIVE_DIALOG_TAG = "START_INTERACTIVE_DIALOG_TAG"
    const val LOGOUT_DIALOG_TAG = "LOGOUT_DIALOG_TAG"
    const val TOKEN_TRACKING_DIALOG = "TOKEN_TRACKING_DIALOG"
}