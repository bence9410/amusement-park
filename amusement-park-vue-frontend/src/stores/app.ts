import { defineStore } from 'pinia'
import { computed, ref } from 'vue'

export const useAppStore = defineStore('app', () => {
  const links: any = ref()
  const getLinks = computed(() => links.value)
  const setLinks = (v: any) => links.value = v

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

  const searchShow = ref(false)
  const getSearchShow = computed(() => searchShow.value)
  const setSearchShow = (v: boolean) => searchShow.value = v

  const createShow = ref(false)
  const getCreateShow = computed(() => createShow.value)
  const setCreateShow = (v: boolean) => createShow.value = v

  return {
    getLinks, setLinks, getVisitor, setVisitor, getMessages, addMessage, removeMessage,
    getSearchShow, setSearchShow, getCreateShow, setCreateShow,
  }
})
