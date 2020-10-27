<template>
  <v-app>
    <Navbar :show="showNavbar" />
    <v-main>
      <router-view @login="showNavbar = true" />
    </v-main>
  </v-app>
</template>

<script>
import Navbar from "@/components/Navbar";
export default {
  name: "App",

  components: {
    Navbar,
  },

  data: () => ({
    showNavbar: false,
  }),
};
</script>
