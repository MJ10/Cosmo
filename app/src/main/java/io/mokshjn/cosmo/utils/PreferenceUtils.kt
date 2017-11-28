package io.mokshjn.cosmo.utils

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import io.mokshjn.cosmo.helpers.SortOrder

/**
 * Created by moksh on 28/11/17.
 */
class PreferenceUtils private constructor(context: Context) {
    private val mPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    val defaultStartPage: Int
        get() = Integer.parseInt(mPreferences.getString(DEFAULT_START_PAGE, "-1"))

    var lastPage: Int
        get() = mPreferences.getInt(LAST_PAGE, 0)
        set(value) {
            val editor = mPreferences.edit()
            editor.putInt(LAST_PAGE, value)
            editor.apply()
        }

    var lastMusicChooser: Int
        get() = mPreferences.getInt(LAST_MUSIC_CHOOSER, 0)
        set(value) {
            val editor = mPreferences.edit()
            editor.putInt(LAST_MUSIC_CHOOSER, value)
            editor.apply()
        }

    var artistSortOrder: String?
        get() = mPreferences.getString(ARTIST_SORT_ORDER, SortOrder.ArtistSortOrder.ARTIST_A_Z)
        set(sortOrder) {
            val edit = this.mPreferences.edit()
            edit.putString(ARTIST_SORT_ORDER, sortOrder)
            edit.apply()
        }

    val artistSongSortOrder: String?
        get() = mPreferences.getString(ARTIST_SONG_SORT_ORDER,
                SortOrder.ArtistSongSortOrder.SONG_A_Z)

    val artistAlbumSortOrder: String?
        get() = mPreferences.getString(ARTIST_ALBUM_SORT_ORDER,
                SortOrder.ArtistAlbumSortOrder.ALBUM_YEAR)

    var albumSortOrder: String?
        get() = mPreferences.getString(ALBUM_SORT_ORDER, SortOrder.AlbumSortOrder.ALBUM_A_Z)
        set(shortOrder) = mPreferences.edit()
                .putString(ALBUM_SORT_ORDER, shortOrder)
                .apply()


    val albumSongSortOrder: String?
        get() = mPreferences.getString(ALBUM_SONG_SORT_ORDER,
                SortOrder.AlbumSongSortOrder.SONG_TRACK_LIST)

    var songSortOrder: String?
        get() = mPreferences.getString(SONG_SORT_ORDER, SortOrder.SongSortOrder.SONG_A_Z)
        set(sortOrder) {
            val edit = this.mPreferences.edit()
            edit.putString(SONG_SORT_ORDER, sortOrder)
            edit.apply()
        }

    var lastSleepTimerValue: Int
        get() = mPreferences.getInt(LAST_SLEEP_TIMER_VALUE, 30)
        set(value) {
            val editor = mPreferences.edit()
            editor.putInt(LAST_SLEEP_TIMER_VALUE, value)
            editor.apply()
        }

    val nextSleepTimerElapsedRealTime: Long
        get() = mPreferences.getLong(NEXT_SLEEP_TIMER_ELAPSED_REALTIME, -1)


    val lastAddedCutoff: Long
        get() {
            val calendarUtil = CalendarUtil()
            val interval: Long

            interval = when (mPreferences.getString(LAST_ADDED_CUTOFF, "")) {
                "today" -> calendarUtil.elapsedToday
                "this_week" -> calendarUtil.elapsedWeek
                "past_three_months" -> calendarUtil.getElapsedMonths(3)
                "this_year" -> calendarUtil.elapsedYear
                "this_month" -> calendarUtil.elapsedMonth
                else -> calendarUtil.elapsedMonth
            }

            return (System.currentTimeMillis() - interval) / 1000
        }

    val adaptiveColor: Boolean
        get() = mPreferences.getBoolean(ADAPTIVE_COLOR_APP, false)

    val lockScreen: Boolean
        get() = mPreferences.getBoolean(LOCK_SCREEN, false)

    var userName: String
        get() = mPreferences.getString(USER_NAME, "")
        set(name) = mPreferences.edit().putString(USER_NAME, name).apply()


