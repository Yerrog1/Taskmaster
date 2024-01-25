<template lang="pug">
  b-container.view-body
    p(v-if="validationFailed") ¡Ha ocurrido un error!
    p(v-else) Por favor, espera...
</template>

<script>
export default {
  name: 'ConfirmPage',

  data () {
    return {
      validationFailed: true
    }
  },

  async fetch () {
    try {
      await this.$axios.post('/api/v1/confirm', {
        email: this.$route.query.email,
        token: this.$route.query.code
      })

      this.validationFailed = false
    } catch (error) {
      this.validationFailed = true
    }

    await this.$router.push('/')
  }
}
</script>

<style scoped lang="sass">
  .view-body
    background-color: $light-bg-color
    border-radius: 10px
    padding: 100px
    margin-top: 150px
</style>
