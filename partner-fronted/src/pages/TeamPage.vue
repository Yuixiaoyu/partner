<template>
    <div id="teamPage">
        <van-search v-model="searchText" placeholder="搜索房间" @search="onSearch"/>
        <van-button color="linear-gradient(to right, #ff6034, #ee0a24)" class="add-button" type="primary" icon="plus"
                    round
                    @click="toAddTeam" block/>
        <van-loading size="24px" vertical v-show="isLoading">加载中...</van-loading>
        <div style="margin-bottom: -5px"/>
        <!--        <van-dropdown-menu>-->
        <!--            <van-dropdown-item v-model="active" :options="activeOption" @change="onTabChange"/>-->
        <!--        </van-dropdown-menu>-->
        <van-tabs v-model:active="active" @change="onTabChange">
            <van-tab title="公开" name="0"/>
            <van-tab title="加密" name="2"/>
            <van-tab title="私有" name="1"/>
        </van-tabs>
        <div class="card-body">
            <friend-card-list v-if="teamList.length > 0" :status="parseInt(active)" :teamList="teamList"/>
            <van-empty v-if="teamList.length === 0" description="暂无聊天室"/>
        </div>
        <!--        <team-card-list :status="active" :teamList="teamList" @refreshTeamList="listTeam"/>-->
    </div>
</template>

<script setup lang="ts">

import {useRouter} from "vue-router";
import {onMounted, ref} from "vue";
import request from "../plugins/request";
import {showFailToast} from "vant";
import FriendCardList from "../components/FriendCardList.vue";

const active = ref('0')
const isDataEmpty = ref(false)
const isLoading = ref(true)
const router = useRouter();
const teamList = ref([]);
const activeOption = [
    {text: '公开', value: 0},
    {text: '加密', value: 2},
    {text: '私有', value: 1},
];
const searchText = ref("")

/**
 * 切换查询状态
 * @param name
 */
const onTabChange = () => {
    isLoading.value = true;
    listTeam(active.value);
    isLoading.value = false;
}


// 页面加载时只触发一次
onMounted(() => {
    listTeam();
    isLoading.value = false;
})

const onSearch = (val: string | undefined) => {
    isLoading.value = true;
    listTeam(active.value, val);
    isLoading.value = false;
};
// 跳转到创建房间页
const toAddTeam = () => {
    router.push({
        path: "/team/add"
    })
}

/**
 * 搜索房间
 * @param val
 * @param status
 * @returns {Promise<void>}
 */
const listTeam = async (status = '0', searchText = '') => {
    await request.get("/team/list", {
        params: {
            pageNum: 1,
            status,
            searchText
        },
    }).then(res => {
        if (res?.code === 0) {
            teamList.value = res?.data ? res?.data : [];
            if (teamList.value.length < 1) {
                isDataEmpty.value = true;
            }
        } else {
            teamList.value = []
            showFailToast(res?.message)
        }
    })
}

</script>

<style scoped>
.card-body {
    display: flex;
    flex-wrap: wrap;
    justify-content: space-around; /* 水平居中 */
}
</style>
