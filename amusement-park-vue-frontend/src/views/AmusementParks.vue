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
          <v-text-field label="Capital min" outlined required dense></v-text-field>
        </v-col>
        <v-col cols="12" md="3">
          <v-text-field label="Capital max" outlined required dense></v-text-field>
        </v-col>
        <v-col cols="12" md="3">
          <v-text-field label="
Total area min" outlined required dense></v-text-field>
        </v-col>
        <v-col cols="12" md="3">
          <v-text-field label="Total area max" outlined required dense></v-text-field>
        </v-col>
        <v-col cols="12" md="3">
          <v-text-field label="Entrance fee min" outlined required dense></v-text-field>
        </v-col>
        <v-col cols="12" md="3">
          <v-text-field label="Entrance fee max" outlined required dense></v-text-field>
        </v-col>
        <v-col cols="12" md="3">
          <v-btn color="green" block class="mt-1"> Search </v-btn>
        </v-col>
      </v-row>
    </v-container>
    <v-container>
      <v-data-table :headers="headers" :items="amusementParks" class="green"> </v-data-table>
    </v-container>
    <v-dialog v-model="openCreateDialog" persistent width="50%" eager>
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
                  <v-text-field label="Name" outlined required dense v-model="amusementParkCreate.name" :rules="[
                    (v) =>
                      (!!v && v.length >= 5 && v.length <= 20) ||
                      'Name size must be between 5 and 20.',
                  ]" :counter="20">
                  </v-text-field>
                  <v-text-field label="Capital" outlined required dense v-model="amusementParkCreate.capital" :rules="[
                    (v) =>
                      (!!v && Number(v) >= 500 && Number(v) <= 50000) ||
                      'Capital must be between 500 and 50000.',
                  ]"></v-text-field>
                  <v-text-field label="Total area" outlined required dense v-model="amusementParkCreate.totalArea"
                    :rules="[
                      (v) =>
                        (!!v && Number(v) >= 50 && Number(v) <= 5000) ||
                        'TotalArea must be between 50 and 5000. ',
                    ]"></v-text-field>
                  <v-text-field label="Entrance fee" outlined required dense v-model="amusementParkCreate.entranceFee"
                    :rules="[
                      (v) =>
                        (!!v && Number(v) >= 5 && Number(v) <= 200) ||
                        'EntranceFee must be between 5 and 200.',
                    ]"></v-text-field>
                </v-col>
              </v-row> </v-form></v-card-text>
          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn color="blue darken-1" class="px-4" dark @click="createAmusementPark">
              Create
            </v-btn>
          </v-card-actions>
        </v-container>
      </v-card>
    </v-dialog>
  </div>
</template>
<script>
export default {
  props: ["searchInputShow", "amusementParksLink", "openCreateDialog"],
  data: () => ({
    amusementParks: [],
    headers: [
      { text: "Name", value: "name" },
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
  created() {
    this.getAmusementParksIfLoaded();
  },
  watch: {
    amusementParksLink() {
      this.getAmusementParksIfLoaded();
    },
    openCreateDialog(to) {
      if (to) {
        this.$refs.amusementParkCreateForm.reset();
      }
    },
  },
  methods: {
    getAmusementParksIfLoaded() {
      if (this.amusementParksLink != null) {
        fetch(this.amusementParksLink).then(async response => {
          if (response.ok) {
            let amusementParksResponse = await response.json();
            this.amusementParks = amusementParksResponse._embedded.amusementParkDetailResponseDtoList;
          }
        });
      }
    },
    createAmusementPark() {
      if (this.$refs.amusementParkCreateForm.validate()) {
        fetch(this.amusementParksLink, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: this.amusementParkCreate
        }).then(async response => {
          if (response.ok) {
            this.amusementParkResponseBody();
            this.$emit("toggleCreateDialog");
            this.$bus.$emit("addMessage", {
              type: "success",
              text:
                "Successfully created new amusement park " +
                this.amusementParkCreate.name +
                ".",
            });
          } else {
            this.$bus.$emit("addMessage", {
              type: "error",
              text: await response.text(),
            });
          }
        });
      }
    },
  },
};
</script>