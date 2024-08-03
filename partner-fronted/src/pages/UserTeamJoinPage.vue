<template>
    <div id="teamPage">
        <van-search v-model="searchText" placeholder="搜索房间" @search="listTeam"/>
        <friend-card-list :teamList="teamList"/>
        <van-empty v-if="teamList?.length < 1" description="数据为空"/>
    </div>
</template>

<script setup lang="ts">

import {useRouter} from "vue-router";
import {onMounted, ref} from "vue";
import {showFailToast} from "vant";
import {listMyJoinTeam} from "../services/team";
import FriendCardList from "../components/FriendCardList.vue";

const router = useRouter();
const searchText = ref('');

const teamList = ref([]);

// 页面加载时只触发一次
onMounted(() => {
    listTeam();
})

/**
 * 搜索房间
 * @param searchText
 * @returns {Promise<void>}
 */
const listTeam = async (searchText = '') => {
    console.log("searchText: ", searchText)
    listMyJoinTeam(searchText).then(res => {
        if (res?.code === 0) {
            console.log("请求成功：", res)
            teamList.value = res.data;
        } else {
            showFailToast(res.message);
        }
    }).catch(error => {
        console.log(error)
        showFailToast('加载房间失败，请刷新重试');
    });
}


</script>

<style scoped>
#teamPage {

}
</style>
