<template>
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

      <template #item.action="{ item }">
        <v-btn
          color="black"
          text="Get on"
          variant="flat"
          @click="getOnMachine(item)"
        />
      </template>

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
            <v-text-field
              v-model="machineSearch.minNumberOfVisitorsOnMachine"
              class="ma-1"
              density="compact"
              placeholder="Min number of visitors on"
            />
          </td>
        </tr>
        <tr>
          <td />
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
          <td>
            <v-text-field
              v-model="machineSearch.maxNumberOfVisitorsOnMachine"
              class="ma-1"
              density="compact"
              placeholder="Max number of visitors on"
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
                  (!!v && v.length >= 5 && v.length <= 50) ||
                  'Fantasy name size must be between 5 and 50.',
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
                  (!!v && Number(v) >= 1 && Number(v) <= 30) ||
                  'Ticket price must be between 1 and 30.',
              ]"
            />
            <v-text-field
              v-model="machineCreate.video"
              label="YouTube video id"
              :readonly="machineCreateFormIsLoading"
              required
              :rules="[
                (v) =>
                  (!!v) ||
                  'Video is required.'
              ]"
            />
            <v-text-field
              v-model="machineCreate.videoLengthInSeconds"
              label="Video length is seconds"
              :readonly="machineCreateFormIsLoading"
              required
              :rules="[
                (v) =>
                  (!!v && Number(v) >= 5 && Number(v) <= 300) ||
                  'Video length must be between 5 and 300.',
              ]"
            />
            <img src="../assets/videoHint.png">
            <iframe
              allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share"
              allowfullscreen
              frameborder="0"
              :src="'https://www.youtube.com/embed/' + machineCreate.video"
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
  <v-dialog
    v-model="onMachineDialog"
    fullscreen
    transition="dialog-bottom-transition"
  >
    <v-card>
      <v-toolbar color="green">
        <v-btn
          icon="mdi-close"
          @click="onMachineDialog = false"
        />
        <v-toolbar-title>{{ onMachineFantasyName }}</v-toolbar-title>
        <v-toolbar-items>
          <v-btn
            color="black"
            text="Get off"
            variant="flat"
            @click="onMachineDialog = false"
          />
        </v-toolbar-items>
      </v-toolbar>
      <iframe
        allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share"
        allowfullscreen
        frameborder="0"
        height="100%"
        :src="video"
        width="100%"
      />
    </v-card>
  </v-dialog>
  <v-dialog v-model="store.getGuestBookWritingShow" eager persistent width="50%">
    <v-card>
      <v-container>
        <div class="text-right" style="width: 100%">
          <v-btn class="ma-2" icon="mdi-close" @click="store.setGuestBookWritingShow(false)" />
        </div>
        <v-card-title>
          <h2 style="background-color: #e9ecef">Guest book registry</h2>
        </v-card-title>
        <v-form
          ref="guestBookRegistryCreateForm"
          v-model="guestBookRegistryCreateFormIsInvalid"
          @submit.prevent="createGuestBookRegistry"
        >
          <v-card-text>
            <v-textarea
              v-model="guestBookRegistryContent"
              :counter="100"
              label="Content"
              :readonly="guestBookRegistryCreateFormIsLoading"
              required
              :rules="[
                (v) =>
                  (!!v && v.length >= 2 && v.length <= 100) ||
                  'Length must be between 2 and 100.',
              ]"
            />
          </v-card-text>
          <v-card-actions>
            <v-spacer />
            <v-btn
              class="px-4"
              color="green"
              :disabled="!guestBookRegistryCreateFormIsInvalid"
              :loading="guestBookRegistryCreateFormIsLoading"
              text="Submit"
              type="submit"
              variant="flat"
            />
          </v-card-actions>
        </v-form>
        <guest-book-registry-table :force-update="forceUpdate" :link="'/api/amusement-parks/' + store.getAmusementParkId + '/visitors/guest-book-registries'" />
      </v-container>
    </v-card>
  </v-dialog>
