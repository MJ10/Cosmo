package io.mokshjn.cosmo.interfaces

import io.mokshjn.cosmo.models.Album
import io.mokshjn.cosmo.models.Artist
import io.mokshjn.cosmo.models.Playlist
import io.mokshjn.cosmo.models.Song
import io.reactivex.Observable

/**
 * Created by moksh on 28/11/17.
 */
interface Repository {
    fun getAllSongs(): Observable<ArrayList<Song>>
    fun getSongs(id: Int): Observable<Song>
    fun getAllAlbums(): Observable<ArrayList<Album>>
    fun getAlbum(id: Int): Observable<Album>
    fun getArtist(id: Int): Observable<Artist>
    fun getAllArtists(): Observable<ArrayList<Artist>>
    fun getAllPlaylists(): Observable<ArrayList<Playlist>>
    fun search(): Observable<ArrayList<Song>>
    fun getPlaylistSongs(playlist: Playlist): Observable<ArrayList<Song>>
}