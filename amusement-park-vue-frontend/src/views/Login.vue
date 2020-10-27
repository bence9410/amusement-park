<template>
  <div>
    <div class="text-center py-5" style="background-color: #e9ecef">
      <h1>Welcome visitor</h1>
    </div>
    <v-form ref="loginForm">
      <v-container>
        <v-col col="12" md="4" offset-md="4">
          <v-text-field
            label="Email*"
            v-model="email"
            required
            :rules="[
              (v) =>
                (!!v && emailRegexp.test(v)) ||
                'Email must be well-formed, for example: somebody@example.com',
            ]"
            :counter="50"
          ></v-text-field>
          <!-- :rules="[
              (v) =>
                (!!v && passwordRegexp.test(v)) ||
                'Must contain at least one upper and lowercase characters and number and the length must be between 8-25.',
            ]"-->
          <v-text-field
            v-model="password"
            :append-icon="showPassword ? 'mdi-eye' : 'mdi-eye-off'"
            :type="showPassword ? 'text' : 'password'"
            name="input-10-1"
            label="Password*"
            hint="At least 8 characters"
            :counter="25"
            @click:append="showPassword = !showPassword"
          ></v-text-field>
          <div class="text-center mt-2">
            <v-btn
              color="blue darken-1"
              elevation="7"
              dark
              class="mr-2 px-5"
              @click="login"
            >
              Login
            </v-btn>
            <v-btn color="blue darken-1" elevation="7" dark to="sign-up">
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
  props: ["loginLink"],
  data: () => ({
    emailRegexp: /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/,
    passwordRegexp: /^(?=.*\d)(?=.*[a-z])(?=.*[A-Z])[0-9a-zA-Z]{8,25}$/,
    showPassword: false,
    email: "",
    password: "",
  }),
  methods: {
    login() {
      if (this.$refs.loginForm.validate()) {
        $.ajax({
          url: this.loginLink,
          method: "POST",
          data: "email=" + this.email + "&password=" + this.password,
          success: (responseBody) => {
            this.$emit("login", responseBody);
          },
          error: (response) => {
            alert(response.responseText);
            //TODO fancy error message
          },
        });
      }
    },
  },
};
</script>