<template>
  <div>
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
                @update:options="guestBookRegistryTableLoadItems(item)"
              >
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
                        v-model="guestBookRegistrySearch.visitorEmail"
                        class="ma-1"
                        density="compact"
                        placeholder="Like email"
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
  import { useRouter } from 'vue-router'
  import { useAppStore } from '@/stores/app'

  const store = useAppStore()
  const router = useRouter()
  const amusementParkTableItemsPerPage = ref(5)
  const amusementParkTablePage = ref(1)
  const amusementParkTableSortBy = ref([])
  const amusementParkTableIsLoading = ref(false)
  const amusementParkTableTotalItems = ref(0)
  const amusementParkTableSearch = ref('')
  const amusementParkTableExpandedRows: any = ref([])
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
  const amusementParkTableItems = ref([])
  const amusementParkTableHeaders = [
    { title: 'Name', key: 'name' },
    { title: 'Capital', key: 'capital' },
    { title: 'Total area', key: 'totalArea' },
    { title: 'Entrance fee', key: 'entranceFee' },
    { title: 'Machines', key: 'numberOfMachines' },
    { title: 'Guest Book Registries', key: 'numberOfGuestBookRegistries' },
    { title: 'Active Visitors', key: 'numberOfActiveVisitors' },
    { title: 'Known Visitors', key: 'numberOfKnownVisitors' },
  ]
  const guestBookRegistryTableItemsPerPage = ref(5)
  const guestBookRegistryTablePage = ref(1)
  const guestBookRegistryTableSortBy: any = ref([])
  const guestBookRegistryTableHeaders = [
    { title: 'Date', key: 'dateOfRegistry' },
    { title: 'Content', key: 'textOfRegistry' },
    { title: 'Visitor email', key: 'visitorEmail' },
  ]
  const guestBookRegistryTableItems = ref([])
  const guestBookRegistryTableTotalItems = ref(0)
  const guestBookRegistryTableIsLoading = ref(false)
  const guestBookRegistryTableSearch = ref('')
  const guestBookRegistrySearch = ref({
    minDateOfRegistry: '',
    maxDateOfRegistry: '',
    textOfRegistry: '',
    visitorEmail: '',
  })
  const amusementParkCreateForm = ref()
  const amusementParkCreateFormIsInvalid = ref(false)
  const amusementParkCreateFormIsLoading = ref(false)
  const amusementParkCreate = ref({
    name: '',
    capital: '',
    totalArea: '',
    entranceFee: '',
  })
  let amusementParkTimer = 0
  let guestBookRegistryTimer = 0

  function amusementParkTableLoadItems (params: any) {
    amusementParkTableIsLoading.value = true
    clearTimeout(amusementParkTimer)
    amusementParkTimer = setTimeout(() => {
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
        amusementParkTableIsLoading.value = false
        if (response.ok) {
          const amusementParkResponse = await response.json()
          amusementParkTableTotalItems.value = amusementParkResponse.page.totalElements
          amusementParkTableItems.value = amusementParkResponse._embedded ? amusementParkResponse._embedded.amusementParkDetailResponseDtoList : []
        }
      })
    }, amusementParkTimer === 0 ? 0 : 2000)
  }

  function amusementParkTableExpanded (ids: any) {
    amusementParkTableExpandedRows.value = ids.length > 1 ? [ids.at(-1)] : ids
    guestBookRegistryTimer = 0
    guestBookRegistryTableItems.value = []
  }

  function guestBookRegistryTableLoadItems (p: any) {
    guestBookRegistryTableIsLoading.value = true
    clearTimeout(guestBookRegistryTimer)
    guestBookRegistryTimer = setTimeout(() => {
      let url = p._links.addRegistry.href
      const input: { [key: string]: string } = {}
      const entries = Object.entries(guestBookRegistrySearch.value)
      console.log(guestBookRegistrySearch.value)
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
          guestBookRegistryTableTotalItems.value = guestBookRegistrysResponse.page.totalElements
          guestBookRegistryTableItems.value = guestBookRegistrysResponse._embedded ? guestBookRegistrysResponse._embedded.guestBookRegistrySearchResponseDtoList : []
        }
      })
    }, guestBookRegistryTimer === 0 ? 0 : 2000)
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
        amusementParkTableLoadItems({ page: amusementParkTablePage.value, itemsPerPage: amusementParkTableItemsPerPage.value, sortBy: amusementParkTableSortBy.value })
        store.setCreateShow(false)
        store.addMessage('success', 'Successfully created new amusement park ' + amusementParkCreate.value.name + '.')
      } else {
        store.addMessage('error', await response.text())
      }
    })
  }

  function enterPark (amusementPark: any) {
    fetch(amusementPark._links.visitorEnterPark.href, {
      method: 'PUT',
    }).then(async response => {
      if (response.ok) {
        const visitor = store.getVisitor
        const newVisitor = await response.json()
        visitor.spendingMoney = newVisitor.spendingMoney
        visitor._links = newVisitor._links
        store.getLinks.machine = amusementPark._links.machine.href
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
    amusementParkTableSearch.value = String(Date.now())
  })

  watch(guestBookRegistrySearch.value, () => {
    guestBookRegistryTableSearch.value = String(Date.now())
  })
</script>
<style scoped>
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
