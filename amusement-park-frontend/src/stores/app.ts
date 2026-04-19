import { defineStore } from 'pinia'
import { computed, ref } from 'vue'

export const useAppStore = defineStore('app', () => {
  const visitor: any = ref()
  const getVisitor = computed(() => visitor.value)
  const setVisitor = (v: any) => visitor.value = v

  let messageIdGenerator = 0
  const messages = ref<any>([])
  const getMessages = computed(() => messages.value)
  const addMessage = (type: any, text: any) => {
    const m = {
      id: messageIdGenerator++,
      type,
      text,
    }
    messages.value.push(m)
    setTimeout(() => {
      removeMessage(m)
    }, 10_000)
  }
  const removeMessage = (m: any) => {
    messages.value.splice(messages.value.indexOf(m), 1)
  }

  const amusementParkId = ref(0)
  const getAmusementParkId = computed(() => amusementParkId.value)
  const setAmusementParkId = (v: number) => amusementParkId.value = v

  const createShow = ref(false)
  const getCreateShow = computed(() => createShow.value)
  const setCreateShow = (v: boolean) => createShow.value = v

  const guestBookWritingShow = ref(false)
  const getGuestBookWritingShow = computed(() => guestBookWritingShow.value)
  const setGuestBookWritingShow = (v: boolean) => guestBookWritingShow.value = v

  return {
    getVisitor, setVisitor, getMessages, addMessage, removeMessage, getAmusementParkId, setAmusementParkId,
    getCreateShow, setCreateShow, getGuestBookWritingShow, setGuestBookWritingShow,
  }
})
