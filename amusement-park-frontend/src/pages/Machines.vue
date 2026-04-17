<template>
  <div>
    <div class="text-center py-5" style="background-color: #e9ecef">
      <h1>Machines</h1>
    </div>
    <v-container>
      <v-data-table-server
        v-model:items-per-page="machineTableItemsPerPage"
        v-model:page="machineTablePage"
        v-model:sort-by="machineTableSortBy"
        class="custom-table"
        :headers="machineTableHeaders"
        :items="machineTableItems"
        :items-length="machineTableTotalItems"
        :loading="machineTableIsLoading"
        loading-text="Loading... Please wait"
        :search="machineTableSearch"
        @update:options="machineTableLoadItems"
      >
        <template #tfoot>
          <tr>
            <td>
              <v-text-field
                v-model="machineSearch.fantasyName"
                class="ma-1"
                density="compact"
                placeholder="Like fantasy name"
              />
            </td>
            <td>
              <v-text-field
                v-model="machineSearch.minSize"
                class="ma-1"
                density="compact"
                placeholder="Min size"
              />
            </td>
            <td>
              <v-text-field
                v-model="machineSearch.minPrice"
                class="ma-1"
                density="compact"
                placeholder="Min price"
              />
            </td>
            <td>
              <v-text-field
                v-model="machineSearch.minNumberOfSeats"
                class="ma-1"
                density="compact"
                placeholder="Min number of seats"
              />
            </td>
            <td>
              <v-text-field
                v-model="machineSearch.minMinimumRequiredAge"
                class="ma-1"
                density="compact"
                placeholder="Min minimum required age"
              />
            </td>
            <td>
              <v-text-field
                v-model="machineSearch.minTicketPrice"
                class="ma-1"
                density="compact"
                placeholder="Min ticket price"
              />
            </td>
            <td>
              <v-select
                v-model="machineSearch.type"
                class="ma-1"
                clearable
                density="compact"
                :items="types"
                placeholder="Type"
              />
            </td>
          </tr>
          <tr>
            <td />
            <td>
              <v-text-field
                v-model="machineSearch.maxSize"
                class="ma-1"
                density="compact"
                placeholder="Max size"
              />
            </td>
            <td>
              <v-text-field
                v-model="machineSearch.maxPrice"
                class="ma-1"
                density="compact"
                placeholder="Max price"
              />
            </td>
            <td>
              <v-text-field
                v-model="machineSearch.maxNumberOfSeats"
                class="ma-1"
                density="compact"
                placeholder="Max number of seats"
              />
            </td>
            <td>
              <v-text-field
                v-model="machineSearch.maxMinimumRequiredAge"
                class="ma-1"
                density="compact"
                placeholder="Max minimum required age"
              />
            </td>
            <td>
              <v-text-field
                v-model="machineSearch.maxTicketPrice"
                class="ma-1"
                density="compact"
                placeholder="Max ticket price"
              />
            </td>
            <td />
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
            <h2 style="background-color: #e9ecef">Create machine</h2>
          </v-card-title>
          <v-form
            ref="machineCreateForm"
            v-model="machineCreateFormIsInvalid"
            @submit.prevent="createMachine"
          >
            <v-card-text>
              <v-text-field
                v-model="machineCreate.fantasyName"
                :counter="25"
                label="Fantasy name"
                :readonly="machineCreateFormIsLoading"
                required
                :rules="[
                  (v) =>
                    (!!v && v.length >= 5 && v.length <= 25) ||
                    'Fantasy name size must be between 5 and 25.',
                ]"
              />
              <v-text-field
                v-model="machineCreate.size"
                label="Size"
                :readonly="machineCreateFormIsLoading"
                required
                :rules="[
                  (v) =>
                    (!!v && Number(v) >= 20 && Number(v) <= 200) ||
                    'Size must be between 20 and 200.',
                ]"
              />
              <v-text-field
                v-model="machineCreate.price"
                label="Price"
                :readonly="machineCreateFormIsLoading"
                required
                :rules="[
                  (v) =>
                    (!!v && Number(v) >= 50 && Number(v) <= 2000) ||
                    'Price must be between 50 and 2000. ',
                ]"
              />
              <v-text-field
                v-model="machineCreate.numberOfSeats"
                label="Number of seats"
                :readonly="machineCreateFormIsLoading"
                required
                :rules="[
                  (v) =>
                    (!!v && Number(v) >= 5 && Number(v) <= 250) ||
                    'Number of seats must be between 5 and 250.',
                ]"
              />
              <v-text-field
                v-model="machineCreate.minimumRequiredAge"
                label="Minumum required age"
                :readonly="machineCreateFormIsLoading"
                required
                :rules="[
                  (v) =>
                    (!!v && Number(v) >= 0 && Number(v) <= 25) ||
                    'Minimum required age must be between 0 and 25.',
                ]"
              />
              <v-text-field
                v-model="machineCreate.ticketPrice"
                label="Ticket price"
                :readonly="machineCreateFormIsLoading"
                required
                :rules="[
                  (v) =>
                    (!!v && Number(v) >= 5 && Number(v) <= 30) ||
                    'Ticket price must be between 5 and 30.',
                ]"
              />
              <v-select
                v-model="machineCreate.type"
                :items="types"
                label="Type"
                :readonly="machineCreateFormIsLoading"
                required
                :rules="[
                  (v) =>
                    (!!v) ||
                    'Type is required.'
                ]"
              />
            </v-card-text>
            <v-card-actions>
              <v-spacer />
              <v-btn
                class="px-4"
                color="green"
                :disabled="!machineCreateFormIsInvalid"
                :loading="machineCreateFormIsLoading"
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

  const machineTableItemsPerPage = ref(5)
  const machineTablePage = ref(1)
  const machineTableSortBy: any = ref([])
  const machineTableIsLoading = ref(false)
  const machineTableTotalItems = ref(0)
  const machineTableSearch = ref('')
  const machineSearch = ref({
    fantasyName: '',
    minSize: '',
    maxSize: '',
    minPrice: '',
    maxPrice: '',
    minNumberOfSeats: '',
    maxNumberOfSeats: '',
    minMinimumRequiredAge: '',
    maxMinimumRequiredAge: '',
    minTicketPrice: '',
    maxTicketPrice: '',
    type: '',
  })
  const machineTableItems = ref([])
  const machineTableHeaders = [
    { title: 'Fantasy name', key: 'fantasyName' },
    { title: 'Size', key: 'size' },
    { title: 'Price', key: 'price' },
    { title: 'Number of seats', key: 'numberOfSeats' },
    { title: 'Minimum required age', key: 'minimumRequiredAge' },
    { title: 'Ticket price', key: 'ticketPrice' },
    { title: 'Type', key: 'type' },
  ]

  const types = [
    { title: 'Carousel', value: 'CAROUSEL' },
    { title: 'Roller coaster', value: 'ROLLER_COASTER' },
    { title: 'Gokart', value: 'GOKART' },
    { title: 'Dodgem', value: 'DODGEM' },
    { title: 'Ship', value: 'SHIP' },
  ]
  const machineCreateForm = ref()
  const machineCreateFormIsInvalid = ref(false)
  const machineCreateFormIsLoading = ref(false)
  const machineCreate = ref({
    fantasyName: '',
    size: '',
    price: '',
    numberOfSeats: '',
    minimumRequiredAge: '',
    ticketPrice: '',
    type: '',
  })

  let machineTimer = 0

  function machineTableLoadItems () {
    machineTableIsLoading.value = true
    clearTimeout(machineTimer)
    machineTimer = setTimeout(() => {
      let url = store.getLinks.machine
      const input: { [key: string]: number | string } = {}
      if (machineSearch.value.fantasyName != '') {
        input.fantasyName = machineSearch.value.fantasyName
      }
      const entries = Object.entries(machineSearch.value)
      for (let i = 1; i < entries.length - 1; i++) {
        const e = entries[i]
        if (e[1] != '' && !Number.isNaN(Number(e[1]))) {
          input[e[0]] = Number(e[1])
        }
      }
      if (machineSearch.value.type != '') {
        input.type = machineSearch.value.type
      }
      url += '?input=' + encodeURI(JSON.stringify(input))
      url += '&page=' + (machineTablePage.value - 1)
      url += '&size=' + machineTableItemsPerPage.value
      if (machineTableSortBy.value.length === 1) {
        url += '&sort=' + machineTableSortBy.value[0].key + ',' + machineTableSortBy.value[0].order
      }
      fetch(url).then(async response => {
        machineTableIsLoading.value = false
        if (response.ok) {
          const machineResponse = await response.json()
          machineTableTotalItems.value = machineResponse.page.totalElements
          machineTableItems.value = machineResponse._embedded ? machineResponse._embedded.machineSearchResponseDtoList : []
        }
      })
    }, machineTimer === 0 ? 0 : 2000)
  }

  function createMachine () {
    machineCreateFormIsLoading.value = true
    fetch(store.getLinks.machine, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(machineCreate.value),
    }).then(async response => {
      machineCreateFormIsLoading.value = false
      if (response.ok) {
        machineTableLoadItems()
        store.setCreateShow(false)
        store.addMessage('success', 'Successfully created new machine ' + machineCreate.value.fantasyName + '.')
      } else {
        store.addMessage('error', await response.text())
      }
    })
  }

  watch(computed(() => store.getCreateShow), () => {
    machineCreateForm.value.reset()
  })

  watch(machineSearch.value, () => {
    machineTableSearch.value = String(Date.now())
  })
</script>
