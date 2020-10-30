<template>
  <div>
    <div class="text-center py-5" style="background-color: #e9ecef">
      <h1>Amusement Park</h1>
    </div>
    <v-container v-if="searchInputShow">
      <v-row>
        <v-col cols="12" md="3">
          <v-text-field label="Name" outlined required dense></v-text-field>
        </v-col>
        <v-col cols="12" md="3">
          <v-text-field
            label="Capital min"
            outlined
            required
            dense
          ></v-text-field>
        </v-col>
        <v-col cols="12" md="3">
          <v-text-field
            label="Capital max"
            outlined
            required
            dense
          ></v-text-field>
        </v-col>
        <v-col cols="12" md="3">
          <v-text-field
            label="
Total area min"
            outlined
            required
            dense
          ></v-text-field>
        </v-col>
        <v-col cols="12" md="3">
          <v-text-field
            label="Total area max"
            outlined
            required
            dense
          ></v-text-field>
        </v-col>
        <v-col cols="12" md="3">
          <v-text-field
            label="Entrance fee min"
            outlined
            required
            dense
          ></v-text-field>
        </v-col>
        <v-col cols="12" md="3">
          <v-text-field
            label="Entrance fee max"
            outlined
            required
            dense
          ></v-text-field>
        </v-col>
        <v-col cols="12" md="3">
          <v-btn color="blue darken-1" dark block class="mt-1"> Search </v-btn>
        </v-col>
      </v-row>
    </v-container>
    <v-container>
      <v-data-table :headers="headers" :items="amusementParks"> </v-data-table>
    </v-container>
    <v-dialog v-model="openCreateDialog" persistent max-width="600px" eager>
      <v-card>
        <v-container>
          <div class="text-right" style="width: 100%">
            <v-btn icon @click="$emit('toggleCreateDialog')">
              <v-icon>close</v-icon>
            </v-btn>
          </div>
          <v-card-title>
            <h2>Create amusement park</h2>
          </v-card-title>
          <v-card-text>
            <v-form ref="amusementParkCreateForm">
              <v-row>
                <v-col cols="12">
                  <v-text-field
                    label="Name"
                    outlined
                    required
                    dense
                    v-model="amusementParkCreate.name"
                    :rules="[
                      (v) =>
                        (!!v && v.length > 5 && v.length < 20) ||
                        'Name size must be between 5 and 20.',
                    ]"
                    :counter="20"
                  >
                  </v-text-field>
                  <v-text-field
                    label="Capital"
                    outlined
                    required
                    dense
                    v-model="amusementParkCreate.capital"
                    :rules="[
                      (v) =>
                        (!!v && Number(v) > 500 && Number(v) < 50000) ||
                        'Capital must be between 500 and 50000.',
                    ]"
                  ></v-text-field>
                  <v-text-field
                    label="Total area"
                    outlined
                    required
                    dense
                    v-model="amusementParkCreate.totalArea"
                    :rules="[
                      (v) =>
                        (!!v && Number(v) > 50 && Number(v) < 5000) ||
                        'TotalArea must be between 50 and 5000. ',
                    ]"
                  ></v-text-field>
                  <v-text-field
                    label="Entrance fee"
                    outlined
                    required
                    dense
                    v-model="amusementParkCreate.entranceFee"
                    :rules="[
                      (v) =>
                        (!!v && Number(v) > 5 && Number(v) < 200) ||
                        'EntranceFee must be between 5 and 200.',
                    ]"
                  ></v-text-field>
                </v-col>
              </v-row> </v-form
          ></v-card-text>
          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn
              color="blue darken-1"
              class="px-4"
              dark
              @click="createAmusementPark"
            >
              Create
            </v-btn>
          </v-card-actions>
        </v-container>
      </v-card>
    </v-dialog>
  </div>
</template>
<script>
import $ from "jquery";
export default {
  props: ["searchInputShow", "amusementParksLink", "openCreateDialog"],
  data: () => ({
    amusementParksResponse: null,
    headers: [
      {
        text: "Name",
        align: "start",
        value: "name",
      },
      { text: "Capital", value: "capital" },
      { text: "Total area", value: "totalArea" },
      { text: "Entrance fee", value: "entranceFee" },
      { text: "Machines", value: "numberOfMachines" },
      { text: "Guest Book Registries", value: "numberOfGuestBookRegistries" },
      { text: "Active Visitors", value: "numberOfActiveVisitors" },
      { text: "Known Visitors", value: "numberOfKnownVisitors" },
    ],
    amusementParkCreate: {
      name: "",
      capital: "",
      totalArea: "",
      entranceFee: "",
    },
  }),
  computed: {
    amusementParks() {
      if (this.amusementParksResponse == null) {
        return [];
      } else {
        return this.amusementParksResponse._embedded
          .amusementParkDetailResponseDtoList;
      }
    },
  },
  created() {
    this.amusementParkResponseBody();
  },
  watch: {
    openCreateDialog(to) {
      if (to) {
        this.$refs.amusementParkCreateForm.reset();
      }
    },
  },
  methods: {
    amusementParkResponseBody() {
      $.ajax({
        url: this.amusementParksLink,
        success: (responseBody) => {
          this.amusementParksResponse = responseBody;
        },
      });
    },
    createAmusementPark() {
      if (this.$refs.amusementParkCreateForm.validate()) {
        $.ajax({
          url: this.amusementParksLink,
          method: "POST",
          contentType: "application/json",
          data: JSON.stringify(this.amusementParkCreate),
          success: () => {
            this.amusementParkResponseBody();
            this.$emit("toggleCreateDialog");
            this.$bus.$emit("addMessage", {
              type: "success",
              text:
                "Successfully created new amusement park " +
                this.amusementParkCreate.name +
                ".",
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
  },
};
</script>