<template>
    <template v-if="user">
        <van-cell title="头像" is-link size="large" style="align-items: center">
            <van-uploader :after-read="afterRead">
                <van-image
                    round
                    height="40px"
                    :src="user.avatarUrl"
                />
            </van-uploader>
        </van-cell>

        <van-cell title="昵称" size="large" is-link to="/user/edit" :value="user.username"
                  @click="toEdit('username', '昵称', user.username)"/>
        <van-cell title="账号" size="large" :value="user.userAccount"/>

        <van-cell title="性别" size="large" is-link :value="user.gender === 1 ? '男' : '女'"
                  @click="toEdit('gender', '性别', user.gender)"/>
        <van-cell title="电话" size="large" is-link to="/user/edit" :value="user.phone"
                  @click="toEdit('phone', '电话', user.phone)"/>
        <van-cell title="邮箱" size="large" is-link to="/user/edit" :value="user.email"
                  @click="toEdit('email', '邮箱', user.email)"/>
        <van-cell title="注册时间" size="large" :value="moment(user.createTime).format('YYYY-MM-DD HH:mm:ss')"/>
    </template>
</template>

<script setup lang="ts">
import {onMounted, ref} from "vue";
import {useRouter} from "vue-router";
import {getCurrentUser, imageUpload, userLogout} from "../services/user";
import {showFailToast, showSuccessToast} from "vant";
import moment from "moment";

const user = ref();

const router = useRouter();
const toEdit = (editKey: string, editName: string, currentValue: string) => {
    router.push({
        path: '/user/edit',
        query: {
            editKey,
            editName,
            currentValue
        }
    })
}

onMounted(async () => {
    const currentUser = await getCurrentUser()
    if (currentUser) {
        user.value = currentUser;
    } else {
        showFailToast("获取用户信息失败")
    }
})

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


const afterRead = async (file: any) => {
    file.status = 'uploading';
    file.message = '上传中';

    // 此时可以自行将文件上传至服务器
    let formData = new FormData();
    formData.append("file", file.file);
    await imageUpload(formData).then(res => {
        console.log(res)
        if (res?.code === 0) {
            user.value.avatarUrl = res.data.url
        } else {
            file.status = 'failed';
            file.message = '上传失败';
            showFailToast('上传失败，' + (res.description ? `，${res.description}` : ''));
        }
    }).catch(error => {
        console.log("错误信息：", error)
        showFailToast('请求失败');

    })
};
</script>

<style scoped>
.cover {
    background-color: #f03d37;
    padding: 40px;
    text-align: center;
    color: #fff;
}

.user-top-image {
    width: 100%;
    height: 200px;
    border-bottom: 1px solid #eeeeee;
}

</style>
