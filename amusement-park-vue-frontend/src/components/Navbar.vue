<template>
  <div v-if="visitor">
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
          <v-img class="pt-0" :src="visitor.photo" alt="Profile picture" />
        </v-avatar>
        <div class="ml-1 mr-2">
          <p id="email" class="mb-1">{{ visitor.email }}</p>
          <p id="spendingMoney">{{ visitor.spendingMoney }}</p>
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
        <v-dialog v-model="dialog.show" persistent max-width="600px" eager>
          <v-card>
            <div class="text-right" style="width: 100%">
              <v-btn icon @click="dialog.show = false">
                <v-icon>close</v-icon>
              </v-btn>
            </div>
            <v-card-title>
              <h2>Upload money</h2>
            </v-card-title>
            <v-card-text>
              <v-form ref="form" v-model="dialog.form">
                <v-row>
                  <v-text-field
                    label="Money:"
                    outlined
                    required
                    dense
                    class="pr-4 pl-3"
                  ></v-text-field>
                  <v-btn color="blue darken-1" dark class="mt-1">
                    Upload
                  </v-btn>
                </v-row>
              </v-form>
            </v-card-text>
            <v-card-actions> </v-card-actions>
          </v-card>
        </v-dialog>

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
  props: ["visitor"],
  data: () => ({
    dialog: {
      form: true,
      show: false,
    },
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