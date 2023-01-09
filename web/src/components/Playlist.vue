<template>
  <el-row align="middle" justify="end">
    <el-button v-show="player.hasSong" size="large" type="primary" circle :icon="Location()" @click="locate"/>
  </el-row>
  <el-table :data="songs" :show-header="false" stripe style="width: 100%; margin-top: 10px" @row-dblclick="playRow">
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
        <el-row :gutter="10" align="middle" justify="end">
          <el-button type="success" v-if="scope.row.url" :icon="VideoPlay" circle @click="play(scope.row)"/>
          <el-button type="warning" :icon="Edit" circle @click="suggest(scope.row)"/>
          <el-button type="danger" :icon="Delete()" circle @click="deleteAs(scope.row.id)"/>
        </el-row>
      </template>
    </el-table-column>
  </el-table>
  <el-row style="width: 100%;padding-top: 15px" align="middle" justify="center">
    <el-pagination background layout="prev, pager, next" :total="total" v-model:current-page="page"/>
  </el-row>

  <el-drawer v-model="showReplacer" :with-header="false" size="50%">
    <SearchPage :replace-id="mid" @replace="reload" ref="search"/>
  </el-drawer>

</template>

<script>
import {api} from "@/core/api";
import {Delete, Edit, Location, Search, VideoPlay} from "@element-plus/icons-vue";
import {playingStore} from "@/store/playing";
import SearchPage from "@/components/SearchPage.vue";
import {ElMessageBox} from "element-plus";

export default {
  name: "Playlist",
  components: {SearchPage},
  data() {
    return {
      associations: [],
      songs: [],
      page: 1,
      player: playingStore(),
      showReplacer: false,
      pid: null,
      mid: null,
      keyword: ""
    }
  },
  methods: {
    Search() {
      return Search
    },
    Location() {
      return Location
    },
    Delete() {
      return Delete
    },
    suggest(song) {
      this.showReplacer = true
      this.mid = song.id
      this.$nextTick(() => {
        this.$refs['search'].setKeyword(song.name)
        this.$refs['search'].doSearch()
      })
    },
    deleteAs(id) {
      let association = this.associations.filter(obj => obj.song === id)
      if (association.length > 0) {
        ElMessageBox.confirm('delete from playlist?', {
          confirmButtonText: 'Yes',
          cancelButtonText: 'No',
          type: 'info',
        }).then(() => {
          api.deleteAssociation(association[0].id).then(ret => {
            this.$message.success("delete success")
            this.reload()
          })
        })
      }
    },
    reload() {
      if (this.pid) {
        api.fetchAssociations(this.pid).then(ret => {
          this.associations = ret.data
          this.player.playlist = this.associations
          this.fetchPage()
        })
      }
    },
    playRow(row, co, ev) {
      this.play(row)
    },
    changePid(pid) {
      if (pid) {
        this.pid = pid
      }
      if (this.pid) {
        api.fetchAssociations(this.pid).then(ret => {
          this.associations = ret.data
          this.player.playlist = this.associations
          this.page = 1
          this.fetchPage()
        })
      }
    },
    fetchPage() {
      if (this.total === 0) {
        return
      }
      let end = this.page * 10
      let start = (this.page - 1) * 10
      if (end > this.total) {
        end = this.total
      }
      let ids = this.associations.slice(start, end).map(obj => obj.song)
      api.fetchSongs(ids).then(ret => {
        this.songs = ret.data
      })
    },
    play(song) {
      this.player.play(song)
    },
    locate() {
      let index = this.player.getIndex
      if (index >= 0) {
        this.page = parseInt(index / 10) + 1
      }
    }
  },
  watch: {
    page(nVal) {
      this.fetchPage(nVal)
    }
  },
  computed: {
    Edit() {
      return Edit
    },
    VideoPlay() {
      return VideoPlay
    },
    total() {
      return this.associations.length
    }
  }
}
</script>

<style scoped>

</style>