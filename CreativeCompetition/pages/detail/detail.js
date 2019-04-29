// pages/detail/detail.js
const WXAPI = require('../../wxapi/main')
Page({

    /**
     * 页面的初始数据
     */
    data: {
      time: '2019年1月20日-5月20日',
      //  systemInfo: {}, 
      aid:'', 
      headimg:'',
      nickname:'',
        createTime:'',
        content:'',
        rank:'',
        thumbs:'',
        zan:'点赞',
        isthumbs:'',
        aid:'',
        imgs:'',
        sessionId:'',
        aid1:'',
        topImg:'',
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function (options) {
      var that = this;
      var str = options.detail;
      var detail = str.split("|");
      this.setData({
        aid1: detail[0],
        time: detail[2],
        topImg: detail[3],
      })
      const _this = this
      const token = wx.getStorageSync('token');
      if (!token) {
        _this.goLoginPageTimeOut()
        return
      }
      /**
       * token 检查**/
      WXAPI.checkToken(token).then(function (res) {
        console.log(res)
        if (res.code != 1000) {
          wx.removeStorageSync('token')
          _this.goLoginPageTimeOut()
        }
      })

    },

    /**
     * 生命周期函数--监听页面初次渲染完成
     */
    onReady: function () {

    },
    /**
     * 生命周期函数--监听页面显示
     */
    onShow: function () {
      var that = this;
      var aid1 = that.data.aid1;
      var sessionId = wx.getStorageSync('token');
      WXAPI.getwxarticle({
        sessionId: sessionId,
        aid: aid1,
        apiName: 'WX_GET_ARTICLE',
        appId: 'wxa192d06cd30b94b7'
      }).then(function (res) {
        that.setData({
          sessionId: sessionId,
          headimg: res.data.art.author.headimg,
          nickname: res.data.art.author.nickname,
          createTime: res.data.art.createTime,
          content: res.data.art.content,
          rank: res.data.art.rank,
          thumbs: res.data.art.thumbs,
          isthumbs: res.data.art.isthumbs,
          aid: res.data.art.aid,
          imgs: res.data.imgsList
        })
      })
    },

    /**
     * 生命周期函数--监听页面隐藏
     */
    onHide: function () {

    },

    /**
     * 生命周期函数--监听页面卸载
     */
    onUnload: function () {

    },

    /**
     * 页面相关事件处理函数--监听用户下拉动作
     */
    onPullDownRefresh: function () {

    },

    /**
     * 页面上拉触底事件的处理函数
     */
    onReachBottom: function () {

    },

    /**
     * 用户点击右上角分享
     */
    onShareAppMessage: function () {
        return {

            title: '我正在参加创意大赛，集赞有奖！帮我点个赞。。',


          //  path: '/page/user?id=123' // 路径，传递参数到指定页面。

        }
  },//点赞
    zan: function (e) {
      var that = this;
      var method = 1;
      var aid = this.data.aid;
      var sessionId = that.data.sessionId;
        WXAPI.zan({
          sessionId: sessionId,
          method: method,
          aid: aid,
          apiName: 'WX_ARTICLES_THUMBS',
          appId: 'wxa192d06cd30b94b7'
        }).then(function (res) {
          if (res.code == 4000) {
            that.setData({
              thumbs: that.data.thumbs + 1
            })
            wx.showToast({
              title: "点赞成功",
              icon: 'none',
              duration: 2000
            })
          } else {
            wx.showToast({
              title: res.msg,
              icon: 'none',
              duration: 2000
            })
          }
        })
    },
    //说明
    introFun: function () {
        wx.navigateTo({
            url: "../explain/explain"

        })
    },
    //查看其它创意
    findOther:function(){
        wx.navigateTo({
            url: "../competition/competition"

        })
  },
  goLoginPageTimeOut: function () {
    // wx.removeStorageSync('token')
    setTimeout(function () {
      wx.navigateTo({
        url: "/pages/authorize/index"
      })
    }, 1000)
  },
    //查看放大预览
    imgYu: function (event) {
        var src = event.currentTarget.dataset.src;//获取data-src
        var imgList = event.currentTarget.dataset.list;//获取data-list
        //图片预览
        wx.previewImage({
            current: src, // 当前显示图片的http链接
            urls: imgList // 需要预览的图片http链接列表
        })
    }
})