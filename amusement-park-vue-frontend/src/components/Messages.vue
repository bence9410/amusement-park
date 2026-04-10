<template>
  <div align="center" class="ma-3" style="position: absolute; width: 100%; z-index: 1">
    <v-alert
      v-for="message in store.getMessages"
      :key="message.id"
      class="mt-3"
      style="width: 60%"
      :text="message.text"
      :type="message.type"
      @click="store.removeMessage(message)"
    />
  </div>
</template>
<script setup lang="ts">
  import { useAppStore } from '@/stores/app'
  const store = useAppStore()
</script>
