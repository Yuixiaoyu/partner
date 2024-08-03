<template>
  <template v-if="user">
    <div class="cover">
      <van-image round fit="cover" width="6rem" height="6rem" :src="user.avatarUrl" />
      <div v-if="!user.username">点击登录</div>
      <div v-if="user.username">{{ user.username }}</div>
      <div class="tagsStyle">
        <van-tag v-for="tag in tagList" style="margin: 4px" :show="true" closeable size="medium" plain color="#f03d37" @close="delTag(tag)">
          {{ tag }}
        </van-tag>
        <van-tag style="margin: 4px" :show="true" size="medium" plain color="#f03d37" @click="showAddTag"> + </van-tag>
        <!-- 底部弹出 -->
        <van-popup v-model:show="isShowAddTag" round>
          <van-cell-group inset>
            <van-field v-model="tag" center placeholder="请输入标签名">
              <template #button>
                <van-button size="small" type="danger" @click="submitAddTag">提交</van-button>
              </template>
            </van-field>
          </van-cell-group>
        </van-popup>
      </div>
    </div>
    <van-grid :column-num="3">
      <van-grid-item icon="manager-o" text="个人资料" is-link to="/user/info" />
      <van-grid-item icon="flag-o" text="创建的队伍" is-link to="/user/team/create" />
      <van-grid-item icon="friends-o" text="加入的队伍" is-link to="/user/team/join" />
      <van-grid-item icon="setting-o" text="设置" is-link to="/user/settings" />
      <van-grid-item icon="question-o" text="关于我们" is-link @click="showFailToast('开发中')" />
    </van-grid>

    <!--        <van-cell icon="setting-o" title="个人资料" is-link to="/user/team/join"/>-->
    <!--        <van-cell icon="setting-o" title="创建的队伍" is-link to="/user/team/join"/>-->
    <!--        <van-cell icon="setting-o" title="加入的队伍" is-link to="/user/team/join"/>-->
    <!--        <van-cell icon="setting-o" title="设置" is-link to="/user/team/join"/>-->
    <!--        <van-cell icon="phone-o" title="关于我们" is-link to="/user/team/join"/>-->
  </template>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getCurrentUser } from '../services/user'
import { showConfirmDialog, showFailToast, showSuccessToast } from 'vant'
import { addTag, removeTag } from '../services/tags'

const user = ref()

const router = useRouter()
const tagList = ref([])
const tag = ref('')
const isShowAddTag = ref(false)
const toEdit = (editKey: string, editName: string, currentValue: string) => {
  router.push({
    path: '/user/edit',
    query: {
      editKey,
      editName,
      currentValue,
    },
  })
}

onMounted(() => {
  loadUserInfo()
})

const loadUserInfo = async () => {
  const currentUser = await getCurrentUser()
  if (currentUser) {
    user.value = currentUser
    console.log(currentUser.tags);
    // const jsonStr =JSON.stringify(['nn', 'bb', 'vv']);
    // console.log(jsonStr);
    // console.log(JSON.parse(jsonStr));
    tagList.value = JSON.parse(currentUser.tags)
    // tagList.value = JSON.parse(JSON.stringify(currentUser.tags))
  } else {
    showFailToast('获取用户信息失败')
  }
}

/**
 * 显示添加标签输入框
 */
const showAddTag = async () => {
  isShowAddTag.value = true
}

/**
 * 添加标签
 *
 * @param tag
 */
const submitAddTag = async () => {
  const flag = await addTag({ tag: tag.value, userId: user.value.userId })
  if (flag?.code === 0 && flag.data) {
    showSuccessToast('添加成功')
    isShowAddTag.value = false
    tag.value = ''
    await loadUserInfo()
  } else {
    showFailToast('添加失败')
  }
}
/**
 * 删除标签
 *
 * @param tag
 */
const delTag = (tag: string) => {
  if (tagList.value.length > 1) {
    showConfirmDialog({
      message: `确定删除 ${tag} 标签嘛？`,
      theme: 'round-button',
    })
      .then(async () => {
        // on confirm
        const flag = await removeTag({ tag, oldTags: tagList.value, userId: user.value.userId })
        if (flag?.code === 0 && flag.data) {
          showSuccessToast('删除成功')
          await loadUserInfo()
        } else {
          showFailToast('删除失败')
        }
      })
      .catch(() => {
        // on cancel
      })
  } else {
    showFailToast('至少保留一个标签')
  }
}
</script>

<style scoped>
.cover {
  background-color: #f03d37;
  padding: 40px 0;
  text-align: center;
  color: #fff;
}

.user-top-image {
  width: 100%;
  height: 200px;
  border-bottom: 1px solid #eeeeee;
}

.tagsStyle {
  width: 100%;
  margin: 10px 0 -20px 0;
}
</style>
