<template>
  <v-app-bar v-if="store.getVisitor" color="green">
    <v-avatar class="mr-4 ml-5">
      <v-img alt="Profile picture" :src="store.getVisitor.photo" />
    </v-avatar>
    <span>
      {{ store.getVisitor.email }}
    </span>
    <span class="ml-3">
      {{ store.getVisitor.spendingMoney }}
    </span>
    <v-icon icon="mdi-currency-eur" />
    <v-spacer />
    <v-btn
      v-if="store.getVisitor._links.addRegistry"
      class="ma-1"
      color="black"
      text="Guest book writing"
      variant="flat"
      @click="store.setGuestBookWritingShow(true)"
    />
    <v-btn
      v-if="store.getVisitor._links.leavePark"
      class="ma-1"
      color="black"
      text="Leave park"
      variant="flat"
      @click="leavePark"
    />
    <v-btn
      v-if="isAdmin"
      class="ma-1"
      color="black"
      text="Create"
      variant="flat"
      @click="store.setCreateShow(true)"
    />
    <v-btn
      class="ma-1"
      color="black"
      text="Upload money"
      variant="flat"
      @click="uploadMoneyForm.reset(), uploadMoneyDialogShow = true"
    />
    <v-btn
      class="ma-1 mr-3"
      color="black"
      :loading="logoutIsLoading"
      text="Logout"
      variant="flat"
      @click="logout"
    />
  </v-app-bar>

  <v-dialog v-model="uploadMoneyDialogShow" eager persistent width="50%">
    <v-card>
      <div class="text-right" style="width: 100%">
        <v-btn class="ma-2" icon="mdi-close" @click="uploadMoneyDialogShow = false" />
      </div>
      <v-card-title class="text-h5">Upload money</v-card-title>
      <v-form ref="uploadMoneyForm" v-model="uploadMoneyFormIsInvalid" @submit.prevent="uploadMoney">
        <v-card-text>
          <v-text-field
            v-model="uploadMoneyValue"
            label="Money"
            :readonly="uploadMoneyFormIsLoading"
            required
            :rules="[
              (v) =>
                (!!v && Number(v) > 0) || 'Value must be greater than 0.',
            ]"
          />
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn
            color="green"
            :disabled="!uploadMoneyFormIsInvalid"
            :loading="uploadMoneyFormIsLoading"
            text="Upload"
            type="submit"
            variant="flat"
          />
        </v-card-actions>
      </v-form>
    </v-card>
  </v-dialog>
</template>
<script setup lang="ts">
  import { computed, ref } from 'vue'
  import { useRouter } from 'vue-router'
  import { useAppStore } from '@/stores/app'

  const store = useAppStore()
  const router = useRouter()
  const uploadMoneyForm = ref()
  const uploadMoneyFormIsInvalid = ref(false)
  const uploadMoneyFormIsLoading = ref(false)
  const logoutIsLoading = ref(false)
  const uploadMoneyDialogShow = ref(false)
  const uploadMoneyValue = ref('')

  const isAdmin = computed(() => 'ROLE_ADMIN' == store.getVisitor.authority)

  function logout () {
    logoutIsLoading.value = true
    fetch(store.getLinks.logout, {
      method: 'POST',
    }).then(async response => {
      logoutIsLoading.value = false
      if (response.ok) {
        router.push('/')
        store.setVisitor(null)
        store.addMessage('success', 'Successfull logout.')
      } else {
        store.addMessage('error', await response.text())
      }
    })
  }
  async function uploadMoney () {
    uploadMoneyFormIsLoading.value = true
    fetch(store.getVisitor._links.uploadMoney.href, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: uploadMoneyValue.value,
    }).then(async response => {
      uploadMoneyFormIsLoading.value = false
      if (response.ok) {
        store.getVisitor.spendingMoney = store.getVisitor.spendingMoney + Number(uploadMoneyValue.value)
        store.addMessage('success', 'Successfully uploaded ' + uploadMoneyValue.value + ' money.')
        uploadMoneyDialogShow.value = false
      } else {
        store.addMessage('error', await response.text())
      }
    })
  }

  function leavePark () {
    fetch(store.getVisitor._links.leavePark.href, {
      method: 'PUT',
    }).then(async response => {
      if (response.ok) {
        const visitor = store.getVisitor
        const newVisitor = await response.json()
        visitor._links = newVisitor._links
        router.push('/amusement-parks')
        store.addMessage('success', 'Successfully left park.')
      } else {
        store.addMessage('error', await response.text())
      }
    })
  }
</script>
