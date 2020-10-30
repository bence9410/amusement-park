<template>
  <v-app>
    <Navbar
      :visitor="visitor"
      :logoutLink="links.logout"
      @logout="logout"
      @uploadMoney="uploadMoney"
      @toggleSearch="searchInputShow = !searchInputShow"
    />

    <v-main>
      <Message />
      <router-view
        v-if="loaded"
        :loginLink="links.login"
        :signUpLink="links.signUp"
        @login="login"
        :searchInputShow="searchInputShow"
        :amusementParksLink="links.amusementPark"
      />
      <div v-else class="text-center py-5" style="background-color: #e9ecef">
        <h1>Welcome visitor</h1>
      </div>
    </v-main>
  </v-app>
</template>

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
    loaded: false,
    visitor: null,
    searchInputShow: false,
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
