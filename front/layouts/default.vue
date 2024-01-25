<template lang="pug">
  div
    navigation-navbar
    div.view
      Nuxt
</template>

<script>
export default {
  name: 'DefaultLayout'
}
</script>

<style lang="sass">
  body
    height: 100vh
    background-color: $background-color

  .view
    height: 100%

  .modal-content
    border-radius: 15px

    input
      margin-top: 15px

    span, label, p
      color: white

  .modal-header
    color: $light-text-color
    border-top-left-radius: 10px
    border-top-right-radius: 10px

  .modal-footer
    border-bottom-left-radius: 10px
    border-bottom-right-radius: 10px
    justify-content: space-around

  .floating-btn
    position: fixed
    bottom: 40px
    right: 100px
    width: 65px
    height: 65px
    border-radius: 50%
    z-index: 500

  ::-webkit-scrollbar
    width: 10px

  ::-webkit-scrollbar-track
    background: #bdbdbd

  ::-webkit-scrollbar-thumb
    background: #595959

  ::-webkit-scrollbar-thumb:hover
    background: #3b3b3b
</style>
