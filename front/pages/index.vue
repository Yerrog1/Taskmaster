<template lang="pug">
  div
    div#taskmaster-feature
      b-jumbotron
        template(#header='') TaskMaster
        template(#lead='') Consigue más, con TaskMaster
      div.feature-body
        p Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.
    div#search-feature
      b-jumbotron
        template(#header='') No se, ¿Un buscador?
        template(#lead='') Dios mio, necesito saber que poner
      div.feature-body
        p Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.
</template>

<script>
import Vue from 'vue'

export default Vue.extend({
  name: 'IndexPage'
})
</script>

<style scoped lang="sass">
  .jumbotron
    background-size: cover
    border-radius: 0
    color: white
    margin-bottom: 0
    padding: 75px 50px
    text-align: center
    -webkit-box-shadow: inset 0 8px 15px 0 $separator-color
    -moz-box-shadow: inset 0 8px 15px 0 $separator-color
    box-shadow: inset 0 8px 15px 0 $separator-color

  .feature-body
    background-size: cover
    background-attachment: fixed
    padding: 200px 100px
    color: white
    -webkit-box-shadow: inset 0 8px 15px 0 $separator-color
    -moz-box-shadow: inset 0 8px 15px 0 $separator-color
    box-shadow: inset 0 8px 15px 0 $separator-color

  #taskmaster-feature
    .jumbotron
      margin-top: -70px
      padding-top: 140px
      background-image: url('~@/assets/img/banner-blue.jpg')
    .feature-body
      background-image: url('~@/assets/img/background-green.jpg')

  #search-feature
    .jumbotron
      background-image: url('~@/assets/img/banner-gray.jpg')
    .feature-body
      background-image: url('~@/assets/img/background-red.jpg')
</style>