</template>
<script setup lang="ts">
  import { computed, ref, watch } from 'vue'
  import GuestBookRegistryTable from '@/components/GuestBookRegistryTable.vue'
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
    minMinimumRequiredAge: '',
    maxMinimumRequiredAge: '',
    minTicketPrice: '',
    maxTicketPrice: '',
    minNumberOfVisitorsOnMachine: '',
    maxNumberOfVisitorsOnMachine: '',
  })
  const machineTableItems = ref([])
  const machineTableHeaders = [
    { title: 'Fantasy name', key: 'fantasyName' },
    { title: 'Minimum required age', key: 'minimumRequiredAge' },
    { title: 'Ticket price', key: 'ticketPrice' },
    { title: 'Number of visitors on machine', key: 'numberOfVisitorsOnMachine' },
    { title: 'Action', key: 'action', sortable: false },
  ]

  const machineCreateForm = ref()
  const machineCreateFormIsInvalid = ref(false)
  const machineCreateFormIsLoading = ref(false)
  const machineCreate = ref({
    fantasyName: '',
    minimumRequiredAge: '',
    ticketPrice: '',
    video: '',
    videoLengthInSeconds: '',
  })

  const guestBookRegistryCreateForm = ref()
  const guestBookRegistryCreateFormIsInvalid = ref(false)
  const guestBookRegistryCreateFormIsLoading = ref(false)
  const guestBookRegistryContent = ref('')

  const onMachineDialog = ref(false)
  const onMachineFantasyName = ref('')
  const video = ref('')

  const forceUpdate = ref(0)

  let getOffMachineId: number
  let getOffMachineTimer: number
  let machineTimer: number

  function machineTableLoadItems () {
    machineTableIsLoading.value = true
    let url = '/api/amusement-parks/' + store.getAmusementParkId + '/machines'
    const input: { [key: string]: number | string } = {}
    if (machineSearch.value.fantasyName != '') {
      input.fantasyName = machineSearch.value.fantasyName
    }
    const entries = Object.entries(machineSearch.value)
    for (let i = 1; i < entries.length; i++) {
      const e = entries[i]
      if (e[1] != '' && !Number.isNaN(Number(e[1]))) {
        input[e[0]] = Number(e[1])
      }
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
        machineTableTotalItems.value = machineResponse.totalElements
        machineTableItems.value = machineResponse.content || []
      }
    })
  }

  function createMachine () {
    machineCreateFormIsLoading.value = true
    fetch('/api/amusement-parks/' + store.getAmusementParkId + '/machines', {
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

  function getOnMachine (machine: any) {
    fetch('/api/amusement-parks/' + store.getAmusementParkId + '/machines/' + machine.id + '/visitors/get-on-machine', {
      method: 'PUT',
    }).then(async response => {
      if (response.ok) {
        store.addMessage('success', 'Successfully got on machine ' + machine.fantasyName + '.')
        onMachineFantasyName.value = machine.fantasyName
        video.value = 'https://www.youtube.com/embed/' + machine.video
        onMachineDialog.value = true
        store.getVisitor.spendingMoney = store.getVisitor.spendingMoney - machine.ticketPrice
        getOffMachineId = machine.id
        getOffMachineTimer = setTimeout(() => {
          onMachineDialog.value = false
        }, machine.videoLengthInSeconds * 1000)
      } else {
        store.addMessage('error', await response.text())
      }
    })
  }

  function createGuestBookRegistry () {
    guestBookRegistryCreateFormIsLoading.value = true
    fetch('/api/amusement-parks/' + store.getAmusementParkId + '/visitors/guest-book-registries', {
      method: 'POST',
      body: guestBookRegistryContent.value,
    }).then(async response => {
      guestBookRegistryCreateFormIsLoading.value = false
      if (response.ok) {
        forceUpdate.value++
        store.addMessage('success', 'Successfully writed to the guest book registry.')
      } else {
        store.addMessage('error', await response.text())
      }
    })
  }

  watch(computed(() => store.getCreateShow), () => {
    machineCreateForm.value.reset()
  })

  watch(machineSearch.value, () => {
    machineTableIsLoading.value = true
    clearTimeout(machineTimer)
    machineTimer = setTimeout(() => machineTableSearch.value = String(Date.now()), 1500)
  })

  watch(onMachineDialog, () => {
    if (!onMachineDialog.value) {
      clearTimeout(getOffMachineTimer)
      fetch('/api/amusement-parks/' + store.getAmusementParkId + '/machines/' + getOffMachineId + '/visitors/get-off-machine', {
        method: 'PUT',
      }).then(async response => {
        if (response.ok) {
          store.addMessage('success', 'Successfully got off machine ' + onMachineFantasyName.value + '.')
        } else {
          store.addMessage('error', await response.text())
        }
      })
    }
  })

  watch(computed(() => store.getGuestBookWritingShow), () => {
    guestBookRegistryCreateForm.value.reset()
  })
</script>
