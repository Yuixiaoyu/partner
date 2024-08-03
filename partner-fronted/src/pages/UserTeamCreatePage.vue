<template>
    <div id="teamPage">
        <van-search v-model="searchText" placeholder="搜索房间" @search="listTeam"/>
        <!--    <van-button type="primary" @click="doJoinTeam">创建房间</van-button>-->
        <van-button color="linear-gradient(to right, #ff6034, #ee0a24)" class="add-button" type="primary" icon="plus"
                    round
                    @click="toAddTeam" block/>
        <friend-card-list :teamList="teamList"/>
        <van-empty v-if="teamList?.length < 1" description="数据为空"/>
    </div>
</template>

<script setup lang="ts">

import {useRouter} from "vue-router";
import {onMounted, ref} from "vue";
import request from "../plugins/request";
import {showFailToast} from "vant";
import FriendCardList from "../components/FriendCardList.vue";

const router = useRouter();
const searchText = ref('');

// 跳转到创建房间页
const toAddTeam = () => {
    router.push({
        path: "/team/add"
    })
}

const teamList = ref([]);

/**
 * 搜索房间
 * @param val
 * @returns {Promise<void>}
 */
const listTeam = async (val = '') => {
    const res = await request.get("/team/list/my/create", {
        params: {
            searchText: val,
            pageNum: 1,
        },
    });
    if (res?.code === 0) {
        teamList.value = res.data;
    } else {
        showFailToast('加载房间失败，请刷新重试');
    }
}


// 页面加载时只触发一次
onMounted(() => {
    listTeam();
})


</script>

<style scoped>
#teamPage {

}
</style>
