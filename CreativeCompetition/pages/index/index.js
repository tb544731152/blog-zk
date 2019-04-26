//index.js
//获取应用实例
const app = getApp()
const WXAPI = require('../../wxapi/main')

// Page({
//   data: {
//     motto: 'Hello World',
//     userInfo: {},
//     hasUserInfo: false,
//     canIUse: wx.canIUse('button.open-type.getUserInfo')
//   },
//   //事件处理函数
//   bindViewTap: function() {
//     wx.navigateTo({
//       url: '../logs/logs'
//     })
//   },
//   onLoad: function () {
//     if (app.globalData.userInfo) {
//       this.setData({
//         userInfo: app.globalData.userInfo,
//         hasUserInfo: true
//       })
//     } else if (this.data.canIUse){
//       // 由于 getUserInfo 是网络请求，可能会在 Page.onLoad 之后才返回
//       // 所以此处加入 callback 以防止这种情况
//       app.userInfoReadyCallback = res => {
//         this.setData({
//           userInfo: res.userInfo,
//           hasUserInfo: true
//         })
//       }
//     } else {
//       // 在没有 open-type=getUserInfo 版本的兼容处理
//       wx.getUserInfo({
//         success: res => {
//           app.globalData.userInfo = res.userInfo
//           this.setData({
//             userInfo: res.userInfo,
//             hasUserInfo: true
//           })
//         }
//       })
//     }
//   },
//   getUserInfo: function(e) {
//     console.log(e)
//     app.globalData.userInfo = e.detail.userInfo
//     this.setData({
//       userInfo: e.detail.userInfo,
//       hasUserInfo: true
//     })
//   }
// })

Page({
    data: {
        //判断小程序的API，回调，参数，组件等是否在当前版本可用。
        canIUse: wx.canIUse('button.open-type.getUserInfo'),
        sessionId: '',
    },
    onLoad: function() {
        var that = this;
        //登录
        wx.login({
            success: function(res) {
                let code = res.code;
                WXAPI.login(code).then(function(res) {
                   console.log(res)                   
                  
                    wx.setStorageSync('sessionId', res.data)
                })

            }
        })
        // 查看是否授权
        wx.getSetting({
            success: function (res) {
                if (res.authSetting['scope.userInfo']) {
                    console.log('授权了')
                    wx.getUserInfo({
                        success: function (res) {
                            console.log(res);
                            let iv = res.iv;
                            let encryptedData = res.encryptedData;
                            let sessionId = wx.getStorageSync('token');
                            let referrer_storge = wx.getStorageSync('referrer');
                            if (referrer_storge) {
                                referrer = referrer_storge;
                            }
                            // console.log(sessionId)
                            // console.log(encryptedData)
                            // console.log(iv);
                            // 下面开始调用注册接口
                            WXAPI.register({
                                sessionId: sessionId,
                                encryptedData: encryptedData,
                                iv: iv,
                                appId: 'wx4a29790198c4777a',
                                apiName: 'WX_DECODE_USERINFO'
                            }).then(function (res) {
                                console.log(res);
                                wx.hideLoading();
                                //that.login();
                            })
                        }
                    })
                }else{
                    console.log('没有授权');
                }
            }
        })



      
   

    },
    bindGetUserInfo: function(e) {
        console.log(e.detail.userInfo)
        if (e.detail.userInfo) {
            //用户按了允许授权按钮
            console.log('同意')
        } else {
            //用户按了拒绝按钮
        }
    }
})