//app.js
const WXAPI = require('wxapi/main')
App({
  navigateToLogin: false,
  onLaunch: function () {
    const that = this;
    const updateManager = wx.getUpdateManager()
    // 检测新版本
    updateManager.onUpdateReady(function () {
      wx.showModal({
        title: '更新提示',
        content: '新版本已经准备好，是否重启应用？',
        success(res) {
          if (res.confirm) {
            // 新的版本已经下载好，调用 applyUpdate 应用新版本并重启
            updateManager.applyUpdate()
          }
        }
      })
    })
    /**
     * 初次加载判断网络情况
     * 无网络状态下根据实际情况进行调整
     */
    wx.getNetworkType({
      success(res) {
        const networkType = res.networkType
        if (networkType === 'none') {
          that.globalData.isConnected = false
          wx.showToast({
            title: '当前无网络',
            icon: 'loading',
            duration: 2000
          })
        }
      }
    });
    /**
     * 监听网络状态变化
     * 可根据业务需求进行调整
     */
    wx.onNetworkStatusChange(function (res) {
      if (!res.isConnected) {
        that.globalData.isConnected = false
        wx.showToast({
          title: '网络已断开',
          icon: 'loading',
          duration: 2000,
          complete: function () {
            that.goStartIndexPage()
          }
        })
      } else {
        that.globalData.isConnected = true
        wx.hideToast()
      }
    });
    //判断是ios还是android  
    wx.getSystemInfo({
          success: function (res) {
              if (res.platform == "devtools") {
                } else if (res.platform == "ios") {
                  wx.loadFontFace({
                      family: 'FontRegular',
                      source: 'url("https://wxapp.1zzs.cc/PingFang Medium.ttf")',
                      success: console.log
                  })

                  wx.loadFontFace({
                      family: 'FontLight',
                      source: 'url("https://wxapp.1zzs.cc/PingFang Regular.ttf")',
                      success: console.log
                  })

                } else if (res.platform == "android") {
                  wx.loadFontFace({
                      family: 'FontRegular',
                      source: 'url                         ("https://wxapp.1zzs.cc/SourceHanSansCN-Regular.otf")',
                      success: console.log
                  })

                  wx.loadFontFace({
                      family: 'FontLight',
                      source: 'url("https://wxapp.1zzs.cc/SourceHanSansCN-Light.otf")',
                      success: console.log
                  })

                }

          }
      })
    
  },
  goLoginPageTimeOut: function () {
    if (this.navigateToLogin) {
      return
    }
    wx.removeStorageSync('token')
    this.navigateToLogin = true
    setTimeout(function () {
      wx.navigateTo({
        url: "/pages/authorize/index"
      })
    }, 1000)
  },
  goStartIndexPage: function () {
    setTimeout(function () {
      wx.redirectTo({
        url: "/pages/competition/competition"
      })
    }, 1000)
  },
  onShow(e) {
     /** 暂时不做任何处理
    const _this = this
    const token = wx.getStorageSync('token');
    console.log(token)
    if (!token) {
      _this.goLoginPageTimeOut()
      return
    }
   
     * token 检查
    WXAPI.checkToken(token).then(function (res) {
      console.log(res)
      if (res.code != 1000) {
        wx.removeStorageSync('token')
        _this.goLoginPageTimeOut()
      }
    })
    **/
  },

  globalData: {
    isConnected: true,
    userInfo: null,
  }
})