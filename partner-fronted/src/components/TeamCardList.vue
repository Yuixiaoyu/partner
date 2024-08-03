<template>
    <div id="teamCardList">
        <van-card
                v-for="team in props.teamList"
                :thumb="ikun"
                :desc="team.description"
                :title="`${team.name}`"
        >
            <template #tags>
                <van-tag plain type="danger" style="margin-right: 8px; margin-top: 8px">
                    {{ teamStatusEnum[team.status] }}
                </van-tag>
            </template>
            <template #bottom>
                <div>
                    {{ `房间人数: ${team.joinNum}/${team.maxNum}` }}
                </div>
                <div v-if="team.expireTime">
                    {{ '过期时间: ' + moment(team.expireTime).format("YYYY-MM-DD HH:mm:ss") }}
                </div>
                <div>
                    {{ '创建时间: ' + moment(team.createTime).format("YYYY-MM-DD HH:mm:ss") }}
                </div>
            </template>
            <template #footer>
                <van-button size="small" type="primary"
                            v-if="team.userId !== currentUser?.userId && !team.hasJoin && team.maxNum > team.joinNum"
                            plain
                            @click="preJoinTeam(team)">
                    加入房间
                </van-button>
                <van-button size="small" type="danger" disabled
                            v-if="team.userId !== currentUser?.userId && !team.hasJoin && team.maxNum <= team.joinNum"
                            plain>
                    房间已满员
                </van-button>
                <van-button v-if="team.userId === currentUser?.userId" size="small" plain
                            @click="doUpdateTeam(team.teamId)">更新房间
                </van-button>
                <!-- 仅加入房间可见 -->
                <van-button v-if="team.userId !== currentUser?.userId && team.hasJoin" size="small" plain
                            @click="doQuitTeam(team.teamId)">退出房间
                </van-button>
                <van-button v-if="team.userId === currentUser?.userId" size="small" type="danger" plain
                            @click="doDeleteTeam(team.teamId)">解散房间
                </van-button>
            </template>
        </van-card>
        <van-dialog v-model:show="showPasswordDialog" title="请输入密码" show-cancel-button @confirm="doJoinTeam"
                    @cancel="doJoinCancel">
            <van-field v-model="password" placeholder="请输入密码"/>
        </van-dialog>
    </div>

</template>

<script setup lang="ts">
import {TeamType} from "../models/team";
import {teamStatusEnum} from "../constants/team";
import ikun from '../assets/images/ikun.png';
import request from "../plugins/request";
import {showFailToast, showSuccessToast} from "vant";
import {onMounted, ref} from "vue";
import {getCurrentUser} from "../services/user";
import {useRouter} from "vue-router";
import moment from "moment";
import {joinTeam} from "../services/team";

interface TeamCardListProps {
    teamList: TeamType[];
    status: string;
}

const props = withDefaults(defineProps<TeamCardListProps>(), {
    // @ts-ignore
    teamList: [] as TeamType[],
    status: '0' as string,
});

const showPasswordDialog = ref(false);
const password = ref('');
const joinTeamId = ref(0);
const currentUser = ref();


const router = useRouter();

const refreshTeamListEmit = defineEmits(['refreshTeamList'])

onMounted(async () => {
    currentUser.value = await getCurrentUser();
})

const preJoinTeam = (team: TeamType) => {
    joinTeamId.value = team.teamId;
    if (team.status === 0) {
        doJoinTeam()
    } else {
        showPasswordDialog.value = true;
    }
}

const doJoinCancel = () => {
    joinTeamId.value = 0;
    password.value = '';
}

/**
 * 加入房间
 */
const doJoinTeam = async () => {
    console.log()
    if (!joinTeamId.value) {
        return;
    }
    joinTeam({
        teamId: joinTeamId.value,
        password: password.value
    }).then(res => {
        if (res?.code === 0 && res?.data) {
            refreshTeamListEmit('refreshTeamList', props.status);
            showSuccessToast('加入成功');
            doJoinCancel();
        } else {
            showFailToast('加入失败' + (res.description ? `，${res.description}` : ''));
        }
    }).catch(error => {
        console.log(error)
        showFailToast('请求失败');
    })
}

/**
 * 跳转至更新房间页
 * @param id
 */
const doUpdateTeam = (id: number) => {
    router.push({
        path: '/team/update',
        query: {
            id,
        }
    })
}

/**
 * 退出房间
 * @param id
 */
const doQuitTeam = async (id: number) => {
    const res = await request.post('/team/quit', {
        teamId: id
    });
    if (res?.code === 0) {
        refreshTeamListEmit('refreshTeamList', props.status);
        showSuccessToast('退出成功');
    } else {
        showFailToast('操作失败' + (res.description ? `，${res.description}` : ''));
    }
}

/**
 * 解散房间
 * @param id
 */
const doDeleteTeam = async (id: number) => {
    const res = await request.post('/team/delete', {
        id
    });
    if (res?.code === 0) {
        showSuccessToast('操作成功');
    } else {
        showFailToast('操作失败' + (res.description ? `，${res.description}` : ''));
    }
}

</script>

<style scoped>
#teamCardList :deep(.van-image__img) {
    height: 128px;
    object-fit: unset;
}
</style>
