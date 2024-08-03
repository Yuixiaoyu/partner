<template>
    <template v-if="user">
        <van-cell title="头像" center>
            <van-uploader :after-read="afterRead">
                <van-image
                    round
                    height="3rem"
                    :src="user.avatarUrl"
                >
                    <template v-slot:error>加载失败</template>
                </van-image>
            </van-uploader>
        </van-cell>

        <van-cell title="昵称" is-link to="/user/edit" :value="user.username"
                  @click="toEdit('username', '昵称', user.username)"/>
        <!--            <van-cell title="账号" :value="user.userAccount"/>-->
        <!-- 圆角弹窗（底部） -->
        <van-cell title="性别" is-link :value="user.gender === '1' ? '男' : '女'"
                  @click="showSelectSex = true"/>
        <van-cell title="电话" is-link to="/user/edit" :value="user.phone"
                  @click="toEdit('phone', '电话', user.phone)"/>
        <van-cell title="邮箱" is-link to="/user/edit" :value="user.email"
                  @click="toEdit('email', '邮箱', user.email)"/>
        <!--            <van-cell title="注册时间" :value="moment(user.createTime).format('YYYY-MM-DD HH:mm:ss')"/>-->
        <van-popup
            v-model:show="showSelectSex"
            round
            :style="{ height: '20%', width: '80%' }"
        >
            <van-radio-group v-model="user.gender" style="margin-top: 5%;">
                <van-cell-group inset>
                    <van-cell title="男" clickable @click="changeSex('1')">
                        <template #right-icon>
                            <van-radio name="1"/>
                        </template>
                    </van-cell>
                    <van-cell title="女" clickable @click="changeSex('2')">
                        <template #right-icon>
                            <van-radio name="2"/>
                        </template>
                    </van-cell>
                </van-cell-group>
            </van-radio-group>
        </van-popup>
    </template>
</template>

<script setup lang="ts">
import {useRouter} from "vue-router";
import {onMounted, ref} from "vue";
import {getCurrentUser, imageUpload, updateUser} from "../services/user";
import {showFailToast} from "vant";

const user = ref();
const showSelectSex = ref(false)
const fileList = ref([])

const router = useRouter();

onMounted(async () => {
    user.value = await getCurrentUser();
    user.value.gender += '';

})

const changeSex = async (sex: string) => {
    user.value.gender = sex
    await updateUser(user.value).catch(error => {
        console.log(error)
        showFailToast("请求失败")
    })
    showSelectSex.value = false
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

const toEdit = (editKey: string, editName: string, currentValue: string) => {
    router.push({
        path: '/user/edit',
        query: {
            editKey,
            editName,
            currentValue,
        }
    })
}

</script>

<style scoped>

</style>
