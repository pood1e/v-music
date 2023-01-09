<template>
  <el-row style="height: 100%; width: 100%" v-show="player.hasSong" align="middle" justify="space-around">
    <div style="height: 100%; width: calc(100% - 100px)" id="aplayer"/>
    <el-button :icon="Right()" circle size="large" type="success" @click="player.next"/>
  </el-row>
</template>

<script>
import 'aplayer/dist/APlayer.min.css';
import APlayer from 'aplayer';
import {playingStore} from "@/store/playing";
import {Right} from "@element-plus/icons-vue";

export default {
  name: "Player",
  methods: {
    Right() {
      return Right
    }
  },
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