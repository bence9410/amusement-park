<template>
  <div v-if="visitor">
    <v-app-bar dense app class="navbarColor" style="opacity: 0.9" elevation="4">
      <v-app-bar-nav-icon
        @click="showDrawer"
        class="smallScreen"
      ></v-app-bar-nav-icon>

      <v-avatar class="mr-4 ml-5 bigScreen" size="40">
        <v-img :src="visitor.photo" alt="Profile picture" />
      </v-avatar>
      <div class="mt-1 mr-2 bigScreen" style="color: white">
        <h5 id="email" style="font-weight: 500">
          {{ visitor.email }} <br />{{ visitor.spendingMoney }}
          <v-icon class="mb-1" style="font-size: 16px; color: white"
            >attach_money</v-icon
          >
        </h5>
      </div>
      <v-spacer></v-spacer>
      <div
        style="
          width: 135px;
          position: absolute;
          left: 50%;
          -webkit-transform: translateX(-50%);
          transform: translateX(-50%);
          z-index: 20;
        "
      >
        <v-img
          src="../assets/logoWithoutBackground.png"
          alt="Logo amusement park."
          elevation="10"
        />
      </div>
      <v-tabs dark class="bigScreen" right>
        <v-tabs-slider color="white"></v-tabs-slider>

        <v-tab @click="toggleSearch">
          <v-icon class="mr-1">mdi-magnify</v-icon>Search</v-tab
        >
        <v-tab v-if="isAdmin" @click="toggleCreateDialog"
          ><v-icon class="mr-1">add_circle_outline</v-icon>Create</v-tab
        >
        <v-tab @click="openUploadMoneyDialog">
          <v-icon>attach_money</v-icon>Upload money</v-tab
        >

        <v-tab @click="logout"
          ><v-icon class="mr-1">power_settings_new</v-icon>Logout</v-tab
        >
      </v-tabs>
    </v-app-bar>
    <v-navigation-drawer
      v-model="drawer"
      temporary
      class="navbarColor smallScreen"
      dark
      app
      :width="drawerWidth"
      ><v-container>
        <v-btn icon @click.stop="drawer = !drawer">
          <v-icon>close</v-icon>
        </v-btn>
        <v-card class="text-center" color="transparent" outlined>
          <v-col class="text-center" style="position: relative">
            <div class="aroundAvatar pt-0"></div>
            <v-avatar
              size="100"
              style="
                margin-top: 10px;
                position: absolute;
                box-shadow: 0 15px 35px rgba(0, 0, 0, 0.4);
                left: 50%;
                -webkit-transform: translateX(-50%);
                transform: translateX(-50%);
              "
            >
              <v-img class="pt-0" :src="visitor.photo" alt="Profile picture" />
            </v-avatar>
          </v-col>
          <v-col>
            <p id="email" class="mb-1">{{ visitor.email }}</p>
            <div id="spendingMoney">
              {{ visitor.spendingMoney }}
              <v-icon class="mb-1" style="font-size: 16px">attach_money</v-icon>
            </div>
          </v-col>
        </v-card>
        <v-list rounded>
          <v-list-item-group>
            <v-list-item @click="toggleSearch">
              <v-icon class="mr-1">mdi-magnify</v-icon>
              <v-list-item-title>Search</v-list-item-title>
            </v-list-item>
            <v-list-item @click="toggleCreateDialog">
              <v-icon class="mr-1">add_circle_outline</v-icon>
              <v-list-item-title>Create</v-list-item-title>
            </v-list-item>
            <v-list-item @click="openUploadMoneyDialog">
              <v-icon class="mr-1">attach_money</v-icon>
              <v-list-item-title>Upload money</v-list-item-title>
            </v-list-item>
            <v-list-item @click="logout">
              <v-icon class="mr-1">power_settings_new</v-icon>
              <v-list-item-title>Logout</v-list-item-title>
            </v-list-item>
          </v-list-item-group>
        </v-list>
      </v-container>
    </v-navigation-drawer>

    <v-dialog
      v-model="uploadMoneyDialogShow"
      persistent
      max-width="600px"
      eager
    >
      <v-card>
        <div class="text-right" style="width: 100%">
          <v-btn icon @click="uploadMoneyDialogShow = false">
            <v-icon>close</v-icon>
          </v-btn>
        </div>
        <v-card-title>
          <h2>Upload money</h2>
        </v-card-title>
        <v-card-text>
          <v-form ref="uploadMoneyForm">
            <v-row>
              <v-text-field
                label="Money:"
                outlined
                required
                dense
                class="pr-4 pl-3"
                :rules="[
                  (v) =>
                    (!!v && Number(v) > 0) || 'Value must be greater than 0.',
                ]"
                v-model="uploadMoneyValue"
              ></v-text-field>
              <v-btn
                color="blue darken-1"
                dark
                class="mt-1"
                @click="uploadMoney"
              >
                Upload
              </v-btn>
            </v-row>
          </v-form>
        </v-card-text>
      </v-card>
    </v-dialog>
  </div>
