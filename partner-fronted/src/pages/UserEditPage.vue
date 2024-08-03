<template>
    <van-form @submit="onSubmit">
        <van-cell-group inset>
            <van-field
                v-model="editUser.currentValue"
                :name="editUser.editKey.toString()"
                :label="editUser.editName.toString()"
                :placeholder="`请输入${editUser.editName}`"
                :rules="[{ required: true, message: `请填写${editUser.editName}` }]"
            />
        </van-cell-group>
        <div style="margin: 16px;">
            <van-button color="linear-gradient(to right, #ff6034, #ee0a24)" round block type="primary"
                        native-type="submit">
                提交
            </van-button>
        </div>
    </van-form>

</template>

<script setup lang="ts">
import {useRoute, useRouter} from "vue-router";
import {ref} from "vue";
import {getCurrentUser, updateUser} from "../services/user.ts";
import {showFailToast, showSuccessToast} from "vant";

const route = useRoute()
const router = useRouter()
const {editKey, editName, currentValue} = route.query
const editUser = ref({
    editKey,
    editName,
    currentValue
})

const onSubmit = async (values) => {
    const currentUser = await getCurrentUser()
    console.log("currentUser.tags:", currentUser.tags)
    if (!currentUser) {
        showFailToast("用户未登录")
        return;
    }
    const res = await updateUser({
        'userId': currentUser.userId,
        [editUser.value.editKey]: editUser.value.currentValue
    })
    if (res.code === 0 && res.data > 0) {
        showSuccessToast("修改成功")
        router.back();
    } else {
        showFailToast("修改失败")
    }

};

console.log(route.query)

</script>

<style scoped>

</style>
