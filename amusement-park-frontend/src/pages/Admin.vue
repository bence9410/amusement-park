<template>
  <div class="text-center py-5" style="background-color: #e9ecef">
    <h1>Users</h1>
  </div>
  <v-container>
    <v-data-table-server
      v-model:items-per-page="userTableItemsPerPage"
      v-model:page="userTablePage"
      v-model:sort-by="userTableSortBy"
      class="custom-table"
      :headers="userTableHeaders"
      :items="userTableItems"
      :items-length="userTableTotalItems"
      :loading="userTableIsLoading"
      loading-text="Loading... Please wait"
      :search="userTableSearch"
      @update:options="userTableLoadItems"
    >

      <template #item.photo="{ item }">
        <v-avatar class="mr-4 ml-5">
          <v-img alt="Profile picture" :src="(item as any).photo" />
        </v-avatar>
      </template>

      <template #item.authority="{ item }">
        {{ convertAuthority(item) }}
      </template>

      <template #item.isActivatedCoupon="{ item }">
        <v-checkbox v-model="(item as any).isActivatedCoupon" readonly />
      </template>

      <template #item.action="{ item }">
        <v-btn
          color="black"
          text="Modify money"
          variant="flat"
          @click="modifyMoneyForm.reset(), modifyMoneyDialogShow = true, modifyMoneyUserName = (item as any).name"
        />
        <v-btn
          v-if="(item as any).authority === 'ROLE_VISITOR'"
          class="ma-1"
          color="black"
          text="Make creator"
          variant="flat"
          @click="makeCreator(item)"
        />
      </template>

      <template #tfoot>
        <tr>
          <td />
          <td>
            <v-text-field
              v-model="userSearch.name"
              class="ma-1"
              density="compact"
              placeholder="Like name"
            />
          </td>
          <td>
            <v-text-field
              v-model="userSearch.authority"
              class="ma-1"
              density="compact"
              placeholder="Like authority"
            />
          </td>
          <td>
            <v-text-field
              v-model="userSearch.minMoney"
              class="ma-1"
              density="compact"
              placeholder="Min money"
            />
          </td>
          <td>
            <v-text-field
              v-model="userSearch.minCoupon"
              class="ma-1"
              density="compact"
              placeholder="Min coupon"
            />
          </td>
          <td>
            <v-select
              v-model="userSearch.isActivatedCoupon"
              class="ma-1"
              density="compact"
              :items="[{title:'', value:''}, {title: 'True', value: true}, {title:'False', value: false}]"
              label="Is activated coupon"
            />
          </td>
        </tr>
        <tr>
          <td />
          <td />
          <td>
            <v-text-field
              v-model="userSearch.maxMoney"
              class="ma-1"
              density="compact"
              placeholder="Max money"
            />
          </td>
          <td>
            <v-text-field
              v-model="userSearch.maxCoupon"
              class="ma-1"
              density="compact"
              placeholder="Max coupon"
            />
          </td>
        </tr>
      </template>
    </v-data-table-server>
  </v-container>
  <v-dialog v-model="modifyMoneyDialogShow" eager persistent width="50%">
    <v-card>
      <div class="text-right" style="width: 100%">
        <v-btn class="ma-2" icon="mdi-close" @click="modifyMoneyDialogShow = false" />
      </div>
      <v-card-title class="text-h5">Modify money</v-card-title>
      <v-form ref="modifyMoneyForm" v-model="modifyMoneyFormIsInvalid" @submit.prevent="modifyMoney">
        <v-card-text>
          <v-text-field
            v-model="modifyMoneyValue"
            label="Coupon code"
            :readonly="modifyMoneyFormIsLoading"
            required
            :rules="[
              (v) =>
                (!!v) || 'Coupon code is required.',
            ]"
          />
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn
            color="green"
            :disabled="!modifyMoneyFormIsInvalid"
            :loading="modifyMoneyFormIsLoading"
            text="Modify"
            type="submit"
            variant="flat"
          />
        </v-card-actions>
      </v-form>
    </v-card>
  </v-dialog>
