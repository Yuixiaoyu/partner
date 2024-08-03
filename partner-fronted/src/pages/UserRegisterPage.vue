<template>
    <div class="wrapper-div">
        <div class="login-top">
            <div class="login-logo"></div>
            <div class="login-title">
                伙伴交友中心
            </div>
        </div>
        <div class="login-form-div">
            <van-form @submit="onSubmit">
                <van-field
                    v-model="userAccount"
                    placeholder="请输入账号"
                    autocomplete="off"
                    :rules="[{ required: true, message: '请输入账号', trigger: 'onSubmit' }]"
                />
                <van-field
                    v-model="userPassword"
                    type="password"
                    placeholder="请输入密码"
                    autocomplete="off"
                    :rules="[{ required: true, message: '请输入密码', trigger: 'onSubmit' }]"
                />
                <van-field
                    v-model="checkPassword"
                    type="password"
                    placeholder="请再次输入密码"
                    autocomplete="off"
                    :rules="[{ required: true, message: '请输入密码', trigger: 'onSubmit' }]"
                />
                <div @click="router.push('/user/login')"
                     style="float: right; color: #38b9fa; font-size: 14px; margin-right: 8%;">
                    已有账号，立即登陆
                </div>
                <div style="width:88%; margin: 0 auto; padding-top: 40px">
                    <van-button style="border-radius: 10px" color="linear-gradient(to right, #ff6034, #ee0a24)" block
                                :disabled="isEnableBtn"
                                type="primary" native-type="submit">
                        注册
                    </van-button>
                </div>
            </van-form>
        </div>
    </div>
</template>

<script setup lang="ts">

import {useRoute, useRouter} from "vue-router";
import {ref, watchEffect} from "vue";
// @ts-ignore
import {userLogin, userRegister} from "../services/user.ts";
import {showConfirmDialog, showFailToast, showSuccessToast} from "vant";

const route = useRoute();
const router = useRouter();

const userAccount = ref('');
const userPassword = ref('');
const checkPassword = ref('');
const isEnableBtn = ref(true);

const onSubmit = async () => {
    await userRegister({
        userAccount: userAccount.value,
        userPassword: userPassword.value,
        checkPassword: checkPassword.value
    }).then((res: any) => {
        if (res.code === 0 && res.data) {
            showSuccessToast("注册成功")
            // 跳转到之前的页面
            router.push("/user/login")
        } else {
            showFailToast(res.description ? res.description : "注册失败")
        }
    })
};

/**
 * 点击 登陆遇到问题？
 */
const showHasQuestion = () => {
    showConfirmDialog({
        message: '系统问题请联系开发者。',
        theme: 'round-button',
    }).then(() => {
        // on confirm
        // window.location.href =
    }).catch(() => {
        // on cancel
    })
}

watchEffect(() => {
    isEnableBtn.value = !(userPassword.value && userAccount.value);
})
</script>

<style scoped>
.login-top {
    width: 100%;
    height: 30%;
    margin-top: 10%;
}

.login-logo {
    margin: 0 auto;
    width: 7rem;
    height: 7rem;
    background: url("../assets/images/logo.png");
    background-size: 100% 100%;
    border-radius: 50%;
}

.login-title {
    width: 9rem;
    margin: 0 auto;
    padding: 10px 0 0 10px;
    font-size: 22px;
    font-weight: bold;
}


.van-cell {
    width: 90%;
    margin: 15px auto;
    border-radius: 10px;
}

.wrapper-div {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: #eef0f3;
}

.login-form-div {
    width: 90%;
    position: relative;
    top: 20%;
    left: 50%;
    height: 50%;
    transform: translate(-50%, -50%);
}
</style>
