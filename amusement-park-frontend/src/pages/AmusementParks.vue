<template>
  <div>
    <div class="text-center py-5" style="background-color: #e9ecef">
      <h1>Amusement park</h1>
    </div>
    <v-container>
      <v-data-table-server
        v-model:items-per-page="itemsPerPage"
        v-model:page="page"
        v-model:sort-by="sortBy"
        class="custom-table"
        :headers="headers"
        :items="amusementParks"
        :items-length="totalItems"
        :loading="tableIsLoading"
        loading-text="Loading... Please wait"
        :search="search"
        @update:options="loadItems"
      >
        <template #tfoot>
          <tr>
            <td>
              <v-text-field v-model="amusementParkSearch.name" class="ma-1" density="compact" placeholder="Like name" />
            </td>
            <td>
              <v-text-field
                v-model="amusementParkSearch.minCapital"
                class="ma-1"
                density="compact"
                placeholder="Min capital"
              />
            </td>
            <td>
              <v-text-field
                v-model="amusementParkSearch.minTotalArea"
                class="ma-1"
                density="compact"
                placeholder="Min total area"
              />
            </td>
            <td>
              <v-text-field
                v-model="amusementParkSearch.minEntranceFee"
                class="ma-1"
                density="compact"
                placeholder="Min entrance fee"
              />
            </td>
            <td>
              <v-text-field
                v-model="amusementParkSearch.minMachines"
                class="ma-1"
                density="compact"
                placeholder="Min machines"
              />
            </td>
            <td>
              <v-text-field
                v-model="amusementParkSearch.minGuestBookRegistries"
                class="ma-1"
                density="compact"
                placeholder="Min guest book registries"
              />
            </td>
            <td>
              <v-text-field
                v-model="amusementParkSearch.minActiveVisitors"
                class="ma-1"
                density="compact"
                placeholder="Min active visitors"
              />
            </td>
            <td>
              <v-text-field
                v-model="amusementParkSearch.minKnownVisitors"
                class="ma-1"
                density="compact"
                placeholder="Min known visitors"
              />
            </td>
          </tr>
          <tr>
            <td />
            <td>
              <v-text-field
                v-model="amusementParkSearch.maxCapital"
                class="ma-1"
                density="compact"
                placeholder="Max capital"
              />
            </td>
            <td>
              <v-text-field
                v-model="amusementParkSearch.maxTotalArea"
                class="ma-1"
                density="compact"
                placeholder="Max total area"
              />
            </td>
            <td>
              <v-text-field
                v-model="amusementParkSearch.maxEntranceFee"
                class="ma-1"
                density="compact"
                placeholder="Max entrance fee"
              />
            </td>
            <td>
              <v-text-field
                v-model="amusementParkSearch.maxMachines"
                class="ma-1"
                density="compact"
                placeholder="Max machines"
              />
            </td>
            <td>
              <v-text-field
                v-model="amusementParkSearch.maxGuestBookRegistries"
                class="ma-1"
                density="compact"
                placeholder="Max guest book registries"
              />
            </td>
            <td>
              <v-text-field
                v-model="amusementParkSearch.maxActiveVisitors"
                class="ma-1"
                density="compact"
                placeholder="Max active visitors"
              />
            </td>
            <td>
              <v-text-field
                v-model="amusementParkSearch.maxKnownVisitors"
                class="ma-1"
                density="compact"
                placeholder="Max known visitors"
              />
            </td>
          </tr>
        </template>
      </v-data-table-server>
    </v-container>
    <v-dialog v-model="store.getCreateShow" eager persistent width="50%">
      <v-card>
        <v-container>
          <div class="text-right" style="width: 100%">
            <v-btn class="ma-2" icon="mdi-close" @click="store.setCreateShow(false)" />
          </div>
          <v-card-title>
            <h2 style="background-color: #e9ecef">Create amusement park</h2>
          </v-card-title>
          <v-form
            ref="amusementParkCreateForm"
            v-model="amusementParkCreateFormIsInvalid"
            @submit.prevent="createAmusementPark"
          >
            <v-card-text>
              <v-text-field
                v-model="amusementParkCreate.name"
                :counter="20"
                label="Name"
                :readonly="amusementParkCreateFormIsLoading"
                required
                :rules="[
                  (v) =>
                    (!!v && v.length >= 5 && v.length <= 20) ||
                    'Name size must be between 5 and 20.',
                ]"
              />
              <v-text-field
                v-model="amusementParkCreate.capital"
                label="Capital"
                :readonly="amusementParkCreateFormIsLoading"
                required
                :rules="[
                  (v) =>
                    (!!v && Number(v) >= 500 && Number(v) <= 50000) ||
                    'Capital must be between 500 and 50000.',
                ]"
              />
              <v-text-field
                v-model="amusementParkCreate.totalArea"
                label="Total area"
                :readonly="amusementParkCreateFormIsLoading"
                required
                :rules="[
                  (v) =>
                    (!!v && Number(v) >= 50 && Number(v) <= 5000) ||
                    'TotalArea must be between 50 and 5000. ',
                ]"
              />
              <v-text-field
                v-model="amusementParkCreate.entranceFee"
                label="Entrance fee"
                :readonly="amusementParkCreateFormIsLoading"
                required
                :rules="[
                  (v) =>
                    (!!v && Number(v) >= 5 && Number(v) <= 200) ||
                    'EntranceFee must be between 5 and 200.',
                ]"
              />
            </v-card-text>
            <v-card-actions>
              <v-spacer />
              <v-btn
                class="px-4"
                color="green"
                :disabled="!amusementParkCreateFormIsInvalid"
                :loading="amusementParkCreateFormIsLoading"
                text="Create"
                type="submit"
                variant="flat"
              />
            </v-card-actions>
          </v-form>
        </v-container>
      </v-card>
    </v-dialog>
  </div>
