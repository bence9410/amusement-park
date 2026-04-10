<template>
  <div>
    <div class="text-center py-5" style="background-color: #e9ecef">
      <h1>Amusement park</h1>
    </div>
    <v-container v-if="store.getSearchShow">
      <v-row>
        <v-col cols="12" md="3">
          <v-text-field label="Name" />
        </v-col>
        <v-col cols="12" md="3">
          <v-text-field label="Capital min" />
        </v-col>
        <v-col cols="12" md="3">
          <v-text-field label="Capital max" />
        </v-col>
        <v-col cols="12" md="3">
          <v-text-field label="Total area min" />
        </v-col>
        <v-col cols="12" md="3">
          <v-text-field label="Total area max" />
        </v-col>
        <v-col cols="12" md="3">
          <v-text-field label="Entrance fee min" />
        </v-col>
        <v-col cols="12" md="3">
          <v-text-field label="Entrance fee max" />
        </v-col>
        <v-col cols="12" md="3">
          <v-btn block class="mt-1" color="green" text="Search" />
        </v-col>
      </v-row>
    </v-container>
    <v-container>
      <v-data-table class="custom-table" :headers="headers" :items="amusementParks" />
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

  function getAmusementParks () {
    fetch(store.getLinks.amusementPark).then(async response => {
      if (response.ok) {
        const amusementParksResponse = await response.json()
        amusementParks.value = amusementParksResponse._embedded.amusementParkDetailResponseDtoList
      }
    })
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
        getAmusementParks()
        store.setCreateShow(false)
        store.addMessage('success', 'Successfully created new amusement park ' + amusementParkCreate.value.name + '.')
      } else {
        store.addMessage('error', await response.text())
      }
    })
  }
  getAmusementParks()

  watch(computed(() => store.getCreateShow), () => {
    amusementParkCreateForm.value.reset()
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
