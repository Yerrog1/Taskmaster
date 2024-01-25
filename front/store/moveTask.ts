import { GetterTree, MutationTree } from 'vuex'

export const state = () => ({
  taskNumber: -1
})

export type MoveState = ReturnType<typeof state>

export const getters: GetterTree<MoveState, MoveState> = {
  task: state => state.taskNumber
}

export const mutations: MutationTree<MoveState> = {
  SET: (state, taskNumber: number) => {
    state.taskNumber = taskNumber
  }
}
