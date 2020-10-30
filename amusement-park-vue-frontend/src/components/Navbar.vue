<template>
  <div v-if="visitor">
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
        <v-img class="pt-0" :src="visitor.photo" alt="Profile picture" />
      </v-avatar>
      <div class="ml-1 mr-2">
        <p id="email" class="mb-1">{{ visitor.email }}</p>
        <p id="spendingMoney">{{ visitor.spendingMoney }}</p>
      </div>

      <v-btn
        color="green darken-3"
        elevation="7"
        dark
        class="mt-2 bigScreen"
        @click="toggleSearch"
        ><v-icon class="mr-1">mdi-magnify</v-icon>
        Search
      </v-btn>
      <v-spacer></v-spacer>
      <v-btn
        color="green darken-3"
        elevation="7"
        dark
        @click="openUploadMoneyDialog"
        class="mr-2 mt-2 bigScreen"
      >
        <v-icon>attach_money</v-icon>
        Upload money
      </v-btn>

      <v-btn
        color="green darken-3"
        elevation="7"
        dark
        @click="logout"
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
          <v-list-item @click="toggleSearch">
            <v-icon class="mr-1">mdi-magnify</v-icon>
            <v-list-item-title>Search</v-list-item-title>
          </v-list-item>
          <v-list-item @click="openUploadMoneyDialog">
            <v-icon class="mr-1">attach_money</v-icon>
            <v-list-item-title>Upload money</v-list-item-title>
          </v-list-item>
          <v-list-item @click="logout">
            <v-icon class="mr-1">power_settings_new</v-icon>
            <v-list-item-title>Logout</v-list-item-title>
          </v-list-item>
        </v-list>
      </v-menu>
    </v-toolbar>
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
<script>
import $ from "jquery";
export default {
  props: ["visitor", "logoutLink"],
  data: () => ({
    uploadMoneyDialogShow: false,
    uploadMoneyValue: "",
  }),

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
  },
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