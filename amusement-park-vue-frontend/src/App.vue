<template>
  <v-app>
    <Navbar
      :visitor="visitor"
      :logoutLink="links.logout"
      @logout="logout"
      @uploadMoney="uploadMoney"
      @toggleSearch="searchInputShow = !searchInputShow"
      @toggleCreateDialog="openCreateDialog = !openCreateDialog"
    />

    <v-main class="image">
      <Message />
      <router-view
        :loginLink="links.login"
        :signUpLink="links.signUp"
        :visitor="visitor"
        @login="login"
        :searchInputShow="searchInputShow"
        :amusementParksLink="links.amusementPark"
        :openCreateDialog="openCreateDialog"
        @toggleCreateDialog="openCreateDialog = !openCreateDialog"
      />
    </v-main>
  </v-app>
</template>
<style>
#app {
  font-family: " Sans-serif" !important;
}
.image {
  background-repeat: no-repeat;
  background-image: url("./assets/background.jpg");
  padding: 0;
  margin: 0;
  background-repeat: no-repeat;
  background-attachment: fixed;
  background-size: cover;
  position: static;
}
.formButton {
  background: -moz-linear-gradient(right, #067998, #16363c, #16363c, #067998);
}
.neonGreen {
  background-color: #00b712;
  background-image: linear-gradient(315deg, #00b712 0%, #5aff15 74%);
}
</style>

<script>
import Navbar from "@/components/Navbar";
import Message from "@/components/Message";
import $ from "jquery";
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
      let links = {};
      for (let i = 0; i < responseBody.length; i++) {
        let element = responseBody[i];
        links[element.rel] = element.href;
      }
      this.links = links;
    },
    getUserData() {
      $.ajax({
        url: this.links.me,
        success: (responseBody) => {
          this.login(responseBody);
        },
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
