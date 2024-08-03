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
                    style="border-top-left-radius: 10px; border-top-right-radius: 10px"
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
                <van-field name="stepper" label="最大人数">
                    <template #input>
                        <van-stepper v-model="addTeamData.maxNum" max="10" min="3"/>
                    </template>
                </van-field>
                <van-field name="radio" label="房间状态">
                    <template #input>
                        <van-radio-group v-model="addTeamData.status" direction="horizontal">
                            <van-radio checked-color="#ee0a24" name="0">公开</van-radio>
                            <van-radio checked-color="#ee0a24" name="1">私有</van-radio>
                            <van-radio checked-color="#ee0a24" name="2">加密</van-radio>
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

import {useRouter} from "vue-router";
import {ref} from "vue";
import request from "../plugins/request";
import {showFailToast, showSuccessToast} from "vant";
import {imageUpload} from "../services/user";

const router = useRouter();
// 展示日期选择器
const showPicker = ref(false);

const initFormData = {
    "name": "",
    "teamImage": "https://image-bed-ichensw.oss-cn-hangzhou.aliyuncs.com/006VVqOWgy1h43uaynbyxj30go0go0u7.jpg",
    "description": "",
    "expireTime": null as Date | null,
    "maxNum": 10,
    "creatorId": 0,
    "userId": 0,
    "password": "",
    "status": "0",
}

const currentDate = ref([]);

const currentTime = ref([]);

const expireTimeStr = ref('');

const minDate = new Date();

// 需要用户填写的表单数据
const addTeamData = ref({...initFormData})


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
    const res = await request.post("/team/add", postData);
    if (res?.code === 0 && res.data) {
        showSuccessToast('添加成功');
        router.push({
            path: '/team',
            replace: true,
        });
    } else {
        console.log(res?.description, res?.description.length)
        showFailToast(res?.description.length > 0 ? res?.description : '添加失败');
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

.van-cell {
    background-color: #eef0f3;
}

.van-cell__value {
    color: #ffffff;
}

.van-cell:after {
    border-bottom: 1px solid #9b9fa5;
}

.last-cell {
    margin-bottom: 40px;
    border-bottom-left-radius: 8px;
    border-bottom-right-radius: 8px;
}
</style>