</template>
<style scoped>
.aroundAvatar {
  height: 120px;
  width: 120px;
  background-image: linear-gradient(
    315deg,
    rgba(98, 113, 255, 0.05),
    hsla(0, 0%, 100%, 0.05)
  );
  box-shadow: inset 0 -1px 0 0 rgba(156, 190, 227, 0.11),
    inset 0px 30.0211px 43.1072px -27.7118px rgba(255, 255, 255, 0),
    inset 0px 5.38841px 8.46749px -3.07909px #ffffff82;
  color: #fff;
  display: inline-block;
  border-radius: 50%;
}

.cardColor {
  border-radius: 20px;
  background-image: linear-gradient(
    315deg,
    rgba(98, 113, 255, 0.05),
    hsla(0, 0%, 100%, 0.05)
  );
  box-shadow: inset 0 -1px 0 0 rgba(156, 190, 227, 0.11),
    inset 0px 30.0211px 43.1072px -27.7118px rgba(255, 255, 255, 0),
    inset 0px 5.38841px 8.46749px -3.07909px #ffffff82;
  color: #fff;
}
.dot {
  height: 120px;
  width: 120px;
  background-color: #bbb;
  border-radius: 50%;
  display: inline-block;
  background-color: rgba(255, 255, 255, 0.06) !important;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.2);

  border: 1px solid rgba(255, 255, 255, 0.01);
}
.navbarColor {
  background-color: #3bb78f;
  background-image: linear-gradient(315deg, #3bb78f 0%, #0bab64 74%);
}
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
<script>
import $ from "jquery";
export default {
  props: ["visitor", "logoutLink"],
  data: () => ({
    uploadMoneyDialogShow: false,
    uploadMoneyValue: "",
    drawer: false,
    drawerWidth: "",
  }),
  computed: {
    isAdmin() {
      return "ROLE_ADMIN" == this.visitor.authority;
    },
  },

  methods: {
    logout() {
      $.ajax({
        url: this.logoutLink,
        method: "POST",
        success: () => {
          this.$emit("logout");
          this.$bus.$emit("addMessage", {
            type: "success",
            text: "Successfull logout.",
          });
        },
        error: (response) => {
          this.$bus.$emit("addMessage", {
            type: "error",
            text: response.responseText,
          });
        },
      });
    },
    uploadMoney() {
      if (this.$refs.uploadMoneyForm.validate()) {
        this.uploadMoneyDialogShow = false;
        $.ajax({
          url: this.visitor._links.uploadMoney.href,
          method: "POST",
          contentType: "application/json",
          data: this.uploadMoneyValue,
          success: () => {
            this.$emit("uploadMoney", this.uploadMoneyValue);
            this.$bus.$emit("addMessage", {
              type: "success",
              text:
                "Successfully uploaded " + this.uploadMoneyValue + " money.",
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
    openUploadMoneyDialog() {
      this.$refs.uploadMoneyForm.reset();
      this.uploadMoneyDialogShow = true;
    },
    toggleSearch() {
      this.$emit("toggleSearch");
    },
    toggleCreateDialog() {
      this.$emit("toggleCreateDialog");
    },
    showDrawer() {
      this.drawer = true;
      if (window.innerWidth < 1000) {
        this.drawerWidth = "90%";
      } else {
        this.drawerWidth = "40%";
      }
    },
  },
};
</script>
