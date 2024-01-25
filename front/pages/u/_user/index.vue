<template lang="pug">
  b-container(fluid).view-body
    b-modal#new-project-modal(ref="newProjectModal", title="Nuevo proyecto", centered, body-bg-variant="dark", header-bg-variant="dark", footer-bg-variant="dark" no-stacking )
      b-form(@submit.prevent)
        b-form-group
          b-form-input(v-model="projectId", :state="idValid", @change="checkName", placeholder="Identificador" )
          b-form-invalid-feedback#id-invalid(v-if="!idLengthValid") El identificador debe tener entre 3 y 50 carácteres
          b-form-invalid-feedback#user-available-invalid(v-if="!idAvailable") Este identificador está ya en uso
        b-form-group
          b-form-input(v-model="projectName", :state="nameValid", placeholder="Nombre", aria-describedby="name-invalid" )
          b-form-invalid-feedback#name-invalid El nombre debe tener entre 3 y 50 carácteres
        b-form-group
          b-form-textarea(v-model="projectDesc", :state="descValid", placeholder="Añade un comentario...", aria-describedby="desc-invalid" )
          b-form-invalid-feedback#desc-invalid La descripción debe tener como máximo 250 carácteres
        b-form-group
          b-form-checkbox(v-model="projectPublic" ) Proyecto público
      template(#modal-footer)
        b-button.submit(@click="newProject") Crear
    b-button.floating-btn(v-b-modal.new-project-modal )
      b-icon(icon="plus-lg" )
    b-tabs(content-class="mt-3", align="center" )
      b-tab(title="Perfil" )
        div.profile-body
          b-row
            b-col.profile-col#data-col
              b-icon(@click="editPhoto", icon="chat-right-text-fill").h3.mb-2.bio-icon
              b-avatar(:src="member.profilePhotoUrl", size="15rem")
              b-form-file(v-if="photoEditable", accept="image/*", v-model="photo", :state="Boolean(photo)", placeholder="Arrastra aquí la imágen", drop-placeholder="Suelta aquí la imágen")
              p {{ member.name }}
              em {{ `/u/${member.memberId}` }}
            b-col(cols=8).profile-col#bio-col
              b-icon(@click="editBio", icon="chat-right-text-fill").h3.mb-2.bio-icon
              b-textarea(v-if="bioEditable", v-model="member.biography").edit
              p(v-else) {{ member.biography }}
      b-tab(title="Proyectos")
        div.project-wrapper
          template(v-for="project in member.projects")
            project-widget(:project="project", :user="$route.params.user" )

</template>

<script>
import ProjectWidget from '~/components/project/widget.vue'

export default {
  name: 'ProfilePage',
  components: { ProjectWidget },
  middleware: 'auth',

  data () {
    return {
      projectId: '',
      projectName: '',
      projectDesc: '',
      projectPublic: false,
      idAvailable: true,
      bioEditable: false,
      photoEditable: false,

      photo: null,

      member: {
        projects: []
      }
    }
  },
  async fetch () {
    try {
      this.member = (await this.$axios.get(`/api/v1/members/${this.$route.params.user}`)).data
    } catch (error) {
      return this.$nuxt.error({ statusCode: 404, message: 'El usuario no existe' })
    }
  },

  computed: {
    idLengthValid () {
      return this.projectId === '' ? null : this.projectId.length >= 3 && this.projectId.length <= 50
    },
    idValid () {
      return this.idLengthValid && this.idAvailable
    },
    nameValid () {
      return this.projectName === '' ? null : this.projectName.length >= 3 && this.projectName.length <= 50
    },
    descValid () {
      return this.projectDesc === '' ? null : this.projectDesc.length <= 250
    }
  },

  methods: {
    async editPhoto () {
      this.photoEditable = !this.photoEditable

      if (this.photoEditable) {
        return
      }

      const formData = new FormData()
      formData.append('photo', this.photo)
      await this.$axios.post('/api/v1/profile/photo', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      })

      this.$fetch()
    },
    async editBio () {
      this.bioEditable = !this.bioEditable

      if (this.bioEditable) {
        return
      }

      await this.$axios.patch('/api/v1/profile', {
        biography: this.member.biography
      })

      this.$fetch()
    },
    async checkName () {
      try {
        const response = await this.$axios.get('/api/v1/projectidAvailable', {
          params: { projectId: this.registerUser }
        })

        if (response.status !== 200) {
          return
        }

        this.idAvailable = response.data.available
      } catch (error) {
      }
    },
    async newProject () {
      try {
        await this.$axios.post('/api/v1/projects', {
          id: this.projectId,
          name: this.projectName,
          description: this.projectDesc,
          isPublic: this.projectPublic
        })

        this.$refs.newProjectModal.hide()
        this.$fetch()
      } catch (error) {
      }
    }
  }
}
</script>

<style scoped lang="sass">
  .container
    margin-top: 70px
    text-align: center

  .view-body
    margin-top: 20px

  .project-wrapper
    margin-top: 70px
    padding: 0 100px
    text-align: center
    display: grid
    grid-template-columns: repeat(3, 1fr)
    grid-gap: 50px
    grid-auto-rows: minmax(100px, auto)

  .profile-body
    padding: 50px 100px

  .profile-col
    border: 1px solid #5e5e5e
    border-radius: 10px
    margin: 0 50px
    padding: 30px
    background-color: $light-bg-color

  #data-col
    p, em
      margin-top: 20px
      text-align: center
      display: block

  #bio-col
    p
     margin-top: 30px

    .edit
      color: #5e5e5e
      background-color: unset

  .bio-icon
    position: absolute
    right: 50px
    cursor: pointer

  .b-avatar
    position: relative
    left: 50%
    transform: translate(-50%)
    margin: 20px 0
</style>

<style lang="sass">
  .nav-tabs
    border-bottom: 1px solid $light-separator-color

    .nav-link
      background-color: transparent !important
      border: none
      color: $light-text-color !important
      margin-left: 15px
      margin-right: 15px
      width: 200px
      text-align: center

      &:hover
        border-bottom: 3px solid #215d54
        font-weight: 500

      &.active
        border-bottom: 3px solid #143a39
        font-weight: 500
</style>
