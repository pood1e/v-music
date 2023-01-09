<template>
  <el-row style="height: 100%; width: 100%">
    <div style="height: 100%; width: 100%" id="aplayer"
         v-show="player.hasSong"/>
  </el-row>
</template>

<script>
import 'aplayer/dist/APlayer.min.css';
import APlayer from 'aplayer';
import {playingStore} from "@/store/playing";

export default {
  name: "Player",
  data() {
    return {
      player: playingStore()
    }
  },
  mounted() {
    this.player.initial(new APlayer({
      container: document.getElementById('aplayer'),
      audio: [],
      loop: 'none'
    }))

  }
}
</script>

<style scoped>

</style>