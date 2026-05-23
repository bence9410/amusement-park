<template>
  <v-app-bar v-if="store.getUser" color="green">
    <v-avatar class="mr-4 ml-5">
      <v-img alt="Profile picture" :src="store.getUser.photo" />
    </v-avatar>
    <span>
      {{ store.getUser.name }}
    </span>
    <span class="ml-3">
      {{ store.getUser.money }}
    </span>
    <v-icon icon="mdi-currency-eur" />
    <span class="ml-2">
      {{ store.getUser.coupon }}
    </span>
    <v-icon icon="mdi-ticket-percent" />
    <v-spacer />
    <v-btn
      v-if="route.path === '/machines'"
      class="ma-1"
      color="black"
      text="Guest book writing"
      variant="flat"
      @click="store.setGuestBookWritingShow(true)"
    />
    <v-btn
      v-if="route.path === '/machines'"
      class="ma-1"
      color="black"
      text="Leave park"
      variant="flat"
      @click="leavePark"
    />
    <v-btn
      v-if="showCreate"
      class="ma-1"
      color="black"
      text="Create"
      variant="flat"
      @click="store.setCreateShow(true)"
    />
    <v-btn
      v-if="!store.getUser.isActivatedCoupon"
      class="ma-1"
      color="black"
      text="Activate coupon"
      variant="flat"
      @click="activateCouponForm.reset(), activateCouponDialogShow = true"
    />
    <v-btn
      class="ma-1"
      color="black"
      text="Info"
      variant="flat"
      @click="infoDialogShow = true"
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

  <v-dialog v-model="infoDialogShow" eager persistent width="50%">
    <v-card>
      <div class="text-right" style="width: 100%">
        <v-btn class="ma-2" icon="mdi-close" @click="infoDialogShow = false" />
      </div>
      <v-card-title class="text-h5">Upload money</v-card-title>
      <v-card-text>
        Send money on Revolut in euros to @bence1022 or send an email to nembence1994@gmail.com to discuss bank transfer.
      </v-card-text>
      <v-card-title class="text-h5">Withdraw money</v-card-title>
      <v-card-text>
        Send mesage on Revolut to @bence1022 or send an email to nembence1994@gmail.com to discuss bank transfer.
      </v-card-text>
      <v-card-title class="text-h5">Become an amusement park creator</v-card-title>
      <v-card-text>
        Send 100 euro on Revolut to @bence1022 or send an email to nembence1994@gmail.com to discuss bank transfer.
      </v-card-text>
      <v-card-actions>
        <v-spacer />
        <v-btn
          color="green"
          text="Close"
          variant="flat"
          @click="infoDialogShow = false"
        />
      </v-card-actions>
    </v-card>
  </v-dialog>
  <v-dialog v-model="activateCouponDialogShow" eager persistent width="50%">
    <v-card>
      <div class="text-right" style="width: 100%">
        <v-btn class="ma-2" icon="mdi-close" @click="activateCouponDialogShow = false" />
      </div>
      <v-card-title class="text-h5">Activate coupon</v-card-title>
      <v-form ref="activateCouponForm" v-model="activateCouponFormIsInvalid" @submit.prevent="activateCoupon">
        <v-card-text>
          <v-text-field
            v-model="activateCouponValue"
            label="Coupon code"
            :readonly="activateCouponFormIsLoading"
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
            :disabled="!activateCouponFormIsInvalid"
            :loading="activateCouponFormIsLoading"
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
  import { useRoute, useRouter } from 'vue-router'
  import { useAppStore } from '@/stores/app'

  const store = useAppStore()
  const route = useRoute()
  const router = useRouter()
  const logoutIsLoading = ref(false)
  const infoDialogShow = ref(false)

  const activateCouponForm = ref()
  const activateCouponFormIsInvalid = ref(false)
  const activateCouponFormIsLoading = ref(false)
  const activateCouponDialogShow = ref(false)
  const activateCouponValue = ref('')

  const showCreate = computed(() => {
    if (route.path === '/amusement-parks') {
      return 'ROLE_ADMIN' === store.getUser.authority
    } else if (route.path === '/machines') {
      return 'ROLE_ADMIN' === store.getUser.authority && store.getAmusementParkOwner === store.getUser.name
    }
    return false
  })

  function logout () {
    logoutIsLoading.value = true
    fetch('/api/logout', {
      method: 'POST',
    }).then(async response => {
      logoutIsLoading.value = false
      if (response.ok) {
        router.push('/')
        store.setUser(null)
        store.addMessage('success', 'Successfull logout.')
      } else {
        store.addMessage('error', await response.text())
      }
    })
  }

  function activateCoupon () {
    activateCouponFormIsLoading.value = true
    fetch('/api/activate-coupon', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: activateCouponValue.value,
    }).then(async response => {
      activateCouponFormIsLoading.value = false
      activateCouponDialogShow.value = false
      if (response.ok) {
        const user = await response.json()
        store.getUser.coupon = user.coupon
        store.getUser.isActivatedCoupon = user.isActivatedCoupon
        store.addMessage('success', 'Successfully activated coupone ' + activateCouponValue.value + '.')
      } else {
        store.addMessage('error', await response.text())
      }
    })
  }

  function leavePark () {
    fetch('/api/amusement-parks/' + store.getAmusementParkId + '/leave-park', {
      method: 'PUT',
    }).then(async response => {
      if (response.ok) {
        router.push('/amusement-parks')
        store.addMessage('success', 'Successfully left park.')
      } else {
        store.addMessage('error', await response.text())
      }
    })
  }
</script>
