<template lang="pug">
  b-container(fluid)#project-body
    b-button.floating-btn(v-b-modal.new-task-modal, @click="addTask")
      b-icon(icon="plus-lg")
    b-button.floating-btn#add-member(v-b-modal.new-user-modal)
      b-icon(icon="person-plus-fill")
    b-modal#new-user-modal(ref="newUserModal", title="Añadir miembro", centered, body-bg-variant="dark", header-bg-variant="dark", footer-bg-variant="dark" no-stacking )
      b-form(@submit.prevent, autocomplete="off")
        b-form-group
          b-form-input(v-model="memberName", list="app-members-list", placeholder="Id del usuario", :state="memberValid", @keyup="checkMember", aria-describedby="member-invalid")
          datalist#app-members-list
            option(v-for="member in availableMembers", :key="member.id") {{ member.id }}
      template(#modal-footer)
        b-button.submit(@click="submitMember") {{ btnName }}
    b-modal#new-task-modal(ref="newTaskModal", :title="titleName", centered, body-bg-variant="dark", header-bg-variant="dark", footer-bg-variant="dark" no-stacking )
      b-form(@submit.prevent)
        b-form-group
          b-form-input(v-model="taskName", placeholder="Nombre", aria-describedby="name-invalid")
          b-form-invalid-feedback#name-invalid El nombre no debe estar en blanco
        b-form-group
          b-form-textarea(v-model="taskDesc", placeholder="Descripción", aria-describedby="desc-invalid")
          b-form-invalid-feedback#desc-invalid La descripción no debe estar en blanco
        b-form-group
          b-form-input(v-model="taskAsigned", list="members-list", placeholder="Id del usuario", :state="asignedValid", aria-describedby="member-invalid")
          datalist#members-list
            option(v-for="member in members", :key="member.id") {{ member.id }}
        b-form-group
          b-form-select(v-model="taskStatus", :options="statusList")
      template(#modal-footer)
        b-button.submit(@click="submitTask") {{ btnName }}
    b-row.project-description
      b-col
        h2 {{ project.name }}
        p
          span Participantes:
          template(v-for="member in members")
            NuxtLink(:key="member.id", :to="`/u/${member.id}`") {{ member.id }}
    b-row
      project-column(title="Sin asignar", status="REPORTADA", :tasks="unassignedTasks")
      project-column(title="Asignadas", status="POR_HACER", :tasks="assignedTasks")
      project-column(title="En proceso", status="EN_PROCESO", :tasks="activeTasks")
      project-column(title="Finalizadas", status="TERMINADA", :tasks="finishedTasks")
</template>

<script>
import ProjectWidget from '~/components/project/widget.vue'
import TaskWidget from '~/components/task/widget.vue'
import ProjectColumn from '~/components/project/column.vue'

export default {
  name: 'TaskPage',
  components: { ProjectColumn, TaskWidget, ProjectWidget },
  middleware: 'auth',

  data () {
    return {
      taskNumber: '',
      taskName: '',
      taskDesc: '',
      taskStatus: '',
      taskAsigned: '',

      memberName: '',

      project: {
        name: ''
      },
      members: [],
      appMembers: [],
      tasks: [],
      statusList: [
        { value: 'REPORTADA', text: 'Sin asignar' },
        { value: 'POR_HACER', text: 'Asignadas' },
        { value: 'EN_PROCESO', text: 'En proceso' },
        { value: 'TERMINADA', text: 'Finalizadas' }
      ]
    }
  },

  async fetch () {
    try {
      this.project = (await this.$axios.get(`/api/v1/projects/${this.$route.params.user}/${this.$route.params.project}`)).data
      this.members = (await this.$axios.get(`/api/v1/projects/${this.$route.params.user}/${this.$route.params.project}/members`, {
        params: { query: '' }
      })).data
      this.tasks = (await this.$axios.get(`/api/v1/${this.$route.params.user}/${this.$route.params.project}/tasks`,
        {
          params: {
            limit: 100,
            page: 1
          }
        })).data.tasks
      this.appMembers = (await this.$axios.get('/api/v1/members/search', {
        params: { query: this.memberName }
      })).data
    } catch (error) {
      return this.$nuxt.error({ statusCode: 404, message: 'El proyecto no existe' })
    }
  },

  computed: {
    availableMembers () {
      return this.appMembers.filter(member => this.members.filter(member2 => member.id === member2.id).length === 0)
    },
    memberValid () {
      return this.availableMembers.filter(member => member.id === this.memberName).length > 0
    },
    nameValid () {
      return this.taskName !== ''
    },
    asignedValid () {
      return this.members.filter(member => member.id === this.taskAsigned).length > 0
    },
    descValid () {
      return this.taskDesc !== ''
    },
    unassignedTasks () {
      return this.tasks.filter(task => task.status === 'REPORTADA')
    },
    assignedTasks () {
      return this.tasks.filter(task => task.status === 'POR_HACER')
    },
    activeTasks () {
      return this.tasks.filter(task => task.status === 'EN_PROCESO')
    },
    finishedTasks () {
      return this.tasks.filter(task => task.status === 'TERMINADA')
    },
    titleName () {
      return this.taskNumber === -1 ? 'Crear tarea' : 'Actualizar tarea'
    },
    btnName () {
      return this.taskNumber === -1 ? 'Crear' : 'Actualizar'
    }
  },

  methods: {
    findTask (taskNumber) {
      return this.tasks.find(task => task.taskNumber === taskNumber)
    },

    addTask () {
      this.taskNumber = -1
      this.taskName = ''
      this.taskDesc = ''
      this.taskStatus = ''
      this.taskAsigned = ''
    },

    updateTask (task) {
      this.taskNumber = task.taskNumber
      this.taskName = task.name
      this.taskDesc = task.description
      this.taskStatus = task.status
      this.taskAsigned = task.assignedTo
    },

    async submitMember () {
      if (!this.memberValid) {
        return
      }

      await this.$axios.post(`/api/v1/${this.$route.params.user}/${this.$route.params.project}/members`, {
        memberToAddId: this.memberName
      })

      this.memberName = ''

      this.$refs.newUserModal.hide()
      this.$fetch()
    },

    async checkMember () {
      this.appMembers = (await this.$axios.get('/api/v1/members/search', {
        params: { query: this.memberName }
      })).data
    },

    async submitTask () {
      if (!this.nameValid || !this.descValid || !this.asignedValid) {
        return
      }

      try {
        if (this.taskNumber === -1) {
          await this.$axios.post(`/api/v1/${this.$route.params.user}/${this.$route.params.project}/tasks`, {
            name: this.taskName,
            description: this.taskDesc,
            status: this.taskStatus,
            assignedMemberId: this.taskAsigned
          })
        } else {
          await this.$axios.patch(`/api/v1/${this.$route.params.user}/${this.$route.params.project}/${this.taskNumber}`, {
            name: this.taskName,
            description: this.taskDesc,
            status: this.taskStatus,
            assigneeId: this.taskAsigned
          })
        }

        this.$refs.newTaskModal.hide()
        this.$fetch()
      } catch (error) {
      }
    }
  }
}
</script>

<style scoped lang="sass">
  .project-description
    text-align: center
    background-color: $light-bg-color
    border-radius: 10px
    padding: 30px
    margin: 20px 0

    h2
      margin-bottom: 50px

    span
      margin-right: 5px

    a
      margin-left: 5px
      color: #151515
      font-weight: bold

      &:not(:last-child)::after
        content: ", "

  #project-body
    padding: 0 100px
    margin-top: 50px

  #add-member
    right: 200px
</style>
