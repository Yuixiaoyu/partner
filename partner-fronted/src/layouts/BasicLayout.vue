<template>
    <van-nav-bar
            v-if="route.meta.showHeader"
            :title="title"
            :left-arrow="route.meta.showBack"
            @click-left="onClickLeft"
            @click-right="onClickRight"
    >
        <template #right v-if="title === '匹配xxx'">
            <van-icon name="search" size="18"/>
        </template>
    </van-nav-bar>
    <div id="content">
        <router-view/>
    </div>
    <van-tabbar v-if="route.meta.showBottom" route active-color="#ee0a24">
        <van-tabbar-item to="/" icon="home-o" name="index">主页</van-tabbar-item>
        <van-tabbar-item to="/team" icon="friends-o" name="team">聊天室</van-tabbar-item>
        <van-tabbar-item to="/message" icon="search" name="team">消息</van-tabbar-item>
        <van-tabbar-item to="/user" icon="user-o" name="user">个人</van-tabbar-item>
    </van-tabbar>
</template>

<script setup lang="ts">
import {onBeforeRouteLeave, useRoute, useRouter} from "vue-router";
import {onMounted, ref} from "vue";
import routes from "../config/router";

const route = useRoute();
const router = useRouter();
const DEFAULT_TITLE = '匹配摩友';
const title = ref(DEFAULT_TITLE);
/**
 * 根据路由切换标题
 */
router.beforeEach((to, from) => {
    const toPath = to.path;
    const route = routes.find((route) => {
        return toPath == route.path;
    })
    title.value = route?.title ?? DEFAULT_TITLE;
})

const onClickLeft = () => {
    router.back();
};

const onClickRight = () => {
    console.log("跳转Search")
    router.push('/search')
};

</script>

<style scoped>
#content {
    padding-bottom: 50px;
}
</style>
