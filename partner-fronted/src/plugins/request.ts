import axios from 'axios';
import qs from "qs";
import {showFailToast} from "vant";

// 创建实例时配置默认值
const request = axios.create({
    // baseURL: 'http://42.192.149.3:8801/api/',
    baseURL: 'http://localhost:8080/api/',
    paramsSerializer: {
        serialize:function(params) {
            return qs.stringify(params, { arrayFormat: 'repeat' })
        }
    }
});

// true：向后台发送请求时，携带上cookie凭证
request.defaults.withCredentials = true

// 添加请求拦截器
request.interceptors.request.use(function (config) {
    // 在发送请求之前做些什么
    return config;
}, function (error) {
    // 对请求错误做些什么
    return Promise.reject(error);
});

// 添加响应拦截器
request.interceptors.response.use(function (response) {
    // console.log('我收到你的响应啦', response)
    // 未登录则跳转到登录页
    if (response?.data?.code === 40100) {
        showFailToast("未登录")
        const redirectUrl = window.location.href;
        window.location.href = `/user/login?redirect=${redirectUrl}`;
    }
    // Do something with response data
    return response.data;
}, function (error) {
    // Do something with response error
    return Promise.reject(error);
});

export default request
