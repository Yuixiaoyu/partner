<template>
    <van-list
        v-model:loading="continueLoading"
        :finished="finished"
        finished-text="暂无该标签的小伙伴"
        @load="loadData"
    >
        <user-card-list v-for="user in userList" :user="user" :key="user.userId" :loading="loading"/>
    </van-list>
    <van-back-top style="margin-bottom: 10%"/>
</template>

<script setup lang="ts">
import {useRoute} from "vue-router";
import {ref} from "vue";
import {showFailToast} from "vant";
import {SearchUserByTags} from '../services/tags';
import UserCardList from '../components/UserCardList.vue';

const route = useRoute();
/** 用户列表 */
const userList = ref([])
/** 继续加载标识 */
const continueLoading = ref(false);
/** 加载标识 */
const loading = ref(false);
/** 加载完成标识 */
const finished = ref(false);
/** 路由参数：标签 */
const {tags, username} = route.query
/** 分页参数 */
let pageInfo = {pageSize: 10, pageNum: 1};

const loadData = async () => {
    continueLoading.value = true;

    const userListData = await SearchUserByTags(tags, username, pageInfo.pageSize, pageInfo.pageNum++)
        .then((response) => {
            console.log('/user/search/tags succeed', response);
            if (response?.data?.records.length < 1) {
                finished.value = true
            }
            return response?.data?.records;
        })
        .catch(function (error) {
            console.log('/user/search/tags error', error);
            showFailToast("请求失败")
        })
    if (userListData) {
        userListData.forEach(user => {
            if (user.tags) {
                user.tags = JSON.parse(user.tags)
            }
            userList.value.push(user);
        })
    }
    continueLoading.value = false;
}


</script>

<style lang="scss" scoped>
</style>
