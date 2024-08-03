<template>
    <div class="card-body">
        <message-card-list v-if="messageList.length > 0" v-for="message in messageList" :message="message"
                           :key="message.messageId"
                           :loading="loading"/>
        <van-empty v-if="messageList.length === 0" description="暂无消息"/>
    </div>
</template>

<script lang="ts" setup>

import MessageCardList from "../components/MessageCardList.vue";
import {useRouter} from "vue-router";
import {onMounted, ref} from "vue";
import {getMessageList} from "../services/message";

const router = useRouter();

const messageList = ref([])
const loading = ref(true)

onMounted(async () => {
    // 1. 加载消息
    await getMessageList()
        .then((res) => {
            if (res.code === 0) {
                messageList.value = res.data
                loading.value = false;
            }
        })
})

</script>

<style scoped>
</style>
