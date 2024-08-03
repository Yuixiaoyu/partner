// 定义一些路由
import Index from "../pages/Index.vue";
import Team from "../pages/TeamPage.vue";
import UserPage from "../pages/UserPage.vue";
import UserUpdatePage from "../pages/UserUpdatePage.vue";
import UserTeamJoinPage from "../pages/UserTeamJoinPage.vue";
import UserTeamCreatePage from "../pages/UserTeamCreatePage.vue";
import SearchPage from "../pages/SearchPage.vue";
import SearchResultPage from "../pages/SearchResultPage.vue";
import UserEditPage from "../pages/UserEditPage.vue";
import UserLoginPage from "../pages/UserLoginPage.vue";
import TeamAddPage from "../pages/TeamAddPage.vue";
import TeamUpdatePage from "../pages/TeamUpdatePage.vue";
import MessagePage from "../pages/MessagePage.vue";
import TeamDetailPage from "../pages/TeamDetailPage.vue";
import UserInfoPage from "../pages/UserInfoPage.vue";
import UserSettingPage from "../pages/UserSettingPage.vue";
import UserAboutPage from "../pages/UserAboutPage.vue";
import UserChatPage from "../pages/UserChatPage.vue";
import UserRegisterPage from "../pages/UserRegisterPage.vue";


const routes = [
    {
        path: '/',
        meta: {showBottom: true, showHeader: false, showBack: false},
        component: Index
    },
    {
        path: '/team',
        title: '聊天室',
        meta: {showBottom: true, showHeader: true, showBack: false},
        component: Team
    },
    {
        path: '/team/add',
        title: '创建房间',
        meta: {showBottom: true, showHeader: true, showBack: false},
        component: TeamAddPage
    },
    {
        path: '/team/update',
        title: '更新房间',
        meta: {showBottom: true, showHeader: true, showBack: true},
        component: TeamUpdatePage
    },
    {
        path: '/team/detail',
        title: '房间信息',
        meta: {showBottom: true, showHeader: true, showBack: true},
        component: TeamDetailPage
    },
    {
        path: '/user',
        title: '个人信息',
        meta: {showBottom: true, showHeader: false, showBack: false},
        component: UserPage
    },
    {
        path: '/search',
        title: '寻找小伙伴',
        meta: {showBottom: false, showHeader: true, showBack: true},
        component: SearchPage
    },
    {
        path: '/user/list',
        title: '伙伴列表',
        meta: {showBottom: false, showHeader: true, showBack: true},
        component: SearchResultPage
    },
    {
        path: '/user/edit',
        title: '编辑信息',
        meta: {showBottom: true, showHeader: false, showBack: false},
        component: UserEditPage
    },
    {
        path: '/user/login',
        title: '登录',
        meta: {showBottom: false, showHeader: false, showBack: false},
        component: UserLoginPage
    },
    {
        path: '/user/register',
        title: '注册',
        meta: {showBottom: false, showHeader: false, showBack: false},
        component: UserRegisterPage
    },
    {
        path: '/user/update',
        title: '更新信息',
        meta: {showBottom: true, showHeader: false, showBack: false},
        component: UserUpdatePage
    },
    {
        path: '/user/team/join',
        title: '加入的房间',
        meta: {showBottom: true, showHeader: true, showBack: true},
        component: UserTeamJoinPage
    },
    {
        path: '/user/team/create',
        title: '创建的房间',
        meta: {showBottom: true, showHeader: true, showBack: true},
        component: UserTeamCreatePage
    },
    {
        path: '/message',
        title: '我的消息',
        meta: {showBottom: true, showHeader: true, showBack: false},
        component: MessagePage
    },
    {
        path: '/user/info',
        title: '个人资料',
        meta: {showBottom: true, showHeader: true, showBack: true},
        component: UserInfoPage
    },
    {
        path: '/user/settings',
        title: '设置',
        meta: {showBottom: true, showHeader: true, showBack: true},
        component: UserSettingPage
    },
    {
        path: '/user/about',
        title: '关于我们',
        meta: {showBottom: true, showHeader: true, showBack: true},
        component: UserAboutPage
    },
    {
        name: 'userChat',
        path: '/user/chat/:toUserId/:receiveType/:status',
        title: '聊天页面',
        meta: {showBottom: false, showHeader: false, showBack: false},
        component: UserChatPage
    },
]

export default routes;
