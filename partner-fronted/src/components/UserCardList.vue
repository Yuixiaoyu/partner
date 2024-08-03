<template>
  <van-skeleton title avatar :row="3" :loading="loading" >
    <van-card
        :desc="`${user.profile}`"
        :title="`${user.username}`"
        :thumb="`${user.avatarUrl}`"
        class="card-style"
    >
      <template #tags>
        <van-tag plain type="danger" v-for="tag in user.tags" style="margin-right: 8px; margin-top: 8px">
          {{ tag }}
        </van-tag>
      </template>
      <template #footer>
        <van-button size="mini" @click="toMessageChat(user.userId)">联系我</van-button>
      </template>
    </van-card>
  </van-skeleton>
</template>

<script setup lang="ts">
import {UserType} from "../models/user";
import {onMounted} from "vue";
import {useRouter} from "vue-router";
import UserChatPage from "../pages/UserChatPage.vue";

interface UserCardListProps {
  user: UserType,
  loading: boolean;
}
// const props = defineProps<{ user: object, loading: boolean}>()
withDefaults(defineProps<UserCardListProps>(), {
  user: undefined,
  loading: true
});

const router = useRouter()

const toMessageChat = (userId: any) => {
    router.push({
        name: "userChat",
        params: {
            toUserId: userId,
            receiveType: 0,
            status: 0
        }
    })
}

</script>

<style scoped>
.card-style {
  background: white;
  border-radius: 10px;
  width: 96%;
  margin: 0 auto;
}
</style>
