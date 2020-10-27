<template>
  <v-app>
    <Navbar :visitor="visitor" />
    <v-main>
      <router-view v-if="loaded" :loginLink="links.login" @login="login" />
      <div v-else class="text-center py-5" style="background-color: #e9ecef">
        <h1>Welcome visitor</h1>
      </div>
    </v-main>
  </v-app>
</template>

<script>
import Navbar from "@/components/Navbar";
import $ from "jquery";
export default {
  name: "App",

  components: {
    Navbar,
  },

  data: () => ({
    links: {},
    loaded: false,
    visitor: null,
  }),
  created() {
    $.ajax({
      url: "/api/links",
      success: (responseBody) => {
        this.setLinks(responseBody);
        this.getUserData();
      },
    });
  },
  methods: {
    setLinks(responseBody) {
      for (let i = 0; i < responseBody.length; i++) {
        let element = responseBody[i];
        this.links[element.rel] = element.href;
      }
    },
    getUserData() {
      $.ajax({
        url: this.links.me,
        success: (responseBody) => {
          this.login(responseBody);
          this.loaded = true;
        },
        error: () => {
          this.loaded = true;
        },
      });
    },
    login(responseBody) {
      this.visitor = responseBody;
      this.$router.push("/amusement-park");
    },
  },
};
</script>
