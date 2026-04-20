<template>
  <div class="text-center py-5" style="background-color: #e9ecef">
    <h1>Guest book registries</h1>
  </div>
  <v-data-table-server
    v-model:items-per-page="guestBookRegistryTableItemsPerPage"
    v-model:page="guestBookRegistryTablePage"
    v-model:sort-by="guestBookRegistryTableSortBy"
    class="inner-table"
    :headers="guestBookRegistryTableHeaders"
    :items="guestBookRegistryTableItems"
    :items-length="guestBookRegistryTableTotalItems"
    :loading="guestBookRegistryTableIsLoading"
    loading-text="Loading... Please wait"
    :search="guestBookRegistryTableSearch"
    theme="dark"
    @update:options="guestBookRegistryTableLoadItems"
  >

    <template #item.dateOfRegistry="{ value }">
      {{ new Date(value).toLocaleString() }}
    </template>

    <template #tfoot>
      <tr>
        <td>
          <v-text-field
            v-model="guestBookRegistrySearch.minDateOfRegistry"
            placeholder="Min date"
            type="datetime-local"
          />
        </td>
        <td>
          <v-text-field
            v-model="guestBookRegistrySearch.textOfRegistry"
            class="ma-1"
            density="compact"
            placeholder="Like content"
          />
        </td>
        <td>
          <v-text-field
            v-model="guestBookRegistrySearch.userName"
            class="ma-1"
            density="compact"
            placeholder="Like name"
          />
        </td>
      </tr>
      <tr>
        <td>
          <v-text-field
            v-model="guestBookRegistrySearch.maxDateOfRegistry"
            placeholder="Max date"
            type="datetime-local"
          />
        </td>
      </tr>
    </template>
  </v-data-table-server>
</template>
<script setup lang="ts">
  import { computed, ref, watch } from 'vue'

  const props = defineProps({
    link: {
      type: String,
      required: true,
    },
    forceUpdate: Number,
  })

  const guestBookRegistryTableItemsPerPage = ref(5)
  const guestBookRegistryTablePage = ref(1)
  const guestBookRegistryTableSortBy: any = ref([])
  const guestBookRegistryTableHeaders = [
    { title: 'Date', key: 'dateOfRegistry' },
    { title: 'Content', key: 'textOfRegistry' },
    { title: 'User name', key: 'userName' },
  ]
  const guestBookRegistryTableItems = ref([])
  const guestBookRegistryTableTotalItems = ref(0)
  const guestBookRegistryTableIsLoading = ref(false)
  const guestBookRegistryTableSearch = ref('')
  const guestBookRegistrySearch = ref({
    minDateOfRegistry: '',
    maxDateOfRegistry: '',
    textOfRegistry: '',
    userName: '',
  })
  let guestBookRegistryTimer: number

  function guestBookRegistryTableLoadItems () {
    guestBookRegistryTableIsLoading.value = true
    let url = props.link
    const input: { [key: string]: string } = {}
    const entries = Object.entries(guestBookRegistrySearch.value)
    for (const e of entries) {
      if (e[1] != '') {
        input[e[0]] = e[1]
      }
    }
    url += '?input=' + encodeURI(JSON.stringify(input))
    url += '&page=' + (guestBookRegistryTablePage.value - 1)
    url += '&size=' + guestBookRegistryTableItemsPerPage.value
    if (guestBookRegistryTableSortBy.value.length === 1) {
      url += '&sort=' + guestBookRegistryTableSortBy.value[0].key + ',' + guestBookRegistryTableSortBy.value[0].order
    }
    fetch(url).then(async response => {
      guestBookRegistryTableIsLoading.value = false
      if (response.ok) {
        const guestBookRegistrysResponse = await response.json()
        guestBookRegistryTableTotalItems.value = guestBookRegistrysResponse.totalElements
        guestBookRegistryTableItems.value = guestBookRegistrysResponse.content || []
      }
    })
  }

  watch(guestBookRegistrySearch.value, () => {
    guestBookRegistryTableIsLoading.value = true
    clearTimeout(guestBookRegistryTimer)
    guestBookRegistryTimer = setTimeout(() => guestBookRegistryTableSearch.value = String(Date.now()), 1500)
  })

  watch(computed(() => props.forceUpdate), () => guestBookRegistryTableLoadItems())
</script>
