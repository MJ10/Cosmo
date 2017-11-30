package io.mokshjn.cosmo.provider

import android.annotation.SuppressLint
import android.content.Context
import io.mokshjn.cosmo.interfaces.Repository
import io.mokshjn.cosmo.loaders.*
import io.mokshjn.cosmo.models.Playlist
import io.mokshjn.cosmo.models.Song
import io.reactivex.Observable

/**
 * Created by moksh on 28/11/17.
 */
class RepositoryImpl(var context: Context) : Repository {

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var INSTANCE: RepositoryImpl? = null

        fun getInstance(context: Context): RepositoryImpl {
            if (INSTANCE == null)
                INSTANCE = RepositoryImpl(context)
            return INSTANCE!!
        }
    }

    override fun getAllSongs() = SongLoader.getAllSongs(context)

    override fun getSongs(id: Int): Observable<Song> = SongLoader.getSong(context, id)

    override fun getAllAlbums() = AlbumLoader.getAllAlbums(context)

    override fun getAlbum(id: Int) = AlbumLoader.getAlbum(context, id)

    override fun getAllArtists() = ArtistLoader.getAllArtists(context)

    override fun getArtist(id: Int) = ArtistLoader.getArtist(context, id)

    override fun getAllPlaylists() = PlaylistLoader.getAllPlaylists(context)

    override fun getPlaylistSongs(playlist: Playlist) = PlaylistSongLoader.getPlaylistSongList(
            context, playlist)

    override fun search(query: String) = SearchLoader.searchAll(context, query)
}