</template>
<script setup lang="ts">
  import { ref, watch } from 'vue'
  import { useAppStore } from '@/stores/app'

  const store = useAppStore()

  const userTableItemsPerPage = ref(5)
  const userTablePage = ref(1)
  const userTableSortBy: any = ref([])
  const userTableIsLoading = ref(false)
  const userTableTotalItems = ref(0)
  const userTableSearch = ref('')
  const userSearch = ref({
    name: '',
    authority: '',
    minMoney: '',
    maxMoney: '',
    minCoupon: '',
    maxCoupon: '',
    isActivatedCoupon: '',
  })
  const userTableItems = ref([])
  const userTableHeaders = [
    { title: 'Photo', key: 'photo', sortable: false },
    { title: 'Name', key: 'name' },
    { title: 'Authority', key: 'authority' },
    { title: 'Money', key: 'money' },
    { title: 'Coupon', key: 'coupon' },
    { title: 'Is activated coupon', key: 'isActivatedCoupon' },
    { title: 'Action', key: 'action', sortable: false },
  ]

  const modifyMoneyForm = ref()
  const modifyMoneyDialogShow = ref(false)
  const modifyMoneyFormIsInvalid = ref(false)
  const modifyMoneyFormIsLoading = ref(false)
  const modifyMoneyValue = ref('')
  const modifyMoneyUserName = ref('')

  let userTimer: number

  function userTableLoadItems () {
    userTableIsLoading.value = true
    let url = '/api/admin/users'
    const input: { [key: string]: number | string } = {}
    if (userSearch.value.name != '') {
      input.name = userSearch.value.name
    }
    if (userSearch.value.authority != '') {
      input.authority = userSearch.value.authority
    }
    const entries = Object.entries(userSearch.value)
    for (let i = 2; i < 4; i++) {
      const e = entries[i]
      if (e[1] != '' && !Number.isNaN(Number(e[1]))) {
        input[e[0]] = Number(e[1])
      }
    }
    if (userSearch.value.isActivatedCoupon !== '') {
      input.isActivatedCoupon = userSearch.value.isActivatedCoupon
    }
    url += '?input=' + encodeURI(JSON.stringify(input))
    url += '&page=' + (userTablePage.value - 1)
    url += '&size=' + userTableItemsPerPage.value
    if (userTableSortBy.value.length === 1) {
      url += '&sort=' + userTableSortBy.value[0].key + ',' + userTableSortBy.value[0].order
    }
    fetch(url).then(async response => {
      userTableIsLoading.value = false
      if (response.ok) {
        const userResponse = await response.json()
        userTableTotalItems.value = userResponse.totalElements
        userTableItems.value = userResponse.content || []
      }
    })
  }

  function convertAuthority (item: any) {
    switch (item.authority) {
      case 'ROLE_ADMIN': {
        return 'Admin'
      }
      case 'ROLE_CREATOR': {
        return 'Creator'
      }
      case 'ROLE_VISITOR': {
        return 'Visitor'
      }
      default: {
        return ''
      }
    }
  }

  function modifyMoney () {
    modifyMoneyFormIsLoading.value = true
    fetch('api/admin/modify-money', {
      method: 'PATCH',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ userName: modifyMoneyUserName.value, value: modifyMoneyValue.value }),
    }).then(async response => {
      modifyMoneyFormIsLoading.value = false
      if (response.ok) {
        modifyMoneyDialogShow.value = false
        store.addMessage('success', 'Successfully modified money of ' + modifyMoneyUserName.value + '.')
        userTableSearch.value = String(Date.now())
        if (store.getUser.name === modifyMoneyUserName.value) {
          store.getUser.money = store.getUser.money + Number(modifyMoneyValue.value)
        }
      } else {
        store.addMessage('error', await response.text())
      }
    })
  }

  function makeCreator (item: any) {
    fetch('/api/admin/make-creator', {
      method: 'PATCH',
      body: item.name,
    }).then(async response => {
      if (response.ok) {
        store.addMessage('success', 'Successfully granted creator authority to ' + item.name + '.')
        userTableSearch.value = String(Date.now())
      } else {
        store.addMessage('error', await response.text())
      }
    })
  }

  watch(userSearch.value, () => {
    userTableIsLoading.value = true
    clearTimeout(userTimer)
    userTimer = setTimeout(() => userTableSearch.value = String(Date.now()), 1500)
  })
</script>
