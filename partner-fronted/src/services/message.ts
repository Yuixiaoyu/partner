import request from "../plugins/request";

const messageApi = {
    getMessageList: "/message/list",
    getUserHistoryMessage: "/message/getUserHistoryMessage",
    getRoomHistoryMessage: "/message/getRoomHistoryMessage",
}

export function getMessageList() {
    return request({
        url: messageApi.getMessageList,
        method: 'get'
    })
}

export function getUserHistoryMessage(fromUserId: string, toUserId: string) {
    return request({
        url: messageApi.getUserHistoryMessage,
        method: 'get',
        params: {
            fromUserId: fromUserId,
            toUserId: toUserId
        }
    })
}



export function getRoomHistoryMessage(fromUserId: any, toRoomId: any) {
    return request({
        url: messageApi.getRoomHistoryMessage,
        method: 'get',
        params: {
            fromUserId: fromUserId,
            toRoomId: toRoomId
        }
    })
}