</template>
<script setup lang="ts">
  import { computed, ref, watch } from 'vue'
  import { useAppStore } from '@/stores/app'

  const store = useAppStore()
  const itemsPerPage = ref(5)
  const sortBy = ref([])
  const page = ref(1)
  const tableIsLoading = ref(false)
  const totalItems = ref(0)
  const search = ref('')
  const amusementParkSearch = ref({
    name: '',
    minCapital: '',
    maxCapital: '',
    minTotalArea: '',
    maxTotalArea: '',
    minEntranceFee: '',
    maxEntranceFee: '',
    minMachines: '',
    maxMachines: '',
    minGuestBookRegistries: '',
    maxGuestBookRegistries: '',
    minActiveVisitors: '',
    maxActiveVisitors: '',
    minKnownVisitors: '',
    maxKnownVisitors: '',
  })
  const amusementParks = ref([])
  const headers = [
    { title: 'Name', key: 'name' },
    { title: 'Capital', key: 'capital' },
    { title: 'Total area', key: 'totalArea' },
    { title: 'Entrance fee', key: 'entranceFee' },
    { title: 'Machines', key: 'numberOfMachines' },
    { title: 'Guest Book Registries', key: 'numberOfGuestBookRegistries' },
    { title: 'Active Visitors', key: 'numberOfActiveVisitors' },
    { title: 'Known Visitors', key: 'numberOfKnownVisitors' },
  ]
  const amusementParkCreateForm = ref()
  const amusementParkCreateFormIsInvalid = ref(false)
  const amusementParkCreateFormIsLoading = ref(false)
  const amusementParkCreate = ref({
    name: '',
    capital: '',
    totalArea: '',
    entranceFee: '',
  })
  let timer: any = null

  function loadItems (params: any) {
    tableIsLoading.value = true
    clearTimeout(timer)
    timer = setTimeout(() => {
      let url = store.getLinks.amusementPark
      const input: { [key: string]: number | string } = {}
      if (amusementParkSearch.value.name != '') {
        input.name = amusementParkSearch.value.name
      }
      const entries = Object.entries(amusementParkSearch.value)
      for (let i = 1; i < entries.length; i++) {
        const e = entries[i]
        if (e[1] != '' && !Number.isNaN(Number(e[1]))) {
          input[e[0]] = Number(e[1])
        }
      }
      url += '?input=' + encodeURI(JSON.stringify(input))
      url += '&page=' + (params.page - 1)
      url += '&size=' + params.itemsPerPage
      if (params.sortBy.length === 1) {
        url += '&sort=' + params.sortBy[0].key + ',' + params.sortBy[0].order
      }
      fetch(url).then(async response => {
        tableIsLoading.value = false
        if (response.ok) {
          const amusementParksResponse = await response.json()
          totalItems.value = amusementParksResponse.page.totalElements
          amusementParks.value = amusementParksResponse._embedded ? amusementParksResponse._embedded.amusementParkDetailResponseDtoList : []
        }
      })
    }, 2000)
  }
  async function createAmusementPark () {
    amusementParkCreateFormIsLoading.value = true
    fetch(store.getLinks.amusementPark, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(amusementParkCreate.value),
    }).then(async response => {
      amusementParkCreateFormIsLoading.value = false
      if (response.ok) {
        loadItems({ page: page.value, itemsPerPage: itemsPerPage.value, sortBy: sortBy.value })
        store.setCreateShow(false)
        store.addMessage('success', 'Successfully created new amusement park ' + amusementParkCreate.value.name + '.')
      } else {
        store.addMessage('error', await response.text())
      }
    })
  }

  watch(computed(() => store.getCreateShow), () => {
    amusementParkCreateForm.value.reset()
  })

  watch(amusementParkSearch.value, () => {
    search.value = String(Date.now())
  })
</script>
<style scoped>
.custom-table {
  background-color: lightgreen;
}

.custom-table :deep(tbody tr:hover) {
  background-color: #e3f2fd !important;
}
</style>
