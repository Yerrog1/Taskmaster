<template lang="pug">
  b-navbar(toggleable='lg' type='dark' variant='dark' sticky=true)
    b-navbar-brand(to="/") TaskMaster
    b-navbar-toggle(target='nav-collapse')
    b-collapse#nav-collapse(is-nav='')
      b-navbar-nav(v-if="this.$auth.loggedIn")
        b-nav-item(:to="`/u/${this.$auth.user.id}`") Mi perfil
      // Right aligned nav items
      b-navbar-nav.ml-auto
        b-nav-form.search-form
          b-input-group
            b-form-input(placeholder='Buscar proyecto')
            b-input-group-append
              b-button.my-2.my-sm-0(size='sm' type='submit')
                b-icon(icon="search")
        b-nav-item-dropdown(right='', v-if="this.$auth.loggedIn")
          // Using 'button-content' slot
          template(#button-content='')
            em#user {{$auth.user.id}}
          b-dropdown-item(@click="logout") Salir
        div(v-else)
          navigation-login
          b-nav-item(v-b-modal.login) Acceder
</template>

<script>
import NavigationLogin from '~/components/navigation/login.vue'

export default {
  name: 'NavigationNavbar',
  components: { NavigationLogin },

  methods: {
    async logout () {
      await this.$auth.logout()
      location.reload()
    }
  }
}
</script>

<style scoped lang="sass">
  .navbar
    padding: 15px 100px
    backdrop-filter: blur(8px)
    background-color: $navbar-color !important

  .navbar-nav
    margin-left: 15px
    margin-right: 15px

  .search-form
    margin-right: 50px
</style>
