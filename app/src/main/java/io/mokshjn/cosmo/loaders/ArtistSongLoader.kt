package io.mokshjn.cosmo.loaders

import android.content.Context
import android.provider.MediaStore
import io.mokshjn.cosmo.utils.PreferenceUtils

/**
 * Created by moksh on 28/11/17.
 */
class ArtistSongLoader : SongLoader() {
    companion object {
        fun getArtistSongList(context: Context, artistId: Int) = getSong(makeArtistSongCursor(
                context, artistId
        ))

        fun makeArtistSongCursor(context: Context, artistId: Int) = try {
            makeSongCursor(context,
                    "${MediaStore.Audio.AudioColumns.ARTIST_ID} = ?",
                    arrayOf("$artistId"),
                    PreferenceUtils.getInstance(context).artistSongSortOrder!!)
        } catch (e: SecurityException) {
            null
        }
    }
}