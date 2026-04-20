<template>
  <div class="text-center py-5" style="background-color: #e9ecef">
    <h1>Amusement park</h1>
  </div>
  <v-container>
    <v-data-table-server
      v-model:items-per-page="amusementParkTableItemsPerPage"
      v-model:page="amusementParkTablePage"
      v-model:sort-by="amusementParkTableSortBy"
      class="custom-table"
      :expanded="amusementParkTableExpandedRows"
      :headers="amusementParkTableHeaders"
      :items="amusementParkTableItems"
      :items-length="amusementParkTableTotalItems"
      :loading="amusementParkTableIsLoading"
      loading-text="Loading... Please wait"
      :search="amusementParkTableSearch"
      show-expand
      @update:expanded="amusementParkTableExpanded"
      @update:options="amusementParkTableLoadItems"
    >

      <template #item.data-table-expand="{ internalItem, isExpanded, toggleExpand }">
        <v-btn
          :append-icon="isExpanded(internalItem) ? 'mdi-chevron-up' : 'mdi-chevron-down'"
          border
          class="text-none"
          color="medium-emphasis"
          size="small"
          slim
          :text="isExpanded(internalItem) ? 'Collapse' : 'More info'"
          variant="text"
          width="105"
          @click="toggleExpand(internalItem)"
        />
      </template>

      <template #expanded-row="{ columns, item }">
        <tr>
          <td class="py-2" :colspan="columns.length - 2">
            <guest-book-registry-table :link="'/api/amusement-parks/' + (item as any).id + '/visitors/guest-book-registries'" />
          </td>
          <td :colspan="2">
            <v-btn
              block
              class="ma-1"
              color="black"
              text="Enter park"
              @click="enterPark(item)"
            />
          </td>
        </tr>
      </template>

      <template #tfoot>
        <tr>
          <td>
            <v-text-field
              v-model="amusementParkSearch.name"
              class="ma-1"
              density="compact"
              placeholder="Like name"
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
                  (!!v && v.length >= 5 && v.length <= 50) ||
                  'Name length must be between 5 and 50.',
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
</template>
<script setup lang="ts">
  import { computed, ref, watch } from 'vue'
  import { useRouter } from 'vue-router'
  import GuestBookRegistryTable from '@/components/GuestBookRegistryTable.vue'
  import { useAppStore } from '@/stores/app'

  const store = useAppStore()
  const router = useRouter()
  const amusementParkTableItemsPerPage = ref(5)
  const amusementParkTablePage = ref(1)
  const amusementParkTableSortBy: any = ref([])
  const amusementParkTableIsLoading = ref(false)
  const amusementParkTableTotalItems = ref(0)
  const amusementParkTableSearch = ref('')
  const amusementParkTableExpandedRows: any = ref([])
  const amusementParkSearch = ref({
    name: '',
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
  const amusementParkTableItems = ref([])
  const amusementParkTableHeaders = [
    { title: 'Name', key: 'name' },
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
    entranceFee: '',
  })
  let amusementParkTimer: number

  function amusementParkTableLoadItems () {
    amusementParkTableIsLoading.value = true
    let url = '/api/amusement-parks'
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
    url += '&page=' + (amusementParkTablePage.value - 1)
    url += '&size=' + amusementParkTableItemsPerPage.value
    if (amusementParkTableSortBy.value.length === 1) {
      url += '&sort=' + amusementParkTableSortBy.value[0].key + ',' + amusementParkTableSortBy.value[0].order
    }
    fetch(url).then(async response => {
      amusementParkTableIsLoading.value = false
      if (response.ok) {
        const amusementParkResponse = await response.json()
        amusementParkTableTotalItems.value = amusementParkResponse.totalElements
        amusementParkTableItems.value = amusementParkResponse.content || []
      }
    })
  }

  function amusementParkTableExpanded (ids: any) {
    amusementParkTableExpandedRows.value = ids.length > 1 ? [ids.at(-1)] : ids
  }

  async function createAmusementPark () {
    amusementParkCreateFormIsLoading.value = true
    fetch('/api/amusement-parks', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(amusementParkCreate.value),
    }).then(async response => {
      amusementParkCreateFormIsLoading.value = false
      if (response.ok) {
        amusementParkTableLoadItems()
        store.setCreateShow(false)
        store.addMessage('success', 'Successfully created new amusement park ' + amusementParkCreate.value.name + '.')
      } else {
        store.addMessage('error', await response.text())
      }
    })
  }

  function enterPark (amusementPark: any) {
    fetch('/api/amusement-parks/' + amusementPark.id + '/visitors/enter-park', {
      method: 'PUT',
    }).then(async response => {
      if (response.ok) {
        const visitor = await response.json()
        store.getVisitor.money = visitor.money
        store.getVisitor.coupon = visitor.coupon
        store.setAmusementParkId(amusementPark.id)
        store.setAmusementParkOwner(amusementPark.ownerEmail)
        router.push('/machines')
        store.addMessage('success', 'Successfully entered park ' + amusementPark.name + '.')
      } else {
        store.addMessage('error', await response.text())
      }
    })
  }

  watch(computed(() => store.getCreateShow), () => {
    amusementParkCreateForm.value.reset()
  })

  watch(amusementParkSearch.value, () => {
    amusementParkTableIsLoading.value = true
    clearTimeout(amusementParkTimer)
    amusementParkTimer = setTimeout(() => amusementParkTableSearch.value = String(Date.now()), 1500)
  })
</script>
<style>
.custom-table {
  background-color: lightgreen;
}

.custom-table :deep(tbody tr:hover) {
  background-color: #e3f2fd !important;
}

.inner-table {
  background-color: black;
}

.inner-table :deep(tbody tr:hover) {
  background-color: black !important;
}

.inner-table :deep(thead tr:hover) {
  background-color: black !important;
}

.inner-table :deep(table tr:hover) {
  background-color: black !important;
}
</style>
