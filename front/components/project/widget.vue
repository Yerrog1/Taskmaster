<template lang="pug">
  b-card(:title="project.name", @click="openProject")
    b-card-text {{ project.description }}
    em(v-if="project.owner !== user") {{ project.owner }}
</template>

<script>
export default {
  name: 'ProjectWidget',

  props: {
    user: {
      type: String,
      default: ''
    },
    project: {
      type: Object,
      default: null
    }
  },

  methods: {
    openProject () {
      this.$router.push(`/u/${this.project.owner}/${this.project.projectID}`)
    }
  }
}
</script>

<style scoped lang="sass">
  .card-title
    margin-bottom: 20px
  .card
    padding: 10px
    background-color: $light-bg-color
    border-radius: 10px
    cursor: pointer
    transform: perspective(1px) translateZ(0)
    box-shadow: 0 0 1px rgba(0, 0, 0, 0)
    -webkit-transition-duration: 0.3s
    transition-duration: 0.3s
    -webkit-transition-property: transform
    transition-property: transform
    -webkit-transition-timing-function: ease-out
    transition-timing-function: ease-out

    em
      display: block
      text-align: right
      color: #545454

    &:hover
      transform: translateY(-8px)
</style>
