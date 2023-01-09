import axios from "axios";

const baseUrl = "/api"

const rewriteUrl = url => {
    return baseUrl + url
}

export const api = {
    fetchPlaylists: () => {
        return axios.get(rewriteUrl("/playlist/all"))
    },
    fetchAssociations: id => {
        return axios.get(rewriteUrl("/association/playlist"), {
            params: {
                id: id
            }
        })
    },
    fetchSongs: ids => {
        return axios.post(rewriteUrl("/song/find"), ids)
    },
    searchKeyword: (source, keyword) => {
        return axios.post(rewriteUrl(`/search/${source}/keyword`), {
            keyword: keyword,
            page: 1,
            pageSize: 10
        })
    },
    updateSong: song => {
        return axios.put(rewriteUrl('/song/update'), song)
    },
    deleteAssociation: id => {
        return axios.delete(rewriteUrl("/association/delete"), {
            params: {
                id: id
            }
        })
    },
    createSong: song => {
        return axios.post(rewriteUrl('/song/create'), song)
    },
    createAssociation: association => {
        return axios.post(rewriteUrl('/association/create'), association)
    }
}
