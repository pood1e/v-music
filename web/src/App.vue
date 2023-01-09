<script>

import Player from "@/components/Player.vue";
import Aside from "@/components/Aside.vue";
import Playlist from "@/components/Playlist.vue";
import {playingStore} from "@/store/playing";
import SearchPage from "@/components/SearchPage.vue";


export default {
  name: 'app',
  components: {
    SearchPage,
    Player, Aside, Playlist
  },
  data() {
    return {
      player: playingStore(),
      menu: 'playlist',
      pid: null
    }
  },
  methods: {
    changeMain(value) {
      this.menu = value
    },
    changePid(value) {
      this.pid = value
      this.$nextTick(function () {
        this.$refs['playlist'].changePid(value)
      })
    }
  }
}
</script>

<template>
  <el-container class="el-container">
    <el-container>
      <el-aside width="200px">
        <Aside v-on:menu="changeMain" v-on:pid="changePid"/>
      </el-aside>
      <el-container>
        <el-main>
          <Playlist v-if="menu === 'playlist'" ref="playlist"/>
          <SearchPage v-if="menu === 'search'"/>
        </el-main>
      </el-container>
    </el-container>
    <el-footer>
      <Player/>
    </el-footer>
  </el-container>
</template>

<style scoped>
.el-container {
  height: 100%;
  width: 100%;
}
</style>
