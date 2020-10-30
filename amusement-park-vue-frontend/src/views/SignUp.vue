<template>
  <div>
    <div class="text-center py-5" style="background-color: #e9ecef">
      <h1>Welcome visitor</h1>
      <h2>Sign Up</h2>
    </div>
    <v-form ref="signUpForm">
      <v-container>
        <v-col col="12" md="4" offset-md="4">
          <v-text-field
            label="Email*"
            required
            v-model="signUpData.email"
            :rules="[
              (v) =>
                (!!v && emailRegexp.test(v)) ||
                'Email must be a well-formed email for example: email@example.com',
            ]"
            :counter="50"
          ></v-text-field>
          <v-text-field
            v-model="signUpData.password"
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
            v-model="signUpData.confirmPassword"
            :append-icon="confirmPasswordShow ? 'mdi-eye' : 'mdi-eye-off'"
            :rules="[
              (v) =>
                (!!v && passwordRegexp.test(v) && signUpData.password == v) ||
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
            ref="signUpBirthdateMenu"
            v-model="signUpBirthdateMenu"
            :close-on-content-click="false"
            transition="scale-transition"
            offset-y
            min-width="290px"
          >
            <template v-slot:activator="{ on, attrs }">
              <v-text-field
                v-model="signUpData.dateOfBirth"
                label="Birthdate*"
                readonly
                v-bind="attrs"
                v-on="on"
                :rules="[(v) => !!v || 'Cannot be empty.']"
              ></v-text-field>
            </template>
            <v-date-picker
              ref="signUpBirthdatePicker"
              v-model="signUpData.dateOfBirth"
              :max="new Date().toISOString().substr(0, 10)"
              min="1900-01-01"
              @change="selectBirthdate"
            ></v-date-picker>
          </v-menu>
          <v-file-input
            accept="image/png, image/jpeg,image/bmp"
            label="Profile photo (size: 1:1)*"
            prepend-icon="mdi-camera"
            v-model="image"
            @change="showImg"
            truncate-length="30"
            required
            :rules="[
              (v) =>
                !v ||
                v.size < 1000000 ||
                'Avatar size should be less than 1 MB!',
              (v) => !!v || 'File is required',
            ]"
          ></v-file-input>
          <v-col align="center" v-if="signUpData.photo != ''">
            <v-img
              :src="signUpData.photo"
              width="100"
              height="100"
              class="rounded-circle"
            ></v-img>
          </v-col>
          <div class="text-center mt-2">
            <v-btn
              color="blue darken-1"
              elevation="7"
              dark
              class="mr-2 px-5"
              to="/"
            >
              Back
            </v-btn>
            <v-btn color="blue darken-1" elevation="7" dark @click="signUp">
              Sign up
            </v-btn>
          </div>
        </v-col>
      </v-container>
    </v-form>
  </div>
</template>
<script>
import $ from "jquery";
export default {
  props: ["signUpLink"],
  data: () => ({
    emailRegexp: /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/,
    passwordRegexp: /^(?=.*\d)(?=.*[a-z])(?=.*[A-Z])[0-9a-zA-Z]{8,25}$/,
    passwordShow: false,
    confirmPasswordShow: false,
    signUpData: {
      email: "",
      password: "",
      confirmPassword: "",
      dateOfBirth: "",
      photo: "",
    },

    signUpBirthdateMenu: false,
    image: null,
  }),
  watch: {
    signUpBirthdateMenu(val) {
      val &&
        setTimeout(
          () => (this.$refs.signUpBirthdatePicker.activePicker = "YEAR")
        );
    },
  },
  methods: {
    signUp() {
      if (this.$refs.signUpForm.validate()) {
        $.ajax({
          url: this.signUpLink,
          method: "POST",
          contentType: "application/json",
          data: JSON.stringify(this.signUpData),
          success: (responseBody) => {
            this.$emit("login", responseBody);
            this.$bus.$emit("addMessage", {
              type: "success",
              text: "Successfull sign up.",
            });
          },
          error: (response) => {
            this.$bus.$emit("addMessage", {
              type: "error",
              text: response.responseText,
            });
          },
        });
      }
    },
    selectBirthdate(date) {
      this.$refs.signUpBirthdateMenu.save(date);
    },
    showImg() {
      if (this.image == null) {
        this.signUpData.photo = "";
      } else if (this.image.size < 1000000) {
        var reader = new FileReader();
        reader.onload = (e) => (this.signUpData.photo = e.target.result);
        reader.readAsDataURL(this.image);
      }
    },
  },
};
</script>