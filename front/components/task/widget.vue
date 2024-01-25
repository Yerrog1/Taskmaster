<template lang="pug">
  div.widget(:class="movingClass")
    p {{ task.name }}
    p {{ task.description }}
    em(v-if="task.assignedTo !== ''") {{ task.assignedTo }}
</template>

<script>
export default {
  name: 'TaskWidget',

  props: {
    task: {
      type: Object,
      default: () => {
        return {
          name: '',
          description: ''
        }
      }
    }
  },

  computed: {
    movingClass () {
      return this.$store.state.moveTask.taskNumber === this.task.taskNumber ? 'moving' : ''
    }
  }
}
</script>

<style scoped lang="sass">
  .widget
    display: flex
    flex-direction: column
    justify-content: space-around
    background-color: #8d8d8d
    border: 1px solid $light-separator-color
    border-radius: 5px
    margin: 15px 0
    padding: 15px
    cursor: pointer

    p
      margin: 2px 20px

    em
      color: #545454
      text-align: right

  .moving
    opacity: 0.5
    border-style: dashed
</style>
