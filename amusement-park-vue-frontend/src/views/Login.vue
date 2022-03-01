<template>
  <div>
    <sequential-entrance fromTop>
      <v-col sm="12 " md="4" offset-md="4" class="box">
        <v-col md="12">
          <v-card
            class="col mt-8"
            style="opacity: 0.9; border-radius: 20px"
            elevation="10"
            ><v-card>
              <v-img
                src="../assets/logo.png"
                alt="Logo amusement park."
                style="
                  opacity: 3;
                  border-top-left-radius: 20px;
                  border-top-right-radius: 20px;
                "
                elevation="10"
              />
            </v-card>
            <v-col class="text-center headline">Login</v-col>
            <v-form ref="loginForm">
              <v-container>
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
                <v-text-field
                  v-model="password"
                  :append-icon="showPassword ? 'mdi-eye' : 'mdi-eye-off'"
                  :type="showPassword ? 'text' : 'password'"
                  :rules="[
                    (v) =>
                      (!!v && passwordRegexp.test(v)) ||
                      'Must contain at least one upper and lowercase characters and number and the length must be between 8-25.',
                  ]"
                  name="input-10-1"
                  label="Password*"
                  hint="At least 8 characters"
                  :counter="25"
                  @click:append="showPassword = !showPassword"
                ></v-text-field>

                <v-btn
                  class="mt-4 mr-2 px-5 formButton"
                  elevation="10"
                  block
                  dark
                  rounded
                  @click="login"
                >
                  Login
                </v-btn>
                <v-col class="row" cols="12">
                  <v-col
                    md="7"
                    xs="6"
                    style="font-size: 13px"
                    class="pr-0 pl-1 mr-0"
                    >Don’t have an account?</v-col
                  >
                  <v-btn
                    text
                    to="sign-up"
                    xs="6"
                    class="pa-0 mt-1"
                    style="text-transform: none"
                  >
                    Sign up
                  </v-btn>
                </v-col>
              </v-container>
            </v-form>
          </v-card>
        </v-col>
      </v-col>
    </sequential-entrance>
  </div>
</template>
<style scoped>
.formButton {
  background: -moz-linear-gradient(right, #067998, #16363c, #16363c, #067998);
}
</style>
<script>
import $ from "jquery";
export default {
  props: ["loginLink"],
  data: () => ({
    emailRegexp:
      /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/,
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
            this.$bus.$emit("addMessage", {
              type: "success",
              text: "Successfull login.",
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
  },
};
</script>