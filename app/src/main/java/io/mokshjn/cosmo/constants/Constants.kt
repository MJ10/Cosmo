package io.mokshjn.cosmo.constants

/**
 * Created by moksh on 27/11/17.
 */
class Constants {
    companion object {
        val COSMO_PACKAGE_NAME = "code.name.monkey.retromusic"
        val MUSIC_PACKAGE_NAME = "com.android.music"

        val ACTION_TOGGLE_PAUSE = COSMO_PACKAGE_NAME + ".togglepause"
        val ACTION_PLAY = COSMO_PACKAGE_NAME + ".play"
        val ACTION_PLAY_PLAYLIST = COSMO_PACKAGE_NAME + ".play.playlist"
        val ACTION_PAUSE = COSMO_PACKAGE_NAME + ".pause"
        val ACTION_STOP = COSMO_PACKAGE_NAME + ".stop"
        val ACTION_SKIP = COSMO_PACKAGE_NAME + ".skip"
        val ACTION_REWIND = COSMO_PACKAGE_NAME + ".rewind"
        val ACTION_QUIT = COSMO_PACKAGE_NAME + ".quitservice"
        val INTENT_EXTRA_PLAYLIST = COSMO_PACKAGE_NAME + "intentextra.playlist"
        val INTENT_EXTRA_SHUFFLE_MODE = COSMO_PACKAGE_NAME + ".intentextra.shufflemode"

        val APP_WIDGET_UPDATE = COSMO_PACKAGE_NAME + ".appwidgetupdate"
        val EXTRA_APP_WIDGET_NAME = COSMO_PACKAGE_NAME + "app_widget_name"

        // do not change these three strings as it will break support with other apps (e.g. last.fm scrobbling)
        val META_CHANGED = COSMO_PACKAGE_NAME + ".metachanged"
        val QUEUE_CHANGED = COSMO_PACKAGE_NAME + ".queuechanged"
        val PLAY_STATE_CHANGED = COSMO_PACKAGE_NAME + ".playstatechanged"

        val REPEAT_MODE_CHANGED = COSMO_PACKAGE_NAME + ".repeatmodechanged"
        val SHUFFLE_MODE_CHANGED = COSMO_PACKAGE_NAME + ".shufflemodechanged"
        val MEDIA_STORE_CHANGED = COSMO_PACKAGE_NAME + ".mediastorechanged"

        val GOOGLE_PLUS_PROFILE = "https://plus.google.com/u/0/+hemanthkumarh4h13"
        val KARIM_GOOGLE_PLUS = "https://google.com/+KarimAbouZeid23697"
        val KARIM_GITHUB = "https://github.com/kabouzeid"
        val MATERIAL_DESIGN_ICONS = "https://materialdesignicons.com"
        val GITHUB_PROJECT = "https://github.com/MJ10/Cosmo"

        val FLATICON_LINK = "https://www.flaticon.com/"
    }
}