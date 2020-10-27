<template>
  <v-col col="12">
    <div class="text-center py-5" style="background-color: #e9ecef">
      <h1>Welcome visitor</h1>
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
                'Email must be a well-formed valaki@pelda.com ',
            ]"
            :counter="50"
          ></v-text-field>
          <v-text-field
            v-model="password"
            :append-icon="show1 ? 'mdi-eye' : 'mdi-eye-off'"
            :rules="[rules.required, rules.min]"
            :type="show1 ? 'text' : 'password'"
            name="input-10-1"
            label="Password*"
            hint="At least 8 characters"
            :counter="25"
            @click:append="show1 = !show1"
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
  </v-col>
</template>
<script>
export default {
  data: () => ({
    emailRegexp: /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/,

    show1: false,
    password: "",
    rules: {
      required: (value) => !!value || "Required.",
      min: (v) =>
        (v.length >= 8 && v.length <= 25) ||
        "Password size must be between 8 and 25 ",
      emailMatch: () => "The email and password you entered don't match",
    },
  }),
  methods: {
    login() {
      this.$router.push("/amusement-park");
      this.$emit("login");
    },
  },
};
</script>