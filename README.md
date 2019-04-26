> 生活日志类小程序

>>##后台采用 springboot + redis + mongodb
>>>### redis 用来做单点登录
>>>### mongdb存储文章（非结构化数据）；存储访问日志；存储点赞记录;存储中奖结果
>>>### springboot 微服务基础框架（为后续更新spring cloud奠定基础）

>>>## 小程序端(遇到并解决的问题)
>>>### 小程序跟app 一样，存在版本迭代，所以必须加入版本更新代码，如下：

    ` ``javascript
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
    ` ``
    
    >>>### 小程序统一授权（app.js）获取token,未获取到需重新授权登录，此授权指第三方服务器授权，并非微信，注意区分
   
    
   ` ``Bash  goLoginPageTimeOut: function () 
   
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
    
  ` ``Bash  goStartIndexPage: function ()
  
   setTimeout(function () {
      wx.redirectTo({
        url: "/pages/competition/competition"
      })
    }, 1000)
    
  ` ``
  
  
  >>>---------------------------token判断------------------------------
  
  
  ` ``Bash
   onShow(e) {
   
    const _this = this
    const token = wx.getStorageSync('token');
    console.log(token)
    if (!token) {
      _this.goLoginPageTimeOut()
      return
    }
    WXAPI.checkToken(token).then(function (res) {
      console.log(res)
      if (res.code != 1000) {
        wx.removeStorageSync('token')
        _this.goLoginPageTimeOut()
      }
    }) 
    
  }
  ` ``
  
 >>> ###下拉刷新留白问题 wx.stopPullDownRefresh(); ，把对应的代码加入setTimeout 中，即可解决
 >>>###---------------------页面相关事件处理函数--监听用户下拉动作----
 
    ` ``javascript
    onPullDownRefresh: function() {
      wx.stopPullDownRefresh();
      var that = this ;
        wx.showLoading({
            title: '加载中',
        })
        this.setData({
                page1 : 1,
                ourselfs: [],
                ranks: [],
                noMoreHidden: true,
        })
        setTimeout(function() {
            that.onLoad();
            wx.hideLoading();
        }, 2000);

    }
    ` ``
