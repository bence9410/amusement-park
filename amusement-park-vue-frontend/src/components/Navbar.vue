<template>
  <div v-if="visitor">
    <v-app-bar color="green">
      <v-avatar class="mr-4 ml-5">
        <v-img :src="visitor.photo" alt="Profile picture" />
      </v-avatar>
      <span>
        {{ visitor.email }}
      </span>
      <span class="ml-3">
        {{ visitor.spendingMoney }}
      </span>
      <v-icon icon="mdi-currency-eur">mdi-currency-eur</v-icon>

      <v-spacer></v-spacer>

      <v-btn class="ma-1" @click="toggleSearch">Search</v-btn>
      <v-btn class="ma-1" v-if="isAdmin" @click="toggleCreateDialog">Create</v-btn>
      <v-btn class="ma-1" @click="openUploadMoneyDialog">Upload money</v-btn>
      <v-btn class="ma-1" @click="logout">Logout</v-btn>
    </v-app-bar>

    <v-dialog v-model="uploadMoneyDialogShow" persistent width="50%" eager>
      <v-card>
        <div class="text-right" style="width: 100%">
          <v-btn icon @click="uploadMoneyDialogShow = false">
            <v-icon>close</v-icon>
          </v-btn>
        </div>

        <v-card-title class="text-h5"> Upload money </v-card-title>

        <v-card-text>
          <v-form ref="uploadMoneyForm">
            <v-text-field label="Money" required outlined dense v-model="uploadMoneyValue" :rules="[
              (v) =>
                (!!v && Number(v) > 0) || 'Value must be greater than 0.',
            ]"></v-text-field>
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="green" @click="uploadMoney">Upload</v-btn>
        </v-card-actions>
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
    }
  },
};
</script>
