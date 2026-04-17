<template>
  <div>
    <div class="text-center py-5" style="background-color: #e9ecef">
      <h1>Machines</h1>
    </div>
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
</script>
