<template>
  <div>
    <div class="text-center py-5" style="background-color: #e9ecef">
      <h1>Welcome visitor</h1>
      <h2>Sign Up</h2>
    </div>
    <v-form>
      <v-container>
        <v-col col="12" md="4" offset-md="4">
          <v-text-field
            label="Email*"
            required
            :rules="[
              (v) =>
                (!!v && emailRegexp.test(v)) ||
                'Email must be a well-formed email for example: email@example.com',
            ]"
            :counter="50"
          ></v-text-field>
          <v-text-field
            v-model="password"
            :append-icon="passwordShow ? 'mdi-eye' : 'mdi-eye-off'"
            :rules="[
              (v) =>
                (!!v && passwordRegexp.test(v)) ||
                'Must contain upper and lowercase characters and number and the length must be beetwen 8-25.',
            ]"
            :type="passwordShow ? 'text' : 'password'"
            label="Password*"
            hint="At least 8 characters and must contain upper and lowercase character and number."
            :counter="25"
            @click:append="passwordShow = !passwordShow"
            required
          ></v-text-field>
          <v-text-field
            v-model="confirmPassword"
            :append-icon="confirmPasswordShow ? 'mdi-eye' : 'mdi-eye-off'"
            :rules="[
              (v) =>
                (!!v && passwordRegexp.test(v) && password == v) ||
                'Must be equals with password and contain upper and lowercase characters and number and the length must be beetwen 8-25.',
            ]"
            :type="confirmPasswordShow ? 'text' : 'password'"
            label="Confirm Password*"
            hint="At least 8 characters and must contain upper and lowercase character and number."
            :counter="25"
            @click:append="confirmPasswordShow = !confirmPasswordShow"
            required
          ></v-text-field>
          <v-menu
            ref="menu"
            v-model="menu"
            :close-on-content-click="false"
            transition="scale-transition"
            offset-y
            min-width="290px"
          >
            <template v-slot:activator="{ on, attrs }">
              <v-text-field
                v-model="date"
                label="Birthday date*"
                readonly
                v-bind="attrs"
                v-on="on"
                :rules="[(v) => !!v || 'Cannot be empty.']"
              ></v-text-field>
            </template>
            <v-date-picker
              ref="picker"
              v-model="date"
              :max="new Date().toISOString().substr(0, 10)"
              min="1900-01-01"
              @change="save"
            ></v-date-picker>
          </v-menu>
          <v-file-input
            accept="image/png, image/jpeg,image/bmp"
            label="Photo (size: 2:3)*"
            prepend-icon="mdi-camera"
            v-model="image"
            @change="showImg"
            truncate-length="30"
            :rules="[
              (value) =>
                !value ||
                value.size < 2000000 ||
                'Avatar size should be less than 2 MB!',
            ]"
          ></v-file-input>
          <v-col align="center" v-if="imgSrc != null"
            ><v-img :src="imgSrc" width="200" height="300"></v-img
          ></v-col>
          <div class="text-center mt-2">
            <v-btn color="blue darken-1" elevation="7" dark class="mr-2 px-5">
              Back
            </v-btn>
            <v-btn color="blue darken-1" elevation="7" dark> Sign up </v-btn>
          </div>
        </v-col>
      </v-container>
    </v-form>
  </div>
</template>
<script>
export default {
  data: () => ({
    emailRegexp: /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/,
    passwordRegexp: /^(?=.*\d)(?=.*[a-z])(?=.*[A-Z])[0-9a-zA-Z]{8,25}$/,
    passwordShow: false,
    confirmPasswordShow: false,
    password: "",
    confirmPassword: "",
    date: null,
    menu: false,
    imgSrc: null,
    image: null,
  }),
  watch: {
    menu(val) {
      val && setTimeout(() => (this.$refs.picker.activePicker = "YEAR"));
    },
  },
  methods: {
    save(date) {
      this.$refs.menu.save(date);
    },
    showImg() {
      if (this.image == null) {
        this.imgSrc = null;
      } else if (this.image.size < 2000000) {
        var reader = new FileReader();
        reader.onload = (e) => (this.imgSrc = e.target.result);
        reader.readAsDataURL(this.image);
      }
    },
  },
};
</script>