    val fullScreenMode: Boolean
        get() = mPreferences.getBoolean(TOGGLE_FULL_SCREEN, false)

    val gradientColors: IntArray
        get() = intArrayOf(mPreferences.getInt(START_COLOR, 0), mPreferences.getInt(END_COLOR, 0))

    val profileImage: String
        get() = mPreferences.getString(PROFILE_IMAGE_PATH, "")

    var albumDetailSongSortOrder: String?
        get() =
            mPreferences.getString(ALBUM_DETAIL_SONG_SORT_ORDER, SortOrder.AlbumSongSortOrder.SONG_TRACK_LIST)
        set(sortOrder) {
            val edit = this.mPreferences.edit()
            edit.putString(ALBUM_DETAIL_SONG_SORT_ORDER, sortOrder)
            edit.apply()
        }

    var artistDetailSongSortOrder: String?
        get() =
            mPreferences.getString(ARTIST_DETAIL_SONG_SORT_ORDER, SortOrder.ArtistSongSortOrder.SONG_A_Z)
        set(sortOrder) {
            val edit = this.mPreferences.edit()
            edit.putString(ARTIST_DETAIL_SONG_SORT_ORDER, sortOrder)
            edit.apply()
        }


    fun registerOnSharedPreferenceChangedListener(sharedPreferenceChangeListener: SharedPreferences.OnSharedPreferenceChangeListener) {
        mPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener)
    }

    fun unregisterOnSharedPreferenceChangedListener(sharedPreferenceChangeListener: SharedPreferences.OnSharedPreferenceChangeListener) {
        mPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener)
    }

    fun coloredNotification(): Boolean {
        return mPreferences.getBoolean(COLORED_NOTIFICATION, true)
    }

    fun classicNotification(): Boolean {
        return mPreferences.getBoolean(CLASSIC_NOTIFICATION, false)
    }

    fun setClassicNotification(value: Boolean) {
        val editor = mPreferences.edit()
        editor.putBoolean(CLASSIC_NOTIFICATION, value)
        editor.apply()
    }

    fun setColoredAppShortcuts(value: Boolean) {
        val editor = mPreferences.edit()
        editor.putBoolean(COLORED_APP_SHORTCUTS, value)
        editor.apply()
    }

    fun coloredAppShortcuts(): Boolean {
        return mPreferences.getBoolean(COLORED_APP_SHORTCUTS, true)
    }

    fun gaplessPlayback(): Boolean {
        return mPreferences.getBoolean(GAPLESS_PLAYBACK, false)
    }

    fun audioDucking(): Boolean {
        return mPreferences.getBoolean(AUDIO_DUCKING, true)
    }

    fun albumArtOnLockscreen(): Boolean {
        return mPreferences.getBoolean(ALBUM_ART_ON_LOCKSCREEN, true)
    }

    fun blurredAlbumArt(): Boolean {
        return mPreferences.getBoolean(BLURRED_ALBUM_ART, false)
    }

    fun ignoreMediaStoreArtwork(): Boolean {
        return mPreferences.getBoolean(IGNORE_MEDIA_STORE_ARTWORK, false)
    }

    fun setNextSleepTimerElapsedRealtime(value: Long) {
        val editor = mPreferences.edit()
        editor.putLong(NEXT_SLEEP_TIMER_ELAPSED_REALTIME, value)
        editor.apply()
    }


    fun introShown(): Boolean {
        return mPreferences.getBoolean(INTRO_SHOWN, false)
    }

    fun autoDownloadImagesPolicy(): String {
        return mPreferences.getString(AUTO_DOWNLOAD_IMAGES_POLICY, "only_wifi")
    }

    fun synchronizedLyricsShow(): Boolean {
        return mPreferences.getBoolean(SYNCHRONIZED_LYRICS_SHOW, true)
    }

    fun setGradientColors(startColor: Int, endColor: Int) {
        mPreferences.edit()
                .putInt(START_COLOR, startColor)
                .putInt(END_COLOR, endColor)
                .apply()
    }

    fun saveProfileImage(profileImagePath: String) {
        mPreferences.edit().putString(PROFILE_IMAGE_PATH, profileImagePath)
                .apply()

    }

    fun setInitializedBlacklist() {
        val editor = mPreferences.edit()
        editor.putBoolean(INITIALIZED_BLACKLIST, true)
        editor.apply()
    }

    fun initializedBlacklist(): Boolean {
        return mPreferences.getBoolean(INITIALIZED_BLACKLIST, false)
    }

    companion object {
        val GENERAL_THEME = "general_theme"
        val DEFAULT_START_PAGE = "default_start_page"
        val LAST_PAGE = "last_start_page"
        val LAST_MUSIC_CHOOSER = "last_music_chooser"
        val NOW_PLAYING_SCREEN_ID = "now_playing_screen_id"
        val ARTIST_SORT_ORDER = "artist_sort_order"
        val ARTIST_SONG_SORT_ORDER = "artist_song_sort_order"
        val ARTIST_ALBUM_SORT_ORDER = "artist_album_sort_order"
        val ALBUM_SORT_ORDER = "album_sort_order"
        val ALBUM_SONG_SORT_ORDER = "album_song_sort_order"
        val SONG_SORT_ORDER = "song_sort_order"
        val ALBUM_GRID_SIZE = "album_grid_size"
        val ALBUM_GRID_SIZE_LAND = "album_grid_size_land"
        val SONG_GRID_SIZE = "song_grid_size"
        val SONG_GRID_SIZE_LAND = "song_grid_size_land"
        val ARTIST_GRID_SIZE = "artist_grid_size"
        val ARTIST_GRID_SIZE_LAND = "artist_grid_size_land"
        val ALBUM_COLORED_FOOTERS = "album_colored_footers"
        val SONG_COLORED_FOOTERS = "song_colored_footers"
        val ARTIST_COLORED_FOOTERS = "artist_colored_footers"
        val ALBUM_ARTIST_COLORED_FOOTERS = "album_artist_colored_footers"
        val FORCE_SQUARE_ALBUM_COVER = "force_square_album_art"
        val COLORED_NOTIFICATION = "colored_notification"
        val CLASSIC_NOTIFICATION = "classic_notification"
        val COLORED_APP_SHORTCUTS = "colored_app_shortcuts"
        val AUDIO_DUCKING = "audio_ducking"
        val GAPLESS_PLAYBACK = "gapless_playback"
        val LAST_ADDED_CUTOFF = "last_added_interval"
        val ALBUM_ART_ON_LOCKSCREEN = "album_art_on_lockscreen"
        val BLURRED_ALBUM_ART = "blurred_album_art"
        val LAST_SLEEP_TIMER_VALUE = "last_sleep_timer_value"
        val NEXT_SLEEP_TIMER_ELAPSED_REALTIME = "next_sleep_timer_elapsed_real_time"
        val IGNORE_MEDIA_STORE_ARTWORK = "ignore_media_store_artwork"
        val LAST_CHANGELOG_VERSION = "last_changelog_version"
        val INTRO_SHOWN = "intro_shown"
        val AUTO_DOWNLOAD_IMAGES_POLICY = "auto_download_images_policy"
        val START_DIRECTORY = "start_directory"
        val SYNCHRONIZED_LYRICS_SHOW = "synchronized_lyrics_show"
        private val ADAPTIVE_COLOR_APP = "adaptive_color_app"
        private val LOCK_SCREEN = "lock_screen"
        private val USER_NAME = "user_name"
        private val USER_NAME_SKIPPED = "user_name_skipped"
        private val TOGGLE_FULL_SCREEN = "toggle_full_screen"
        private val START_COLOR = "start_color"
        private val END_COLOR = "end_color"
        private val PROFILE_IMAGE_PATH = "profile_image_path"
        private val INITIALIZED_BLACKLIST = "initialized_blacklist"
        private val ALBUM_DETAIL_SONG_SORT_ORDER = "album_detail_song_sort_order"
        private val ARTIST_DETAIL_SONG_SORT_ORDER = "artist_detail_song_sort_order"
        private var sInstance: PreferenceUtils? = null

        fun getInstance(context: Context): PreferenceUtils {
            if (sInstance == null) {
                sInstance = PreferenceUtils(context.applicationContext)
            }
            return sInstance!!
        }
    }
}
