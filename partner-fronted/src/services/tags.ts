import request from "../plugins/request";
import {UserType} from "../models/user";
import {LocationQueryValue} from "vue-router";

const tagsApi = {
    searchUser: '/user/search/tags',
    removeTag: '/user/tag/remove',
    addTag: '/user/tag/add',
}

export function SearchUserByTags(parameter: string | LocationQueryValue[], username: string | LocationQueryValue[], pageSize: number, pageNum: number) {
    return request({
        url: tagsApi.searchUser,
        method: 'get',
        params: {
            tagNameList: parameter,
            username: username,
            pageSize: pageSize,
            pageNum: pageNum
        }
    })
}

export function removeTag(parameter: { tag: string, oldTags: [] | unknown, userId: number } ) {
    return request({
        url: tagsApi.removeTag,
        method: 'post',
        data: parameter
    })
}


export function addTag(parameter: { tag: string, userId: number } ) {
    return request({
        url: tagsApi.addTag,
        method: 'post',
        data: parameter
    })
}
