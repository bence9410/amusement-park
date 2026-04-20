<template>
  <div class="text-center py-5" style="background-color: #e9ecef">
    <h1>Welcome</h1>
  </div>
  <v-row>
    <v-col md="4" offset-md="4" sm="12">
      <v-card class="mt-8">
        <v-container>
          <v-form v-model="loginFormIsInvalid" @submit.prevent="login">
            <v-text-field
              v-model="email"
              :counter="50"
              label="Email*"
              :readonly="loginFormIsLoading"
              required
              :rules="[
                (v) =>
                  (!!v && emailRegexp.test(v)) ||
                  'Email must be well-formed, for example: somebody@example.com',
              ]"
            />
            <v-text-field
              v-model="password"
              :append-icon="showPassword ? 'mdi-eye' : 'mdi-eye-off'"
              :counter="25"
              hint="At least 8 characters and must contain upper and lowercase character and number."
              label="Password*"
              :readonly="loginFormIsLoading"
              required
              :rules="[
                (v) =>
                  (!!v && passwordRegexp.test(v)) ||
                  'Must contain at least one upper and lowercase characters and number and the length must be between 8-25.',
              ]"
              :type="showPassword ? 'text' : 'password'"
              @click:append="showPassword = !showPassword"
            />
            <v-row>
              <v-col cols="6">
                <v-btn block color="green" text="Sign up" to="sign-up" />
              </v-col>
              <v-col cols="6">
                <v-btn
                  block
                  color="green"
                  :disabled="!loginFormIsInvalid"
                  :loading="loginFormIsLoading"
                  text="Login"
                  type="submit"
                />
              </v-col>
            </v-row>
          </v-form>
        </v-container>
      </v-card>
    </v-col>
  </v-row>
</template>
<script setup lang="ts">
  import { ref } from 'vue'
  import { useRouter } from 'vue-router'
  import { useAppStore } from '@/stores/app'

  const store = useAppStore()
  const router = useRouter()
  const loginFormIsInvalid = ref(false)
  const loginFormIsLoading = ref(false)
  const emailRegexp = /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
  const passwordRegexp = /^(?=.*\d)(?=.*[a-z])(?=.*[A-Z])[0-9a-zA-Z]{8,25}$/

  const showPassword = ref(false)
  const email = ref('')
  const password = ref('')

  async function login () {
    loginFormIsLoading.value = true
    fetch('/api/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
      },
      body: 'email=' + email.value + '&password=' + password.value,
    }).then(async response => {
      loginFormIsLoading.value = false
      if (response.ok) {
        store.setUser(await response.json())
        store.addMessage('success', 'Successfull login.')
        router.push('/amusement-parks')
      } else {
        store.addMessage('error', await response.text())
      }
    })
  }
</script>
