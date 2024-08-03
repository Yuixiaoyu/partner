<template>
    <template v-if="team">
        <van-row justify="center">
            <van-col span="6" style="padding: 30px 0; "><img :src="team.teamImage" alt=""
                                                             style="width: 6rem;border-radius: 50%"></van-col>
        </van-row>
        <van-cell-group inset class="my-cell-group">
            <van-cell title="房间名" :value="team.name"/>
            <van-cell title="房间描述" :value="team.description"/>
            <van-cell title="加入人数" :value="<string>team.joinNum"/>
            <van-cell title="最大人数" :value="team.maxNum"/>
            <van-cell title="房间状态" :value="team.status === 0 ? '公开' : '加密'"/>
            <van-cell title="创建人" :value="team.createUser.username"/>
            <van-cell title="创建时间" class="last-cell"
                      :value="moment(team.createTime).format('YYYY-MM-DD HH:mm:ss')"/>
            <van-button
                v-if="!isDetail"
                color="linear-gradient(to right, #ff6034, #ee0a24)" @click="doJoinTeam"
                style="width: 90%; margin: 0 auto" block>
                加入聊天室
            </van-button>
            <van-button
                v-if="isDetail"
                color="linear-gradient(to right, #ff6034, #ee0a24)" @click="doExitTeam"
                style="width: 90%; margin: 0 auto" block>
                退出聊天室
            </van-button>
        </van-cell-group>
    </template>
</template>

<script setup lang="ts">
import {onMounted, ref} from "vue";
import {useRoute, useRouter} from "vue-router";
import moment from "moment";
import {joinTeam, quitTeam} from "../services/team";

const team = ref();
const isDetail = ref();

const router = useRouter();
const route = useRoute();

onMounted(() => {
    team.value = JSON.parse(decodeURIComponent(<string>route.query.team));
    console.log(team.value);
    isDetail.value = route.query.isDetail
})

const doJoinTeam = async () => {
    // 加入队伍 -> 跳转聊天室
    await joinTeam(team.value).then((res) => {
        if (res?.code === 0) {
            // 进入聊天室界面，建立ws连接
            router.push({
                name: 'userChat',
                params: {
                    toUserId: team.value.teamId,
                    receiveType: 1,
                    status: team.value.status
                }
            })
        }
    });
}
const doExitTeam = async () => {
    // TODO: 退出队伍 -> 跳转消息首页
    await quitTeam({
        teamId: team.value.teamId
    }).then((res) => {
        if (res?.code === 0 && res.data) {
            router.push("/message")
        }
    });
}
</script>

<style scoped>
.van-cell {
    background-color: #eef0f3;
}

.van-cell__value {
    color: #ffffff;
}

.van-cell:after {
    border-bottom: 1px solid #9b9fa5;
}

.last-cell:after {
    border-bottom: none;
}

.last-cell {
    margin-bottom: 40px;
    border-bottom-left-radius: 8px;
    border-bottom-right-radius: 8px;
}
</style>
