<template>
  <v-app>
    <Navbar :visitor="visitor" :logoutLink="links.logout" @logout="logout" @uploadMoney="uploadMoney"
      @toggleSearch="searchInputShow = !searchInputShow" @toggleCreateDialog="openCreateDialog = !openCreateDialog" />

    <v-main class="image">
      <Message />
      <router-view :loginLink="links.login" :signUpLink="links.signUp" :visitor="visitor" @login="login"
        :searchInputShow="searchInputShow" :amusementParksLink="links.amusementPark"
        :openCreateDialog="openCreateDialog" @toggleCreateDialog="openCreateDialog = !openCreateDialog" />
    </v-main>
  </v-app>
</template>

<script>
import Navbar from "@/components/Navbar";
import Message from "@/components/Message";
export default {
  name: "App",

  components: {
    Navbar,
    Message,
  },

  data: () => ({
    links: {},
    visitor: null,
    searchInputShow: false,
    openCreateDialog: false,
  }),
  created() {
    fetch("/api/links").then(async response => {
      if (response.ok) {
        this.setLinks(await response.json());
        this.getUserData();
      }
    });
  },
  methods: {
    setLinks(responseBody) {
      let links = {};
      for (let i = 0; i < responseBody.length; i++) {
        let element = responseBody[i];
        links[element.rel] = element.href;
      }
      this.links = links;
    },
    getUserData() {
      fetch(this.links.me).then(async response => {
        if (response.ok) {
          this.login(await response.json());
        }
      });
    },
    login(responseBody) {
      this.visitor = responseBody;
      if (this.$route.path != "/amusement-parks") {
        this.$router.push("/amusement-parks");
      }
    },
    logout() {
      this.visitor = null;
      this.searchInputShow = false;
      this.$router.push("/");
    },
    uploadMoney(uploadedMoney) {
      this.visitor.spendingMoney += Number(uploadedMoney);
    },
  },
};
</script>
