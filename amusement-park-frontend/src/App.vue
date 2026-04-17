<template>
  <v-app>
    <navbar />
    <v-main>
      <messages />
      <router-view />
    </v-main>
  </v-app>
</template>

<script setup lang="ts">
  import { useRouter } from 'vue-router'
  import { useAppStore } from '@/stores/app'
  import Messages from './components/Messages.vue'
  import Navbar from './components/Navbar.vue'
  const store = useAppStore()
  const router = useRouter()

  fetch('/api/links').then(async response => {
    if (response.ok) {
      const responseBody = await response.json()
      const links: any = {}
      for (const element of responseBody) {
        links[element.rel] = element.href
      }
      store.setLinks(links)
      fetch(store.getLinks.me).then(async response => {
        if (response.ok) {
          const visitor = await response.json()
          visitor._links.addRegistry = null
          visitor._links.leavePark = null
          store.setVisitor(visitor)
          router.push('/amusement-parks')
        } else {
          router.push('/')
        }
      })
    }
  })
</script>
