const WXAPI = require('../../wxapi/main')
var app = getApp();
Page({

  /**
   * 页面的初始数据
   */
  data: {
      canIUse: wx.canIUse('button.open-type.getUserInfo'),

  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function(options) {

  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function() {

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function() {

  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide: function() {

  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload: function() {

  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh: function() {

  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function() {

  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function() {

  },
  bindGetUserInfo: function(e) {
      var that = this;
      if (e.detail.userInfo) {
          //用户按了允许授权按钮         
          wx.setStorageSync('userInfo', e.detail);
          that.login();
         
      } else {
          //用户按了拒绝按钮
      }
  },
  login: function() {
      var that = this;
      wx.login({
          success: res => {
              // 发送 res.code 到后台换取 openId, sessionKey, unionId
              let code = res.code;
              WXAPI.login(code).then(function (res) {
                  if (res.code == 1001) {
                      wx.setStorageSync('tk', res.data); 
                      that.registerUser();
                  }else{
                    wx.setStorageSync('token', res.data); 
                  }
                  wx.navigateBack();
              })
          }
      })
  },
  registerUser: function() {
      let res1 = wx.getStorageSync('userInfo');
      let iv = res1.iv;
      let encryptedData = res1.encryptedData;
      let tk = wx.getStorageSync('tk');   
      // 下面开始调用注册接口
      WXAPI.register({
          sessionId: tk,
          encryptedData: encryptedData,
          iv: iv,
          appId: 'wxa192d06cd30b94b7',
          apiName: 'WX_DECODE_USERINFO'
      }).then(function (res) {
        console.log("重新保存" + res.data);
         wx.setStorageSync('token', res.data); 
      })
  }

})