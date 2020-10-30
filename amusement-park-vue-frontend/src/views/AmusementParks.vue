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
  </div>
</template>
<script>
import $ from "jquery";
export default {
  props: ["searchInputShow", "amusementParksLink"],
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
    $.ajax({
      url: this.amusementParksLink,
      success: (responseBody) => {
        this.amusementParksResponse = responseBody;
      },
    });
  },
};
</script>