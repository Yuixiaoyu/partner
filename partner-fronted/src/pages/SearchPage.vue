<template>
    <form action="/">
        <van-search
            v-model="searchText"
            show-action
            placeholder="请输入搜索关键词"
            @search="onSearch"
            @cancel="onCancel"
        >
            <template #action>
                <div type="primary" @click="doSearchResult">搜索</div>
            </template>
        </van-search>
    </form>
    <van-divider content-position="left">已选标签</van-divider>
    <div v-if="activeIds.length === 0">请选择标签</div>
    <van-row :gutter="16">
        <van-col v-for="tag in activeIds" style="padding: 5px 10px 5px 0px">
            <van-tag closeable size="medium" type="primary" @close="close(tag)">
                {{ tag }}
            </van-tag>
        </van-col>
    </van-row>
    <van-divider content-position="left">选择标签</van-divider>
    <van-tree-select
        v-model:active-id="activeIds"
        v-model:main-active-index="activeIndex"
        :items="tagList"
        style="width: 100%; height: 100%; position: fixed"
    />

</template>

<script setup lang="ts">
import {ref} from "vue";
import {useRouter} from "vue-router";

const searchText = ref('');
// 已选中的标签
const activeIds = ref([]);
// 父级标签
const activeIndex = ref(0);
// router
const router = useRouter();
// 标签集合
const originTextList = [
    {
        text: '性别',
        children: [
            {text: '男', id: '男'},
            {text: '女', id: '女'},
        ],
    },
    // {
    //   text: '车型',
    //   children: [
    //     {text: '春风250SR', id: '春风250SR'},
    //     {text: '豪爵铃木GSX250R', id: '豪爵铃木GSX250R'},
    //     {text: '春风450SR', id: '春风450SR'},
    //     {text: '凯越321RR', id: '凯越321RR'},
    //     {text: '雅马哈YZF-R3', id: '雅马哈YZF-R3'},
    //     {text: '摩枭500RR', id: '摩枭500RR'},
    //     {text: '力帆KPR150', id: '力帆KPR150'},
    //     {text: '川崎Ninja400', id: '川崎Ninja400'},
    //     {text: '奔达金吉拉', id: '奔达金吉拉'},
    //     {text: '川崎NinjaH2', id: '川崎NinjaH2'},
    //     {text: '宝马G310RR', id: '宝马G310RR'},
    //     {text: '五羊本田NX125', id: '五羊本田NX125'},
    //     {text: '豪爵TR300', id: '豪爵TR300'},
    //     {text: 'QJMOTOR赛600', id: 'QJMOTOR赛600'},
    //     {text: '春风250NK', id: '春风250NK'},
    //     {text: '川崎Z400', id: '川崎Z400'},
    //     {text: '豪爵铃木骊驰GW250', id: '豪爵铃木骊驰GW250'},
    //     {text: '春风150NK', id: '春风150NK'},
    //     {text: '春风800NK', id: '春风800NK'},
    //     {text: '五羊本田帅影150', id: '五羊本田帅影150'},
    //     {text: '雅马哈MT-03', id: '雅马哈MT-03'}
    //   ],
    // },
    {
        text: '语言',
        children: [
            {text: 'c++', id: 'c++'},
            {text: 'python', id: 'python'},
            {text: 'java', id: 'java'},
            {text: 'go', id: 'go'}
        ],
    }
]
const tagList = ref(originTextList);
const onSearch = (val) => {
    router.push({
        path: '/user/list',
        query: {
            tags: activeIds.value,
            username: searchText.value
        }
    })
    /*// 数据扁平化：将所有children集合拼接在一起返回新集合
    tagList.value = originTextList
        .map(parentTag => {
          const tempParentTag = {...parentTag}
          tempParentTag.children = tempParentTag.children.filter(item => item.text.includes(searchText.value))
          return tempParentTag;
        })*/
};
const onCancel = () => {
    searchText.value = ''
    tagList.value = originTextList
}
const close = (tag) => {
    activeIds.value = activeIds.value.filter(item => {
        return item !== tag
    })
}

const doSearchResult = () => {
    router.push({
        path: '/user/list',
        query: {
            tags: activeIds.value,
            username: searchText.value
        }
    })
}
</script>

<style lang="scss" scoped>
</style>
