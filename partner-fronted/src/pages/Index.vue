<template>
    <van-nav-bar>
        <template #left>
            <b style="font-size: 20px">首页</b>
        </template>
        <template #right>
            <van-icon name="search" size="18" @click="onClickRight"/>
        </template>
    </van-nav-bar>
    <div class="div-my-swipe">
        <van-swipe class="my-swipe" :autoplay="3000" indicator-color="white">
            <van-swipe-item class="scroll-div-image1"></van-swipe-item>
            <van-swipe-item class="scroll-div-image2"></van-swipe-item>
            <van-swipe-item class="scroll-div-image3"></van-swipe-item>
            <van-swipe-item class="scroll-div-image4"></van-swipe-item>
        </van-swipe>
    </div>
    <div class="my-div">
        <van-cell center class="my-div-cell">
            <template #title>
                <b style="font-size: 13px">精准匹配</b>
            </template>
            <template #right-icon>
                <van-switch v-model="isMatchMode" size="24" @change="changeMatchMode"/>
            </template>
        </van-cell>
    </div>
    <van-list
        v-model:loading="continueLoading"
        :finished="finished"
        finished-text="没有更多了"
        @load="loadData"
        style="background-color: #eef0f3;"
    >
        <user-card-list v-for="user in userList" :user="user" :key="user.userId" :loading="loading"/>
    </van-list>
    <van-back-top style="margin-bottom: 10%; "/>
    <van-empty v-show="isDataEmpty" description="数据为空"/>
</template>

<script setup lang="ts">
import {onMounted, ref} from 'vue';
import {showFailToast} from "vant";
import UserCardList from "../components/UserCardList.vue";
import {UserType} from "../models/user";
import {userMatch, userRecommend} from "../services/user";
import {useRouter} from "vue-router";

const isMatchMode = ref<boolean>(false);

const userList = ref<UserType[]>([]);
const loading = ref(true);
const continueLoading = ref(true);
const finished = ref(false);
const isDataEmpty = ref(false);
const pageInfo = {pageSize: 8, pageNum: 0}

const router = useRouter();

onMounted(() => {
    loadData()
})

const onClickRight = () => {
    console.log("跳转Search")
    router.push('/search')
};

const changeMatchMode = (modeFlag: boolean) => {
    // 精确匹配
    if (modeFlag) {
        finished.value = true;
        exactMatching()
    } else {
        userList.value = []
        loading.value = true;
        finished.value = false;
        // 普通列表
        loadData()
    }
}
const exactMatching = async () => {
    const num = 10;
    loading.value = true;
    let userListData = await userMatch(num)
        .then(function (response) {
            return response?.data;
        })
        .catch(function (error) {
            showFailToast('请求失败');
        })
    // 初始化分页参数 - 由于计算匹配度过于耗时，暂时固定分页长度，
    pageInfo.pageSize = 8
    pageInfo.pageNum = 1

    userListData.forEach((user: UserType) => {
        if (user.tags) {
            user.tags = JSON.parse(user.tags);
        }
    })
    userList.value = userListData;
    loading.value = false;
    finished.value = true;
}
/**
 * 加载数据
 */
const loadData = async () => {
    const {pageSize, pageNum} = pageInfo
    // 普通模式，直接分页查询用户
    let userListData = await userRecommend(pageSize, pageNum)
        .then(function (response) {
            console.log(response)
            return response?.data.records;
        })
        .catch(function (error) {
            showFailToast('请求失败');
        })
    console.log("请求数据：", userListData, pageSize, pageNum)
    if (!userListData) {
        isDataEmpty.value = true;
    }
    // 页码+1
    pageInfo.pageNum++;
    /*
      普通模式，则追加
     */
    userListData.forEach((user: UserType) => {
        if (user.tags) {
            user.tags = JSON.parse(user.tags);
        }
        userList.value.push(user);
    })
    continueLoading.value = false;
    loading.value = false;
}
</script>

<style scoped>

.my-div {
    background-color: #eef0f3;
    padding: 10px 0;
}

.my-div-cell {
    width: 96%;
    margin: 0 auto;
    border-radius: 10px;
}

.div-my-swipe {
    background-color: #eef0f3;
    padding-top: 7px;
}

.my-swipe {
    width: 96%;
    height: 200px;
    margin: 0 auto;
    border-radius: 10px;
}

.scroll-div-image1 {
    background-image: url("../assets/images/1.jpg");
    background-size: 100% 100%;
}

.scroll-div-image2 {
    background-image: url("../assets/images/2.jpg");
    background-size: 100% 100%;
}

.scroll-div-image3 {
    background-image: url("../assets/images/3.jpg");
    background-size: 100% 100%;
}

.scroll-div-image4 {
    background-image: url("../assets/images/4.jpg");
    background-size: 100% 100%;
}

</style>
