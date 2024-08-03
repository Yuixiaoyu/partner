<template>
    <van-cell title="用户协议" size="large" is-link @click="showFailToast('开发中')"/>
    <van-cell title="隐私政策" size="large" is-link @click="showFailToast('开发中')"/>
    <van-button color="linear-gradient(to right, #ff6034, #ee0a24)" @click="exitLogin" round
                style="width: 90%; margin: 30px auto" block>
        退出登录
    </van-button>
</template>

<script setup lang="ts">

import {userLogout} from "../services/user";
import {showFailToast, showSuccessToast} from "vant";
import {useRouter} from "vue-router";

const router = useRouter();
const exitLogin = async () => {
    // TODO: 退出登录实现
    const res = await userLogout();
    if (res?.code === 0) {
        showSuccessToast("退出登陆成功")
        await router.push({
            path: '/user/login'
        })
    }
}
</script>

<style scoped>

</style>
