<template>
  <el-menu v-model="index" :default-openeds="['playlist']" @select="changeIndex">
    <el-menu-item index="search">search</el-menu-item>
    <el-sub-menu index="playlist">
      <template #title>
        playlists
      </template>
      <el-menu-item :index="fmtIndex(playlist.id)" v-for="playlist in playlists" :key="playlist.id">
        {{ playlist.name }}
      </el-menu-item>
    </el-sub-menu>
  </el-menu>
</template>

<script>
import {api} from "@/core/api";
import {playingStore} from "@/store/playing";

export default {
  name: "Aside",
  data: () => {
    return {
      playlists: [],
      index: null,
      player: playingStore()
    }
  },
  emits: ['menu', 'pid'],
  methods: {
    fetchPlaylists() {
      api.fetchPlaylists().then(ret => {
        this.playlists = ret.data
        this.player.playlists = this.playlists
        if (this.index === null && this.playlists.length > 0) {
          this.index = this.fmtIndex(this.playlists[0].id)
          this.changeIndex(this.index)
        }
      })
    },
    fmtIndex(pid) {
      return "playlist-" + pid
    },
    changeIndex(index) {
      this.index = index
      if (index.indexOf('playlist') >= 0) {
        this.$emit('menu', 'playlist')
        this.$emit('pid', index.split("-")[1])
      } else {
        this.$emit('menu', index)
      }
    }
  },
  mounted() {
    this.fetchPlaylists()
  }
}
</script>

<style scoped>

</style>