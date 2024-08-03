<template>
  <div class="wrapper-div">
    <div class="login-top">
      <div class="login-logo"></div>
      <div class="login-title">伙伴交友中心</div>
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

        <div @click="goRegister" style="float: left; color: #38b9fa; font-size: 14px; margin-left: 6%">没有账号，立即注册</div>
        <div @click="showHasQuestion" style="float: right; color: darkgray; font-size: 14px; margin-right: 2%">登陆遇到问题？</div>
        <div style="width: 88%; margin: 0 auto; padding-top: 35px">
          <van-button
            style="border-radius: 10px"
            color="linear-gradient(to right, #ff6034, #ee0a24)"
            block
            :disabled="isEnableBtn"
            type="primary"
            native-type="submit"
          >
            登陆
          </van-button>
        </div>
      </van-form>
    </div>

    <!-- 圆角弹窗（居中） -->
    <van-popup v-model:show="showWx">
      <van-image width="300" height="300" src="https://s1.ax1x.com/2023/07/15/pC5dhPx.jpg" />
    </van-popup>
  </div>
</template>

<script setup lang="ts">
import { useRoute, useRouter } from 'vue-router'
import { ref, watchEffect } from 'vue'
// @ts-ignore
import { userLogin } from '../services/user.ts'
import { showConfirmDialog, showFailToast, showSuccessToast } from 'vant'

const route = useRoute()
const router = useRouter()

const userAccount = ref('')
const userPassword = ref('')
const isEnableBtn = ref(true)
const showWx = ref(false)

const onSubmit = async () => {
  await userLogin({
    userAccount: userAccount.value,
    userPassword: userPassword.value,
  }).then((res: any) => {
    console.log("登录信息",res)
    if (res.code === 0 && res.data) {
      showSuccessToast('登陆成功')
      // 跳转到之前的页面
      window.location.href = (route.query?.redirect as string) ?? '/'
    } else {
      showFailToast(res.description ? res.description : '登录失败')
    }
  })
}

/**
 * 跳转注册页面
 */
const goRegister = () => {
  router.push('/user/register')
}

/**
 * 点击 登陆遇到问题？
 */
const showHasQuestion = () => {
  showConfirmDialog({
    message: '系统问题请联系开发者wx:Hyer-z\n（虽然联系他也不会理你，哈哈哈~）',
    theme: 'round-button',
    confirmButtonText: '联系开发者',
    showCancelButton: false,
  })
    .then(() => {
      // on confirm
      showWx.value = true
      // window.location.href = "https://s1.ax1x.com/2023/07/15/pC5d328.jpg"
      // window.location.href = "https://yupao-project-ichensw.oss-cn-hangzhou.aliyuncs.com/1689404487192.jpg"
    })
    .catch(() => {
      showWx.value = false
      // on cancel
    })
}

watchEffect(() => {
  isEnableBtn.value = !(userPassword.value && userAccount.value)
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
  background: url('../assets/images/logo.png');
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
