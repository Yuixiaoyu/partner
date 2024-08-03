<template>
    <div id="teamAddPage">
        <van-form @submit="onSubmit">
            <van-cell-group inset>
                <van-uploader class="my-van-image" :after-read="afterRead">
                    <van-image
                        round
                        height="5rem"
                        :src="addTeamData.teamImage"
                    >
                        <template v-slot:error>加载失败</template>
                    </van-image>
                </van-uploader>
                <van-field
                    v-model="addTeamData.name"
                    name="name"
                    label="房间名"
                    placeholder="请输入房间名"
                    :rules="[{ required: true, message: '请输入房间名' }]"
                />
                <van-field
                    v-model="addTeamData.description"
                    rows="4"
                    autosize
                    label="房间描述"
                    type="textarea"
                    placeholder="请输入房间描述"
                />
                <van-field
                    is-link
                    readonly
                    name="datetimePicker"
                    label="过期时间"
                    :placeholder="expireTimeStr ?? '点击选择过期时间'"
                    @click="showPicker = true"
                />
                <van-popup v-model:show="showPicker" position="bottom">
                    <van-picker-group
                        title="请选择过期时间"
                        :tabs="['选择日期', '选择时间']"
                        @confirm="onConfirm"
                        @cancel="showPicker = false"
                    >
                        <van-date-picker
                            v-model="currentDate"
                            type="datetime"
                            title="请选择过期时间"
                            :min-date="minDate"
                        />
                        <van-time-picker v-model="currentTime"/>
                    </van-picker-group>
                </van-popup>
                <van-field name="radio" label="房间状态">
                    <template #input>
                        <van-radio-group v-model="addTeamData.status" direction="horizontal">
                            <van-radio name="0">公开</van-radio>
                            <van-radio name="1">私有</van-radio>
                            <van-radio name="2">加密</van-radio>
                        </van-radio-group>
                    </template>
                </van-field>
                <van-field
                    v-if="Number(addTeamData.status) === 2"
                    v-model="addTeamData.password"
                    type="password"
                    name="password"
                    label="密码"
                    placeholder="请输入房间密码"
                    :rules="[{ required: true, message: '请填写密码' }]"
                />
            </van-cell-group>
            <div style="margin: 16px;">
                <van-button color="linear-gradient(to right, #ff6034, #ee0a24)" round block type="primary"
                            native-type="submit">
                    提交
                </van-button>
            </div>
        </van-form>
    </div>
</template>

<script setup lang="ts">

import {useRoute, useRouter} from "vue-router";
import {onMounted, ref} from "vue";
import request from "../plugins/request";
import {showFailToast, showSuccessToast} from "vant";
import moment from "moment";
import {imageUpload} from "../services/user";

const router = useRouter();
const route = useRoute();

// 展示日期选择器
const showPicker = ref(false);

const minDate = new Date();

const currentDate = ref([]);
const currentTime = ref([]);
const expireTimeStr = ref('');

const id = route.query.id;

// 需要用户填写的表单数据
const addTeamData = ref({})

// 获取之前的房间信息
onMounted(async () => {
        if (Number(id) <= 0) {
            showFailToast('加载房间失败');
            return;
        }
        const res = await request.get("/team/get", {
            params: {
                id,
            }
        });
        if (res?.code === 0) {
            const {status, expireTime} = res.data;
            addTeamData.value = res.data;
            addTeamData.value.status = status + '';
            expireTimeStr.value = moment(expireTime).format("YYYY/MM/DD HH:mm");
        } else {
            showFailToast('加载房间失败，请刷新重试');
        }
    }
)


const afterRead = async (file: any) => {
    file.status = 'uploading';
    file.message = '上传中';

    // 此时可以自行将文件上传至服务器
    let formData = new FormData();
    formData.append("file", file.file);
    await imageUpload(formData).then(res => {
        console.log(res)
        if (res?.code === 0) {
            addTeamData.value.teamImage = res.data.url
        } else {
            file.status = 'failed';
            file.message = '上传失败';
            showFailToast('上传失败，' + (res.description ? `，${res.description}` : ''));
        }
    }).catch(error => {
        showFailToast('请求失败');
    })
};

const onConfirm = async () => {
    showPicker.value = false;
    let dateTime = currentDate.value.join('/') + ' ' + currentTime.value.join(':')
    addTeamData.value.expireTime = new Date(dateTime)
    expireTimeStr.value = dateTime
}

// 提交
const onSubmit = async () => {
    const postData = {
        ...addTeamData.value,
        status: Number(addTeamData.value.status)
    }
    // todo 前端参数校验
    const res = await request.put("/team/update", postData);
    if (res?.code === 0 && res.data) {
        showSuccessToast('更新成功');
        router.push({
            path: '/team',
            replace: true,
        });
    } else {
        showSuccessToast('更新失败');
    }
}
</script>

<style scoped>
.my-van-image {
    display: flex;
    justify-content: center;
    align-items: center;
    margin: 30px 0;
}
</style>
