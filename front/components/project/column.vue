<template lang="pug">
  b-col.task-col
    h5.task-col-header {{ this.title }}
    div.task-col-body(@drop="onDrop($event)", @dragover.prevent, @dragenter.prevent)
      template(v-for="task in this.tasks")
        div(draggable="true", :key="task.taskNumber", @dragstart="startDrag($event, task)", @click="updateTask(task)", v-b-modal.new-task-modal)
          task-widget(:task="task")
</template>

<script>
import TaskWidget from '~/components/task/widget.vue'

export default {
  name: 'ProjectColumn',
  components: { TaskWidget },

  props: {
    title: {
      type: String,
      default: ''
    },

    status: {
      type: String,
      default: ''
    },

    tasks: {
      type: Array,
      default: null
    }
  },

  methods: {
    updateTask (task) {
      this.$parent.updateTask(task)
    },

    startDrag (event, task) {
      event.dataTransfer.dropEffect = 'move'
      event.dataTransfer.effectAllowed = 'move'
      event.dataTransfer.setData('taskNumber', task.taskNumber)

      this.$store.commit('moveTask/SET', task.taskNumber)
    },

    async onDrop () {
      const task = this.$parent.findTask(this.$store.state.moveTask.taskNumber)
      if (task === undefined) {
        return
      }

      this.$store.commit('moveTask/SET', -1)
      task.status = this.status

      try {
        await this.$axios.patch(`/api/v1/${this.$route.params.user}/${this.$route.params.project}/${task.taskNumber}`, {
          status: task.status
        })
      } catch (error) {
        window.console.log(error)
      }
    }
  }
}
</script>

<style scoped lang="sass">
  .task-col-header
    background-color: $light-bg-color
    border-bottom: .5px solid #858585
    line-height: 50px
    margin: 0
    border-radius: 10px 10px 0 0

  .task-col-body
    background-color: $light-bg-color
    border-radius: 0 0 10px 10px
    height: 65vh
    padding: 20px
    overflow-y: auto

  .task-col
    text-align: center
    margin: 15px
    padding: 0

  ::-webkit-scrollbar-track
    border-bottom-right-radius: 10px

  ::-webkit-scrollbar-thumb
    border-bottom-right-radius: 10px
</style>
