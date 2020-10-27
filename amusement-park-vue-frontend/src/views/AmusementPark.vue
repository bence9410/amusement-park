<template>
  <div>
    <div class="text-center py-5" style="background-color: #e9ecef">
      <h1>Amusement Park</h1>
    </div>
    <v-container>
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
          <v-btn color="blue darken-1" dark block> Search </v-btn>
        </v-col>
      </v-row>
    </v-container>

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
            <v-col cols="12" md="9" class="pl-0">
              <v-text-field label="Money:" outlined required></v-text-field>
            </v-col>
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="blue darken-1" dark> Upload </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>
<script>
export default {
  data: () => ({
    dialog: {
      form: true,
      show: false,
    },
  }),
};
</script>