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
  </div>
</template>
<script>
export default {
  props: ["searchInputShow"],
  data: () => ({}),
};
</script>