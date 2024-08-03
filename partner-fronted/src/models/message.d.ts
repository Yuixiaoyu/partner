/**
 * 消息类别
 */
export type MessageType = {
    cancelTime: date
    content: string,
    isCancel: number
    isRead: number,
    messageId: number,
    readTime: date,
    receiveType: number,
    avatarUrl: string,
    receiveUserId: number,
    sendTime: date,
    sendType: number,
    sendUserId: number,
    userName: string,
    type: number
};
