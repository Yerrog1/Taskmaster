<template lang="pug">
  div
    b-modal#validate(ref="validate", title="Registrarse", centered, body-bg-variant="dark", header-bg-variant="dark", footer-bg-variant="dark" no-stacking )
      div.validate-mail
        p Por favor, revisa tu e-mail para confirmar tu cuenta.
      template(#modal-footer)
        b-button(v-b-modal.register, v-b-modal.login ) Cerrar
    b-modal#register(ref="register", title="Registrarse", centered, body-bg-variant="dark", header-bg-variant="dark", footer-bg-variant="dark" no-stacking )
      b-form(@submit.prevent="register")
        b-form-group
          b-form-input(v-model="registerUser", @change="checkName", :state="userValid", placeholder="Nombre de usuario")
          b-form-invalid-feedback#user-length-invalid(v-if="!userLengthValid", :state="userLengthValid") El nombre de usuario debe tener entre 3 y 20 carácteres.
          b-form-invalid-feedback#user-available-invalid(v-if="!userAvailable", :state="userAvailable") Ese nombre de usuario está ya en uso
        b-form-group
          b-form-input(v-model="registerRealName", placeholder="Tu nombre real")
        b-form-group
          b-form-input(v-model="registerEmail", type="email", :state="emailValid", placeholder="Email", aria-describedby="email-invalid")
          b-form-invalid-feedback#email-invalid El email no es válido
        b-form-group
          b-form-input(v-model="registerPwd", type="password", :state="pwdValid", placeholder="Contraseña", aria-describedby="pwd-invalid")
          b-form-invalid-feedback#pwd-invalid(:state="pwdValid") La contraseña debe tener entre 8 y 80 carácteres.
        b-form-group
          b-form-input(v-model="repeatPwd", type="password", :state="pwdMatch", placeholder="Repite la contraseña", aria-describedby="repeat-pwd-invalid")
          b-form-invalid-feedback#repeat-pwd-invalid(:state="pwdMatch") Las contraseñas no coinciden.
        b-button.submit(type="submit", variant="secondary") Registrarse
      template(#modal-footer)
        span ¿Ya tienes cuenta?
        b-button(v-b-modal.login) Acceder
    b-modal#login(ref="login", title="Login", centered, body-bg-variant="dark", header-bg-variant="dark", footer-bg-variant="dark" no-stacking )
      b-form(@submit.prevent="login")
        b-form-group
          b-form-input(v-model="loginUser", :state="loginValid" placeholder="Nombre de usuario")
          b-form-input(v-model="loginPwd", type="password", :state="loginValid", placeholder="Contraseña", aria-describedby="login-invalid")
          b-form-invalid-feedback#login-invalid El nombre de usuario o contraseña no son válidos.
        b-button.submit(type="submit", variant="secondary") Acceder
      template(#modal-footer)
        span ¿Aún no tienes cuenta?
        b-button(v-b-modal.register) Registrarse
</template>

<script>
export default {
  name: 'NavigationLogin',

  data () {
    return {
      // Login
      loginUser: '',
      loginPwd: '',

      // Register
      registerUser: '',
      registerRealName: '',
      registerEmail: '',
      registerPwd: '',
      repeatPwd: '',

      userAvailable: false,
      loginValid: null
    }
  },

  computed: {
    userValid () {
      return this.userLengthValid && this.userAvailable
    },
    userLengthValid () {
      return this.registerUser === '' ? null : this.registerUser.length >= 3 && this.registerUser.length <= 20
    },
    pwdValid () {
      return this.registerPwd === '' ? null : this.registerPwd.length >= 8 && this.registerPwd.length <= 80
    },
    emailValid () {
      return this.registerEmail === '' ? null : /^[\w-.]+@([\w-]+\.)+[\w-]{2,4}$/.test(this.registerEmail)
    },
    pwdMatch () {
      return this.registerPwd === '' ? null : this.registerPwd === this.repeatPwd && this.registerPwd !== ''
    }
  },

  methods: {
    async checkName () {
      try {
        const response = await this.$axios.get('/api/v1/memberidAvailable', {
          params: { id: this.registerUser }
        })

        if (response.status !== 200) {
          return
        }

        this.userAvailable = response.data.available
      } catch (error) {
      }
    },
    async login () {
      try {
        await this.$auth.loginWith('local', {
          data: { memberId: this.loginUser, plainPassword: this.loginPwd }
        })

        this.loginUser = ''
        this.loginPwd = ''

        this.$refs.login.hide()
        await this.$router.push(`/u/${this.$auth.user.id}`)
      } catch (err) {
        this.loginValid = false
      }
    },

    async register () {
      if (!this.userValid || !this.pwdValid || !this.emailValid || !this.pwdMatch) {
        return
      }

      try {
        await this.$axios.post('/api/v1/signup', {
          id: this.registerUser,
          realName: this.registerRealName,
          plainPassword: this.registerPwd,
          email: this.registerEmail
        })

        this.registerUser = ''
        this.registerRealName = ''
        this.registerPwd = ''
        this.registerEmail = ''

        this.$refs.validate.show()
      } catch (err) {
      }
    }
  }
}
</script>

<style scoped lang="sass">
  .submit
    position: relative
    left: 50%
    transform: translateX(-50%)
    -ms-transform: translateX(-50%)
    margin-top: 5px
    margin-bottom: 10px
    width: 150px
  .validate-mail
    text-align: center
    margin: 50px
</style>
