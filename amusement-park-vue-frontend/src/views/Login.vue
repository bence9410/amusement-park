<template>
  <div>
    <div class="text-center py-5" style="background-color: #e9ecef">
      <h1>Welcome Visitor</h1>
    </div>
    <v-col sm="12" md="4" offset-md="4">
      <v-card class="mt-8">
        <v-container>
          <v-form ref="loginForm">
            <v-text-field label="Email*" v-model="email" required :rules="[
              (v) =>
                (!!v && emailRegexp.test(v)) ||
                'Email must be well-formed, for example: somebody@example.com',
            ]" :counter="50"></v-text-field>
            <v-text-field v-model="password" :append-icon="showPassword ? 'mdi-eye' : 'mdi-eye-off'"
              :type="showPassword ? 'text' : 'password'" :rules="[
                (v) =>
                  (!!v && passwordRegexp.test(v)) ||
                  'Must contain at least one upper and lowercase characters and number and the length must be between 8-25.',
              ]" label="Password*"
              hint="At least 8 characters and must contain upper and lowercase character and number." :counter="25"
              @click:append="showPassword = !showPassword"></v-text-field>
            <v-row>
              <v-col cols="6">
                <v-btn block color="green" @click="login">
                  Login
                </v-btn>
              </v-col>
              <v-col cols="6">
                <v-btn block color="green" to="sign-up">
                  Sign up
                </v-btn>
              </v-col>
            </v-row>
          </v-form>
        </v-container>
      </v-card>
    </v-col>
  </div>
</template>
<script>
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
        fetch(this.loginLink, {
          method: "POST",
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
          },
          body: "email=" + this.email + "&password=" + this.password
        }).then(async response => {
          if (response.ok) {
            this.$emit("login", await response.json());
            this.$bus.$emit("addMessage", {
              type: "success",
              text: "Successfull login.",
            });
          } else {
            this.$bus.$emit("addMessage", {
              type: "error",
              text: await response.text(),
            });
          }
        });
      }
    },
  },
};
</script>