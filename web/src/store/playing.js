import {defineStore} from 'pinia'
import {api} from "@/core/api";

export const playingStore = defineStore('playing', {
    state: () => {
        return {
            player: null,
            playlist: null,
            playId: null,
            playlists: null
        }
    },
    actions: {
        initial(ap) {
            this.player = ap
            this.player.on('ended', () => {
                this.next()
            })
            this.player.on('error', e => {
                console.log(e)
                this.next()
            })
            navigator.mediaSession.setActionHandler(
                'nexttrack',
                () => {
                    this.next()
                }
            );
            navigator.mediaSession.setActionHandler(
                'previoustrack',
                () => {
                    this.next()
                }
            );
            navigator.mediaSession.metadata = new MediaMetadata()
        },
        next() {
            if (this.playlist) {
                let id = this.playlist[Math.floor(Math.random() * this.playlist.length)].song
                api.fetchSongs([id]).then(ret => {
                    if (ret.data && ret.data[0].url) {
                        this.play(ret.data[0])
                    } else {
                        this.next()
                    }
                }).catch(e => {
                    this.next()
                })
            }
        },
        play(song) {
            this.player.list.clear()
            this.playId = song['id']
            this.player.list.add({
                name: song['name'],
                artist: song['authors'].join(','),
                url: song['url'],
                cover: song['cover']
            })
            this.player.list.switch(0)
            navigator.mediaSession.metadata.title = song.name
            navigator.mediaSession.metadata.artist = song['authors'].join(',')
            navigator.mediaSession.metadata.artwork = [{src: song['cover']}]
            this.player.on('canplay', () => {
                this.player.play()
            })
        }
    },
    getters: {
        hasSong(state) {
            return state.player && state.player.list.audios.length > 0
        },
        hasPlaylist(state) {
            return state.playlist && state.playlist.length > 0
        },
        getIndex(state) {
            if (!state.playlist || !state.player || !state.playId) {
                return -1
            }
            let song = state.playlist.filter(s => s.song === state.playId)
            if (song.length === 0) {
                return -1
            }
            return state.playlist.indexOf(song[0])
        }
    }
})
