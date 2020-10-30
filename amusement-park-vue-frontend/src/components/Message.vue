<template>
  <div
    class="pt-3"
    style="position: absolute; width: 100%; z-index: 1"
    align="center"
  >
    <v-alert
      v-for="message in messages"
      :key="message.id"
      style="width: 60%"
      :type="message.type"
      transition="slide-y-transition"
      @click="removeMessage(message)"
      >{{ message.text }}
    </v-alert>
  </div>
</template>
<script>
export default {
  data: () => ({
    messages: [],
    messageIdGenerator: 0,
  }),

  methods: {
    removeMessage(message) {
      this.messages.splice(this.messages.indexOf(message), 1);
    },
    addMessage(message) {
      message.id = this.messageIdGenerator++;
      this.messages.push(message);
      setTimeout(() => {
        this.removeMessage(message);
      }, 10000);
    },
  },
  created() {
    this.$bus.$on("addMessage", this.addMessage);
  },
};
</script>