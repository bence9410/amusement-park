<template>
  <div v-if="show">
    <div>
      <v-toolbar prominent dense color="green" class="pt-5">
        <v-img
          max-height="50"
          max-width="150"
          class="rounded mb-3 bigScreen"
          :aspect-ratio="2.9"
          src="../assets/logoAmusementPark.png"
          alt="Logo amusement park."
        />

        <v-avatar class="ml-1">
          <v-img
            class="pt-0"
            src="https://cdn.vuetifyjs.com/images/john.jpg"
            alt="Profile picture"
          />
        </v-avatar>
        <div class="ml-1 mr-2">
          <p id="email" class="mb-1">fenicser@gmail.com</p>
          <p id="spendingMoney">250</p>
        </div>

        <v-btn color="green darken-3" elevation="7" dark class="mt-2 bigScreen"
          ><v-icon class="mr-1">mdi-magnify</v-icon>
          Search
        </v-btn>
        <v-spacer></v-spacer>
        <v-btn
          color="green darken-3"
          elevation="7"
          dark
          @click="dialog.show = true"
          class="mr-2 mt-2 bigScreen"
        >
          <v-icon>attach_money</v-icon>
          Upload money
        </v-btn>

        <v-btn
          color="green darken-3"
          elevation="7"
          dark
          to="login"
          class="mt-2 bigScreen"
        >
          <v-icon class="mr-1">power_settings_new</v-icon>
          Logout
        </v-btn>

        <v-menu bottom left>
          <template v-slot:activator="{ on, attrs }">
            <v-btn dark icon v-bind="attrs" v-on="on" class="smallScreen">
              <v-icon>mdi-dots-vertical</v-icon>
            </v-btn>
          </template>

          <v-list>
            <v-list-item v-for="(item, i) in items" :key="i">
              <v-icon class="mr-1">{{ item.icon }}</v-icon>
              <v-list-item-title>{{ item.title }}</v-list-item-title>
            </v-list-item>
          </v-list>
        </v-menu>
      </v-toolbar>
    </div>
  </div>
</template>
<script>
export default {
  props: ["show"],
  data: () => ({
    toolbar: true,
    drawer: false,
    items: [
      { title: "Search", icon: "mdi-magnify" },
      { title: "Upload money", icon: "attach_money" },
      { title: " Logout", icon: "power_settings_new" },
    ],
  }),

  methods: {},
};
</script>
<style>
@media (max-width: 1000px) {
  .bigScreen {
    display: none !important;
  }
}
@media (min-width: 1001px) {
  .smallScreen {
    display: none !important;
  }
}
</style>