<template>
  <el-row align="middle" justify="space-around">
    <el-select v-model="source" style="width: 100px" @change="doSearch">
      <el-option v-for="item in sources" :key="item" :label="item" :value="item"/>
    </el-select>
    <el-input v-model="keyword" style="width: calc(100% - 150px)" @keydown.enter="doSearch">
      <template #append>
        <el-button :icon="Search()" @click="doSearch"/>
      </template>
    </el-input>
  </el-row>


  <el-table v-if="songs.length > 0" :data="songs" :show-header="false" stripe style="width: 100%;margin-top: 20px">
    <el-table-column width="100">
      <template #default="scope">
        <el-row align="middle" justify="start">
          <el-image :src="scope.row.cover" style="width: 50px;height: 50px"/>
        </el-row>
      </template>
    </el-table-column>
    <el-table-column>
      <template #default="scope">
        {{ scope.row.name }}
      </template>
    </el-table-column>
    <el-table-column>
      <template #default="scope">
        {{ scope.row.authors.join(',') }}
      </template>
    </el-table-column>
    <el-table-column>
      <template #default="scope">
        <el-row align="middle" justify="end">
          <el-button type="success" v-if="scope.row.url" :icon="VideoPlay()" circle @click="play(scope.row)"
                     style="margin: 5px"/>
          <el-button type="warning" v-if="replaceId" :icon="Switch()" circle @click="doReplace(scope.row)"
                     style="margin: 5px"/>
          <el-dropdown v-else style="margin: 5px" trigger="click">
            <el-button type="primary" :icon="Plus()" circle/>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item v-for="item in player.playlists" :key="item.id"
                                  @click="addToPlaylist(scope.row, item.id)">
                  {{ item.name }}
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </el-row>
      </template>
    </el-table-column>
  </el-table>
</template>

<script>
import {Plus, Search, Switch, VideoPlay} from "@element-plus/icons-vue";
import {api} from "@/core/api";
import {playingStore} from "@/store/playing";

export default {
  name: "SearchPage",
  emits: ['replace'],
  methods: {
    Plus() {
      return Plus
    },
    Switch() {
      return Switch
    },
    VideoPlay() {
      return VideoPlay
    },
    Search() {
      return Search
    },
    doSearch() {
      if (this.keyword) {
        api.searchKeyword(this.source, this.keyword).then(ret => {
          this.songs = ret.data
        })
      }
    },
    play(song) {
      this.player.play(song)
    },
    setKeyword(keyword) {
      this.keyword = keyword
    },
    doReplace(song) {
      if (this.replaceId) {
        let replace = Object.assign({}, song)
        replace['id'] = this.replaceId
        api.updateSong(replace).then(ret => {
          this.$emit('replace')
          this.$message.success("replace success")
        })
      }
    },
    addToPlaylist(song, playlist) {
      api.createSong(song).then(ret => {
        if (ret.data.id) {
          api.createAssociation({
            playlist: playlist,
            song: ret.data.id
          })
        }
      })
    }
  },
  data() {
    return {
      keyword: "",
      sources: ['tencent', 'netease', 'migu', 'kuwo', 'kugou'],
      source: 'tencent',
      songs: [],
      player: playingStore(),
    }
  },
  props: {
    replaceId: null
  }
}
</script>

<style scoped>

</style>