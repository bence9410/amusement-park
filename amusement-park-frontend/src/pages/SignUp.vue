<template>
  <div class="text-center py-5" style="background-color: #e9ecef">
    <h1>Welcome</h1>
  </div>
  <v-row>
    <v-col md="4" offset-md="4" sm="12">
      <v-card class="mt-8">
        <v-container>
          <v-form v-model="signUpFormIsInvalid" @submit.prevent="signUp">
            <v-text-field
              v-model="signUpData.name"
              :counter="50"
              label="Name*"
              :readonly="signUpFormIsLoading"
              required
              :rules="[
                (v) =>
                  (!!v && v.length >= 3 && v.length <= 50) ||
                  'Name length must be between 3 and 50.',
              ]"
            />
            <v-text-field
              v-model="signUpData.password"
              :append-icon="passwordShow ? 'mdi-eye' : 'mdi-eye-off'"
              :counter="25"
              hint="Length must be beetwen 8-25 and must contain upper and lowercase character and number."
              label="Password*"
              :readonly="signUpFormIsLoading"
              required
              :rules="[
                (v) =>
                  (!!v && passwordRegexp.test(v)) ||
                  'Must contain upper and lowercase characters and number and the length must be beetwen 8-25.',
              ]"
              :type="passwordShow ? 'text' : 'password'"
              @click:append="passwordShow = !passwordShow"
            />
            <v-text-field
              v-model="signUpData.confirmPassword"
              :append-icon="confirmPasswordShow ? 'mdi-eye' : 'mdi-eye-off'"
              :counter="25"
              hint="Must be equals with password, length must be beetwen 8-25 and must contain upper and lowercase character and number."
              label="Confirm Password*"
              :readonly="signUpFormIsLoading"
              required
              :rules="[
                (v) =>
                  (!!v &&
                    passwordRegexp.test(v) &&
                    signUpData.password == v) ||
                  'Must be equals with password and contain upper and lowercase characters and number and the length must be beetwen 8-25.',
              ]"
              :type="confirmPasswordShow ? 'text' : 'password'"
              @click:append="confirmPasswordShow = !confirmPasswordShow"
            />
            <v-date-input
              v-model="signUpData.dateOfBirth"
              input-format="yyyy-mm-dd"
              label="Birthdate*"
              :max="new Date().toISOString().substr(0, 10)"
              min="1900-01-01"
              :readonly="signUpFormIsLoading"
              required
              :rules="[(v) => !!v || 'Cannot be empty.']"
            />
            <v-file-input
              v-model="image"
              accept="image/png,image/jpeg,image/bmp"
              label="Profile photo (size: 1:1)*"
              prepend-icon="mdi-camera"
              :readonly="signUpFormIsLoading"
              required
              :rules="[
                (v) =>
                  !v ||
                  v.size < 1_048_576 ||
                  'Avatar size should be less than 1 MB!',
                (v) => !!v || 'Photo is required',
              ]"
              @change="showImg"
            />
            <v-col v-if="signUpData.photo != ''" align="center">
              <v-img class="rounded-circle" height="100" :src="signUpData.photo" width="100" />
            </v-col>
            <v-row>
              <v-col cols="6">
                <v-btn block color="green" text="Back" to="/" />
              </v-col>
              <v-col cols="6">
                <v-btn
                  block
                  color="green"
                  :disabled="!signUpFormIsInvalid"
                  :loading="signUpFormIsLoading"
                  text="Sign up"
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
  const signUpFormIsInvalid = ref(false)
  const signUpFormIsLoading = ref(false)
  const passwordRegexp = /^(?=.*\d)(?=.*[a-z])(?=.*[A-Z])[0-9a-zA-Z]{8,25}$/
  const passwordShow = ref(false)
  const confirmPasswordShow = ref(false)
  const signUpData = ref({
    name: '',
    password: '',
    confirmPassword: '',
    dateOfBirth: '',
    photo: '',
  })
  const image = ref()

  async function signUp () {
    signUpFormIsLoading.value = true
    fetch('/api/signUp', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(signUpData.value),
    }).then(async response => {
      signUpFormIsLoading.value = false
      if (response.ok) {
        store.setUser(await response.json())
        store.addMessage('success', 'Successfull sign up.')
        router.push('/amusement-parks')
      } else {
        store.addMessage('error', await response.text())
      }
    })
  }

  function showImg () {
    if (image.value == null) {
      signUpData.value.photo = ''
    } else if (image.value.size < 1_048_576) {
      const reader = new FileReader()
      reader.addEventListener('load', e => (signUpData.value.photo = (e.target?.result as string)))
      reader.readAsDataURL(image.value)
    }
  }
</script>
